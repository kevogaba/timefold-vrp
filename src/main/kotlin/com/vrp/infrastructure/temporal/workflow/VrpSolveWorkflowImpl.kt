package com.vrp.infrastructure.temporal.workflow

import com.vrp.infrastructure.temporal.activity.FetchOrdersActivity
import com.vrp.infrastructure.temporal.activity.FetchVehiclesActivity
import com.vrp.infrastructure.temporal.activity.PersistSolutionActivity
import com.vrp.infrastructure.temporal.activity.RunSolverActivity
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.failure.TemporalFailure
import io.temporal.workflow.Workflow
import java.time.Duration

/**
 * Implementation of VRP solve workflow.
 */
class VrpSolveWorkflowImpl : VrpSolveWorkflow {
    private var currentStatus: String = "PENDING"

    private val fetchOrdersActivity: FetchOrdersActivity
    private val fetchVehiclesActivity: FetchVehiclesActivity
    private val runSolverActivity: RunSolverActivity
    private val persistSolutionActivity: PersistSolutionActivity

    init {
        val defaultOptions =
            ActivityOptions
                .newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(
                    RetryOptions
                        .newBuilder()
                        .setMaximumAttempts(3)
                        .build()
                ).build()

        val solverOptions =
            ActivityOptions
                .newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(10))
                .setHeartbeatTimeout(Duration.ofSeconds(30))
                .setRetryOptions(
                    RetryOptions
                        .newBuilder()
                        .setMaximumAttempts(1)
                        .build()
                ).build()

        fetchOrdersActivity = Workflow.newActivityStub(FetchOrdersActivity::class.java, defaultOptions)
        fetchVehiclesActivity = Workflow.newActivityStub(FetchVehiclesActivity::class.java, defaultOptions)
        runSolverActivity = Workflow.newActivityStub(RunSolverActivity::class.java, solverOptions)
        persistSolutionActivity = Workflow.newActivityStub(PersistSolutionActivity::class.java, defaultOptions)
    }

    override fun solve(input: VrpJobInput): VrpJobResult =
        try {
            currentStatus = "FETCHING_ORDERS"
            val orders = fetchOrdersActivity.fetchOrders(input.organizationId, input.orderIds)

            currentStatus = "FETCHING_VEHICLES"
            val vehicles = fetchVehiclesActivity.fetchVehicles(input.organizationId, input.vehicleIds)

            currentStatus = "SOLVING"
            val solverResult = runSolverActivity.runSolver(input.jobId, input.organizationId, orders, vehicles)

            currentStatus = "PERSISTING"
            persistSolutionActivity.persist(input.jobId, input.organizationId, solverResult)

            currentStatus = "COMPLETED"
            VrpJobResult(
                jobId = input.jobId,
                success = true,
                hardScore = solverResult.hardScore,
                softScore = solverResult.softScore,
                errorMessage = null
            )
        } catch (e: TemporalFailure) {
            currentStatus = "FAILED"
            VrpJobResult(
                jobId = input.jobId,
                success = false,
                hardScore = null,
                softScore = null,
                errorMessage = e.cause?.message ?: e.message
            )
        } catch (e: IllegalStateException) {
            currentStatus = "FAILED"
            VrpJobResult(
                jobId = input.jobId,
                success = false,
                hardScore = null,
                softScore = null,
                errorMessage = e.message
            )
        }

    override fun getStatus(): String = currentStatus
}
