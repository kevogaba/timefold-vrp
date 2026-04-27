package com.example.vrp.solver.mapper

import com.example.vrp.solver.domain.VrpSolution
import com.example.vrp.trip.domain.Trip
import com.example.vrp.trip.domain.Visit
import com.example.vrp.trip.domain.VisitType
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SolverOutputMapper {

    fun toTrips(solution: VrpSolution, jobId: String, organizationId: String): List<Trip> =
        solution.vehicles.mapNotNull { vehicle ->
            val visits = solution.visits.filter { it.vehicle?.id == vehicle.id }
            if (visits.isEmpty()) return@mapNotNull null

            // Total distance = depot→visit₁ + visit₁→visit₂ + … + visitₙ→depot
            val locations = listOf(vehicle.depotLocation) +
                    visits.map { it.location } +
                    listOf(vehicle.depotLocation)
            val totalDistance = locations.zipWithNext().sumOf { (a, b) -> a.distanceTo(b) }

            Trip(
                id = UUID.randomUUID().toString(),
                jobId = jobId,
                organizationId = organizationId,
                vehicleId = vehicle.id,
                visits = visits.map { visit ->
                    Visit(
                        id = UUID.randomUUID().toString(),
                        orderId = visit.orderId,
                        type = VisitType.DELIVERY,
                        location = visit.location,
                        timeWindow = visit.timeWindow,
                    )
                },
                totalDistanceKm = totalDistance,
            )
        }
}
