# Timefold VRP Service

Production-grade Vehicle Routing Problem (VRP) solver service built with Spring Boot, Timefold Solver, and Temporal workflows.

## Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                      Single Gradle Module                         │
│              (Spring Modulith package boundaries)                 │
│                                                                   │
│  ┌─────────┐  ┌──────────┐  ┌──────────┐  ┌────────────────┐   │
│  │  job/   │  │  order/  │  │ vehicle/ │  │    workflow/    │   │
│  │         │──│          │  │          │  │  (Temporal)     │   │
│  │ domain  │  │ domain   │  │ domain   │  │  VrpSolve       │   │
│  │ port/in │  │ port/out │  │ port/out │  │  Workflow       │   │
│  │ port/out│  │ adapter  │  │ adapter  │  │  + Activities   │   │
│  │ adapter │  └──────────┘  └──────────┘  └────────────────┘   │
│  └─────────┘                                                      │
│  ┌──────────┐  ┌──────────┐  ┌───────────────────────────────┐  │
│  │  trip/   │  │ solver/  │  │          shared/               │  │
│  │ domain   │  │ domain   │  │  Location, TimeWindow,         │  │
│  │ port/out │  │ constraints│ │  WorkingHours, Money,         │  │
│  │ adapter  │  │ mapper   │  │  Capacity, AuditLogPort       │  │
│  └──────────┘  └──────────┘  └───────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

## Tech Stack

| Dependency | Version |
|---|---|
| Java | 25 GA |
| Kotlin | 2.3.21 |
| Spring Boot | 4.0.6 |
| Spring Modulith | 2.0.6 |
| Timefold Solver | 2.0.0 |
| Temporal | 1.34.0 |
| Kover | 0.9.1 |
| GraalVM Build Tools | 0.10.4 |

## Local Development

### Prerequisites
- Docker & Docker Compose
- Java 25 (GraalVM for native builds)
- Gradle (wrapper included)

### Start Infrastructure
```bash
docker compose up -d
```

Services started:
- **PostgreSQL**: `localhost:5432`
- **Redis**: `localhost:6379`
- **Temporal**: `localhost:7233`
- **Temporal UI**: `localhost:8088`
- **Grafana (LGTM)**: `localhost:3000` (admin/admin)

### Run the Application
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

`spring-boot-docker-compose` will auto-start Docker services on boot.

### API

**Submit a VRP job:**
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{"orderIds": ["order-1", "order-2", "order-3"]}'
```

**Poll job status:**
```bash
curl http://localhost:8080/api/v1/jobs/{jobId} \
  -H "Authorization: Bearer <jwt>"
```

**Get trips for a job:**
```bash
curl http://localhost:8080/api/v1/trips/job/{jobId} \
  -H "Authorization: Bearer <jwt>"
```

**OpenAPI docs:** http://localhost:8080/swagger-ui.html

## Testing

```bash
# Unit tests only
./gradlew test

# All checks including coverage gate (80% minimum)
./gradlew check

# Coverage HTML report
open build/reports/kover/html/index.html
```

## Native Build (GraalVM)

```bash
# Compile to native binary
./gradlew nativeCompile

# Build OCI container image
./gradlew bootBuildImage
```

## Observability

All telemetry is sent to the `grafana/otel-lgtm` container via OTLP HTTP (`localhost:4318`).

| Signal | Backend |
|---|---|
| Traces | Grafana Tempo |
| Metrics | Grafana Mimir |
| Logs | Grafana Loki |

Open **Grafana** at http://localhost:3000 — datasources are pre-configured.

## Coverage

```
./gradlew check   # Fails if overall coverage < 80% or domain/application < 90%
```

Report: `build/reports/kover/html/index.html`

## Configuration Reference

| Variable | Default | Description |
|---|---|---|
| `SENTRY_DSN` | _(empty)_ | Sentry DSN for error tracking |
| `OTEL_EXPORTER_OTLP_TRACES_ENDPOINT` | `http://localhost:4318/v1/traces` | OTLP traces endpoint |
| `OTEL_EXPORTER_OTLP_METRICS_ENDPOINT` | `http://localhost:4318/v1/metrics` | OTLP metrics endpoint |
| `OTEL_TRACES_SAMPLER_ARG` | `0.1` | Tracing sample rate (0.0–1.0) |
| `SPRING_PROFILES_ACTIVE` | _(none)_ | Active Spring profiles |
