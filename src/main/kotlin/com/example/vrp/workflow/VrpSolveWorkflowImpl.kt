package com.example.vrp.workflow

import com.example.vrp.job.domain.JobStatus
import com.example.vrp.workflow.activity.EmitAuditActivity
import com.example.vrp.workflow.activity.FetchOrdersActivity
import com.example.vrp.workflow.activity.FetchVehiclesActivity
import com.example.vrp.workflow.activity.PersistSolutionActivity
import com.example.vrp.workflow.activity.RunSolverActivity
import com.example.vrp.workflow.activity.UpdateJobStatusActivity
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.workflow.Workflow
import java.time.Duration

class VrpSolveWorkflowImpl : VrpSolveWorkflow {

    private val activityOptions = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofMinutes(30))
        .setRetryOptions(
            RetryOptions.newBuilder()
                .setMaximumAttempts(3)
                .setInitialInterval(Duration.ofSeconds(5))
                .build()
        )
        .build()

    private val fetchOrders = Workflow.newActivityStub(FetchOrdersActivity::class.java, activityOptions)
    private val fetchVehicles = Workflow.newActivityStub(FetchVehiclesActivity::class.java, activityOptions)
    private val runSolver = Workflow.newActivityStub(RunSolverActivity::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofHours(2))
            .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
            .build()
    )
    private val persistSolution = Workflow.newActivityStub(PersistSolutionActivity::class.java, activityOptions)
    private val updateJobStatus = Workflow.newActivityStub(UpdateJobStatusActivity::class.java, activityOptions)
    private val emitAudit = Workflow.newActivityStub(EmitAuditActivity::class.java, activityOptions)

    override fun solve(request: VrpSolveRequest) {
        updateJobStatus.updateStatus(request.jobId, request.organizationId, JobStatus.RUNNING)
        try {
            val orders = fetchOrders.fetchOrders(request.organizationId, request.orderIds)
            val vehicles = fetchVehicles.fetchVehicles(request.organizationId)
            val solution = runSolver.runSolver(request.jobId, orders, vehicles)
            persistSolution.persistSolution(request.jobId, request.organizationId, solution)
            updateJobStatus.updateStatus(request.jobId, request.organizationId, JobStatus.COMPLETED)
            emitAudit.emit(
                request.organizationId,
                "JOB_COMPLETED",
                request.jobId,
                mapOf("orderCount" to request.orderIds.size.toString()),
            )
        } catch (e: Exception) {
            updateJobStatus.updateStatus(
                request.jobId,
                request.organizationId,
                JobStatus.FAILED,
                e.message,
            )
            emitAudit.emit(
                request.organizationId,
                "JOB_FAILED",
                request.jobId,
                mapOf("error" to (e.message ?: "unknown")),
            )
            throw e
        }
    }
}
