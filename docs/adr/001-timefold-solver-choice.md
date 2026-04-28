# ADR-001: Timefold Solver for VRP Optimization

## Status
Accepted

## Date
2024-01-15

## Context
We need a production-grade optimization engine to solve Vehicle Routing Problems (VRP) with complex constraints including:
- Vehicle capacity (weight and volume)
- Time windows for deliveries
- Pickup-before-delivery ordering
- Distance minimization
- Load balancing across vehicles

### Options Considered

1. **Timefold Solver 2.0**
   - Pros: Modern fork of OptaPlanner, active development, Spring Boot 4 support, comprehensive VRP examples
   - Cons: Newer project (less battle-tested than OptaPlanner)

2. **OptaPlanner**
   - Pros: Mature, well-documented, proven in production
   - Cons: Development slowed, Spring Boot 3.x support unclear, uncertain future

3. **Google OR-Tools**
   - Pros: Powerful, free, Google-backed
   - Cons: C++ core with Java bindings, steeper learning curve, less Spring integration

4. **Custom Genetic Algorithm**
   - Pros: Full control, lightweight
   - Cons: Significant development time, unlikely to match specialized solver performance

## Decision
We will use **Timefold Solver 2.0** as our VRP optimization engine.

## Rationale

1. **Active Development**: Timefold is actively maintained with regular releases and Spring Boot 4.0 support
2. **VRP-Specific Features**: Built-in support for common VRP constraints and patterns
3. **Spring Integration**: First-class Spring Boot starter with auto-configuration
4. **Constraint Streams API**: Intuitive, type-safe API for defining constraints
5. **Performance**: Implements proven metaheuristics (Tabu Search, Late Acceptance, etc.)
6. **Community**: Growing community of users and contributors migrating from OptaPlanner

## Consequences

### Positive
- Rapid development using constraint streams and planning annotations
- Excellent Spring Boot integration with minimal configuration
- Strong performance out-of-the-box with proven algorithms
- Clear migration path if we later need OR-Tools for specific use cases

### Negative
- Smaller community than OptaPlanner (mitigated by active support)
- Less third-party tooling/extensions
- Requires JVM runtime (not an issue for our Spring Boot stack)

### Neutral
- Learning curve for team unfamiliar with constraint solvers
- Need to tune solver configuration for production workloads

## Implementation Notes

```kotlin
// Planning solution
@PlanningSolution
data class VrpSolution(
    @PlanningEntityCollectionProperty
    val vehicles: MutableList<SolverVehicle>,
    @PlanningScore
    var score: HardSoftScore?
)

// Constraints
class VrpConstraintProvider : ConstraintProvider {
    override fun defineConstraints(factory: ConstraintFactory) = arrayOf(
        vehicleWeightCapacity(factory),
        timeWindowViolation(factory),
        minimizeTotalDistance(factory)
    )
}
```

## References
- [Timefold Documentation](https://docs.timefold.ai/)
- [Timefold VRP Examples](https://github.com/TimefoldAI/timefold-quickstarts/tree/stable/use-cases/vehicle-routing)
- [OptaPlanner to Timefold Migration Guide](https://docs.timefold.ai/migration-guide/)
