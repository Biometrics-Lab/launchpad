# Launchpad

Backend for the Launchpad SaaS platform — baseball facilities use it to
measure player metrics and manage player development. A **Spring Modulith**
modular monolith (Spring Boot 3.5.3, Java 24, PostgreSQL, Liquibase, Kafka).

Pairs with the [`launchpad-frontend`](../launchpad-frontend/) Next.js app.

## Prerequisites

- Java 24 (managed automatically via the Gradle toolchain — no manual install needed if you use `./gradlew`)
- Docker (for LocalStack S3 and Testcontainers)
- Local PostgreSQL running on `localhost:5432` with database `biolab`, user `biolab`, password `biolab`

## Running locally

Option 1 — one script (starts LocalStack + the app):

```bash
./start.sh
```

Option 2 — manual steps:

```bash
# Start LocalStack (S3) — required before bootRun
docker-compose up -d localstack

# Run the app (requires local PostgreSQL, see Prerequisites)
./gradlew bootRun
```

The API is served at `http://localhost:8080/api/v1/**`, secured with HTTP
Basic auth (`biolab` / `biolab`, role `ADMIN`).

## Running tests

Tests use Testcontainers and require Docker:

```bash
# Full suite
./gradlew test

# Single test class
./gradlew test --tests "com.biolab.launchpad.internal.web.controller.PlayerControllerIntegrationTest"

# Single test method
./gradlew test --tests "com.biolab.launchpad.internal.web.controller.PlayerControllerIntegrationTest.CreateTests.create"
```

CI runs `./gradlew test` on push to `develop`, `master`, `release/*`, and `feature/*` branches.

## Project layout

Spring Modulith enforces module boundaries (verified by `ModulithTest`):

| Module | Role |
|---|---|
| `com.biolab.launchpad` | Core domain — CRUD API, business logic |
| `com.biolab.integration` | External integration gateway |
| `com.biolab.hardware` | Hardware device gateway |
| `com.biolab.common` | Shared types only (records, interfaces) — no Spring beans |

Within `launchpad`:

```
launchpad/
  internal/
    repository/       Spring Data JDBC repositories + domain model classes
      model/           Entity POJOs annotated with @Table
    service/           Service beans (extend EntityService<T>)
    web/
      controller/      @RestController beans at /api/v1/**
      dto/             DTOs (records / classes)
      mapper/          MapStruct interfaces
    security/          SecurityConfig + custom exceptions
```

Schema migrations live in `src/main/resources/db/changelog/changeset/` and
run automatically via Liquibase on startup.

## More

See [`CLAUDE.md`](./CLAUDE.md) for architecture details, entity/service
conventions, and testing patterns.
