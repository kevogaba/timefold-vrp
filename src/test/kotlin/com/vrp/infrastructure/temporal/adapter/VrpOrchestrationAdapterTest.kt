package com.vrp.infrastructure.temporal.adapter

import com.vrp.domain.model.JobStatus
import com.vrp.domain.model.VrpJob
import com.vrp.infrastructure.temporal.workflow.VrpSolveWorkflow
import io.mockk.*
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowStub
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class VrpOrchestrationAdapterTest {

    private lateinit var workflowClient: WorkflowClient
    private lateinit var adapter: VrpOrchestrationAdapter
    private val taskQueue = "test-task-queue"

    @BeforeEach
    fun setup() {
        workflowClient = mockk()
        adapter = VrpOrchestrationAdapter(workflowClient, taskQueue)
    }

    @Test
    fun `should query workflow status successfully`() {
        val workflowId = "vrp-solve-${UUID.randomUUID()}"
        val workflow = mockk<VrpSolveWorkflow>()

        every { workflowClient.newWorkflowStub(VrpSolveWorkflow::class.java, workflowId) } returns workflow
        every { workflow.getStatus() } returns "RUNNING"

        val status = adapter.queryWorkflowStatus(workflowId)

        assertThat(status).isEqualTo("RUNNING")
        verify { workflowClient.newWorkflowStub(VrpSolveWorkflow::class.java, workflowId) }
        verify { workflow.getStatus() }
    }

    @Test
    fun `should return null when query workflow status fails`() {
        val workflowId = "vrp-solve-${UUID.randomUUID()}"

        every { workflowClient.newWorkflowStub(VrpSolveWorkflow::class.java, workflowId) } throws RuntimeException("Workflow not found")

        val status = adapter.queryWorkflowStatus(workflowId)

        assertThat(status).isNull()
    }

    @Test
    fun `should cancel workflow successfully`() {
        val workflowId = "vrp-solve-${UUID.randomUUID()}"
        val workflowStub = mockk<WorkflowStub>()

        every { workflowClient.newUntypedWorkflowStub(workflowId) } returns workflowStub
        every { workflowStub.cancel() } just Runs

        adapter.cancelWorkflow(workflowId)

        verify { workflowClient.newUntypedWorkflowStub(workflowId) }
        verify { workflowStub.cancel() }
    }
}

