# System Overview

## Introduction

Timefold VRP is a production-grade **Vehicle Routing Problem** solver designed to optimize delivery routes for fleet management. It combines AI-powered constraint optimization with enterprise-grade workflow orchestration to solve complex logistics problems at scale.

## Problem Domain

### Vehicle Routing Problem (VRP)

The VRP is an NP-hard optimization problem that asks: *"Given a fleet of vehicles and a set of delivery locations, what are the optimal routes that minimize cost while satisfying all constraints?"*

#### Constraints
**Hard Constraints** (Must be satisfied):
- Vehicle capacity (weight & volume)
- Time windows (deliver between X and Y time)
- Pickup-before-delivery ordering
- Vehicle availability

**Soft Constraints** (Minimize):
- Total distance traveled
- Vehicle utilization variance (load balancing)

#### Real-World Complexity
- 100 orders × 10 vehicles = ~10^215 possible solutions
- Impossible to check all combinations
- Need heuristic/metaheuristic algorithms
- Timefold uses Tabu Search, Late Acceptance, Simulated Annealing

## System Architecture

### High-Level View

```
┌─────────────┐
│   Client    │ (Mobile/Web App with JWT)
└──────┬──────┘
       │ HTTPS
       ↓
┌─────────────────────────────────────┐
│    Spring Boot Application          │
│  ┌─────────────────────────────┐   │
│  │  REST API (Controllers)     │   │
│  └────────────┬────────────────┘   │
│               ↓                     │
│  ┌─────────────────────────────┐   │
│  │  Use Cases                  │   │
│  │  (Submit/Get Job)           │   │
│  └────────────┬────────────────┘   │
│               ↓                     │
│  ┌─────────────────────────────┐   │
│  │  Domain Layer               │   │
│  │  (Business Logic)           │   │
│  └───┬──────────────────────┬──┘   │
│      │                      │       │
│      ↓                      ↓       │
│  ┌────────┐           ┌──────────┐ │
│  │  JPA   │           │ Temporal │ │
│  │Adapters│           │ Adapter  │ │
│  └───┬────┘           └────┬─────┘ │
└──────┼─────────────────────┼───────┘
       │                     │
       ↓                     ↓
  ┌──────────┐         ┌──────────┐
  │PostgreSQL│         │ Temporal │
  │          │         │  Server  │
  └──────────┘         └─────┬────┘
                             │
                             ↓
                       ┌──────────┐
                       │ Timefold │
                       │  Solver  │
                       │ (Worker) │
                       └──────────┘
```

### Component Responsibilities

#### 1. REST API Layer
- **Purpose**: HTTP interface for clients
- **Technology**: Spring MVC, OpenAPI/Swagger
- **Responsibilities**:
  - Validate incoming requests
  - Extract organization ID from JWT
  - Map DTOs to domain models
  - Return HTTP responses

#### 2. Application Layer (Use Cases)
- **Purpose**: Orchestrate business operations
- **Technology**: Spring Services
- **Responsibilities**:
  - Coordinate domain objects
  - Transaction boundaries
  - Call external services (Temporal)
  - Minimal business logic

#### 3. Domain Layer
- **Purpose**: Core business logic
- **Technology**: Pure Kotlin classes
- **Responsibilities**:
  - Domain models (Order, Vehicle, Trip)
  - Business rules validation
  - Define ports (interfaces)

#### 4. Infrastructure Layer
- **Purpose**: Technical concerns
- **Technology**: JPA, Temporal, Redis
- **Responsibilities**:
  - Database persistence
  - Workflow orchestration
  - Caching
  - Security

#### 5. Solver Layer
- **Purpose**: Optimization engine
- **Technology**: Timefold Solver
- **Responsibilities**:
  - Define planning problem
  - Implement constraints
  - Execute solver

## Data Flow

### Submitting a VRP Job

```
1. Client → POST /api/v1/jobs
           {orderIds: [...], vehicleIds: [...]}

2. Controller → Extract JWT org_id
              → Validate input

3. SubmitJobUseCase → Create VrpJob entity
                    → Save to database
                    → Start Temporal workflow

4. Temporal Workflow (async) →
   ├─ FetchOrdersActivity → Load from DB
   ├─ FetchVehiclesActivity → Load from DB
   ├─ RunSolverActivity →
   │    ├─ Map to Timefold models
   │    ├─ Run solver (5-10 min)
   │    └─ Get solution
   └─ PersistSolutionActivity → Save trips

5. Client polls GET /api/v1/jobs/{id}
   → Returns COMPLETED + trips
```

### Retrieving Optimized Routes

```
1. Client → GET /api/v1/trips?jobId={id}

2. Controller → Extract org_id
              → Validate job belongs to org

3. GetTripUseCase → Load trips from DB
                  → Filter by jobId & orgId

4. Controller → Map to DTOs
              → Return trips with visits
```

## Multi-Tenancy Design

### JWT-Based Isolation

Every request carries a JWT with `org_id` claim:

```
┌─────────────────────────────────────┐
│   JWT Claims                        │
├─────────────────────────────────────┤
│ sub: "user-123"                     │
│ org_id: "550e8400-e29b..."          │ ← Extracted by Spring Security
│ exp: 1234567890                     │
└─────────────────────────────────────┘
         ↓ Spring Security validates
┌─────────────────────────────────────┐
│   Security Context                  │
│   organizationId = UUID.fromString()|
└─────────────────────────────────────┘
         ↓ Passed to all layers
┌─────────────────────────────────────┐
│   Repository Calls                  │
│   findById(id, organizationId)      │ ← ALWAYS includes orgId
└─────────────────────────────────────┘
         ↓ SQL WHERE clause
┌─────────────────────────────────────┐
│   Database Query                    │
│   WHERE id = ? AND org_id = ?       │
└─────────────────────────────────────┘
```

