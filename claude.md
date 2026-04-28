# Timefold VRP - Project Context for AI Assistants

> **Purpose**: This document provides comprehensive context about the Timefold Vehicle Routing Problem (VRP) system for AI assistants like Claude Code, GitHub Copilot, and other AI development tools.

## Project Overview

**Timefold VRP** is a production-grade Vehicle Routing Problem solver designed for multi-tenant SaaS deployment. It combines AI-powered optimization (Timefold Solver) with reliable workflow orchestration (Temporal) to solve complex logistics routing problems.

### Core Purpose
Optimize delivery routes for fleets of vehicles considering:
- Vehicle capacity constraints (weight & volume)
- Time window requirements
- Pickup-before-delivery ordering
- Distance minimization
- Load balancing across vehicles

### Key Characteristics
- **Multi-tenant**: Supports multiple organizations with JWT-based isolation
- **Asynchronous**: Long-running solver jobs orchestrated via Temporal workflows
- **Production-ready**: 77%+ test coverage, comprehensive observability, Spring Boot 4.0
- **Scalable**: Stateless design, horizontal scaling, Redis caching

## Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Runtime** | Java | 25 | JVM platform |
| **Language** | Kotlin | 2.3.21 | Primary development language |
| **Framework** | Spring Boot | 4.0.6 | Application framework |
| **Optimization** | Timefold Solver | 2.0.0 | VRP constraint solver |
| **Orchestration** | Temporal | 1.34.0 | Durable workflow engine |
| **Architecture** | Spring Modulith | 2.0.6 | Module boundary enforcement |
| **Database** | PostgreSQL | 17 | Primary data store |
| **Cache** | Redis | 7 | Distributed caching |
| **Observability** | OTEL/Grafana | Latest | Metrics, traces, logs |
| **Testing** | JUnit 5 + Kover | - | Test framework + coverage |

## Architecture

### Hexagonal Architecture (Ports & Adapters)

```
┌─────────────────────────────────────────┐
│         API Layer (Controllers)         │
│    REST endpoints, DTOs, Swagger        │
└─────────────────┬───────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│      Application Layer (Use Cases)      │
│  SubmitJobUseCase, GetJobStatusUseCase  │
└─────────────────┬───────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│         Domain Layer (Core Logic)       │
│  Order, Vehicle, Trip, VrpJob (models)  │
│  Ports (repository interfaces)          │
└─────────────────┬───────────────────────┘
                  ↑ implemented by
┌─────────────────┴───────────────────────┐
│   Infrastructure Layer (Adapters)       │
│  JPA, Temporal, Redis, Security         │
└─────────────────────────────────────────┘
```

### Package Structure

```
com.vrp/
├── domain/              # Pure domain models & ports
│   ├── model/           # Order, Vehicle, Trip, VrpJob, etc.
│   └── port/            # Repository & service interfaces
├── application/         # Use cases (application logic)
│   └── usecase/         # SubmitJobUseCase, GetJobStatusUseCase, GetTripUseCase
├── infrastructure/      # Framework & external adapters
│   ├── persistence/     # JPA entities, repositories, mappers
│   ├── temporal/        # Temporal workflows & activities
│   ├── cache/           # Redis configuration
│   └── security/        # JWT OAuth2 configuration
├── api/                 # REST API layer
│   ├── controller/      # JobController, TripController
│   ├── dto/             # API request/response DTOs
│   └── mapper/          # Domain ↔ DTO mappers
├── solver/              # Timefold Solver (special layer)
│   ├── domain/          # SolverVehicle, SolverVisit, VrpSolution
│   ├── constraints/     # VrpConstraintProvider
│   ├── mapper/          # Domain ↔ Solver mappers
│   └── service/         # VrpSolverService
├── observability/       # Custom metrics
└── config/              # Application configuration
```

## Domain Model

### Core Entities

**Order** - Delivery or pickup request
- Customer information
- Line items (products to deliver)
- Locations (pickup, delivery)
- Time windows
- Service duration

