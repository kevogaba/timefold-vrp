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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Component
class FetchOrdersActivityImpl(
    private val orderRepository: OrderRepository
) : FetchOrdersActivity {

    override fun fetchOrders(organizationId: UUID, orderIds: List<UUID>): List<Order> {
        return orderRepository.findAllByIds(orderIds, organizationId)
    }
}

@Component
class FetchVehiclesActivityImpl(
    private val vehicleRepository: VehicleRepository
) : FetchVehiclesActivity {

    override fun fetchVehicles(organizationId: UUID, vehicleIds: List<UUID>): List<Vehicle> {
        return vehicleRepository.findAllByIds(vehicleIds, organizationId)
    }
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

        // Solve asynchronously
        val latch = CountDownLatch(1)
        var finalSolution = problem

        solverService.solve(jobId, problem) { solution ->
            finalSolution = solution
            latch.countDown()
        }

        // Wait for solution (with timeout)
        latch.await(10, TimeUnit.MINUTES)

        // Map back to domain
        val trips = mapper.toTrips(finalSolution, organizationId, jobId)
        val score = finalSolution.score ?: throw IllegalStateException("No score calculated")

        return SolverResult(
            hardScore = score.hardScore(),
            softScore = score.softScore(),
            trips = trips
        )
    }
}

@Component
class PersistSolutionActivityImpl(
    private val tripRepository: TripRepository
) : PersistSolutionActivity {

    override fun persist(jobId: UUID, organizationId: UUID, result: SolverResult) {
        tripRepository.saveAll(result.trips)
    }
}
