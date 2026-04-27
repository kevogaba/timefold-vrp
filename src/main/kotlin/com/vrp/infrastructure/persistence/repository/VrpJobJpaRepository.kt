package com.vrp.infrastructure.persistence.repository

import com.vrp.domain.model.JobStatus
import com.vrp.infrastructure.persistence.entity.VrpJobEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface VrpJobJpaRepository : JpaRepository<VrpJobEntity, UUID> {
    fun findByIdAndOrganizationId(id: UUID, organizationId: UUID): VrpJobEntity?
    fun findAllByOrganizationId(organizationId: UUID): List<VrpJobEntity>
    fun findAllByOrganizationIdAndStatus(organizationId: UUID, status: JobStatus): List<VrpJobEntity>
}