**Vehicle** - Fleet vehicle
- Capacity (weight, volume)
- Start/end locations
- Availability window
- Driver assignment
- Cost per kilometer

**VrpJob** - Optimization request
- Organization ID (multi-tenancy)
- Order IDs to route
- Vehicle IDs to use
- Status (PENDING → RUNNING → COMPLETED/FAILED)
- Scores (hard/soft)

**Trip** - Optimized route for one vehicle
- Vehicle ID
- Ordered list of visits
- Total distance & duration
- Created from VrpJob solution

### Solver Model (Timefold)

**VrpSolution** - Planning solution
```kotlin
@PlanningSolution
data class VrpSolution(
    @PlanningEntityCollectionProperty
    val vehicles: MutableList<SolverVehicle>,

    @ValueRangeProvider
    @PlanningEntityCollectionProperty
    val visits: MutableList<SolverVisit>,

    @PlanningScore
    var score: HardSoftScore?
)
```

**SolverVehicle** - Planning entity
```kotlin
@PlanningEntity
data class SolverVehicle(
    @PlanningListVariable
    val visits: MutableList<SolverVisit> = mutableListOf()
)
```

## Constraints

### Hard Constraints (Must Satisfy)
1. **Vehicle Weight Capacity** - Total demand ≤ vehicle capacity
2. **Vehicle Volume Capacity** - Total volume ≤ vehicle capacity
3. **Time Windows** - Arrive within customer time windows
4. **Pickup-Before-Delivery** - For paired orders, pickup must precede delivery

### Soft Constraints (Optimize)
1. **Minimize Total Distance** - Reduce overall fleet distance
2. **Balance Load** - Target 80% vehicle utilization

## Workflow Orchestration (Temporal)

### VRP Solve Workflow
```
START
  ↓
FETCH_ORDERS (activity)
  ↓
FETCH_VEHICLES (activity)
  ↓
RUN_SOLVER (activity, 5-10 min timeout)
  ↓
PERSIST_SOLUTION (activity)
  ↓
COMPLETED / FAILED
```

### Activities
- **FetchOrdersActivity**: Load orders from database
- **FetchVehiclesActivity**: Load vehicles from database
- **RunSolverActivity**: Execute Timefold solver
- **PersistSolutionActivity**: Save optimized trips

### Retry Strategy
- Default activities: 3 retries with exponential backoff
- Solver activity: No retries (expensive operation)
- Heartbeat: 30-second intervals during solving

## Multi-Tenancy

### JWT-Based Isolation

Every API request must include JWT with `org_id` claim:
```json
{
  "sub": "user-123",
  "org_id": "550e8400-e29b-41d4-a716-446655440000",
  "exp": 1234567890
}
```

### Data Isolation

All database queries filtered by `organization_id`:
```kotlin
interface OrderRepository {
    fun findById(id: UUID, organizationId: UUID): Order?
    //                     ^^^^^^^^^^^^^^^^^ REQUIRED
}
```

Database tables have `organization_id` column with index.

## API Endpoints

### Submit VRP Job
```http
POST /api/v1/jobs
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "orderIds": ["uuid1", "uuid2"],
  "vehicleIds": ["uuid3"]
}

→ 202 Accepted
{
  "id": "job-uuid",
  "status": "RUNNING",
  "createdAt": "2024-01-20T10:00:00Z"
}
```

### Get Job Status
```http
GET /api/v1/jobs/{jobId}
Authorization: Bearer <JWT>

→ 200 OK
{
  "id": "job-uuid",
  "status": "COMPLETED",
  "hardScore": 0,
  "softScore": -1500,
  "completedAt": "2024-01-20T10:05:32Z"
}
```

### Get Optimized Trips
```http
GET /api/v1/trips?jobId={jobId}
Authorization: Bearer <JWT>

→ 200 OK
[
  {
    "id": "trip-uuid",
    "vehicleId": "vehicle-uuid",
    "visits": [
      {
        "orderId": "order1",
        "location": {"latitude": 40.7589, "longitude": -73.9851},
        "arrivalTime": "2024-01-20T14:30:00Z",
        "sequenceNumber": 0
      }
    ],
    "totalDistanceMeters": 15000,
    "totalDurationMinutes": 45
  }
]
```

