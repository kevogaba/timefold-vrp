package com.vrp.infrastructure.persistence.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class VisitEntityTest {
    @Test
    fun `should create visit with all fields`() {
        val trip =
            TripEntity(
                organizationId = UUID.randomUUID(),
                jobId = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                totalDistanceMeters = 10000L,
                totalDurationMinutes = 90
            )

        val orderId = UUID.randomUUID()
        val arrivalTime = LocalDateTime.now()
        val departureTime = arrivalTime.plusMinutes(30)

        val visit =
            VisitEntity(
                trip = trip,
                orderId = orderId,
                locationLat = 40.7589,
                locationLon = -73.9851,
                arrivalTime = arrivalTime,
                departureTime = departureTime,
                sequenceNumber = 1
            )

        assertThat(visit.id).isNotNull()
        assertThat(visit.guid).isNotNull()
        assertThat(visit.trip).isEqualTo(trip)
        assertThat(visit.orderId).isEqualTo(orderId)
        assertThat(visit.locationLat).isEqualTo(40.7589)
        assertThat(visit.locationLon).isEqualTo(-73.9851)
        assertThat(visit.arrivalTime).isEqualTo(arrivalTime)
        assertThat(visit.departureTime).isEqualTo(departureTime)
        assertThat(visit.sequenceNumber).isEqualTo(1)
    }

    @Test
    fun `should generate unique guid for each visit`() {
        val trip =
            TripEntity(
                organizationId = UUID.randomUUID(),
                jobId = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                totalDistanceMeters = 10000L,
                totalDurationMinutes = 90
            )

        val visits =
            (1..5).map { seq ->
                val arrivalTime = LocalDateTime.now().plusMinutes(seq * 30L)
                VisitEntity(
                    trip = trip,
                    orderId = UUID.randomUUID(),
                    locationLat = 40.7589,
                    locationLon = -73.9851,
                    arrivalTime = arrivalTime,
                    departureTime = arrivalTime.plusMinutes(30),
                    sequenceNumber = seq
                )
            }

        val guids = visits.map { it.guid }.toSet()
        assertThat(guids).hasSize(5)
    }
}
