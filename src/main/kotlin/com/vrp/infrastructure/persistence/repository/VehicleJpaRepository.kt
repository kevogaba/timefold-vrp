package com.vrp.infrastructure.persistence.repository

import com.vrp.infrastructure.persistence.entity.VehicleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface VehicleJpaRepository : JpaRepository<VehicleEntity, UUID> {
    fun findByIdAndOrganizationId(id: UUID, organizationId: UUID): VehicleEntity?
    fun findAllByOrganizationId(organizationId: UUID): List<VehicleEntity>
    fun findAllByIdInAndOrganizationId(ids: List<UUID>, organizationId: UUID): List<VehicleEntity>
    fun deleteByIdAndOrganizationId(id: UUID, organizationId: UUID)
}
