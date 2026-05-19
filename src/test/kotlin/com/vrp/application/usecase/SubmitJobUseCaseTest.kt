package com.vrp.application.usecase

import com.vrp.domain.model.JobStatus
import com.vrp.domain.model.VrpJob
import com.vrp.domain.port.VrpJobRepository
import com.vrp.domain.port.VrpOrchestrationPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SubmitJobUseCaseTest {
    private val jobRepository = mockk<VrpJobRepository>()
    private val orchestrationPort = mockk<VrpOrchestrationPort>()
    private val useCase = SubmitJobUseCase(jobRepository, orchestrationPort)

    @Test
    fun `should submit job and start workflow`() {
        val organizationId = UUID.randomUUID()
        val orderIds = listOf(UUID.randomUUID())
        val vehicleIds = listOf(UUID.randomUUID())
        val workflowId = "vrp-solve-123"

        val pendingJob =
            VrpJob(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                status = JobStatus.PENDING,
                orderIds = orderIds,
                vehicleIds = vehicleIds
            )

        val runningJob = pendingJob.copy(status = JobStatus.RUNNING)

        every { jobRepository.save(any()) } returns pendingJob
        every { orchestrationPort.startSolveWorkflow(pendingJob) } returns workflowId
        every { jobRepository.updateStatus(pendingJob.id, JobStatus.RUNNING, organizationId) } returns runningJob

        val result = useCase.execute(organizationId, orderIds, vehicleIds)

        assertNotNull(result)
        assertEquals(JobStatus.RUNNING, result.status)

        verify(exactly = 1) { jobRepository.save(any()) }
        verify(exactly = 1) { orchestrationPort.startSolveWorkflow(pendingJob) }
        verify(exactly = 1) { jobRepository.updateStatus(pendingJob.id, JobStatus.RUNNING, organizationId) }
    }

    @Test
    fun `should throw exception when order IDs are empty`() {
        val organizationId = UUID.randomUUID()
        val vehicleIds = listOf(UUID.randomUUID())

        try {
            useCase.execute(organizationId, emptyList(), vehicleIds)
            assert(false) { "Should have thrown exception" }
        } catch (e: IllegalArgumentException) {
            assertEquals("Order IDs cannot be empty", e.message)
        }
    }

    @Test
    fun `should throw exception when vehicle IDs are empty`() {
        val organizationId = UUID.randomUUID()
        val orderIds = listOf(UUID.randomUUID())

        try {
            useCase.execute(organizationId, orderIds, emptyList())
            assert(false) { "Should have thrown exception" }
        } catch (e: IllegalArgumentException) {
            assertEquals("Vehicle IDs cannot be empty", e.message)
        }
    }
}
