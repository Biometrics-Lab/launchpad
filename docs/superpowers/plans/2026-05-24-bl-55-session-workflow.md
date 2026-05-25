# BL-55 Session Workflow Endpoints Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add `POST /sessions/{id}/start` and `POST /sessions/{id}/stop` endpoints that manage session lifecycle, publish Spring Modulith events, and persist rep data received from the hardware module.

**Architecture:** Session gets a nullable `status` column (ACTIVE / COMPLETE). Start publishes `SessionStartedEvent` carrying full metric config so downstream modules never query the DB. Stop computes `SessionMetric` aggregates synchronously. `RepDataReceivedEvent` is handled by `RepEventService` which persists Rep + RepMetric rows and calls a `SessionNotificationService` stub (WebSocket-ready seam).

**Tech Stack:** Spring Boot 3.5, Spring Modulith, Spring Data JDBC, Liquibase, JUnit 5, Testcontainers, MockMvc, Awaitility, Lombok, MapStruct.

---

## File Map

**Create:**
- `src/main/resources/db/changelog/changeset/1_bl_55.yml` — adds `status` column to `session`
- `src/main/java/com/biolab/common/SessionStartedEvent.java`
- `src/main/java/com/biolab/common/SessionMetricConfig.java`
- `src/main/java/com/biolab/common/SessionStoppedEvent.java`
- `src/main/java/com/biolab/common/RepDataReceivedEvent.java`
- `src/main/java/com/biolab/common/RepMetricData.java`
- `src/main/java/com/biolab/launchpad/internal/security/exceptions/ConflictException.java`
- `src/main/java/com/biolab/launchpad/internal/service/SessionNotificationService.java`
- `src/main/java/com/biolab/launchpad/internal/service/SessionNotificationServiceImpl.java`
- `src/main/java/com/biolab/launchpad/internal/service/SessionWorkflowService.java`
- `src/main/java/com/biolab/launchpad/internal/service/RepEventService.java`
- `src/test/java/com/biolab/launchpad/internal/web/controller/SessionWorkflowIntegrationTest.java`
- `src/test/java/com/biolab/launchpad/RepDataReceivedEventListenerTest.java`

**Modify:**
- `src/main/resources/db/changelog/biolab.changelog.yml` — include new changeset
- `src/main/java/com/biolab/launchpad/internal/repository/model/Session.java` — add `status` field
- `src/main/java/com/biolab/launchpad/internal/web/dto/SessionDto.java` — add `status` field
- `src/main/java/com/biolab/launchpad/internal/repository/RepRepository.java` — add `findAllBySessionId`
- `src/main/java/com/biolab/launchpad/internal/repository/RepMetricRepository.java` — add `findAllByRepIdIn`
- `src/main/java/com/biolab/launchpad/internal/repository/AssessmentMetricRepository.java` — add `findAllByAssessmentId`
- `src/main/java/com/biolab/launchpad/internal/repository/SessionMetricRepository.java` — add `findBySessionIdAndConditionalMetricId`
- `src/main/java/com/biolab/launchpad/internal/web/controller/SessionController.java` — add `/start` and `/stop` endpoints
- `src/main/java/com/biolab/launchpad/internal/web/controller/exception/GlobalExceptionHandler.java` — add 409 handler
- `src/test/java/com/biolab/launchpad/internal/web/controller/EntityFactory.java` — add `createActiveSession()`

---

## Task 1: DB Migration — add status column to session

**Files:**
- Create: `src/main/resources/db/changelog/changeset/1_bl_55.yml`
- Modify: `src/main/resources/db/changelog/biolab.changelog.yml`

- [ ] **Step 1: Create the changeset file**

```yaml
# src/main/resources/db/changelog/changeset/1_bl_55.yml
databaseChangeLog:
  - changeSet:
      id: 1-BL-55-1
      author: roman.vakulenko
      changes:
        - addColumn:
            tableName: session
            columns:
              - column:
                  name: status
                  type: varchar(20)
                  constraints:
                    nullable: true
```

- [ ] **Step 2: Register the changeset in the master changelog**

Add to the end of `src/main/resources/db/changelog/biolab.changelog.yml`:
```yaml
  - include:
      file: db/changelog/changeset/1_bl_55.yml
```

- [ ] **Step 3: Verify compilation and migration syntax**

```bash
./gradlew build -x test
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/db/changelog/changeset/1_bl_55.yml \
        src/main/resources/db/changelog/biolab.changelog.yml
git commit -m "[BL-55] Add status column to session table"
```

---

## Task 2: Update Session model and DTO

**Files:**
- Modify: `src/main/java/com/biolab/launchpad/internal/repository/model/Session.java`
- Modify: `src/main/java/com/biolab/launchpad/internal/web/dto/SessionDto.java`

