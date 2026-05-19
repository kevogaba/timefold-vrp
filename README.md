# Timefold VRP - Vehicle Routing Problem System

[![CI](https://github.com/kevogaba/timefold-vrp/actions/workflows/ci.yml/badge.svg)](https://github.com/kevogaba/timefold-vrp/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/kevogaba/timefold-vrp/branch/main/graph/badge.svg)](https://codecov.io/gh/kevogaba/timefold-vrp)

Production-grade Vehicle Routing Problem (VRP) solver built with **Spring Boot 4**, **Timefold Solver 2.0**, and **Temporal** for workflow orchestration.

## Architecture

Single Spring Modulith application with hexagonal architecture principles:

```
com.vrp
├── domain/              Pure Kotlin domain models and ports
├── application/         Use cases orchestrating domain logic
├── solver/              Timefold planning models and constraints
├── infrastructure/      Adapters for persistence, Temporal, cache
├── api/                 REST controllers and DTOs
└── observability/       Custom metrics and OTEL configuration
```

## Tech Stack

| Technology              | Version | Purpose                          |
|-------------------------|---------|----------------------------------|
| Java                    | 25      | Runtime platform                 |
| Kotlin                  | 2.3.21  | Primary language                 |
| Spring Boot             | 4.0.6   | Application framework            |
| Timefold Solver         | 2.0.0   | VRP optimization engine          |
| Temporal                | 1.34.0  | Workflow orchestration           |
| Spring Modulith         | 2.0.6   | Module boundaries enforcement    |
| PostgreSQL              | 17      | Primary database                 |
| Redis                   | 7       | Caching and distributed locking  |
| Grafana OTEL LGTM       | latest  | Observability stack              |
| Sentry                  | 8.27.0  | Error tracking                   |

## Features

- ✅ **Multi-tenant VRP solver** with JWT-based organization isolation
- ✅ **Asynchronous job processing** via Temporal workflows
- ✅ **4 HARD + 2 SOFT constraints**:
  - Vehicle weight/volume capacity
  - Time window compliance
  - Pickup-before-delivery ordering
  - Distance minimization
  - Load balancing
- ✅ **Full OTEL observability** (traces, metrics, logs)
- ✅ **Redis caching** with distributed locking
- ✅ **REST API** with OpenAPI documentation
- ✅ **80%+ test coverage** enforced by Kover

## Quick Start

### Prerequisites

- Java 25
- Docker & Docker Compose
- (Optional) GraalVM for native image

### Run Locally

1. **Start infrastructure:**
   ```bash
   docker compose up -d
   ```

2. **Build and run:**
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

3. **Access services:**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Grafana: http://localhost:3000 (admin/admin)
   - Temporal UI: http://localhost:8088

### Example API Usage

**Submit a VRP job:**
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_WITH_ORG_ID>" \
  -d '{
    "orderIds": ["550e8400-e29b-41d4-a716-446655440000"],
    "vehicleIds": ["650e8400-e29b-41d4-a716-446655440001"]
  }'
```

**Get job status:**
```bash
curl http://localhost:8080/api/v1/jobs/{jobId} \
  -H "Authorization: Bearer <JWT_WITH_ORG_ID>"
```

**Fetch optimized routes:**
```bash
curl http://localhost:8080/api/v1/trips?jobId={jobId} \
  -H "Authorization: Bearer <JWT_WITH_ORG_ID>"
```

## Development

### Build Commands

```bash
# Compile
./gradlew build -x test

# Run tests with coverage
./gradlew check

# View coverage report
open build/reports/kover/html/index.html

# Build native image
./gradlew nativeCompile

# Run native image
./build/native/nativeCompile/timefold-vrp
```

### Database Migrations

Flyway migrations auto-run on startup:
- `V1__initial_schema.sql` - Core tables
- `V2__indexes.sql` - Performance indexes
- `V3__audit_log.sql` - Audit logging

### Testing

```bash
# Unit tests
./gradlew test

# Integration tests (with Testcontainers)
./gradlew integrationTest

# Contract tests
./gradlew contractTest

# All tests + coverage gate
./gradlew check
```

Coverage thresholds:
- Overall: 80% line coverage
- Domain/Application packages: 90%

## Configuration

### Multi-Tenancy (JWT)

Add `org_id` claim to your JWT:
```json
{
  "sub": "user-id",
  "org_id": "550e8400-e29b-41d4-a716-446655440000",
  "exp": 1234567890
}
```

### Solver Tuning

Edit `src/main/resources/solverConfig.xml`:
```xml
<termination>
    <minutesSpentLimit>5</minutesSpentLimit>
</termination>
```

Or override via properties:
```yaml
timefold:
  solver:
    termination:
      spent-limit: 10m
```

## Observability

### Metrics (Prometheus/Mimir)

Custom business metrics:
- `vrp.job.submitted` - Jobs submitted counter
- `vrp.job.completed` - Jobs completed (success/failure)
- `vrp.solver.duration.seconds` - Solver execution time
- `vrp.solver.score.{hard|soft}` - Final solution scores

### Traces (Tempo)

Distributed tracing across:
- REST API calls
- Temporal workflows/activities
- Database queries
- Solver execution

### Logs (Loki)

JSON-structured logs with correlation IDs exported via OTLP.

### Dashboards

Access Grafana at http://localhost:3000:
- **Explore** → Tempo for traces
- **Explore** → Loki for logs
- **Explore** → Mimir for metrics

## Deployment

### Docker

```bash
./gradlew bootBuildImage
docker run -p 8080:8080 timefold-vrp:latest
```

### GraalVM Native Image

```bash
./gradlew nativeCompile
./build/native/nativeCompile/timefold-vrp
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

Licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.

## Support

- **Issues**: https://github.com/kevogaba/timefold-vrp/issues
- **Discussions**: https://github.com/kevogaba/timefold-vrp/discussions
