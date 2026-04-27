package com.example.vrp.solver.domain

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty
import ai.timefold.solver.core.api.domain.solution.PlanningScore
import ai.timefold.solver.core.api.domain.solution.PlanningSolution
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore

@PlanningSolution
class VrpSolution(
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "vehicleRange")
    val vehicles: List<SolverVehicle> = emptyList(),

    @PlanningEntityCollectionProperty
    val visits: List<SolverVisit> = emptyList(),

    @PlanningScore
    var score: HardSoftScore = HardSoftScore.ZERO,
)
