package com.vrp.domain.port

import com.vrp.domain.model.Trip
import java.util.UUID

/**
 * Repository port for Trip domain model.
 */
interface TripRepository {
    fun save(trip: Trip): Trip

    fun saveAll(trips: List<Trip>): List<Trip>

    fun findById(
        id: UUID,
        organizationId: UUID
    ): Trip?

    fun findAllByJobId(
        jobId: UUID,
        organizationId: UUID
    ): List<Trip>

    fun findAllByOrganizationId(organizationId: UUID): List<Trip>
}
