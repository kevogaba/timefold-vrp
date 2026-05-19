package com.vrp.solver.mapper

import com.vrp.domain.model.Order
import com.vrp.domain.model.Trip
import com.vrp.domain.model.Vehicle
import com.vrp.domain.model.Visit
import com.vrp.solver.distance.DistanceCalculator
import com.vrp.solver.domain.SolverVehicle
import com.vrp.solver.domain.SolverVisit
import com.vrp.solver.domain.VrpSolution
import java.time.LocalDateTime
import java.util.UUID

/**
 * Mapper for converting between domain models and Timefold Solver planning models.
 *
 * This mapper handles the bidirectional transformation:
 * - **Domain → Solver**: Converts business domain models (Order, Vehicle) into Timefold planning models
 *   (VrpSolution, SolverVehicle, SolverVisit) that the constraint solver can work with.
 * - **Solver → Domain**: Converts optimized solver output back into domain models (Trip, Visit)
 *   that represent the final routing solution.
 *
 * The mapper also calculates:
 * - Travel times and arrival times for each visit
 * - Total distance and duration for each trip
 * - Demand aggregation from order line items
 *
 * @property distanceCalculator Calculator for computing distances and travel times between locations
 *
 * @see VrpSolution The Timefold planning solution containing vehicles and visits
 * @see com.vrp.domain.model.Order Domain model for customer orders
 * @see com.vrp.domain.model.Trip Domain model for optimized vehicle routes
 */
class SolverDomainMapper(
    private val distanceCalculator: DistanceCalculator
) {
    /**
     * Converts domain orders and vehicles into a Timefold planning solution ready for optimization.
     *
     * This method:
     * 1. Creates SolverVehicle instances from domain vehicles
     * 2. Creates SolverVisit instances from domain orders (aggregating line item demands)
     * 3. Assembles them into a VrpSolution that Timefold can solve
     *
     * The initial solution has all visits unassigned (not yet assigned to vehicles).
     *
     * @param jobId The unique identifier for this optimization job
     * @param orders List of customer orders to be routed
     * @param vehicles List of available vehicles for the routes
     * @return VrpSolution ready for Timefold Solver with unassigned visits
     */
    fun toSolverSolution(
        jobId: UUID,
        orders: List<Order>,
        vehicles: List<Vehicle>
    ): VrpSolution {
        val visits =
            orders
                .map { order ->
                    SolverVisit(
                        id = UUID.randomUUID(),
                        orderId = order.id,
                        location = order.deliveryLocation,
                        demandWeight = order.totalWeight,
                        demandVolume = order.totalVolume,
                        timeWindowStart = order.timeWindowStart,
                        timeWindowEnd = order.timeWindowEnd,
                        serviceDurationMinutes = order.serviceDurationMinutes,
                        isPickup = false,
                        isDelivery = true
                    )
                }.toMutableList()

        val solverVehicles =
            vehicles
                .map { vehicle ->
                    SolverVehicle(
                        id = vehicle.id,
                        name = vehicle.name,
                        weightCapacity = vehicle.weightCapacity,
                        volumeCapacity = vehicle.volumeCapacity,
                        startLocation = vehicle.startLocation,
                        endLocation = vehicle.endLocation,
                        availableFrom = vehicle.availableFrom,
                        availableUntil = vehicle.availableUntil,
                        costPerKm = vehicle.costPerKm,
                        visits = mutableListOf()
                    )
                }.toMutableList()

        return VrpSolution(
            vehicles = solverVehicles,
            visits = visits,
            score = null,
            jobId = jobId
        )
    }

    /**
     * Converts an optimized Timefold solution back into domain Trip models.
     *
     * This method:
     * 1. Filters out vehicles with no assigned visits
     * 2. For each vehicle with visits, creates a Trip with calculated arrival/departure times
     * 3. Calculates total distance and duration for each trip
     *
     * Only vehicles that have been assigned at least one visit will produce a Trip.
     *
     * @param solution The optimized VrpSolution from Timefold Solver
     * @param organizationId The organization ID for multi-tenancy
     * @param jobId The job ID this solution belongs to
     * @return List of domain Trip models representing optimized routes
     */
    fun toTrips(
        solution: VrpSolution,
        organizationId: UUID,
        jobId: UUID
    ): List<Trip> =
        solution.vehicles
            .filter { it.visits.isNotEmpty() }
            .map { vehicle ->
                val visits =
                    vehicle.visits.mapIndexed { index, solverVisit ->
                        Visit(
                            id = solverVisit.id,
                            orderId = solverVisit.orderId,
                            location = solverVisit.location,
                            arrivalTime = calculateArrivalTime(vehicle, index),
                            departureTime = calculateDepartureTime(vehicle, index),
                            sequenceNumber = index
                        )
                    }

                val totalDistance = calculateTotalDistance(vehicle)
                val totalDuration = calculateTotalDuration(vehicle)

                Trip(
                    id = UUID.randomUUID(),
                    organizationId = organizationId,
                    jobId = jobId,
                    vehicleId = vehicle.id,
                    visits = visits,
                    totalDistanceMeters = totalDistance,
                    totalDurationMinutes = totalDuration,
                    createdAt = LocalDateTime.now()
                )
            }

    private fun calculateArrivalTime(
        vehicle: SolverVehicle,
        visitIndex: Int
    ): LocalDateTime {
        var currentTime = LocalDateTime.now().with(vehicle.availableFrom)
        var currentLocation = vehicle.startLocation

        for (i in 0..visitIndex) {
            val visit = vehicle.visits[i]
            val distance = distanceCalculator.distanceBetween(currentLocation, visit.location)
            val travelTimeMinutes = distance / 1000 / 60 // Assume 60 km/h average speed
            currentTime = currentTime.plusMinutes(travelTimeMinutes)

            if (i == visitIndex) break

            currentTime = currentTime.plusMinutes(visit.serviceDurationMinutes.toLong())
            currentLocation = visit.location
        }

        return currentTime
    }

    private fun calculateDepartureTime(
        vehicle: SolverVehicle,
        visitIndex: Int
    ): LocalDateTime {
        val arrivalTime = calculateArrivalTime(vehicle, visitIndex)
        val visit = vehicle.visits[visitIndex]
        return arrivalTime.plusMinutes(visit.serviceDurationMinutes.toLong())
    }

    private fun calculateTotalDistance(vehicle: SolverVehicle): Long {
        if (vehicle.visits.isEmpty()) return 0L

        var totalDistance = 0L
        var currentLocation = vehicle.startLocation

        for (visit in vehicle.visits) {
            totalDistance += distanceCalculator.distanceBetween(currentLocation, visit.location)
            currentLocation = visit.location
        }

        totalDistance += distanceCalculator.distanceBetween(currentLocation, vehicle.endLocation)
        return totalDistance
    }

    private fun calculateTotalDuration(vehicle: SolverVehicle): Int {
        if (vehicle.visits.isEmpty()) return 0

        val totalDistance = calculateTotalDistance(vehicle)
        val travelTimeMinutes = (totalDistance / 1000 / 60).toInt()
        val serviceTimeMinutes = vehicle.visits.sumOf { it.serviceDurationMinutes }

        return travelTimeMinutes + serviceTimeMinutes
    }
}
