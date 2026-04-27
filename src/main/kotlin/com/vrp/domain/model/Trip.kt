package com.vrp.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Planned visit within a trip.
 */
data class Visit(
    val id: UUID,
    val orderId: UUID,
    val location: Location,
    val arrivalTime: LocalDateTime,
    val departureTime: LocalDateTime,
    val sequenceNumber: Int
) {
    init {
        require(sequenceNumber >= 0) { "Sequence number cannot be negative" }
        require(departureTime.isAfter(arrivalTime) || departureTime.isEqual(arrivalTime)) {
            "Departure time must be after or equal to arrival time"
        }
    }
}

/**
 * Trip representing a planned route for a vehicle.
 */
data class Trip(
    val id: UUID,
    val organizationId: UUID,
    val jobId: UUID,
    val vehicleId: UUID,
    val visits: List<Visit>,
    val totalDistanceMeters: Long,
    val totalDurationMinutes: Int,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(totalDistanceMeters >= 0) { "Total distance cannot be negative" }
        require(totalDurationMinutes >= 0) { "Total duration cannot be negative" }
    }
}
