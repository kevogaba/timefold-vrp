package com.example.vrp.workflow.activity

import com.example.vrp.order.domain.Order
import com.example.vrp.solver.domain.VrpSolution
import com.example.vrp.vehicle.domain.Vehicle
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface RunSolverActivity {
    @ActivityMethod
    fun runSolver(jobId: String, orders: List<Order>, vehicles: List<Vehicle>): VrpSolution
}
