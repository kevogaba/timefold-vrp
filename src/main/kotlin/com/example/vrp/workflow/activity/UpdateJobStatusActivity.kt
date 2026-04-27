package com.example.vrp.workflow.activity

import com.example.vrp.job.domain.JobStatus
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface UpdateJobStatusActivity {
    @ActivityMethod
    fun updateStatus(jobId: String, organizationId: String, status: JobStatus, errorMessage: String? = null)
}
