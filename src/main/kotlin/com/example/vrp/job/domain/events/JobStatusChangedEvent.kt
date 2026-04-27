package com.example.vrp.job.domain.events

import com.example.vrp.job.domain.JobStatus

data class JobStatusChangedEvent(
    val jobId: String,
    val organizationId: String,
    val previousStatus: JobStatus?,   // null when this is a newly submitted job
    val newStatus: JobStatus,
)