## Testing Strategy

### Coverage: 77.83%
- **Unit Tests**: Domain models, mappers, constraints
- **Integration Tests**: Repositories, Temporal activities
- **Contract Tests**: REST API contracts (Spring Cloud Contract)
- **Module Tests**: Spring Modulith boundary validation

### Test Structure
```
src/test/kotlin/com/vrp/
├── domain/model/          # Domain model unit tests
├── solver/constraints/    # Constraint logic tests
├── application/usecase/   # Use case tests
├── infrastructure/
│   ├── persistence/       # Repository adapter tests
│   └── temporal/          # Workflow activity tests
└── api/mapper/            # DTO mapper tests
```

### Key Testing Patterns
```kotlin
// Domain test (no Spring, no mocks)
@Test
fun `should reject order with empty line items`() {
    assertThatThrownBy {
        Order(lineItems = emptyList(), ...)
    }.isInstanceOf(IllegalArgumentException::class.java)
}

// Repository test (MockK for JPA)
@Test
fun `should find order by id and organization`() {
    every { jpaRepository.findByIdAndOrganizationId(id, orgId) }
        .returns(entity)

    val result = adapter.findById(id, orgId)

    assertThat(result?.id).isEqualTo(id)
}
```

## Development Workflow

### Local Setup
```bash
# Start infrastructure
docker compose up -d

# Run application
./gradlew bootRun --args='--spring.profiles.active=local'

# Access services:
# - App: http://localhost:8080
# - Swagger: http://localhost:8080/swagger-ui.html
# - Grafana: http://localhost:3000
# - Temporal UI: http://localhost:8088
```

### Build Commands
```bash
# Run tests with coverage
./gradlew check

# View coverage report
open build/reports/kover/html/index.html

# Build JAR
./gradlew build

# Build native image (GraalVM)
./gradlew nativeCompile
```

### Database Migrations (Flyway)
- `V1__initial_schema.sql` - Core tables
- `V2__indexes.sql` - Performance indexes
- `V3__audit_log.sql` - Audit logging

Migrations auto-run on startup.

## Observability

### Metrics (Prometheus format)
- `vrp.job.submitted` - Jobs submitted counter
- `vrp.job.completed` - Jobs completed (tagged: success/failure)
- `vrp.solver.duration.seconds` - Solver execution time histogram
- `vrp.solver.score.hard` - Final hard score gauge
- `vrp.solver.score.soft` - Final soft score gauge

### Traces (OpenTelemetry)
- REST API calls
- Temporal workflows & activities
- Database queries
- Solver execution

### Logs (JSON structured)
- Correlation IDs for request tracking
- Organization ID in all log entries
- Error stack traces for failures
- Exported to Loki via OTLP

## Configuration

### Application Properties
```yaml
# Spring profiles: local, dev, prod
spring.profiles.active: local

# Multi-tenancy
spring.security.oauth2.resourceserver.jwt.issuer-uri: ...

# Timefold
timefold.solver.termination.spent-limit: 5m

# Temporal
temporal.connection.target: localhost:7233
temporal.task-queue: vrp-task-queue

# Database
spring.datasource.url: jdbc:postgresql://localhost:5432/vrp
spring.jpa.hibernate.ddl-auto: validate

# Redis
spring.data.redis.host: localhost
spring.data.redis.port: 6379
```

## Key Design Decisions (ADRs)

1. **[ADR-001](docs/adr/001-timefold-solver-choice.md)** - Why Timefold Solver over alternatives
2. **[ADR-002](docs/adr/002-temporal-orchestration.md)** - Why Temporal for workflows
3. **[ADR-003](docs/adr/003-hexagonal-architecture.md)** - Hexagonal architecture pattern
4. **[ADR-004](docs/adr/004-jwt-multitenancy.md)** - JWT-based multi-tenancy approach
5. **[ADR-005](docs/adr/005-spring-modulith.md)** - Spring Modulith for module boundaries

