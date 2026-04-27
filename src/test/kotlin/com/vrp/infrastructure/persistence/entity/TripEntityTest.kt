package com.vrp.infrastructure.persistence.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class TripEntityTest {

    @Test
    fun `should create trip with required fields`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()
        val vehicleId = UUID.randomUUID()

        val trip = TripEntity(
            organizationId = organizationId,
            jobId = jobId,
            vehicleId = vehicleId,
            totalDistanceMeters = 15000L,
            totalDurationMinutes = 120
        )

        assertThat(trip.id).isNotNull()
        assertThat(trip.guid).isNotNull()
        assertThat(trip.organizationId).isEqualTo(organizationId)
        assertThat(trip.jobId).isEqualTo(jobId)
        assertThat(trip.vehicleId).isEqualTo(vehicleId)
        assertThat(trip.totalDistanceMeters).isEqualTo(15000L)
        assertThat(trip.totalDurationMinutes).isEqualTo(120)
        assertThat(trip.visits).isEmpty()
        assertThat(trip.createdAt).isNotNull()
    }

    @Test
    fun `should generate unique guid for each trip`() {
        val trips = (1..5).map {
            TripEntity(
                organizationId = UUID.randomUUID(),
                jobId = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                totalDistanceMeters = it * 1000L,
                totalDurationMinutes = it * 30
            )
        }

        val guids = trips.map { it.guid }.toSet()
        assertThat(guids).hasSize(5)
    }
}
