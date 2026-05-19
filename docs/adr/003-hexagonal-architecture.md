# ADR-003: Hexagonal Architecture Pattern

## Status
Accepted

## Date
2024-01-17

## Context
We need an architecture that:
- Keeps domain logic pure and testable
- Allows easy swapping of infrastructure components
- Supports multiple interfaces (REST API, future CLI/gRPC)
- Facilitates independent evolution of different layers
- Enables clear separation of concerns

### Options Considered

1. **Hexagonal Architecture (Ports & Adapters)**
   - Pros: Clear separation, testable, infrastructure-agnostic domain
   - Cons: More files/directories, requires discipline

2. **Traditional Layered Architecture**
   - Pros: Simple, familiar
   - Cons: Domain depends on infrastructure, harder to test

3. **Clean Architecture (Uncle Bob)**
   - Pros: Similar benefits to Hexagonal
   - Cons: More rigid rules, potentially over-engineered for our size

4. **Domain-Driven Design (DDD) Full Implementation**
   - Pros: Rich domain models, bounded contexts
   - Cons: High complexity for single-bounded-context app

## Decision
We will use **Hexagonal Architecture** (Ports & Adapters pattern).

## Rationale

1. **Domain Independence**: Core business logic has no dependencies on frameworks or databases
2. **Testability**: Domain and application layers can be tested without Spring, databases, or external services
3. **Flexibility**: Easy to swap PostgreSQL for another database, or add gRPC alongside REST
4. **Clear Boundaries**: Ports (interfaces) define clear contracts between layers
5. **Spring Modulith Alignment**: Works well with Spring Modulith's module boundaries

## Architecture Layers

```
┌─────────────────────────────────────────┐
│         API Layer (Inbound)             │
│  Controllers, DTOs, Exception Handlers  │
└─────────────────┬───────────────────────┘
                  │ calls
┌─────────────────▼───────────────────────┐
│       Application Layer                 │
│   Use Cases (orchestrate domain logic)  │
└─────────────────┬───────────────────────┘
                  │ uses
┌─────────────────▼───────────────────────┐
│          Domain Layer                   │
│  Models, Ports (interfaces), Logic      │
└─────────────────┬───────────────────────┘
                  │ implemented by
┌─────────────────▼───────────────────────┐
│    Infrastructure Layer (Outbound)      │
│  Adapters: JPA, Temporal, Cache, etc.   │
└─────────────────────────────────────────┘
```

## Package Structure

```
com.vrp/
├── domain/
│   ├── model/           # Domain entities (Order, Vehicle, Trip)
│   └── port/            # Interfaces for repositories and external services
├── application/
│   └── usecase/         # Application services (SubmitJobUseCase)
├── infrastructure/
│   ├── persistence/     # JPA entities, repositories, mappers
│   ├── temporal/        # Temporal workflows and activities
│   └── cache/           # Redis cache configuration
├── api/
│   ├── controller/      # REST controllers
│   ├── dto/             # API DTOs
│   └── mapper/          # Domain ↔DTO mappers
└── solver/              # Timefold-specific models (crosses layers)
    ├── domain/          # Planning entities and solution
    ├── constraints/     # Constraint provider
    └── mapper/          # Domain ↔ Solver mappers
```

## Consequences

### Positive
- **Testability**: Can test domain logic with simple unit tests (no mocking infrastructure)
- **Independence**: Domain models are POKOs (Plain Old Kotlin Objects) with no annotations
- **Flexibility**: Can replace PostgreSQL with MongoDB without touching domain/application layers
- **Clarity**: Clear dependency flow: API → Application → Domain ← Infrastructure

### Negative
- **More Files**: Each repository needs interface (port) + implementation (adapter)
- **Mapping Overhead**: Need mappers between layers (Entity ↔ Domain ↔ DTO)
- **Learning Curve**: Team needs to understand and respect layer boundaries

### Neutral
- **Solver Layer**: Timefold models cross layers (have to use annotations), treated as special case
- **Spring Modulith**: Enforces boundaries at package level, complements hexagonal architecture

## Implementation Guidelines

1. **Domain Layer**:
   - No framework dependencies (no Spring, JPA, Jackson annotations)
   - Contains business logic and validation
   - Defines ports (interfaces) for external dependencies

2. **Application Layer**:
   - Orchestrates domain objects to implement use cases
   - Depends only on domain layer
   - Thin coordination layer

3. **Infrastructure Layer**:
   - Implements ports defined in domain layer
   - Contains all framework-specific code
   - Maps between infrastructure models and domain models

4. **API Layer**:
   - Exposes application use cases via REST
   - Validates input, formats output
   - Handles HTTP concerns (status codes, headers)

## Example: Order Repository

```kotlin
// Domain port (interface)
interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: UUID, organizationId: UUID): Order?
}

// Infrastructure adapter (implementation)
@Repository
class OrderRepositoryAdapter(
    private val jpaRepository: OrderJpaRepository,
    private val mapper: OrderMapper
) : OrderRepository {
    override fun save(order: Order): Order {
        val entity = mapper.toEntity(order)
        return mapper.toDomain(jpaRepository.save(entity))
    }
}

// Usage in application layer
@Service
class SubmitJobUseCase(
    private val orderRepository: OrderRepository  // Depends on port, not adapter
) {
    fun execute(...) {
        val order = orderRepository.findById(...)
    }
}
```

## References
- [Hexagonal Architecture by Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Get Your Hands Dirty on Clean Architecture](https://www.packtpub.com/product/get-your-hands-dirty-on-clean-architecture/9781839211966)
- [Spring Modulith Documentation](https://docs.spring.io/spring-modulith/reference/)
