package com.example.vrp.workflow.activity

import com.example.vrp.job.domain.JobStatus
import com.example.vrp.job.domain.events.JobStatusChangedEvent
import com.example.vrp.job.port.out.JobCachePort
import com.example.vrp.job.port.out.JobRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class UpdateJobStatusActivityImpl(
    private val jobRepository: JobRepository,
    private val jobCachePort: JobCachePort,
    private val eventPublisher: ApplicationEventPublisher,
) : UpdateJobStatusActivity {

    override fun updateStatus(
        jobId: String,
        organizationId: String,
        status: JobStatus,
        errorMessage: String?,
    ) {
        val job = jobRepository.findById(jobId) ?: return
        val previousStatus = job.status
        val updated = job.withStatus(status, errorMessage)
        val saved = jobRepository.save(updated)
        jobCachePort.put(organizationId, saved)
        eventPublisher.publishEvent(
            JobStatusChangedEvent(
                jobId = jobId,
                organizationId = organizationId,
                previousStatus = previousStatus,
                newStatus = status,
            )
        )
    }
}
