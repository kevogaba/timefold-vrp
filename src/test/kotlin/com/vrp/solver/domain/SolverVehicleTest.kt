package com.vrp.solver.domain

import com.vrp.domain.model.Location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID

class SolverVehicleTest {
    @Test
    fun `should create solver vehicle with required properties`() {
        val id = UUID.randomUUID()
        val vehicle =
            SolverVehicle(
                id = id,
                name = "Test Vehicle",
                weightCapacity = BigDecimal("1000.0"),
                volumeCapacity = BigDecimal("50.0"),
                startLocation = Location(40.7128, -74.0060),
                endLocation = Location(40.7128, -74.0060),
                availableFrom = LocalTime.of(8, 0),
                availableUntil = LocalTime.of(18, 0),
                costPerKm = BigDecimal("0.50")
            )

        assertThat(vehicle.id).isEqualTo(id)
        assertThat(vehicle.name).isEqualTo("Test Vehicle")
        assertThat(vehicle.weightCapacity).isEqualByComparingTo(BigDecimal("1000.0"))
        assertThat(vehicle.volumeCapacity).isEqualByComparingTo(BigDecimal("50.0"))
        assertThat(vehicle.visits).isEmpty()
    }

    @Test
    fun `should create vehicle with no-arg constructor`() {
        val vehicle = SolverVehicle()

        assertThat(vehicle.id).isNotNull()
        assertThat(vehicle.name).isEmpty()
        assertThat(vehicle.weightCapacity).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(vehicle.visits).isEmpty()
    }

    @Test
    fun `should allow adding visits to mutable list`() {
        val vehicle =
            SolverVehicle(
                id = UUID.randomUUID(),
                name = "Van",
                weightCapacity = BigDecimal("1000.0"),
                volumeCapacity = BigDecimal("50.0"),
                startLocation = Location(40.7128, -74.0060),
                endLocation = Location(40.7128, -74.0060),
                availableFrom = LocalTime.of(8, 0),
                availableUntil = LocalTime.of(18, 0)
            )

        val visit =
            SolverVisit(
                id = UUID.randomUUID(),
                orderId = UUID.randomUUID(),
                location = Location(40.7589, -73.9851),
                demandWeight = BigDecimal("10.0"),
                demandVolume = BigDecimal("1.0"),
                timeWindowStart = java.time.LocalDateTime.now(),
                timeWindowEnd =
                    java.time.LocalDateTime
                        .now()
                        .plusHours(2),
                serviceDurationMinutes = 30
            )

        vehicle.visits.add(visit)

        assertThat(vehicle.visits).hasSize(1)
        assertThat(vehicle.visits.first()).isEqualTo(visit)
    }

    @Test
    fun `should support data class equality`() {
        val id = UUID.randomUUID()
        val vehicle1 =
            SolverVehicle(
                id = id,
                name = "Van",
                weightCapacity = BigDecimal("1000.0"),
                volumeCapacity = BigDecimal("50.0"),
                startLocation = Location(40.7128, -74.0060),
                endLocation = Location(40.7128, -74.0060),
                availableFrom = LocalTime.of(8, 0),
                availableUntil = LocalTime.of(18, 0)
            )

        val vehicle2 =
            SolverVehicle(
                id = id,
                name = "Van",
                weightCapacity = BigDecimal("1000.0"),
                volumeCapacity = BigDecimal("50.0"),
                startLocation = Location(40.7128, -74.0060),
                endLocation = Location(40.7128, -74.0060),
                availableFrom = LocalTime.of(8, 0),
                availableUntil = LocalTime.of(18, 0)
            )

        assertThat(vehicle1).isEqualTo(vehicle2)
    }
}
