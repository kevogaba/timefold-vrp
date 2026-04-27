package com.vrp.infrastructure.persistence.mapper

import com.vrp.domain.model.Driver
import com.vrp.domain.model.Location
import com.vrp.domain.model.Vehicle
import com.vrp.infrastructure.persistence.entity.VehicleEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID

class VehicleMapperTest {

    private val mapper = VehicleMapper()

    @Test
    fun `should map entity to domain with driver`() {
        val organizationId = UUID.randomUUID()
        val driverId = UUID.randomUUID()

        val entity = VehicleEntity(
            id = UUID.randomUUID(),
            organizationId = organizationId,
            name = "Van 1",
            licensePlate = "ABC123",
            weightCapacity = BigDecimal("1000.00"),
            volumeCapacity = BigDecimal("50.00"),
            startLat = 40.7128,
            startLon = -74.0060,
            endLat = 40.7589,
            endLon = -73.9851,
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            driverId = driverId,
            driverName = "John Doe",
            driverLicense = "DL123",
            driverPhone = "+1234567890",
            driverEmail = "john@example.com",
            costPerKm = BigDecimal("0.50")
        )

        val domain = mapper.toDomain(entity)

        assertThat(domain.id).isEqualTo(entity.id)
        assertThat(domain.name).isEqualTo("Van 1")
        assertThat(domain.licensePlate).isEqualTo("ABC123")
        assertThat(domain.driver).isNotNull
        assertThat(domain.driver?.id).isEqualTo(driverId)
        assertThat(domain.driver?.name).isEqualTo("John Doe")
    }

    @Test
    fun `should map entity to domain without driver`() {
        val entity = VehicleEntity(
            organizationId = UUID.randomUUID(),
            name = "Van 1",
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

        val domain = mapper.toDomain(entity)

        assertThat(domain.driver).isNull()
    }

    @Test
    fun `should map domain to entity`() {
        val vehicleId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()

        val domain = Vehicle(
            id = vehicleId,
            organizationId = organizationId,
            name = "Van 1",
            licensePlate = "ABC123",
            weightCapacity = BigDecimal("1000.00"),
            volumeCapacity = BigDecimal("50.00"),
            startLocation = Location(40.7128, -74.0060),
            endLocation = Location(40.7589, -73.9851),
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            driver = Driver(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                name = "John Doe",
                licenseNumber = "DL123",
                phoneNumber = "+1234567890",
                email = "john@example.com"
            ),
            costPerKm = BigDecimal("0.50")
        )

        val entity = mapper.toEntity(domain)

        assertThat(entity.id).isNotNull()
        assertThat(entity.name).isEqualTo("Van 1")
        assertThat(entity.driverName).isEqualTo("John Doe")
    }
}
