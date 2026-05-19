# ADR-002: Temporal for Workflow Orchestration

## Status
Accepted

## Date
2024-01-16

## Context
VRP solving is a long-running, multi-step process that requires:
- Fetching orders and vehicles from the database
- Running the optimization solver (potentially 5-10 minutes)
- Persisting the optimized routes
- Handling retries and failures gracefully
- Visibility into workflow execution status

We need a reliable workflow orchestration solution that can handle these requirements in a production environment.

### Options Considered

1. **Temporal**
   - Pros: Purpose-built for durable workflows, excellent developer experience, strong consistency guarantees
   - Cons: Additional infrastructure (Temporal server), learning curve

2. **Spring Batch**
   - Pros: Native Spring integration, simpler setup
   - Cons: Not designed for long-running workflows, limited workflow visibility, harder to handle retries

3. **Camunda/Flowable**
   - Pros: BPMN standard, visual workflow designer
   - Cons: Heavier weight, more complex for our use case, XML configuration

4. **Custom Queue-Based Solution (RabbitMQ/Kafka)**
   - Pros: Full control, lightweight
   - Cons: Significant development effort, error-prone, no workflow visibility

## Decision
We will use **Temporal** for workflow orchestration.

## Rationale

1. **Durability**: Temporal provides automatic state persistence and recovery
2. **Reliability**: Built-in retry logic with exponential backoff and circuit breakers
3. **Visibility**: Web UI for monitoring workflow execution and debugging
4. **Developer Experience**: Code-as-workflow paradigm using standard Kotlin/Java
5. **Scalability**: Proven at scale (Uber, Netflix, Stripe use it in production)
6. **Spring Integration**: Good Spring Boot integration via temporal-spring-boot-starter

## Consequences

### Positive
- Workflows are resilient to process crashes and infrastructure failures
- Easy to add new workflow steps without complex state machine logic
- Excellent observability into long-running processes
- Activities can be retried independently with custom retry policies
- No need to implement custom queue processing or state management

### Negative
- Additional infrastructure to run and monitor (Temporal server)
- Learning curve for team unfamiliar with Temporal concepts
- Increased complexity for simple synchronous operations

### Neutral
- Need to carefully design activity boundaries for optimal performance
- Workflow versioning must be handled when making changes

## Implementation Notes

```kotlin
// Workflow definition
@WorkflowInterface
interface VrpSolveWorkflow {
    @WorkflowMethod
    fun solve(input: VrpJobInput): VrpJobResult

    @QueryMethod
    fun getStatus(): String
}

// Activity stubs
val fetchOrdersActivity = Workflow.newActivityStub(
    FetchOrdersActivity::class.java,
    ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofMinutes(5))
        .setRetryOptions(RetryOptions.newBuilder()
            .setMaximumAttempts(3)
            .build())
        .build()
)
```

## Alternatives Considered for Future

If Temporal proves too heavyweight for simpler workflows, we could use Spring Modulith's application events for synchronous or near-synchronous processing. However, for the core VRP solving workflow, Temporal's guarantees are valuable.

## References
- [Temporal Documentation](https://docs.temporal.io/)
- [Temporal Spring Boot Starter](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha)
- [Temporal Java SDK Samples](https://github.com/temporalio/samples-java)
