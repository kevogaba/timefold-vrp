package com.example.vrp.workflow.adapter

import com.example.vrp.job.port.out.SolveOrchestrationPort
import com.example.vrp.workflow.VrpSolveRequest
import com.example.vrp.workflow.VrpSolveWorkflow
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import org.springframework.stereotype.Component

@Component
class TemporalSolveOrchestrationAdapter(
    private val workflowClient: WorkflowClient,
) : SolveOrchestrationPort {

    override fun startSolving(jobId: String, organizationId: String, orderIds: List<String>) {
        val options = WorkflowOptions.newBuilder()
            .setTaskQueue("vrp-solver")
            .setWorkflowId("vrp-solve-$jobId")
            .build()
        val workflow = workflowClient.newWorkflowStub(VrpSolveWorkflow::class.java, options)
        WorkflowClient.start(workflow::solve, VrpSolveRequest(jobId, organizationId, orderIds))
    }
}
