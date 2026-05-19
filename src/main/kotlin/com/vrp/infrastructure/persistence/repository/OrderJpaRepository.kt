package com.vrp.infrastructure.persistence.repository

import com.vrp.infrastructure.persistence.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderJpaRepository : JpaRepository<OrderEntity, UUID> {
    fun findByIdAndOrganizationId(
        id: UUID,
        organizationId: UUID
    ): OrderEntity?

    fun findAllByOrganizationId(organizationId: UUID): List<OrderEntity>

    fun findAllByIdInAndOrganizationId(
        ids: List<UUID>,
        organizationId: UUID
    ): List<OrderEntity>

    fun deleteByIdAndOrganizationId(
        id: UUID,
        organizationId: UUID
    )
}
