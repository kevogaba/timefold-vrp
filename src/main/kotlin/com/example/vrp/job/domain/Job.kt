package com.example.vrp.job.domain

import java.time.Instant

data class Job(
    val id: String,
    val organizationId: String,
    val status: JobStatus,
    val submittedAt: Instant = Instant.now(),
    val completedAt: Instant? = null,
    val errorMessage: String? = null,
) {
    fun withStatus(newStatus: JobStatus, errorMessage: String? = null): Job =
        copy(
            status = newStatus,
            completedAt = if (newStatus == JobStatus.COMPLETED || newStatus == JobStatus.FAILED)
                Instant.now() else completedAt,
            errorMessage = errorMessage ?: this.errorMessage,
        )
}
