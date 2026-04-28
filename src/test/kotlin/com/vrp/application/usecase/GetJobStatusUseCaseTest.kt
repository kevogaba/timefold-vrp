package com.vrp.application.usecase

import com.vrp.domain.model.JobStatus
import com.vrp.domain.model.VrpJob
import com.vrp.domain.port.VrpJobRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetJobStatusUseCaseTest {
    private val jobRepository = mockk<VrpJobRepository>()
    private val useCase = GetJobStatusUseCase(jobRepository)

    @Test
    fun `should return job when found`() {
        val jobId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()

        val job =
            VrpJob(
                id = jobId,
                organizationId = organizationId,
                status = JobStatus.COMPLETED,
                orderIds = listOf(UUID.randomUUID()),
                vehicleIds = listOf(UUID.randomUUID()),
                hardScore = 0,
                softScore = -1000
            )

        every { jobRepository.findById(jobId, organizationId) } returns job

        val result = useCase.execute(jobId, organizationId)

        assertNotNull(result)
        assertEquals(jobId, result.id)
        assertEquals(JobStatus.COMPLETED, result.status)

        verify(exactly = 1) { jobRepository.findById(jobId, organizationId) }
    }

    @Test
    fun `should return null when job not found`() {
        val jobId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()

        every { jobRepository.findById(jobId, organizationId) } returns null

        val result = useCase.execute(jobId, organizationId)

        assertNull(result)

        verify(exactly = 1) { jobRepository.findById(jobId, organizationId) }
    }

    @Test
    fun `should not return job from different organization`() {
        val jobId = UUID.randomUUID()
        val org1 = UUID.randomUUID()
        val org2 = UUID.randomUUID()

        every { jobRepository.findById(jobId, org2) } returns null

        val result = useCase.execute(jobId, org2)

        assertNull(result)
    }
}
