package com.vrp.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrderTest {

    @Test
    fun `should create valid order`() {
        val customer = Customer(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            name = "Test Customer",
            phoneNumber = "1234567890",
            email = "test@example.com",
            location = Location(37.7749, -122.4194)
        )

        val lineItem = LineItem(
            id = UUID.randomUUID(),
            name = "Test Item",
            quantity = 10,
            weight = BigDecimal("100.00"),
            volume = BigDecimal("50.00"),
            price = BigDecimal("1000.00")
        )

        val order = Order(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            customer = customer,
            lineItems = listOf(lineItem),
            pickupLocation = null,
            deliveryLocation = Location(37.8044, -122.2712),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30
        )

        assertEquals(BigDecimal("1000.00"), order.totalWeight)
        assertEquals(BigDecimal("500.00"), order.totalVolume)
        assertEquals(BigDecimal("10000.00"), order.totalValue)
    }

    @Test
    fun `should fail when time window end is before start`() {
        assertThrows<IllegalArgumentException> {
            Order(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                customer = Customer(
                    id = UUID.randomUUID(),
                    organizationId = UUID.randomUUID(),
                    name = "Test",
                    phoneNumber = null,
                    email = null,
                    location = Location(0.0, 0.0)
                ),
                lineItems = listOf(
                    LineItem(
                        id = UUID.randomUUID(),
                        name = "Item",
                        quantity = 1,
                        weight = BigDecimal.ONE,
                        volume = BigDecimal.ONE,
                        price = BigDecimal.ONE
                    )
                ),
                pickupLocation = null,
                deliveryLocation = Location(0.0, 0.0),
                timeWindowStart = LocalDateTime.now(),
                timeWindowEnd = LocalDateTime.now().minusHours(1),
                serviceDurationMinutes = 30
            )
        }
    }

    @Test
    fun `should fail when order has no line items`() {
        assertThrows<IllegalArgumentException> {
            Order(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                customer = Customer(
                    id = UUID.randomUUID(),
                    organizationId = UUID.randomUUID(),
                    name = "Test",
                    phoneNumber = null,
                    email = null,
                    location = Location(0.0, 0.0)
                ),
                lineItems = emptyList(),
                pickupLocation = null,
                deliveryLocation = Location(0.0, 0.0),
                timeWindowStart = LocalDateTime.now(),
                timeWindowEnd = LocalDateTime.now().plusHours(1),
                serviceDurationMinutes = 30
            )
        }
    }
}
