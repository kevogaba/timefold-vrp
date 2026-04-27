package com.example.vrp.job.application

import com.example.vrp.job.domain.Job
import com.example.vrp.job.domain.JobStatus
import com.example.vrp.job.domain.events.JobStatusChangedEvent
import com.example.vrp.job.port.`in`.SubmitJobUseCase
import com.example.vrp.job.port.out.JobCachePort
import com.example.vrp.job.port.out.JobRepository
import com.example.vrp.job.port.out.SolveOrchestrationPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class SubmitJobService(
    private val jobRepository: JobRepository,
    private val jobCachePort: JobCachePort,
    private val solveOrchestrationPort: SolveOrchestrationPort,
    private val eventPublisher: ApplicationEventPublisher,
) : SubmitJobUseCase {

    override fun submit(organizationId: String, orderIds: List<String>): Job {
        require(orderIds.isNotEmpty()) { "Must provide at least one order" }

        val job = Job(
            id = UUID.randomUUID().toString(),
            organizationId = organizationId,
            status = JobStatus.SUBMITTED,
        )
        val saved = jobRepository.save(job)
        jobCachePort.put(organizationId, saved)

        // Notify listeners that a new job has been submitted (no prior status)
        eventPublisher.publishEvent(
            JobStatusChangedEvent(
                jobId = saved.id,
                organizationId = organizationId,
                previousStatus = null,
                newStatus = JobStatus.SUBMITTED,
            )
        )

        solveOrchestrationPort.startSolving(saved.id, organizationId, orderIds)
        return saved
    }
}
