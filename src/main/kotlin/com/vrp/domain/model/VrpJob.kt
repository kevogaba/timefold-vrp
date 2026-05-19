package com.vrp.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * VRP optimization job request.
 */
data class VrpJob(
    val id: UUID,
    val organizationId: UUID,
    val status: JobStatus,
    val orderIds: List<UUID>,
    val vehicleIds: List<UUID>,
    val hardScore: Int? = null,
    val softScore: Int? = null,
    val errorMessage: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val startedAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null
) {
    init {
        require(orderIds.isNotEmpty()) { "Job must include at least one order" }
        require(vehicleIds.isNotEmpty()) { "Job must include at least one vehicle" }
    }
}
