package com.vrp.api.mapper

import com.vrp.api.dto.*
import com.vrp.domain.model.Trip
import com.vrp.domain.model.VrpJob
import org.springframework.stereotype.Component

@Component
class ApiDtoMapper {

    fun toJobResponse(job: VrpJob): JobResponse {
        return JobResponse(
            id = job.id,
            status = job.status.name,
            hardScore = job.hardScore,
            softScore = job.softScore,
            errorMessage = job.errorMessage,
            createdAt = job.createdAt,
            completedAt = job.completedAt
        )
    }

    fun toTripResponse(trip: Trip): TripResponse {
        return TripResponse(
            id = trip.id,
            jobId = trip.jobId,
            vehicleId = trip.vehicleId,
            visits = trip.visits.map { visit ->
                VisitResponse(
                    orderId = visit.orderId,
                    location = LocationDto(visit.location.latitude, visit.location.longitude),
                    arrivalTime = visit.arrivalTime,
                    departureTime = visit.departureTime,
                    sequenceNumber = visit.sequenceNumber
                )
            },
            totalDistanceMeters = trip.totalDistanceMeters,
            totalDurationMinutes = trip.totalDurationMinutes
        )
    }
}
