package com.vrp.infrastructure.temporal.activity.impl

import com.vrp.domain.model.Order
import com.vrp.domain.model.Vehicle
import com.vrp.domain.port.OrderRepository
import com.vrp.domain.port.TripRepository
import com.vrp.domain.port.VehicleRepository
import com.vrp.infrastructure.temporal.activity.*
import com.vrp.solver.distance.EuclideanDistanceCalculator
import com.vrp.solver.mapper.SolverDomainMapper
import com.vrp.solver.service.VrpSolverService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FetchOrdersActivityImpl(
    private val orderRepository: OrderRepository
) : FetchOrdersActivity {
    override fun fetchOrders(
        organizationId: UUID,
        orderIds: List<UUID>
    ): List<Order> = orderRepository.findAllByIds(orderIds, organizationId)
}

@Component
class FetchVehiclesActivityImpl(
    private val vehicleRepository: VehicleRepository
) : FetchVehiclesActivity {
    override fun fetchVehicles(
        organizationId: UUID,
        vehicleIds: List<UUID>
    ): List<Vehicle> = vehicleRepository.findAllByIds(vehicleIds, organizationId)
}

@Component
class RunSolverActivityImpl(
    private val solverService: VrpSolverService
) : RunSolverActivity {
    override fun runSolver(
        jobId: UUID,
        organizationId: UUID,
        orders: List<Order>,
        vehicles: List<Vehicle>
    ): SolverResult {
        val distanceCalculator = EuclideanDistanceCalculator()
        val mapper = SolverDomainMapper(distanceCalculator)

        // Create solver problem
        val problem = mapper.toSolverSolution(jobId, orders, vehicles)

        // Start solving
        solverService.solve(jobId, problem)

        // Poll for completion
        while (solverService.getSolverStatus(jobId) != ai.timefold.solver.core.api.solver.SolverStatus.NOT_SOLVING) {
            Thread.sleep(1000)
        }

        // Get final solution
        val finalSolution =
            solverService.getFinalBestSolution(jobId)
                ?: throw IllegalStateException("No solution found")

        // Map back to domain
        val trips = mapper.toTrips(finalSolution, organizationId, jobId)
        val score = finalSolution.score ?: throw IllegalStateException("No score calculated")

        return SolverResult(
            hardScore = score.hardScore().toInt(),
            softScore = score.softScore().toInt(),
            trips = trips
        )
    }
}

@Component
class PersistSolutionActivityImpl(
    private val tripRepository: TripRepository
) : PersistSolutionActivity {
    override fun persist(
        jobId: UUID,
        organizationId: UUID,
        result: SolverResult
    ) {
        tripRepository.saveAll(result.trips)
    }
}