- [ ] **Step 1: Add status field to Session**

In `Session.java`, add after the `startTime` field:
```java
@Column("status")
private String status;   // null | "ACTIVE" | "COMPLETE"
```

Full file:
```java
package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("session")
public class Session extends ID {
    @NotNull(message = "Session assessmentId cannot be null")
    @Column("assessment_id")
    private Integer assessmentId;
    @NotNull(message = "Session startTime cannot be null")
    @Column("start_time")
    private Timestamp startTime;
    @Column("status")
    private String status;
}
```

- [ ] **Step 2: Add status field to SessionDto**

Full `SessionDto.java`:
```java
package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record SessionDto(
        Integer id,
        @NotNull(message = "Session assessmentId cannot be null")
        Integer assessmentId,
        @NotNull(message = "Session startTime cannot be null")
        Timestamp startTime,
        String status
) {}
```

- [ ] **Step 3: Verify — MapStruct maps status automatically; no mapper change needed**

```bash
./gradlew build -x test
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/biolab/launchpad/internal/repository/model/Session.java \
        src/main/java/com/biolab/launchpad/internal/web/dto/SessionDto.java
git commit -m "[BL-55] Add status field to Session model and DTO"
```

---

## Task 3: Common event records

**Files:**
- Create: `src/main/java/com/biolab/common/SessionStartedEvent.java`
- Create: `src/main/java/com/biolab/common/SessionMetricConfig.java`
- Create: `src/main/java/com/biolab/common/SessionStoppedEvent.java`
- Create: `src/main/java/com/biolab/common/RepDataReceivedEvent.java`
- Create: `src/main/java/com/biolab/common/RepMetricData.java`
- Create: `src/main/java/com/biolab/common/RepResourceData.java`

- [ ] **Step 1: Create SessionMetricConfig**

```java
// src/main/java/com/biolab/common/SessionMetricConfig.java
package com.biolab.common;

public record SessionMetricConfig(
        Integer conditionalMetricId,
        Integer dataSourceId,
        String dataSourceContent
) {}
```

- [ ] **Step 2: Create SessionStartedEvent**

```java
// src/main/java/com/biolab/common/SessionStartedEvent.java
package com.biolab.common;

import java.util.List;

public record SessionStartedEvent(
        Integer sessionId,
        List<SessionMetricConfig> metrics
) {}
```

- [ ] **Step 3: Create SessionStoppedEvent**

```java
// src/main/java/com/biolab/common/SessionStoppedEvent.java
package com.biolab.common;

public record SessionStoppedEvent(Integer sessionId) {}
```

- [ ] **Step 4: Create RepMetricData**

```java
// src/main/java/com/biolab/common/RepMetricData.java
package com.biolab.common;

public record RepMetricData(
        Integer conditionalMetricId,
        Number value
) {}
```

- [ ] **Step 5: Create RepResourceData**

```java
// src/main/java/com/biolab/common/RepResourceData.java
package com.biolab.common;

public record RepResourceData(
        String url,
        UrlStatus urlStatus
) {}
```

- [ ] **Step 6: Create RepDataReceivedEvent**

```java
// src/main/java/com/biolab/common/RepDataReceivedEvent.java
package com.biolab.common;

import java.sql.Timestamp;
import java.util.List;

public record RepDataReceivedEvent(
        Integer sessionId,
        Timestamp startTime,
        List<RepMetricData> metrics,
        List<RepResourceData> resources  // empty list if none; one entry per camera/source
) {}
```

- [ ] **Step 7: Build to verify**

