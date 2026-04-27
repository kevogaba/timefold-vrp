package com.example.vrp.job.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface JobJpaRepository : JpaRepository<JobJpaEntity, String> {
    fun findAllByOrganizationId(organizationId: String): List<JobJpaEntity>
}
