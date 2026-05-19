package com.vrp.infrastructure.temporal.activity

import com.vrp.domain.model.Order
import io.temporal.activity.ActivityInterface
import java.util.UUID

@ActivityInterface
interface FetchOrdersActivity {
    fun fetchOrders(
        organizationId: UUID,
        orderIds: List<UUID>
    ): List<Order>
}

@ActivityInterface
interface FetchVehiclesActivity {
    fun fetchVehicles(
        organizationId: UUID,
        vehicleIds: List<UUID>
    ): List<com.vrp.domain.model.Vehicle>
}

@ActivityInterface
interface RunSolverActivity {
    fun runSolver(
        jobId: UUID,
        organizationId: UUID,
        orders: List<Order>,
        vehicles: List<com.vrp.domain.model.Vehicle>
    ): SolverResult
}

@ActivityInterface
interface PersistSolutionActivity {
    fun persist(
        jobId: UUID,
        organizationId: UUID,
        result: SolverResult
    )
}

data class SolverResult(
    val hardScore: Int,
    val softScore: Int,
    val trips: List<com.vrp.domain.model.Trip>
)
