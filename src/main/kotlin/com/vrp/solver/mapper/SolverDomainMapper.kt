package com.vrp.solver.mapper

import com.vrp.domain.model.*
import com.vrp.solver.domain.SolverVehicle
import com.vrp.solver.domain.SolverVisit
import com.vrp.solver.domain.VrpSolution
import com.vrp.solver.distance.DistanceCalculator
import java.time.LocalDateTime
import java.util.UUID

/**
 * Mapper between domain and solver models.
 */
class SolverDomainMapper(
    private val distanceCalculator: DistanceCalculator
) {

    /**
     * Convert domain models to solver solution.
     */
    fun toSolverSolution(
        jobId: UUID,
        orders: List<Order>,
        vehicles: List<Vehicle>
    ): VrpSolution {
        val visits = orders.map { order ->
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

        val solverVehicles = vehicles.map { vehicle ->
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
     * Convert solver solution to domain trips.
     */
    fun toTrips(
        solution: VrpSolution,
        organizationId: UUID,
        jobId: UUID
    ): List<Trip> {
        return solution.vehicles
            .filter { it.visits.isNotEmpty() }
            .map { vehicle ->
                val visits = vehicle.visits.mapIndexed { index, solverVisit ->
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
    }

    private fun calculateArrivalTime(vehicle: SolverVehicle, visitIndex: Int): LocalDateTime {
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

    private fun calculateDepartureTime(vehicle: SolverVehicle, visitIndex: Int): LocalDateTime {
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
