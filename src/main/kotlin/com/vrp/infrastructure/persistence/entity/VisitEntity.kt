package com.vrp.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "visits")
data class VisitEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    val trip: TripEntity,

    @Column(nullable = false)
    val orderId: UUID,

    @Column(nullable = false)
    val locationLat: Double,

    @Column(nullable = false)
    val locationLon: Double,

    @Column(nullable = false)
    val arrivalTime: LocalDateTime,

    @Column(nullable = false)
    val departureTime: LocalDateTime,

    @Column(nullable = false)
    val sequenceNumber: Int
)
