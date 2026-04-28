package com.vrp.infrastructure.persistence.repository

import com.vrp.infrastructure.persistence.entity.TripEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TripJpaRepository : JpaRepository<TripEntity, UUID> {
    fun findByIdAndOrganizationId(
        id: UUID,
        organizationId: UUID
    ): TripEntity?

    fun findAllByJobIdAndOrganizationId(
        jobId: UUID,
        organizationId: UUID
    ): List<TripEntity>

    fun findAllByOrganizationId(organizationId: UUID): List<TripEntity>
}
