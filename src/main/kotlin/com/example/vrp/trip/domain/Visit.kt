package com.example.vrp.trip.domain

import com.example.vrp.shared.Location
import com.example.vrp.shared.TimeWindow
import java.time.LocalDateTime

data class Visit(
    val id: String,
    val orderId: String,
    val type: VisitType,
    val location: Location,
    val timeWindow: TimeWindow,
    val arrivalTime: LocalDateTime? = null,
    val departureTime: LocalDateTime? = null,
)
