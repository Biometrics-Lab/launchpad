# BL-55: Assessment Session Workflow Endpoints — Design Spec

## Overview

Add start/stop lifecycle to sessions. On start, publish `SessionStartedEvent` so the integration module can begin pushing rep data. On stop, mark complete and compute session-level metric aggregates. Keep architecture WebSocket-ready without implementing WebSocket yet.

---

## Database

One new column on `session`:

```sql
ALTER TABLE session ADD COLUMN status VARCHAR(20) NULL;
```

Allowed values: `ACTIVE`, `COMPLETE`. Null means the session was created but not yet started. No `PENDING` status.

No other schema changes — `integrationId` is not added to `session`.

---

## Module DB Access Rules

- `launchpad` — owns the main DB, reads/writes freely
- `integration` — no DB access; stateless S3 operations, communicates only via events
- `hardware` — no DB access; fully event-driven

All data downstream modules need must be carried in event payloads. This keeps module boundaries clean and makes future microservice extraction a transport swap, not a data migration.

---

## Common Events (`com.biolab.common`)

Four new records:

```java
// Published by launchpad when session starts — carries full metric config
// so hardware never needs to query the DB
public record SessionStartedEvent(
    Integer sessionId,
    List<SessionMetricConfig> metrics
) {}

public record SessionMetricConfig(
    Integer conditionalMetricId,
    Integer dataSourceId,
    String dataSourceContent    // DataSource.content — integration/hardware config
) {}

// Published by launchpad when session stops
public record SessionStoppedEvent(Integer sessionId) {}

// Published by hardware mock (BL-52) per rep
public record RepDataReceivedEvent(
    Integer sessionId,
    Timestamp startTime,
    List<RepMetricData> metrics,
    List<RepResourceData> resources  // empty if no resources; one entry per camera/source
) {}

// Each resource carries its own URL and processing status
public record RepResourceData(
    String url,
    UrlStatus urlStatus
) {}

public record RepMetricData(
    Integer conditionalMetricId,
    Number value
) {}
```

`RepDataReceivedEvent` is in `common` so both the hardware module (publisher, BL-52) and launchpad (listener, BL-55) can reference it without a cross-module dependency.

---

## Session Model & DTO

**`Session.java`** — add `status` field:
```java
@Column("status")
private String status;   // null | "ACTIVE" | "COMPLETE"
```

**`SessionDto.java`** — add `status` field (nullable, not validated):
```java
String status;
```

**`SessionMapper`** — map new field (no special logic, direct pass-through).

---

## New Endpoints

### `POST /api/v1/sessions/{id}/start`

1. Load session — 404 if not found.
2. Reject if `status` is not null — return 409 with message `"Session already started"`.
3. Set `status = ACTIVE`, save.
4. Load `AssessmentMetric` records for the session's `assessmentId`.
5. For each, load the corresponding `DataSource` to get `content`.
6. Build `List<SessionMetricConfig>` from the results.
7. Publish `SessionStartedEvent(sessionId, metrics)`.
8. Return updated `SessionDto`.

### `POST /api/v1/sessions/{id}/stop`

1. Load session — 404 if not found.
2. Reject if `status != ACTIVE` — return 409 with message `"Session is not active"`.
3. Set `status = COMPLETE`, save.
4. Publish `SessionStoppedEvent(sessionId)`.
5. Compute session-level aggregates (see below).
6. Return updated `SessionDto`.

Both endpoints live in `SessionController` (existing controller, two new `@PostMapping` methods).

---

## Session Aggregate Computation (on stop)

Triggered synchronously inside the stop handler before returning.

For each `RepMetric` belonging to reps of this session:
- Group by `conditionalMetricId`
- Compute `min`, `max`, `avg` of `value`
- Upsert into `SessionMetric` (insert if not exists, update if exists)

Query path: two-step — `RepRepository.findAllBySessionId(sessionId)` to get rep IDs, then `RepMetricRepository.findAllByRepIdIn(repIds)`. Both are standard Spring Data JDBC derived queries; add them to the respective repositories.

Aggregates are computed from whatever `RepMetric` rows exist at stop time. Reps still in-flight (published after stop) are not included — acceptable for MVP.

---

## RepDataReceivedEvent Listener

New `RepEventService` in `com.biolab.launchpad.internal.service` (consistent with existing `ResourceEventService` which handles cross-module events at the same layer):

```
@ApplicationModuleListener
void onRepDataReceived(RepDataReceivedEvent event) {
    1. Load session → assessmentId
    2. Load AssessmentMetric list for assessmentId → extract valid Set<conditionalMetricId>
    3. Create Rep { sessionId, startTime } → save → get repId
    4. For each metric in event.metrics() WHERE conditionalMetricId is in valid set:
           create RepMetric { repId, conditionalMetricId, value } → save
    5. For each resource in event.resources(): create RepResource { repId, url=resource.url(), type=ResourceContentType.VIDEO.getValue(), urlStatus=resource.urlStatus() } → save
    6. Call sessionNotificationService.broadcastRep(sessionId, repDto)
}
```

Launchpad filters against its own data — it is the authority on which metrics are valid for the assessment. Hardware is not expected to pre-filter, but any metrics it sends outside the valid set are silently dropped.

---

## SessionNotificationService (WebSocket-ready abstraction)

Interface in `com.biolab.launchpad.internal.service`:

```java
public interface SessionNotificationService {
    void broadcastRep(Integer sessionId, Object payload);
}
```

Default impl (`SessionNotificationServiceImpl`) logs only:
```java
log.info("Rep broadcast [session={}]: {}", sessionId, payload);
```

When WebSocket is added: swap this impl for one that uses `SimpMessagingTemplate.convertAndSend("/topic/session/" + sessionId + "/reps", payload)`. The listener code does not change.

---

## Tests

All integration tests use `@SpringBootTest + @AutoConfigureMockMvc + Testcontainers` (existing pattern).

| Test class | Cases |
|---|---|
| `SessionWorkflowIntegrationTest` | start happy path (status → ACTIVE, event published); start on already-ACTIVE → 409; start on COMPLETE → 409; start on missing session → 404; stop happy path (status → COMPLETE, aggregates computed); stop on not-ACTIVE session → 409; stop on missing session → 404 |
| `RepDataReceivedEventListenerTest` | Publish `RepDataReceivedEvent` → Rep + RepMetric rows saved; with resourceUrl → RepResource saved; broadcastRep called |

`EntityFactory` gets a `createActiveSession()` helper (create session + call start) to reduce boilerplate in stop tests.

---

## Out of Scope (BL-55)

- WebSocket STOMP broker — deferred; `SessionNotificationService` is the seam
- `integrationId` on Session — not needed yet
- Blast Motion rep generator (BL-52) — publishes `RepDataReceivedEvent`; BL-55 only defines the listener
