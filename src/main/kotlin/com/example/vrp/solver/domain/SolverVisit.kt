package com.example.vrp.solver.domain

import ai.timefold.solver.core.api.domain.entity.PlanningEntity
import ai.timefold.solver.core.api.domain.variable.PlanningVariable
import com.example.vrp.shared.Location
import com.example.vrp.shared.TimeWindow

@PlanningEntity
class SolverVisit(
    val id: String = "",
    val orderId: String = "",
    val location: Location = Location("", 0.0, 0.0),
    val timeWindow: TimeWindow = TimeWindow(
        java.time.LocalDateTime.MIN,
        java.time.LocalDateTime.MAX,
    ),
    val demandWeightKg: Double = 0.0,
    val demandVolumeLiters: Double = 0.0,
) {
    @PlanningVariable(valueRangeProviderRefs = ["vehicleRange"])
    var vehicle: SolverVehicle? = null
}
