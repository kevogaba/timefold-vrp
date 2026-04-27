package com.example.vrp.job.adapter.out.persistence

import com.example.vrp.job.domain.Job
import com.example.vrp.job.port.out.JobRepository
import org.springframework.stereotype.Component

@Component
class JpaJobAdapter(private val repo: JobJpaRepository) : JobRepository {

    override fun findById(id: String): Job? = repo.findById(id).orElse(null)?.toDomain()

    override fun findAllByOrganizationId(organizationId: String): List<Job> =
        repo.findAllByOrganizationId(organizationId).map { it.toDomain() }

    override fun save(job: Job): Job = repo.save(job.toEntity()).toDomain()

    private fun JobJpaEntity.toDomain() = Job(
        id = id,
        organizationId = organizationId,
        status = status,
        submittedAt = submittedAt,
        completedAt = completedAt,
        errorMessage = errorMessage,
    )

    private fun Job.toEntity() = JobJpaEntity(
        id = id,
        organizationId = organizationId,
        status = status,
        submittedAt = submittedAt,
        completedAt = completedAt,
        errorMessage = errorMessage,
    )
}