```bash
./gradlew build -x test
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 8: Commit**

```bash
git add src/main/java/com/biolab/common/
git commit -m "[BL-55] Add session lifecycle and rep data event records to common"
```

---

## Task 4: Repository derived queries

**Files:**
- Modify: `src/main/java/com/biolab/launchpad/internal/repository/RepRepository.java`
- Modify: `src/main/java/com/biolab/launchpad/internal/repository/RepMetricRepository.java`
- Modify: `src/main/java/com/biolab/launchpad/internal/repository/AssessmentMetricRepository.java`
- Modify: `src/main/java/com/biolab/launchpad/internal/repository/SessionMetricRepository.java`

- [ ] **Step 1: Add findAllBySessionId to RepRepository**

```java
package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Rep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepRepository extends CrudRepository<Rep, Integer> {
    List<Rep> findAllBySessionId(Integer sessionId);
}
```

- [ ] **Step 2: Add findAllByRepIdIn to RepMetricRepository**

```java
package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.RepMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepMetricRepository extends CrudRepository<RepMetric, Integer> {
    List<RepMetric> findAllByRepIdIn(List<Integer> repIds);
}
```

- [ ] **Step 3: Add findAllByAssessmentId to AssessmentMetricRepository**

```java
package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentMetricRepository extends CrudRepository<AssessmentMetric, Integer> {
    List<AssessmentMetric> findAllByAssessmentId(Integer assessmentId);
}
```

- [ ] **Step 4: Add findBySessionIdAndConditionalMetricId to SessionMetricRepository**

```java
package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.SessionMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionMetricRepository extends CrudRepository<SessionMetric, Integer> {
    Optional<SessionMetric> findBySessionIdAndConditionalMetricId(Integer sessionId, Integer conditionalMetricId);
}
```

- [ ] **Step 5: Build to verify**

```bash
./gradlew build -x test
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/biolab/launchpad/internal/repository/RepRepository.java \
        src/main/java/com/biolab/launchpad/internal/repository/RepMetricRepository.java \
        src/main/java/com/biolab/launchpad/internal/repository/AssessmentMetricRepository.java \
        src/main/java/com/biolab/launchpad/internal/repository/SessionMetricRepository.java
git commit -m "[BL-55] Add derived queries to Rep, RepMetric, AssessmentMetric, SessionMetric repositories"
```

---

## Task 5: ConflictException + GlobalExceptionHandler + SessionNotificationService

**Files:**
- Create: `src/main/java/com/biolab/launchpad/internal/security/exceptions/ConflictException.java`
- Modify: `src/main/java/com/biolab/launchpad/internal/web/controller/exception/GlobalExceptionHandler.java`
- Create: `src/main/java/com/biolab/launchpad/internal/service/SessionNotificationService.java`
- Create: `src/main/java/com/biolab/launchpad/internal/service/SessionNotificationServiceImpl.java`

- [ ] **Step 1: Create ConflictException**

```java
// src/main/java/com/biolab/launchpad/internal/security/exceptions/ConflictException.java
package com.biolab.launchpad.internal.security.exceptions;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
```

- [ ] **Step 2: Add handler to GlobalExceptionHandler**

Add this method to `GlobalExceptionHandler.java` after the `handleNotFoundByException` method:

```java
@ResponseStatus(HttpStatus.CONFLICT)
@ExceptionHandler(ConflictException.class)
public ResponseDto handleConflictException(ConflictException ex) {
    return ResponseDto.builder()
            .status(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .build();
}
```

Also add the import at the top if not present:
```java
import com.biolab.launchpad.internal.security.exceptions.ConflictException;
```

- [ ] **Step 3: Create SessionNotificationService interface**

```java
// src/main/java/com/biolab/launchpad/internal/service/SessionNotificationService.java
package com.biolab.launchpad.internal.service;

public interface SessionNotificationService {
    void broadcastRep(Integer sessionId, Object payload);
}
```

- [ ] **Step 4: Create SessionNotificationServiceImpl (log-only)**

```java
// src/main/java/com/biolab/launchpad/internal/service/SessionNotificationServiceImpl.java
package com.biolab.launchpad.internal.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SessionNotificationServiceImpl implements SessionNotificationService {

    @Override
    public void broadcastRep(Integer sessionId, Object payload) {
        log.info("Rep broadcast [session={}]: {}", sessionId, payload);
    }
}
```

- [ ] **Step 5: Build to verify**

```bash
./gradlew build -x test
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/biolab/launchpad/internal/security/exceptions/ConflictException.java \
        src/main/java/com/biolab/launchpad/internal/web/controller/exception/GlobalExceptionHandler.java \
        src/main/java/com/biolab/launchpad/internal/service/SessionNotificationService.java \
        src/main/java/com/biolab/launchpad/internal/service/SessionNotificationServiceImpl.java
git commit -m "[BL-55] Add ConflictException, 409 handler, and SessionNotificationService"
```

---

## Task 6: TDD — Start session endpoint

**Files:**
- Create: `src/test/java/com/biolab/launchpad/internal/web/controller/SessionWorkflowIntegrationTest.java`
- Create: `src/main/java/com/biolab/launchpad/internal/service/SessionWorkflowService.java`
- Modify: `src/main/java/com/biolab/launchpad/internal/web/controller/SessionController.java`

- [ ] **Step 1: Write the failing start tests**

```java
// src/test/java/com/biolab/launchpad/internal/web/controller/SessionWorkflowIntegrationTest.java
package com.biolab.launchpad.internal.web.controller;

import com.biolab.TestcontainersConfiguration;
import com.biolab.common.SessionStartedEvent;
import com.biolab.launchpad.internal.repository.SessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, EntityFactory.class})
@DisplayName("Session Workflow Integration Tests")
class SessionWorkflowIntegrationTest {