## Common Development Tasks

### Adding a New Constraint
1. Add constraint method to `VrpConstraintProvider`
2. Register in `defineConstraints()`
3. Add tests in `VrpConstraintProviderTest`

### Adding a New Use Case
1. Create use case in `application.usecase` package
2. Define required ports in `domain.port`
3. Implement adapters in `infrastructure`
4. Add controller in `api.controller`
5. Write tests at each layer

### Adding a Database Migration
1. Create `V{n}__description.sql` in `src/main/resources/db/migration/`
2. Write SQL (up migration only, no down)
3. Test with `./gradlew flywayMigrate`

## Code Style & Conventions

### Naming
- **Entities**: Singular nouns (`Order`, not `Orders`)
- **Repositories**: `{Entity}Repository` interface, `{Entity}RepositoryAdapter` implementation
- **Use Cases**: `{Verb}{Entity}UseCase` (e.g., `SubmitJobUseCase`)
- **DTOs**: `{Entity}Request`/`{Entity}Response`

### Kotlin Conventions
- Data classes for immutable models
- Require validation in init blocks
- Extension functions for domain logic
- Elvis operator for null handling

### Testing
- Test names: `` `should {behavior} when {condition}` ``
- AAA pattern: Arrange, Act, Assert
- Use meaningful variable names in tests
- Prefer AssertJ fluent assertions

## Security Considerations

1. **JWT Validation**: Automatic via Spring Security OAuth2 Resource Server
2. **Organization Isolation**: ALWAYS pass `organizationId` to repository methods
3. **Input Validation**: Use Bean Validation (`@Valid`) on DTOs
4. **SQL Injection**: Use parameterized queries (JPA handles this)
5. **Secrets Management**: Never commit secrets, use environment variables

## Performance Characteristics

- **Solver Time**: 5-10 minutes for typical job (100 orders, 10 vehicles)
- **API Response**: <200ms for job submission/status (solver runs async)
- **Database**: PostgreSQL with indexes on `organization_id` and foreign keys
- **Cache**: Redis for solver solutions (avoids re-solving identical problems)
- **Horizontal Scaling**: Stateless app, can scale to N instances behind load balancer

## Known Limitations

1. **Solver Configuration**: Currently fixed 5-minute termination, needs dynamic tuning
2. **Real-time Updates**: No WebSocket support for job status updates (polling required)
3. **Bulk Operations**: No batch job submission endpoint
4. **Historical Data**: No built-in analytics/reporting on past jobs

## Future Roadmap

- [ ] Dynamic solver configuration per job
- [ ] WebSocket support for real-time status
- [ ] Batch job submission API
- [ ] Historical analytics dashboard
- [ ] Multi-depot routing support
- [ ] Driver break constraints
- [ ] Traffic-aware routing (integrate external API)

## Resources

- **GitHub**: https://github.com/kevogaba/timefold-vrp
- **Issues**: https://github.com/kevogaba/timefold-vrp/issues
- **Timefold Docs**: https://docs.timefold.ai/
- **Temporal Docs**: https://docs.temporal.io/
- **Spring Modulith Docs**: https://docs.spring.io/spring-modulith/

## AI Assistant Guidelines

When helping with this codebase:

1. **Respect Architecture**: Follow hexagonal architecture, don't mix layers
2. **Multi-tenancy**: ALWAYS include `organizationId` in repository calls
3. **Testing**: Write tests for new code (target 77%+ coverage)
4. **K-Doc**: Add K-Doc comments to public classes and methods
5. **Immutability**: Prefer data classes and val over var
6. **Validation**: Add require() checks in domain model init blocks
7. **Mapping**: Don't leak infrastructure concerns into domain layer
8. **ADRs**: Consult ADRs for design decision context

---

**Last Updated**: 2024-04-28
**Coverage**: 77.83% (181 tests)
**Contributors**: See git history