### Data Isolation

All tenant-scoped tables include `organization_id` with index:

```sql
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    -- ... other columns
);

CREATE INDEX idx_orders_org ON orders(organization_id);
```

No row is accessible without matching `organization_id`.

## Scalability

### Horizontal Scaling

```
         ┌──────────────┐
         │ Load Balancer│
         └──────┬───────┘
                │
       ┌────────┼────────┐
       ↓        ↓        ↓
┌────────┐ ┌────────┐ ┌────────┐
│ App    │ │ App    │ │ App    │  (Stateless instances)
│ Pod 1  │ │ Pod 2  │ │ Pod N  │
└───┬────┘ └───┬────┘ └───┬────┘
    │          │          │
    └──────────┴──────────┘
               │
        ┌──────┴──────┐
        ↓             ↓
   ┌─────────┐  ┌─────────┐
   │  Postgres│  │  Redis  │  (Shared state)
   │(Primary) │  │ (Cache) │
   └─────────┘  └─────────┘
```

Key points:
- **Stateless apps**: No server-side sessions, JWT auth
- **Shared database**: Single source of truth
- **Distributed cache**: Redis for solver results
- **Workflow workers**: Temporal manages worker pool

### Performance Characteristics

| Operation | Latency | Throughput |
|-----------|---------|------------|
| Submit job | <200ms | 1000 req/sec |
| Get status | <100ms | 5000 req/sec |
| Solver execution | 5-10 min | N/A (async) |
| Get trips | <150ms | 2000 req/sec |

## Technology Choices Summary

| Need | Technology | Why? |
|------|------------|------|
| Optimization | Timefold Solver | VRP-specific, Spring integration |
| Workflows | Temporal | Durable, handles long-running tasks |
| Database | PostgreSQL | ACID, JSON support, proven |
| Cache | Redis | Fast, distributed, persistent |
| Observability | OTEL/Grafana | Standard, comprehensive |
| Security | OAuth2 JWT | Stateless, standard |

## Deployment Architecture

### Development (docker-compose)

```yaml
services:
  app:
    image: timefold-vrp:latest
    ports: ["8080:8080"]
  postgres:
    image: postgres:17
  redis:
    image: redis:7
  temporal:
    image: temporalio/auto-setup
  grafana:
    image: grafana/otel-lgtm
```

### Production (Kubernetes)

```
┌────────────────────────────────────────┐
│         Kubernetes Cluster             │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │  Ingress (NGINX)                 │ │
│  │  TLS termination                 │ │
│  └────────────┬─────────────────────┘ │
│               │                        │
│  ┌────────────▼─────────────────────┐ │
│  │  Service (LoadBalancer)          │ │
│  └────────────┬─────────────────────┘ │
│               │                        │
│  ┌────────────▼─────────────────────┐ │
│  │  Deployment (3 replicas)         │ │
│  │  - VRP App Pods                  │ │
│  │  - Resource limits               │ │
│  │  - Health checks                 │ │
│  └──────────────────────────────────┘ │
│                                        │
│  External:                             │
│  - PostgreSQL (Cloud SQL/RDS)         │
│  - Redis (ElastiCache/MemoryStore)    │
│  - Temporal Cloud                     │
└────────────────────────────────────────┘
```

## Security Architecture

### Defense in Depth

1. **Network Layer**
   - TLS 1.3 for all external communication
   - VPC/private subnets for databases

2. **Application Layer**
   - JWT signature validation
   - Organization ID verification
   - Input validation (Bean Validation)

3. **Data Layer**
   - Row-level tenant isolation
   - Encrypted at rest (database)
   - Encrypted in transit (TLS)

4. **Observability**
   - Audit logs for all data changes
   - Security monitoring via Sentry
   - Anomaly detection on metrics

## Disaster Recovery

### Backup Strategy
- **Database**: Daily automated backups, 30-day retention
- **Redis**: Persistence enabled (RDB + AOF)
- **Temporal**: Event history retained (30 days)

### Recovery Objectives
- **RTO (Recovery Time)**: 1 hour
- **RPO (Recovery Point)**: 1 hour (hourly incremental backups)

### Failure Scenarios

| Scenario | Impact | Recovery |
|----------|--------|----------|
| App pod crash | None (auto-restart) | 10 seconds |
| Database failover | 30s downtime | Automatic (replica) |
| Redis failure | Slower, no cache | Manual restart |
| Temporal failure | Workflows paused | Resume after restart |

## Monitoring & Alerts

### Key Metrics to Watch

1. **Application**
   - Error rate (target: <1%)
   - p99 latency (target: <500ms)
   - Active VRP jobs

2. **Solver**
   - Avg solve time (trend)
   - Solution quality (hard score = 0)
   - Solver failures

3. **Infrastructure**
   - Database connections
   - Redis hit rate
   - Temporal workflow lag

### Alert Thresholds

| Alert | Threshold | Severity |
|-------|-----------|----------|
| Error rate | >5% | Critical |
| Database connections | >80% | Warning |
| Solver failures | >10% | Critical |
| API latency p99 | >1s | Warning |

## Next Steps

- Read [Hexagonal Architecture](hexagonal-architecture.md) for code organization
- See [Data Flow](data-flow.md) for detailed sequence diagrams
- Check [Tech Stack](tech-stack.md) for version details and rationale
