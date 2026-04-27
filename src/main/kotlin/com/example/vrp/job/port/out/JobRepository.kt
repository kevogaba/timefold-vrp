package com.example.vrp.job.port.out

import com.example.vrp.job.domain.Job

interface JobRepository {
    fun findById(id: String): Job?
    fun findAllByOrganizationId(organizationId: String): List<Job>
    fun save(job: Job): Job
}
