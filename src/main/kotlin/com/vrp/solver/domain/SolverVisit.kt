package com.vrp.solver.domain

import ai.timefold.solver.core.api.domain.common.PlanningId
import com.vrp.domain.model.Location
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Visit in the solver model - can be a pickup or delivery.
 */
data class SolverVisit(
    @PlanningId
    val id: UUID,
    val orderId: UUID,
    val location: Location,
    val demandWeight: BigDecimal,
    val demandVolume: BigDecimal,
    val timeWindowStart: LocalDateTime,
    val timeWindowEnd: LocalDateTime,
    val serviceDurationMinutes: Int,
    val isPickup: Boolean = false,
    val isDelivery: Boolean = true
)
