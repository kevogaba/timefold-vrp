package com.vrp.infrastructure.persistence.mapper

import com.vrp.domain.model.Location
import com.vrp.domain.model.Trip
import com.vrp.domain.model.Visit
import com.vrp.infrastructure.persistence.entity.TripEntity
import com.vrp.infrastructure.persistence.entity.VisitEntity
import org.springframework.stereotype.Component

@Component
class TripMapper {
    fun toDomain(entity: TripEntity): Trip =
        Trip(
            id = entity.id,
            organizationId = entity.organizationId,
            jobId = entity.jobId,
            vehicleId = entity.vehicleId,
            visits =
                entity.visits.map { visitEntity ->
                    Visit(
                        id = visitEntity.id,
                        orderId = visitEntity.orderId,
                        location = Location(visitEntity.locationLat, visitEntity.locationLon),
                        arrivalTime = visitEntity.arrivalTime,
                        departureTime = visitEntity.departureTime,
                        sequenceNumber = visitEntity.sequenceNumber
                    )
                },
            totalDistanceMeters = entity.totalDistanceMeters,
            totalDurationMinutes = entity.totalDurationMinutes,
            createdAt = entity.createdAt
        )

    fun toEntity(domain: Trip): TripEntity {
        val entity =
            TripEntity(
                organizationId = domain.organizationId,
                jobId = domain.jobId,
                vehicleId = domain.vehicleId,
                totalDistanceMeters = domain.totalDistanceMeters,
                totalDurationMinutes = domain.totalDurationMinutes,
                createdAt = domain.createdAt
            )

        entity.visits.addAll(
            domain.visits.map { visit ->
                VisitEntity(
                    trip = entity,
                    orderId = visit.orderId,
                    locationLat = visit.location.latitude,
                    locationLon = visit.location.longitude,
                    arrivalTime = visit.arrivalTime,
                    departureTime = visit.departureTime,
                    sequenceNumber = visit.sequenceNumber
                )
            }
        )

        return entity
    }
}
