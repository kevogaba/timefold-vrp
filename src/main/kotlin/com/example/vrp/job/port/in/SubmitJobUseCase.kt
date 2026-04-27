package com.example.vrp.job.port.`in`

import com.example.vrp.job.domain.Job

interface SubmitJobUseCase {
    fun submit(organizationId: String, orderIds: List<String>): Job
}
