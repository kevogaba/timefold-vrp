package com.vrp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class TripTest {

    @Test
    fun `should create trip with visits`() {
        val id = UUID.randomUUID()
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()
        val vehicleId = UUID.randomUUID()

        val visits = listOf(
            Visit(
                id = UUID.randomUUID(),
                orderId = UUID.randomUUID(),
                location = Location(40.7589, -73.9851),
                arrivalTime = LocalDateTime.of(2024, 1, 1, 10, 0),
                departureTime = LocalDateTime.of(2024, 1, 1, 10, 30),
                sequenceNumber = 0
            )
        )

        val trip = Trip(
            id = id,
            organizationId = organizationId,
            jobId = jobId,
            vehicleId = vehicleId,
            visits = visits,
            totalDistanceMeters = 15000L,
            totalDurationMinutes = 90
        )

        assertThat(trip.id).isEqualTo(id)
        assertThat(trip.organizationId).isEqualTo(organizationId)
        assertThat(trip.jobId).isEqualTo(jobId)
        assertThat(trip.vehicleId).isEqualTo(vehicleId)
        assertThat(trip.visits).hasSize(1)
        assertThat(trip.totalDistanceMeters).isEqualTo(15000L)
        assertThat(trip.totalDurationMinutes).isEqualTo(90)
    }

    @Test
    fun `should fail when total distance is negative`() {
        assertThatThrownBy {
            Trip(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                jobId = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                visits = emptyList(),
                totalDistanceMeters = -100L,
                totalDurationMinutes = 60
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Total distance cannot be negative")
    }

    @Test
    fun `should fail when total duration is negative`() {
        assertThatThrownBy {
            Trip(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                jobId = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                visits = emptyList(),
                totalDistanceMeters = 1000L,
                totalDurationMinutes = -10
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Total duration cannot be negative")
    }
}

class VisitTest {

    @Test
    fun `should create visit with all properties`() {
        val id = UUID.randomUUID()
        val orderId = UUID.randomUUID()
        val location = Location(40.7589, -73.9851)
        val arrivalTime = LocalDateTime.of(2024, 1, 1, 10, 0)
        val departureTime = LocalDateTime.of(2024, 1, 1, 10, 30)

        val visit = Visit(
            id = id,
            orderId = orderId,
            location = location,
            arrivalTime = arrivalTime,
            departureTime = departureTime,
            sequenceNumber = 0
        )

        assertThat(visit.id).isEqualTo(id)
        assertThat(visit.orderId).isEqualTo(orderId)
        assertThat(visit.location).isEqualTo(location)
        assertThat(visit.arrivalTime).isEqualTo(arrivalTime)
        assertThat(visit.departureTime).isEqualTo(departureTime)
        assertThat(visit.sequenceNumber).isEqualTo(0)
    }

    @Test
    fun `should fail when sequence number is negative`() {
        assertThatThrownBy {
            Visit(
                id = UUID.randomUUID(),
                orderId = UUID.randomUUID(),
                location = Location(40.7589, -73.9851),
                arrivalTime = LocalDateTime.now(),
                departureTime = LocalDateTime.now().plusMinutes(30),
                sequenceNumber = -1
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Sequence number cannot be negative")
    }

    @Test
    fun `should fail when departure time is before arrival time`() {
        assertThatThrownBy {
            Visit(
                id = UUID.randomUUID(),
                orderId = UUID.randomUUID(),
                location = Location(40.7589, -73.9851),
                arrivalTime = LocalDateTime.of(2024, 1, 1, 11, 0),
                departureTime = LocalDateTime.of(2024, 1, 1, 10, 0),
                sequenceNumber = 0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Departure time must be after or equal to arrival time")
    }

    @Test
    fun `should allow departure time equal to arrival time`() {
        val time = LocalDateTime.of(2024, 1, 1, 10, 0)

        val visit = Visit(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            location = Location(40.7589, -73.9851),
            arrivalTime = time,
            departureTime = time,
            sequenceNumber = 0
        )

        assertThat(visit.arrivalTime).isEqualTo(visit.departureTime)
    }
}
