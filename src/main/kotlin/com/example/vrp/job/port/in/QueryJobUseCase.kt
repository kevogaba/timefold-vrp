package com.example.vrp.job.port.`in`

import com.example.vrp.job.domain.Job

interface QueryJobUseCase {
    fun getById(id: String): Job?
    fun listByOrganization(organizationId: String): List<Job>
}
