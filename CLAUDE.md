# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run tests (requires Docker for Testcontainers)
./gradlew test

# Run a single test class
./gradlew test --tests "com.biolab.launchpad.internal.web.controller.PlayerControllerIntegrationTest"

# Run a single test method
./gradlew test --tests "com.biolab.launchpad.internal.web.controller.PlayerControllerIntegrationTest.CreateTests.create"

# Run the application (requires local PostgreSQL on localhost:5432, DB: biolab, user: biolab, password: biolab)
./gradlew bootRun

# Initialize the database (Linux only)
./gradlew initDB

# Drop the database (Linux only)
./gradlew dropDB
```

CI runs `./gradlew test` on push to `develop`, `master`, `release/*`, and `feature/*` branches.

## Architecture

This is a **Spring Modulith modular monolith** (Spring Boot 3.5.3, Java 24, PostgreSQL, Liquibase, Kafka).

### Modules

The application root is `com.biolab`. Spring Modulith enforces the module boundaries and the `ModulithTest` verifies them.

| Package | Role |
|---|---|
| `com.biolab.launchpad` | Core domain — CRUD API, business logic |
| `com.biolab.integration` | External integration gateway |
| `com.biolab.hardware` | Hardware device gateway |
| `com.biolab.common` | Shared types only (records, interfaces) — no Spring beans |

**Inter-module calls** happen two ways:
- **Synchronous**: a module exposes a `*ServiceGateway` bean; the calling module injects it via an `internal/client/*Client` interface + `*ClientModulith` implementation that directly calls the gateway.
- **Asynchronous**: `ApplicationEventPublisher.publishEvent(AsyncRequest)` on the sender side; `@ApplicationModuleListener` on the receiving `*ServiceGateway`. The `consumerServiceName` field in `AsyncRequest` routes events — only the gateway whose condition matches processes it (`Dictionary.Service.*` constants hold the names).

### Launchpad module internal layers

```
launchpad/
  internal/
    repository/       Spring Data JDBC repositories + domain model classes
      model/          Entity POJOs annotated with @Table
    service/          Service beans (extend EntityService<T>)
    web/
      controller/     @RestController beans at /api/v1/**
      dto/            DTOs (records / classes)
      mapper/         MapStruct interfaces
    security/         SecurityConfig + custom exceptions
```

### Key patterns

**Entity hierarchy** — all domain entities extend `IDName` (`id: Integer`, `name: String` with `@NotBlank`). Concrete entities add their own fields and are annotated with `@Table`. All use `@SuperBuilder`, `@Data`, `@NoArgsConstructor`.

**Dictionary entities** (e.g. `SportDictionary`, `AgeGroupDictionary`) use a `String` primary key (the name itself). When creating a new dictionary record, you **must** call `entity.markAsNew(true)` before saving, otherwise Spring Data JDBC treats it as an update.

**Service layer** — every service extends `EntityService<T extends IDName>`, which provides `create`, `findAll`, `findById`, `update`, `deleteById`. Most services are a single constructor-injection call to `super(repository)`. Override only when custom logic is needed.

**MapStruct mappers** — each entity has a `*Mapper` interface. Mappers use `componentModel = "spring"` but also expose a static `Mappers.getMapper()` instance (e.g. `PlayerMapper.playerMapper`). Controllers use the static instance; prefer that pattern for consistency.

**Response envelope** — controllers return the DTO directly on success. On error, `GlobalExceptionHandler` catches exceptions and returns `ResponseDto { Integer status, String message }`. Use `ResponseCode` enum for standard messages.

### Security

HTTP Basic auth. Credentials configured in `application.yml` (`biolab`/`biolab`, role `ADMIN`). All `/api/v1/**` endpoints require `ADMIN`. Tests authenticate with `.with(httpBasic("biolab", "biolab"))`.

### Database

Schema migrations live in `src/main/resources/db/changelog/changeset/` as numbered YAML files included by `biolab.changelog.yml`. Liquibase runs on startup. Spring Modulith JDBC event store is enabled (stores async events in the DB for reliability).

### Testing

- **Integration tests** (`*ControllerIntegrationTest`): `@SpringBootTest + @AutoConfigureMockMvc` + Testcontainers (real PostgreSQL spun up via Docker). Tests use `MockMvc` + `EntityFactory` for creating prerequisite entities, and tear down in `@AfterEach`.
- **Service unit tests** (`*ServiceTest`): lighter, mock the repository.
- **`ModulithTest`**: verifies module boundaries and generates PlantUML docs — run this after any package restructuring.
- **`EntityFactory`** (test component): central helper that creates fully-wired test entities including all FK prerequisites. Use it in new integration tests instead of building entities manually.
