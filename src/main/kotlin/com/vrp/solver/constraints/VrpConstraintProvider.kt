package com.vrp.solver.constraints

import ai.timefold.solver.core.api.score.HardSoftScore
import ai.timefold.solver.core.api.score.stream.Constraint
import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.core.api.score.stream.ConstraintProvider
import com.vrp.solver.distance.EuclideanDistanceCalculator
import com.vrp.solver.domain.SolverVehicle
import com.vrp.solver.domain.SolverVisit
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Constraint provider for VRP with 4 HARD and 2 SOFT constraints.
 */
class VrpConstraintProvider : ConstraintProvider {
    private val distanceCalculator = EuclideanDistanceCalculator()

    override fun defineConstraints(constraintFactory: ConstraintFactory): Array<Constraint> =
        arrayOf(
            // HARD constraints
            vehicleWeightCapacity(constraintFactory),
            vehicleVolumeCapacity(constraintFactory),
            timeWindowViolation(constraintFactory),
            pickupBeforeDelivery(constraintFactory),
            // SOFT constraints
            minimizeTotalDistance(constraintFactory),
            balanceLoad(constraintFactory)
        )

    /**
     * HARD: Vehicle weight capacity must not be exceeded.
     */
    private fun vehicleWeightCapacity(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory
            .forEach(SolverVehicle::class.java)
            .filter { vehicle ->
                val totalWeight = vehicle.visits.sumOf { it.demandWeight }
                totalWeight > vehicle.weightCapacity
            }.penalize(HardSoftScore.ONE_HARD)
            .asConstraint("vehicleWeightCapacity")

    /**
     * HARD: Vehicle volume capacity must not be exceeded.
     */
    private fun vehicleVolumeCapacity(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory
            .forEach(SolverVehicle::class.java)
            .filter { vehicle ->
                val totalVolume = vehicle.visits.sumOf { it.demandVolume }
                totalVolume > vehicle.volumeCapacity
            }.penalize(HardSoftScore.ONE_HARD)
            .asConstraint("vehicleVolumeCapacity")

    /**
     * HARD: Visits must arrive within their time windows.
     */
    private fun timeWindowViolation(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory
            .forEach(SolverVehicle::class.java)
            .filter { vehicle ->
                vehicle.visits.any { visit ->
                    val arrivalTime = calculateArrivalTime(vehicle, visit)
                    arrivalTime.isBefore(visit.timeWindowStart) || arrivalTime.isAfter(visit.timeWindowEnd)
                }
            }.penalize(HardSoftScore.ONE_HARD)
            .asConstraint("timeWindowViolation")

    /**
     * HARD: For paired pickup-delivery, pickup must come before delivery.
     */
    private fun pickupBeforeDelivery(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory
            .forEach(SolverVehicle::class.java)
            .filter { vehicle ->
                // Group visits by order
                val visitsByOrder = vehicle.visits.groupBy { it.orderId }
                visitsByOrder.any { (_, visits) ->
                    if (visits.size == 2) {
                        val pickup = visits.find { it.isPickup }
                        val delivery = visits.find { it.isDelivery }
                        if (pickup != null && delivery != null) {
                            val pickupIndex = vehicle.visits.indexOf(pickup)
                            val deliveryIndex = vehicle.visits.indexOf(delivery)
                            pickupIndex >= deliveryIndex
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                }
            }.penalize(HardSoftScore.ONE_HARD)
            .asConstraint("pickupBeforeDelivery")

    /**
     * SOFT: Minimize total distance across all routes.
     */
    private fun minimizeTotalDistance(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory
            .forEach(SolverVehicle::class.java)
            .penalize(HardSoftScore.ONE_SOFT) { vehicle ->
                calculateTotalDistance(vehicle).toInt().toLong()
            }.asConstraint("minimizeTotalDistance")

    /**
     * SOFT: Balance load across vehicles (minimize variance in utilization).
     */
    private fun balanceLoad(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory
            .forEach(SolverVehicle::class.java)
            .penalize(HardSoftScore.ONE_SOFT) { vehicle ->
                val utilizationPercent =
                    if (vehicle.weightCapacity > BigDecimal.ZERO) {
                        val totalWeight = vehicle.visits.sumOf { it.demandWeight }
                        ((totalWeight / vehicle.weightCapacity) * BigDecimal(100)).toInt()
                    } else {
                        0
                    }
                // Penalize deviation from 80% utilization (optimal target)
                Math.abs(utilizationPercent - 80).toLong()
            }.asConstraint("balanceLoad")

    /**
     * Calculate total distance for a vehicle route.
     */
    private fun calculateTotalDistance(vehicle: SolverVehicle): Long {
        if (vehicle.visits.isEmpty()) return 0L

        var totalDistance = 0L
        var currentLocation = vehicle.startLocation

        for (visit in vehicle.visits) {
            totalDistance += distanceCalculator.distanceBetween(currentLocation, visit.location)
            currentLocation = visit.location
        }

        // Return to end location
        totalDistance += distanceCalculator.distanceBetween(currentLocation, vehicle.endLocation)

        return totalDistance
    }

    /**
     * Calculate arrival time at a visit.
     */
    private fun calculateArrivalTime(
        vehicle: SolverVehicle,
        targetVisit: SolverVisit
    ): LocalDateTime {
        val index = vehicle.visits.indexOf(targetVisit)
        if (index == -1) return LocalDateTime.MAX

        var currentTime =
            LocalDateTime
                .now()
                .with(vehicle.availableFrom)
        var currentLocation = vehicle.startLocation

        for (i in 0..index) {
            val visit = vehicle.visits[i]
            val travelTimeMinutes = distanceCalculator.distanceBetween(currentLocation, visit.location) / 1000 / 60
            currentTime = currentTime.plus(travelTimeMinutes, ChronoUnit.MINUTES)

            if (i == index) break

            currentTime = currentTime.plus(visit.serviceDurationMinutes.toLong(), ChronoUnit.MINUTES)
            currentLocation = visit.location
        }

        return currentTime
    }
}
