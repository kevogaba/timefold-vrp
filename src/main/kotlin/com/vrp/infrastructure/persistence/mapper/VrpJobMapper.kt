package com.vrp.infrastructure.persistence.mapper

import com.vrp.domain.model.VrpJob
import com.vrp.infrastructure.persistence.entity.VrpJobEntity
import org.springframework.stereotype.Component

@Component
class VrpJobMapper {
    fun toDomain(entity: VrpJobEntity): VrpJob =
        VrpJob(
            id = entity.id,
            organizationId = entity.organizationId,
            status = entity.status,
            orderIds = entity.orders.map { it.id },
            vehicleIds = entity.vehicles.map { it.id },
            hardScore = entity.hardScore,
            softScore = entity.softScore,
            errorMessage = entity.errorMessage,
            createdAt = entity.createdAt,
            startedAt = entity.startedAt,
            completedAt = entity.completedAt
        )

    fun toEntity(domain: VrpJob): VrpJobEntity =
        VrpJobEntity(
            organizationId = domain.organizationId,
            status = domain.status,
            hardScore = domain.hardScore,
            softScore = domain.softScore,
            errorMessage = domain.errorMessage,
            createdAt = domain.createdAt,
            startedAt = domain.startedAt,
            completedAt = domain.completedAt
        )
}
