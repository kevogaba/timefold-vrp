package com.vrp.infrastructure.persistence.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID

class VehicleEntityTest {

    @Test
    fun `should create vehicle entity with required fields`() {
        val organizationId = UUID.randomUUID()

        val vehicle = VehicleEntity(
            organizationId = organizationId,
            name = "Delivery Van 1",
            licensePlate = "ABC123",
            weightCapacity = BigDecimal("1000.00"),
            volumeCapacity = BigDecimal("50.00"),
            startLat = 40.7128,
            startLon = -74.0060,
            endLat = 40.7128,
            endLon = -74.0060,
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0)
        )

        assertThat(vehicle.id).isNotNull()
        assertThat(vehicle.guid).isNotNull()
        assertThat(vehicle.organizationId).isEqualTo(organizationId)
        assertThat(vehicle.name).isEqualTo("Delivery Van 1")
        assertThat(vehicle.licensePlate).isEqualTo("ABC123")
        assertThat(vehicle.weightCapacity).isEqualByComparingTo(BigDecimal("1000.00"))
        assertThat(vehicle.volumeCapacity).isEqualByComparingTo(BigDecimal("50.00"))
        assertThat(vehicle.costPerKm).isEqualByComparingTo(BigDecimal.ZERO)
    }

    @Test
    fun `should create vehicle with driver information`() {
        val driverId = UUID.randomUUID()

        val vehicle = VehicleEntity(
            organizationId = UUID.randomUUID(),
            name = "Delivery Van 1",
            licensePlate = "ABC123",
            weightCapacity = BigDecimal("1000.00"),
            volumeCapacity = BigDecimal("50.00"),
            startLat = 40.7128,
            startLon = -74.0060,
            endLat = 40.7128,
            endLon = -74.0060,
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            driverId = driverId,
            driverName = "John Doe",
            driverLicense = "DL12345",
            driverPhone = "+1234567890",
            driverEmail = "john@example.com",
            costPerKm = BigDecimal("0.50")
        )

        assertThat(vehicle.driverId).isEqualTo(driverId)
        assertThat(vehicle.driverName).isEqualTo("John Doe")
        assertThat(vehicle.driverLicense).isEqualTo("DL12345")
        assertThat(vehicle.driverPhone).isEqualTo("+1234567890")
        assertThat(vehicle.driverEmail).isEqualTo("john@example.com")
        assertThat(vehicle.costPerKm).isEqualByComparingTo(BigDecimal("0.50"))
    }

    @Test
    fun `should generate unique guid for each vehicle`() {
        val vehicles = (1..5).map {
            VehicleEntity(
                organizationId = UUID.randomUUID(),
                name = "Vehicle $it",
                licensePlate = "VEH$it",
                weightCapacity = BigDecimal("1000.00"),
                volumeCapacity = BigDecimal("50.00"),
                startLat = 40.7128,
                startLon = -74.0060,
                endLat = 40.7128,
                endLon = -74.0060,
                availableFrom = LocalTime.of(8, 0),
                availableUntil = LocalTime.of(18, 0)
            )
        }

        val guids = vehicles.map { it.guid }.toSet()
        assertThat(guids).hasSize(5)
    }
}
