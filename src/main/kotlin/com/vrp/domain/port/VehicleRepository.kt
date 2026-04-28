package com.vrp.domain.port

import com.vrp.domain.model.Vehicle
import java.util.UUID

/**
 * Repository port for Vehicle domain model.
 */
interface VehicleRepository {
    fun save(vehicle: Vehicle): Vehicle

    fun findById(
        id: UUID,
        organizationId: UUID
    ): Vehicle?

    fun findAllByOrganizationId(organizationId: UUID): List<Vehicle>

    fun findAllByIds(
        ids: List<UUID>,
        organizationId: UUID
    ): List<Vehicle>

    fun deleteById(
        id: UUID,
        organizationId: UUID
    )
}
