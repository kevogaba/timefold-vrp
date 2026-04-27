package com.example.vrp.workflow.activity

import com.example.vrp.order.domain.Order
import com.example.vrp.solver.domain.VrpSolution
import com.example.vrp.solver.service.SolverService
import com.example.vrp.vehicle.domain.Vehicle
import org.springframework.stereotype.Component

@Component
class RunSolverActivityImpl(private val solverService: SolverService) : RunSolverActivity {
    override fun runSolver(jobId: String, orders: List<Order>, vehicles: List<Vehicle>): VrpSolution =
        solverService.solve(jobId, orders, vehicles)
}
