package com.vrp.solver.domain

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty
import ai.timefold.solver.core.api.domain.solution.PlanningScore
import ai.timefold.solver.core.api.domain.solution.PlanningSolution
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider
import ai.timefold.solver.core.api.score.HardSoftScore
import java.util.UUID

/**
 * VRP planning solution containing vehicles and visits.
 */
@PlanningSolution
data class VrpSolution(
    @PlanningEntityCollectionProperty
    val vehicles: MutableList<SolverVehicle> = mutableListOf(),

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    val visits: MutableList<SolverVisit> = mutableListOf(),

    @PlanningScore
    var score: HardSoftScore? = null,

    val jobId: UUID? = null
) {
    // No-arg constructor for Timefold
    constructor() : this(mutableListOf(), mutableListOf(), null, null)
}
