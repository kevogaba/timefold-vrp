package com.example.vrp.job.application

import com.example.vrp.job.domain.Job
import com.example.vrp.job.domain.JobStatus
import com.example.vrp.job.port.out.JobCachePort
import com.example.vrp.job.port.out.JobRepository
import com.example.vrp.job.port.out.SolveOrchestrationPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SubmitJobServiceTest {

    private val jobRepository = mockk<JobRepository>()
    private val jobCachePort = mockk<JobCachePort>(relaxed = true)
    private val solveOrchestrationPort = mockk<SolveOrchestrationPort>(relaxed = true)
    private val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)

    private val service = SubmitJobService(
        jobRepository, jobCachePort, solveOrchestrationPort, eventPublisher
    )

    @Test
    fun `submit creates job with SUBMITTED status`() {
        val slot = slot<Job>()
        every { jobRepository.save(capture(slot)) } answers { slot.captured }

        val job = service.submit("org-1", listOf("order-1", "order-2"))

        assertEquals(JobStatus.SUBMITTED, job.status)
        assertEquals("org-1", job.organizationId)
        assertNotNull(job.id)
    }

    @Test
    fun `submit starts workflow orchestration`() {
        val savedJob = slot<Job>()
        every { jobRepository.save(capture(savedJob)) } answers { savedJob.captured }

        service.submit("org-1", listOf("order-1"))

        verify { solveOrchestrationPort.startSolving(any(), "org-1", listOf("order-1")) }
    }

    @Test
    fun `submit fails when order list is empty`() {
        assertThrows<IllegalArgumentException> {
            service.submit("org-1", emptyList())
        }
    }
}
