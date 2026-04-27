package com.vrp.solver.domain

import ai.timefold.solver.core.api.domain.entity.PlanningEntity
import ai.timefold.solver.core.api.domain.lookup.PlanningId
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable
import com.vrp.domain.model.Location
import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID

/**
 * Vehicle planning entity with a list of visits to complete.
 */
@PlanningEntity
data class SolverVehicle(
    @PlanningId
    val id: UUID,
    val name: String,
    val weightCapacity: BigDecimal,
    val volumeCapacity: BigDecimal,
    val startLocation: Location,
    val endLocation: Location,
    val availableFrom: LocalTime,
    val availableUntil: LocalTime,
    val costPerKm: BigDecimal = BigDecimal.ZERO,

    @PlanningListVariable
    val visits: MutableList<SolverVisit> = mutableListOf()
) {
    // No-arg constructor for Timefold
    constructor() : this(
        id = UUID.randomUUID(),
        name = "",
        weightCapacity = BigDecimal.ZERO,
        volumeCapacity = BigDecimal.ZERO,
        startLocation = Location(0.0, 0.0),
        endLocation = Location(0.0, 0.0),
        availableFrom = LocalTime.MIN,
        availableUntil = LocalTime.MAX
    )
}
