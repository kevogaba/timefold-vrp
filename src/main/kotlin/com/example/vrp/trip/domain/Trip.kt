package com.example.vrp.trip.domain

import java.time.Instant

data class Trip(
    val id: String,
    val jobId: String,
    val organizationId: String,
    val vehicleId: String,
    val visits: List<Visit>,
    val totalDistanceKm: Double,
    val createdAt: Instant = Instant.now(),
)