    private static final String API = "/api/v1/sessions";

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SessionRepository sessionRepository;
    @Autowired EntityFactory entityFactory;
    @MockitoSpyBean ApplicationEventPublisher eventPublisher;

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        entityFactory.cleanup();
    }

    @Nested
    @DisplayName("Start Session")
    class StartTests {

        @Test
        @DisplayName("POST /sessions/{id}/start -> sets status ACTIVE and publishes SessionStartedEvent")
        void startHappyPath() throws Exception {
            var session = entityFactory.createSession();

            String jsonResponse = mvc.perform(
                            post(API + "/" + session.getId() + "/start")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            assertEquals("ACTIVE", responseNode.get("status").asText());

            var saved = sessionRepository.findById(session.getId()).orElseThrow();
            assertEquals("ACTIVE", saved.getStatus());

            verify(eventPublisher).publishEvent(any(SessionStartedEvent.class));
        }

        @Test
        @DisplayName("POST /sessions/{id}/start on already ACTIVE session -> 409")
        void startAlreadyActive() throws Exception {
            var session = entityFactory.createActiveSession();

            String jsonResponse = mvc.perform(
                            post(API + "/" + session.getId() + "/start")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            assertEquals(409, responseNode.get("status").asInt());
            assertEquals("Session already started", responseNode.get("message").asText());
        }

        @Test
        @DisplayName("POST /sessions/{id}/start on COMPLETE session -> 409")
        void startAlreadyComplete() throws Exception {
            var session = entityFactory.createSession();
            session.setStatus("COMPLETE");
            sessionRepository.save(session);

            mvc.perform(
                            post(API + "/" + session.getId() + "/start")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("POST /sessions/{id}/start with unknown id -> 404")
        void startNotFound() throws Exception {
            String jsonResponse = mvc.perform(
                            post(API + "/999999/start")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            assertEquals(404, responseNode.get("status").asInt());
        }
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
./gradlew test --tests "com.biolab.launchpad.internal.web.controller.SessionWorkflowIntegrationTest" 2>&1 | tail -20
```
Expected: compilation error or 404 responses (endpoint not mapped yet).

- [ ] **Step 3: Create SessionWorkflowService**

```java
// src/main/java/com/biolab/launchpad/internal/service/SessionWorkflowService.java
package com.biolab.launchpad.internal.service;

import com.biolab.common.SessionMetricConfig;
import com.biolab.common.SessionStartedEvent;
import com.biolab.common.SessionStoppedEvent;
import com.biolab.launchpad.internal.repository.*;
import com.biolab.launchpad.internal.repository.model.*;
import com.biolab.launchpad.internal.security.exceptions.ConflictException;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.web.dto.SessionDto;
import com.biolab.launchpad.internal.web.mapper.SessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.biolab.launchpad.internal.web.mapper.SessionMapper.sessionMapper;

@Service
@RequiredArgsConstructor
@Log4j2
public class SessionWorkflowService {

    private final SessionRepository sessionRepository;
    private final AssessmentMetricRepository assessmentMetricRepository;
    private final DataSourceRepository dataSourceRepository;
    private final RepRepository repRepository;
    private final RepMetricRepository repMetricRepository;
    private final SessionMetricRepository sessionMetricRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SessionDto startSession(Integer id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundByException("Session not found by id: %d", id));

        if (session.getStatus() != null) {
            throw new ConflictException("Session already started");
        }

        session.setStatus("ACTIVE");
        Session saved = sessionRepository.save(session);

        List<AssessmentMetric> assessmentMetrics =
                assessmentMetricRepository.findAllByAssessmentId(session.getAssessmentId());

        List<SessionMetricConfig> metricConfigs = assessmentMetrics.stream()
                .map(am -> {
                    DataSource ds = dataSourceRepository.findById(am.getDataSourceId()).orElseThrow();
                    return new SessionMetricConfig(
                            am.getConditionalMetricId(),
                            am.getDataSourceId(),
                            ds.getContent());
                })
                .toList();

        eventPublisher.publishEvent(new SessionStartedEvent(id, metricConfigs));
        log.info("Session {} started with {} metric configs", id, metricConfigs.size());

        return sessionMapper.toDto(saved);
    }

    @Transactional
    public SessionDto stopSession(Integer id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundByException("Session not found by id: %d", id));

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new ConflictException("Session is not active");
        }

        session.setStatus("COMPLETE");
        Session saved = sessionRepository.save(session);

        eventPublisher.publishEvent(new SessionStoppedEvent(id));

        computeAggregates(id);
        log.info("Session {} stopped", id);

        return sessionMapper.toDto(saved);
    }

    private void computeAggregates(Integer sessionId) {
        List<Rep> reps = repRepository.findAllBySessionId(sessionId);
        if (reps.isEmpty()) return;

        List<Integer> repIds = reps.stream().map(Rep::getId).toList();
        List<RepMetric> repMetrics = repMetricRepository.findAllByRepIdIn(repIds);

        Map<Integer, List<Number>> byMetric = repMetrics.stream()
                .collect(Collectors.groupingBy(
                        RepMetric::getConditionalMetricId,
                        Collectors.mapping(RepMetric::getValue, Collectors.toList())));

        byMetric.forEach((conditionalMetricId, values) -> {
            double min = values.stream().mapToDouble(Number::doubleValue).min().orElse(0);
            double max = values.stream().mapToDouble(Number::doubleValue).max().orElse(0);
            double avg = values.stream().mapToDouble(Number::doubleValue).average().orElse(0);

            sessionMetricRepository
                    .findBySessionIdAndConditionalMetricId(sessionId, conditionalMetricId)
                    .ifPresentOrElse(
                            existing -> {
                                existing.setMinValue(min);
                                existing.setMaxValue(max);
                                existing.setAvgValue(avg);
                                sessionMetricRepository.save(existing);
                            },
                            () -> sessionMetricRepository.save(SessionMetric.builder()
                                    .sessionId(sessionId)
                                    .conditionalMetricId(conditionalMetricId)
                                    .minValue(min)
                                    .maxValue(max)
                                    .avgValue(avg)
                                    .build())
                    );
        });
    }
}
```

- [ ] **Step 4: Add start endpoint to SessionController**

Add to `SessionController.java` — inject `SessionWorkflowService` and add the method:

```java
private final SessionWorkflowService sessionWorkflowService;
```

```java
@PostMapping("/{id}/start")
public SessionDto start(@PathVariable Integer id) {
    return sessionWorkflowService.startSession(id);
}
```

Full updated `SessionController.java`:
```java
package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Session;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.SessionService;
import com.biolab.launchpad.internal.service.SessionWorkflowService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.SessionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.SessionMapper.sessionMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sessions")
@Log4j2
public class SessionController {

    private final SessionService sessionService;
    private final SessionWorkflowService sessionWorkflowService;

    @PostMapping
    public SessionDto create(@Valid @RequestBody SessionDto sessionDto) {
        Session created = sessionService.create(sessionMapper.toModel(sessionDto));
        return sessionMapper.toDto(created);
    }

    @GetMapping
    public List<SessionDto> getAll() {
        return sessionMapper.toDtos(sessionService.findAll());
    }

    @GetMapping("/{id}")
    public SessionDto getById(@PathVariable Integer id) {
        Optional<SessionDto> sessionOptional = sessionService.findById(id).map(sessionMapper::toDto);
        if (sessionOptional.isPresent()) {
            return sessionOptional.get();
        } else {
            log.warn("Could not find session with id {}", id);
            throw new NotFoundByException("Session not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        sessionService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public SessionDto update(@Valid @RequestBody SessionDto sessionDTO) {
        Session updated = sessionService.update(sessionMapper.toModel(sessionDTO));
        return sessionMapper.toDto(updated);
    }

    @PostMapping("/{id}/start")
    public SessionDto start(@PathVariable Integer id) {
        return sessionWorkflowService.startSession(id);
    }

    @PostMapping("/{id}/stop")
    public SessionDto stop(@PathVariable Integer id) {
        return sessionWorkflowService.stopSession(id);
    }
}
```

- [ ] **Step 5: Add createSession() and createActiveSession() to EntityFactory**

Add these two methods to `EntityFactory.java`. Look for the existing `createRep()` method to find what `createSession()` already exists — if `createSession(Integer assessmentId)` already exists, only add `createActiveSession()`. If not, add both.

Add `SessionRepository` field if not already present:
```java
private final SessionRepository sessionRepository;
```

Add `createSession()` (skip if it already exists):
```java
public Session createSession() {
    var assessment = createAssessment();
    return sessionRepository.save(Session.builder()
            .assessmentId(assessment.getId())
            .startTime(Timestamp.valueOf(LocalDateTime.now()))
            .build());
}
```

Add `createActiveSession()`:
```java
public Session createActiveSession() {
    Session session = createSession();
    session.setStatus("ACTIVE");
    return sessionRepository.save(session);
}
```

- [ ] **Step 6: Run start tests — verify they pass**

```bash
./gradlew test --tests "com.biolab.launchpad.internal.web.controller.SessionWorkflowIntegrationTest.StartTests" 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`, all start tests green.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/biolab/launchpad/internal/service/SessionWorkflowService.java \
        src/main/java/com/biolab/launchpad/internal/web/controller/SessionController.java \
        src/test/java/com/biolab/launchpad/internal/web/controller/SessionWorkflowIntegrationTest.java \
        src/test/java/com/biolab/launchpad/internal/web/controller/EntityFactory.java
git commit -m "[BL-55] Implement start session endpoint with SessionWorkflowService"
```

---

## Task 7: TDD — Stop session endpoint

**Files:**
- Modify: `src/test/java/com/biolab/launchpad/internal/web/controller/SessionWorkflowIntegrationTest.java`

`SessionWorkflowService.stopSession()` and `SessionController.stop()` were already added in Task 6. This task adds the tests and verifies them.

- [ ] **Step 1: Add stop tests to SessionWorkflowIntegrationTest**

Add a new `StopTests` nested class inside `SessionWorkflowIntegrationTest`:

```java
@Nested
@DisplayName("Stop Session")
class StopTests {

    @Test
    @DisplayName("POST /sessions/{id}/stop -> sets status COMPLETE and publishes SessionStoppedEvent")
    void stopHappyPath() throws Exception {
        var session = entityFactory.createActiveSession();

        String jsonResponse = mvc.perform(
                        post(API + "/" + session.getId() + "/stop")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(httpBasic("biolab", "biolab"))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(jsonResponse);
        assertEquals("COMPLETE", responseNode.get("status").asText());

        var saved = sessionRepository.findById(session.getId()).orElseThrow();
        assertEquals("COMPLETE", saved.getStatus());

        verify(eventPublisher).publishEvent(any(com.biolab.common.SessionStoppedEvent.class));
    }

    @Test
    @DisplayName("POST /sessions/{id}/stop on non-ACTIVE session -> 409")
    void stopNotActive() throws Exception {
        var session = entityFactory.createSession();

        String jsonResponse = mvc.perform(
                        post(API + "/" + session.getId() + "/stop")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(httpBasic("biolab", "biolab"))
                )
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(jsonResponse);
        assertEquals(409, responseNode.get("status").asInt());
        assertEquals("Session is not active", responseNode.get("message").asText());
    }

    @Test
    @DisplayName("POST /sessions/{id}/stop with unknown id -> 404")
    void stopNotFound() throws Exception {
        mvc.perform(
                        post(API + "/999999/stop")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(httpBasic("biolab", "biolab"))
                )
                .andExpect(status().isNotFound());
    }
}
```

- [ ] **Step 2: Run stop tests — verify they pass**

```bash
./gradlew test --tests "com.biolab.launchpad.internal.web.controller.SessionWorkflowIntegrationTest.StopTests" 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`, all stop tests green.

- [ ] **Step 3: Commit**

```bash
git add src/test/java/com/biolab/launchpad/internal/web/controller/SessionWorkflowIntegrationTest.java
git commit -m "[BL-55] Add stop session integration tests"
```

---

## Task 8: TDD — RepDataReceivedEvent listener

**Files:**
- Create: `src/test/java/com/biolab/launchpad/RepDataReceivedEventListenerTest.java`
- Create: `src/main/java/com/biolab/launchpad/internal/service/RepEventService.java`

- [ ] **Step 1: Write the failing listener tests**

```java
// src/test/java/com/biolab/launchpad/RepDataReceivedEventListenerTest.java
package com.biolab.launchpad;

import com.biolab.TestcontainersConfiguration;
import com.biolab.common.RepDataReceivedEvent;
import com.biolab.common.RepMetricData;
import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.RepRepository;
import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.model.Session;
import com.biolab.launchpad.internal.service.SessionNotificationService;
import com.biolab.launchpad.internal.web.controller.EntityFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import({TestcontainersConfiguration.class, EntityFactory.class})
@DisplayName("RepDataReceivedEvent Listener Tests")
class RepDataReceivedEventListenerTest {

    @Autowired ApplicationEventPublisher eventPublisher;
    @Autowired RepRepository repRepository;
    @Autowired RepMetricRepository repMetricRepository;
    @Autowired RepResourceRepository repResourceRepository;
    @Autowired EntityFactory entityFactory;
    @MockitoSpyBean SessionNotificationService sessionNotificationService;

    Session session;

    @BeforeEach
    void setUp() {
        session = entityFactory.createActiveSession();
    }

    @AfterEach
    void tearDown() {
        repResourceRepository.deleteAll();
        repMetricRepository.deleteAll();
        repRepository.deleteAll();
        entityFactory.cleanup();
    }

    @Test
    @DisplayName("RepDataReceivedEvent -> Rep and RepMetric rows persisted")
    void repAndMetricsPersisted() {
        var conditionalMetric = entityFactory.createConditionalMetric();
        entityFactory.createAssessmentMetric(session.getAssessmentId(), conditionalMetric.getId());

        var event = new RepDataReceivedEvent(
                session.getId(),
                Timestamp.valueOf(LocalDateTime.now()),
                List.of(new RepMetricData(conditionalMetric.getId(), 42.5)),
                List.of()  // no resources in this test
        );

        eventPublisher.publishEvent(event);

        await().atMost(10, TimeUnit.SECONDS).until(() ->
                !repRepository.findAllBySessionId(session.getId()).isEmpty());

        var reps = repRepository.findAllBySessionId(session.getId());
        assertEquals(1, reps.size());

        var repMetrics = repMetricRepository.findAllByRepIdIn(List.of(reps.get(0).getId()));
        assertEquals(1, repMetrics.size());
        assertEquals(conditionalMetric.getId(), repMetrics.get(0).getConditionalMetricId());
        assertEquals(42.5, repMetrics.get(0).getValue().doubleValue(), 0.001);

        verify(sessionNotificationService).broadcastRep(eq(session.getId()), any());
    }

    @Test
    @DisplayName("RepDataReceivedEvent with resourceUrl -> RepResource persisted")
    void repResourcePersisted() {
        var url1 = "http://localhost:4566/biolab-resources/resources/test/cam1.mp4";
        var url2 = "http://localhost:4566/biolab-resources/resources/test/cam2.mp4";
        var event = new RepDataReceivedEvent(
                session.getId(),
                Timestamp.valueOf(LocalDateTime.now()),
                List.of(),
                List.of(
                        new RepResourceData(url1, UrlStatus.READY),
                        new RepResourceData(url2, UrlStatus.PENDING)
                )
        );

        eventPublisher.publishEvent(event);

        await().atMost(10, TimeUnit.SECONDS).until(() ->
                !repRepository.findAllBySessionId(session.getId()).isEmpty());

        var reps = repRepository.findAllBySessionId(session.getId());
        var resources = (List<RepResource>) repResourceRepository.findAll();
        assertEquals(2, resources.size());

        var ready = resources.stream().filter(r -> r.getUrl().equals(url1)).findFirst().orElseThrow();
        assertEquals(UrlStatus.READY, ready.getUrlStatus());
        assertEquals(reps.get(0).getId(), ready.getRepId());

        var pending = resources.stream().filter(r -> r.getUrl().equals(url2)).findFirst().orElseThrow();
        assertEquals(UrlStatus.PENDING, pending.getUrlStatus());
    }

    @Test
    @DisplayName("RepDataReceivedEvent with metric not in AssessmentMetric -> metric silently dropped")
    void invalidMetricDropped() {
        var event = new RepDataReceivedEvent(
                session.getId(),
                Timestamp.valueOf(LocalDateTime.now()),
                List.of(new RepMetricData(999999, 99.0)),
                List.of()  // no resources
        );

        eventPublisher.publishEvent(event);

        await().atMost(10, TimeUnit.SECONDS).until(() ->
                !repRepository.findAllBySessionId(session.getId()).isEmpty());

        var reps = repRepository.findAllBySessionId(session.getId());
        var repMetrics = repMetricRepository.findAllByRepIdIn(List.of(reps.get(0).getId()));
        assertTrue(repMetrics.isEmpty());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
./gradlew test --tests "com.biolab.launchpad.RepDataReceivedEventListenerTest" 2>&1 | tail -20
```
Expected: compilation error (RepEventService doesn't exist yet) or timeout (no listener).

- [ ] **Step 3: Implement RepEventService**

```java
// src/main/java/com/biolab/launchpad/internal/service/RepEventService.java
package com.biolab.launchpad.internal.service;

import com.biolab.common.RepDataReceivedEvent;
import com.biolab.common.ResourceContentType;
import com.biolab.common.UrlStatus;
import com.biolab.launchpad.internal.repository.*;
import com.biolab.launchpad.internal.repository.model.*;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RepEventService {

    private final SessionRepository sessionRepository;
    private final AssessmentMetricRepository assessmentMetricRepository;
    private final RepRepository repRepository;
    private final RepMetricRepository repMetricRepository;
    private final RepResourceRepository repResourceRepository;
    private final SessionNotificationService sessionNotificationService;

    @ApplicationModuleListener
    public void onRepDataReceived(RepDataReceivedEvent event) {
        log.info("RepDataReceivedEvent received for session={}", event.sessionId());

        Session session = sessionRepository.findById(event.sessionId())
                .orElseThrow(() -> new NotFoundByException("Session not found by id: %d", event.sessionId()));

        Set<Integer> validMetricIds = assessmentMetricRepository
                .findAllByAssessmentId(session.getAssessmentId())
                .stream()
                .map(AssessmentMetric::getConditionalMetricId)
                .collect(Collectors.toSet());

        Rep rep = repRepository.save(Rep.builder()
                .sessionId(event.sessionId())
                .startTime(event.startTime())
                .build());

        event.metrics().stream()
                .filter(m -> validMetricIds.contains(m.conditionalMetricId()))
                .forEach(m -> repMetricRepository.save(RepMetric.builder()
                        .repId(rep.getId())
                        .conditionalMetricId(m.conditionalMetricId())
                        .value(m.value())
                        .build()));

        event.resources().forEach(r ->
                repResourceRepository.save(RepResource.builder()
                        .repId(rep.getId())
                        .url(r.url())
                        .type(ResourceContentType.VIDEO.getValue())
                        .urlStatus(r.urlStatus())
                        .build()));

        sessionNotificationService.broadcastRep(event.sessionId(), rep);
    }
}
```

- [ ] **Step 4: Add createConditionalMetric() and createAssessmentMetric() helpers to EntityFactory**

These may already exist — check the EntityFactory. If `createConditionalMetric()` doesn't exist, add it. It needs a `Condition` and a `Metric`:

```java
public ConditionalMetric createConditionalMetric() {
    var condition = createCondition();
    var metric = createMetric("AutoMetric-" + System.nanoTime());
    ConditionalMetric cm = ConditionalMetric.builder()
            .conditionId(condition.getId())
            .metricId(metric.getId())
            .name("AutoConditionalMetric-" + System.nanoTime())
            .build();
    return conditionalMetricRepository.save(cm);
}

public AssessmentMetric createAssessmentMetric(Integer assessmentId, Integer conditionalMetricId) {
    ConditionalMetric cm = conditionalMetricRepository.findById(conditionalMetricId).orElseThrow();
    var dataSource = createDataSource(cm.getMetricId());
    return assessmentMetricRepository.save(AssessmentMetric.builder()
            .assessmentId(assessmentId)
            .conditionalMetricId(conditionalMetricId)
            .dataSourceId(dataSource.getId())
            .build());
}
```

Where `createDataSource(Integer metricId)` creates a `DataSource` row — add if it doesn't exist:
```java
public DataSource createDataSource(Integer metricId) {
    var integration = integrationRepository.save(
            Integration.builder().name("AutoIntegration-" + System.nanoTime()).build());
    var dsType = createDataSourceTypeDictionary("AUTO");
    return dataSourceRepository.save(DataSource.builder()
            .integrationId(integration.getId())
            .metricId(metricId)
            .name("AutoDataSource-" + System.nanoTime())
            .type(dsType.getName())
            .content("{}")
            .build());
}
```

- [ ] **Step 5: Run listener tests — verify they pass**

```bash
./gradlew test --tests "com.biolab.launchpad.RepDataReceivedEventListenerTest" 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`, all three tests green.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/biolab/launchpad/internal/service/RepEventService.java \
        src/test/java/com/biolab/launchpad/RepDataReceivedEventListenerTest.java \
        src/test/java/com/biolab/launchpad/internal/web/controller/EntityFactory.java
git commit -m "[BL-55] Implement RepEventService listener and tests"
```

---

## Task 9: Run full test suite and verify ModulithTest

- [ ] **Step 1: Run all tests**

```bash
./gradlew test 2>&1 | tail -30
```
Expected: `BUILD SUCCESSFUL`, all tests green.

- [ ] **Step 2: Run ModulithTest specifically**

```bash
./gradlew test --tests "com.biolab.ModulithTest" 2>&1 | tail -20
```
Expected: PASS. If it fails with a module boundary violation, check that `RepEventService` and `SessionWorkflowService` only import from `com.biolab.common` and `com.biolab.launchpad.internal.*`, never from `com.biolab.integration.*` or `com.biolab.hardware.*`.

- [ ] **Step 3: Commit spec and plan alongside implementation**

```bash
git add docs/
git commit -m "[BL-55] Add design spec and implementation plan"
```

- [ ] **Step 4: Final commit summary**

```bash
git log --oneline -8
```
Expected output (in order):
```
... [BL-55] Add design spec and implementation plan
... [BL-55] Implement RepEventService listener and tests
... [BL-55] Add stop session integration tests
... [BL-55] Implement start session endpoint with SessionWorkflowService
... [BL-55] Add ConflictException, 409 handler, and SessionNotificationService
... [BL-55] Add derived queries to Rep, RepMetric, AssessmentMetric, SessionMetric repositories
... [BL-55] Add session lifecycle and rep data event records to common
... [BL-55] Add status field to Session model and DTO
... [BL-55] Add status column to session table
```
