package com.example.vrp.job.application

import com.example.vrp.job.domain.Job
import com.example.vrp.job.port.`in`.QueryJobUseCase
import com.example.vrp.job.port.out.JobCachePort
import com.example.vrp.job.port.out.JobRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QueryJobService(
    private val jobRepository: JobRepository,
    private val jobCachePort: JobCachePort,
) : QueryJobUseCase {

    override fun getById(id: String): Job? {
        // not tenant-scoped here; controller has already validated org context
        return jobRepository.findById(id)
    }

    override fun listByOrganization(organizationId: String): List<Job> =
        jobRepository.findAllByOrganizationId(organizationId)
}
