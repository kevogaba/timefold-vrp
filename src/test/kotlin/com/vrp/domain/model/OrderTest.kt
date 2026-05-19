package com.vrp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class OrderTest {
    @Test
    fun `should calculate total weight correctly`() {
        val order =
            createValidOrder(
                lineItems =
                    listOf(
                        LineItem(
                            id = UUID.randomUUID(),
                            name = "Item 1",
                            quantity = 2,
                            weight = BigDecimal("10.0"),
                            volume = BigDecimal("1.0"),
                            price = BigDecimal("50.00")
                        ),
                        LineItem(
                            id = UUID.randomUUID(),
                            name = "Item 2",
                            quantity = 3,
                            weight = BigDecimal("5.0"),
                            volume = BigDecimal("0.5"),
                            price = BigDecimal("25.00")
                        )
                    )
            )

        // 2 * 10.0 + 3 * 5.0 = 20.0 + 15.0 = 35.0
        assertThat(order.totalWeight).isEqualByComparingTo(BigDecimal("35.0"))
    }

    @Test
    fun `should calculate total volume correctly`() {
        val order =
            createValidOrder(
                lineItems =
                    listOf(
                        LineItem(
                            id = UUID.randomUUID(),
                            name = "Item 1",
                            quantity = 2,
                            weight = BigDecimal("10.0"),
                            volume = BigDecimal("2.0"),
                            price = BigDecimal("50.00")
                        ),
                        LineItem(
                            id = UUID.randomUUID(),
                            name = "Item 2",
                            quantity = 4,
                            weight = BigDecimal("5.0"),
                            volume = BigDecimal("1.5"),
                            price = BigDecimal("25.00")
                        )
                    )
            )

        // 2 * 2.0 + 4 * 1.5 = 4.0 + 6.0 = 10.0
        assertThat(order.totalVolume).isEqualByComparingTo(BigDecimal("10.0"))
    }

    @Test
    fun `should calculate total value correctly`() {
        val order =
            createValidOrder(
                lineItems =
                    listOf(
                        LineItem(
                            id = UUID.randomUUID(),
                            name = "Item 1",
                            quantity = 2,
                            weight = BigDecimal("10.0"),
                            volume = BigDecimal("1.0"),
                            price = BigDecimal("50.00")
                        ),
                        LineItem(
                            id = UUID.randomUUID(),
                            name = "Item 2",
                            quantity = 3,
                            weight = BigDecimal("5.0"),
                            volume = BigDecimal("0.5"),
                            price = BigDecimal("30.00")
                        )
                    )
            )

        // 2 * 50.00 + 3 * 30.00 = 100.00 + 90.00 = 190.00
        assertThat(order.totalValue).isEqualByComparingTo(BigDecimal("190.00"))
    }

    @Test
    fun `should throw exception when line items are empty`() {
        assertThatThrownBy {
            createValidOrder(lineItems = emptyList())
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Order must have at least one line item")
    }

    @Test
    fun `should throw exception when time window end is before start`() {
        val now = LocalDateTime.now()
        assertThatThrownBy {
            createValidOrder(
                timeWindowStart = now,
                timeWindowEnd = now.minusHours(1)
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Time window end must be after start")
    }

    @Test
    fun `should throw exception when service duration is negative`() {
        assertThatThrownBy {
            createValidOrder(serviceDurationMinutes = -10)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Service duration cannot be negative")
    }

    @Test
    fun `should throw exception when priority is negative`() {
        assertThatThrownBy {
            createValidOrder(priority = -1)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Priority cannot be negative")
    }

    @Test
    fun `should accept zero service duration`() {
        val order = createValidOrder(serviceDurationMinutes = 0)
        assertThat(order.serviceDurationMinutes).isEqualTo(0)
    }

    @Test
    fun `should accept zero priority`() {
        val order = createValidOrder(priority = 0)
        assertThat(order.priority).isEqualTo(0)
    }

    @Test
    fun `should accept null pickup location`() {
        val order = createValidOrder(pickupLocation = null)
        assertThat(order.pickupLocation).isNull()
    }

    @Test
    fun `should accept null notes`() {
        val order = createValidOrder(notes = null)
        assertThat(order.notes).isNull()
    }

    private fun createValidOrder(
        lineItems: List<LineItem> =
            listOf(
                LineItem(
                    id = UUID.randomUUID(),
                    name = "Default Item",
                    quantity = 1,
                    weight = BigDecimal("1.0"),
                    volume = BigDecimal("1.0"),
                    price = BigDecimal("10.00")
                )
            ),
        pickupLocation: Location? = Location(40.7000, -74.0000),
        notes: String? = "Test notes",
        timeWindowStart: LocalDateTime = LocalDateTime.now(),
        timeWindowEnd: LocalDateTime = LocalDateTime.now().plusHours(2),
        serviceDurationMinutes: Int = 30,
        priority: Int = 0
    ): Order =
        Order(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            customer =
                Customer(
                    id = UUID.randomUUID(),
                    organizationId = UUID.randomUUID(),
                    name = "Test Customer",
                    phoneNumber = "+1234567890",
                    email = "test@example.com",
                    location = Location(40.7128, -74.0060)
                ),
            lineItems = lineItems,
            pickupLocation = pickupLocation,
            deliveryLocation = Location(40.7589, -73.9851),
            timeWindowStart = timeWindowStart,
            timeWindowEnd = timeWindowEnd,
            serviceDurationMinutes = serviceDurationMinutes,
            priority = priority,
            notes = notes,
            createdAt = LocalDateTime.now()
        )
}
