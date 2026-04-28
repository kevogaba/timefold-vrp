# ADR-005: Spring Modulith for Module Boundaries

## Status
Accepted

## Date
2024-01-19

## Context
As the application grows, we need to:
- Enforce clear module boundaries
- Prevent circular dependencies
- Document module interactions
- Validate architecture at build time
- Enable future microservices extraction if needed

### Options Considered

1. **Spring Modulith**
   - Pros: Package-based modules, build-time validation, documentation generation, event-based communication
   - Cons: Additional framework, learning curve

2. **ArchUnit Tests**
   - Pros: Custom architecture rules, fine-grained control
   - Cons: Manual rule writing, no documentation generation, runtime-only

3. **Manual Package Structure**
   - Pros: Simple, no additional dependencies
   - Cons: No enforcement, easy to violate, no documentation

4. **Separate Maven/Gradle Modules**
   - Pros: Strongest enforcement via module dependencies
   - Cons: Over-engineered for monolith, slow builds, complex dependency management

## Decision
We will use **Spring Modulith** to define and enforce module boundaries within our monolith.

## Rationale

1. **Build-Time Validation**: Fails the build if module boundaries are violated
2. **Documentation**: Auto-generates module diagrams and documentation
3. **Application Events**: Promotes loose coupling via Spring application events
4. **Testing**: Provides module tests to verify boundary compliance
5. **Future-Proof**: Makes eventual microservices extraction easier by enforcing clean boundaries now

## Module Structure

```
com.vrp/
├── order/          # Order management module
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── vehicle/        # Vehicle management module
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── job/            # VRP job orchestration module
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── trip/           # Route/trip output module
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── solver/         # Timefold solver module (shared)
│   ├── domain/
│   ├── constraints/
│   └── mapper/
└── shared/         # Shared utilities (open module)
    └── events/
```

## Module Rules

### 1. Package Structure
Each module follows hexagonal architecture:
```
module/
├── domain/         # Public API: models, ports
├── application/    # Public API: use cases
├── infrastructure/ # Internal: adapters, configs
└── api/            # Public API: controllers (optional)
```

### 2. Visibility Rules
- **Public API**: Classes in `domain/` and `application/` packages
- **Internal**: Classes in `infrastructure/` package
- **Events**: Public events in `shared/events/`

### 3. Dependency Rules
- Modules can only depend on other modules' **public API**
- Internal packages are module-private
- Circular dependencies are forbidden

### 4. Communication Patterns
- **Synchronous**: Direct method calls on public API (use cases)
- **Asynchronous**: Spring application events for cross-module communication

## Implementation

### Module Configuration
```kotlin
// No explicit configuration needed
// Spring Modulith auto-detects modules based on package structure
```

### Module Tests
```kotlin
@ApplicationModuleTest
class OrderModuleTest {

    @Test
    fun `should have valid module structure`(modules: ApplicationModules) {
        modules.verify()  // Validates module boundaries
    }

    @Test
    fun `should document module dependencies`(modules: ApplicationModules) {
        modules.forEach { module ->
            println("Module: ${module.name}")
            println("Dependencies: ${module.dependencies}")
        }
    }
}
```

### Event-Based Communication
```kotlin
// In job module
@Service
class SubmitJobUseCase(...) {
    fun execute(...): VrpJob {
        val job = jobRepository.save(...)

        // Publish event instead of direct coupling
        eventPublisher.publishEvent(JobSubmittedEvent(
            jobId = job.id,
            organizationId = job.organizationId
        ))

        return job
    }
}

// In metrics module (listener)
@Service
class MetricsEventListener {
    @EventListener
    fun onJobSubmitted(event: JobSubmittedEvent) {
        metricsService.incrementJobsSubmitted()
    }
}
```

## Consequences

### Positive
- **Enforced Boundaries**: Build fails if modules are incorrectly coupled
- **Documentation**: Auto-generated module diagrams help onboarding
- **Testability**: Can test modules in isolation
- **Microservices Ready**: Clean boundaries make extraction easier if needed
- **Team Autonomy**: Teams can own modules with clear interfaces

### Negative
- **Additional Complexity**: Another framework to learn
- **Event Overhead**: Async communication adds latency for cross-module calls
- **Testing Complexity**: Need to test both module boundaries and integration

### Neutral
- **Package Naming**: Must follow conventions for auto-detection
- **Migration Effort**: Requires refactoring if switching to microservices later

## Documentation Generation

```bash
# Generate module documentation
./gradlew documenter

# Outputs:
# - build/spring-modulith/modules.html (module diagram)
# - build/spring-modulith/components.html (component relationships)
```

## Alternative for Future: Microservices

If we hit monolith limits (>10 teams, >100k LOC per module), Spring Modulith makes extraction straightforward:

1. **Identify Module**: Choose module with clear boundaries
2. **Extract**: Copy module to new service
3. **Replace Events**: Convert Spring events to message broker (Kafka/RabbitMQ)
4. **Replace Calls**: Convert direct calls to REST/gRPC

Spring Modulith's boundaries ensure this is feasible without major refactoring.

## References
- [Spring Modulith Documentation](https://docs.spring.io/spring-modulith/reference/)
- [Spring Modulith GitHub](https://github.com/spring-projects/spring-modulith)
- [Modular Monoliths](https://www.kamilgrzybek.com/blog/posts/modular-monolith-primer)
