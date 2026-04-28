package com.vrp.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID
import kotlin.test.assertEquals

class VehicleTest {
    @Test
    fun `should create valid vehicle`() {
        val vehicle =
            Vehicle(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                name = "Truck 1",
                licensePlate = "ABC-123",
                weightCapacity = BigDecimal("1000.00"),
                volumeCapacity = BigDecimal("500.00"),
                startLocation = Location(0.0, 0.0),
                endLocation = Location(1.0, 1.0),
                availableFrom = LocalTime.of(8, 0),
                availableUntil = LocalTime.of(18, 0),
                driver = null,
                costPerKm = BigDecimal("2.50")
            )

        assertEquals("Truck 1", vehicle.name)
        assertEquals(BigDecimal("1000.00"), vehicle.weightCapacity)
    }

    @Test
    fun `should reject empty name`() {
        assertThrows<IllegalArgumentException> {
            Vehicle(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                name = "",
                licensePlate = "ABC-123",
                weightCapacity = BigDecimal("1000.00"),
                volumeCapacity = BigDecimal("500.00"),
                startLocation = Location(0.0, 0.0),
                endLocation = Location(1.0, 1.0),
                availableFrom = LocalTime.of(8, 0),
                availableUntil = LocalTime.of(18, 0),
                driver = null
            )
        }
    }

    @Test
    fun `should reject zero weight capacity`() {
        assertThrows<IllegalArgumentException> {
            Vehicle(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                name = "Truck 1",
                licensePlate = "ABC-123",
                weightCapacity = BigDecimal.ZERO,
                volumeCapacity = BigDecimal("500.00"),
                startLocation = Location(0.0, 0.0),
                endLocation = Location(1.0, 1.0),
                availableFrom = LocalTime.of(8, 0),
                availableUntil = LocalTime.of(18, 0),
                driver = null
            )
        }
    }

    @Test
    fun `should reject available until before available from`() {
        assertThrows<IllegalArgumentException> {
            Vehicle(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                name = "Truck 1",
                licensePlate = "ABC-123",
                weightCapacity = BigDecimal("1000.00"),
                volumeCapacity = BigDecimal("500.00"),
                startLocation = Location(0.0, 0.0),
                endLocation = Location(1.0, 1.0),
                availableFrom = LocalTime.of(18, 0),
                availableUntil = LocalTime.of(8, 0),
                driver = null
            )
        }
    }
}
