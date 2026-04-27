package com.example.vrp.solver.service

import ai.timefold.solver.core.api.solver.SolverManager
import com.example.vrp.order.domain.Order
import com.example.vrp.solver.domain.VrpSolution
import com.example.vrp.solver.mapper.SolverInputMapper
import com.example.vrp.vehicle.domain.Vehicle
import org.springframework.stereotype.Service

@Service
class SolverService(
    private val solverManager: SolverManager<VrpSolution, String>,
    private val inputMapper: SolverInputMapper,
) {
    fun solve(problemId: String, orders: List<Order>, vehicles: List<Vehicle>): VrpSolution {
        val problem = inputMapper.toSolution(orders, vehicles)
        val result = solverManager.solve(problemId, problem)
        return result.finalBestSolution
    }
}
