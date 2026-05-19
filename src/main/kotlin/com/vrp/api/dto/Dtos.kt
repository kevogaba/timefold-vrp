package com.vrp.api.dto

import java.time.LocalDateTime
import java.util.UUID

data class SubmitJobRequest(
    val orderIds: List<UUID>,
    val vehicleIds: List<UUID>
)

data class JobResponse(
    val id: UUID,
    val status: String,
    val hardScore: Int?,
    val softScore: Int?,
    val errorMessage: String?,
    val createdAt: LocalDateTime,
    val completedAt: LocalDateTime?
)

data class TripResponse(
    val id: UUID,
    val jobId: UUID,
    val vehicleId: UUID,
    val visits: List<VisitResponse>,
    val totalDistanceMeters: Long,
    val totalDurationMinutes: Int
)

data class VisitResponse(
    val orderId: UUID,
    val location: LocationDto,
    val arrivalTime: LocalDateTime,
    val departureTime: LocalDateTime,
    val sequenceNumber: Int
)

data class LocationDto(
    val latitude: Double,
    val longitude: Double
)

data class OrderDto(
    val id: UUID,
    val customerId: UUID,
    val customerName: String,
    val deliveryLocation: LocationDto,
    val timeWindowStart: LocalDateTime,
    val timeWindowEnd: LocalDateTime
)
