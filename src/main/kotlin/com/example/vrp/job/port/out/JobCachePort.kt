package com.example.vrp.job.port.out

import com.example.vrp.job.domain.Job

interface JobCachePort {
    fun get(organizationId: String, jobId: String): Job?
    fun put(organizationId: String, job: Job)
    fun evict(organizationId: String, jobId: String)
}
