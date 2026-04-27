package com.example.vrp.job.adapter.`in`.web

import com.example.vrp.job.domain.Job
import com.example.vrp.job.domain.JobStatus
import java.time.Instant

data class JobResponse(
    val id: String,
    val organizationId: String,
    val status: JobStatus,
    val submittedAt: Instant,
    val completedAt: Instant?,
    val errorMessage: String?,
) {
    companion object {
        fun from(job: Job) = JobResponse(
            id = job.id,
            organizationId = job.organizationId,
            status = job.status,
            submittedAt = job.submittedAt,
            completedAt = job.completedAt,
            errorMessage = job.errorMessage,
        )
    }
}
