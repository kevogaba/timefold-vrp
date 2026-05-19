package com.vrp.infrastructure.temporal.adapter

import com.vrp.domain.model.VrpJob
import com.vrp.domain.port.VrpOrchestrationPort
import com.vrp.infrastructure.temporal.workflow.VrpJobInput
import com.vrp.infrastructure.temporal.workflow.VrpSolveWorkflow
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowNotFoundException
import io.temporal.client.WorkflowOptions
import io.temporal.client.WorkflowQueryException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class VrpOrchestrationAdapter(
    private val workflowClient: WorkflowClient,
    @Value("\${temporal.task-queue:vrp-task-queue}")
    private val taskQueue: String
) : VrpOrchestrationPort {
    private val logger = LoggerFactory.getLogger(VrpOrchestrationAdapter::class.java)

    override fun startSolveWorkflow(job: VrpJob): String {
        val workflowId = "vrp-solve-${job.id}"

        val options =
            WorkflowOptions
                .newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(taskQueue)
                .setWorkflowExecutionTimeout(Duration.ofMinutes(15))
                .build()

        val workflow = workflowClient.newWorkflowStub(VrpSolveWorkflow::class.java, options)

        val input =
            VrpJobInput(
                jobId = job.id,
                organizationId = job.organizationId,
                orderIds = job.orderIds,
                vehicleIds = job.vehicleIds
            )

        WorkflowClient.start(workflow::solve, input)

        return workflowId
    }

    override fun queryWorkflowStatus(workflowId: String): String? =
        try {
            val workflow = workflowClient.newWorkflowStub(VrpSolveWorkflow::class.java, workflowId)
            workflow.getStatus()
        } catch (e: WorkflowNotFoundException) {
            logger.debug("Workflow not found for id={}", workflowId, e)
            null
        } catch (e: WorkflowQueryException) {
            logger.debug("Failed to query workflow status for id={}", workflowId, e)
            null
        }

    override fun cancelWorkflow(workflowId: String) {
        workflowClient.newUntypedWorkflowStub(workflowId).cancel()
    }
}
