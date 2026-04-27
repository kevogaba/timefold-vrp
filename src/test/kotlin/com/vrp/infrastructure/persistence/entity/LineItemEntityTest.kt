package com.vrp.infrastructure.persistence.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class LineItemEntityTest {

    @Test
    fun `should create line item with all fields`() {
        val order = OrderEntity(
            organizationId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            customerName = "Test Customer",
            customerLat = 40.7128,
            customerLon = -74.0060,
            deliveryLat = 40.7589,
            deliveryLon = -73.9851,
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30
        )

        val lineItem = LineItemEntity(
            order = order,
            name = "Product A",
            quantity = 5,
            weight = BigDecimal("10.50"),
            volume = BigDecimal("2.00"),
            price = BigDecimal("99.99")
        )

        assertThat(lineItem.id).isNotNull()
        assertThat(lineItem.guid).isNotNull()
        assertThat(lineItem.order).isEqualTo(order)
        assertThat(lineItem.name).isEqualTo("Product A")
        assertThat(lineItem.quantity).isEqualTo(5)
        assertThat(lineItem.weight).isEqualByComparingTo(BigDecimal("10.50"))
        assertThat(lineItem.volume).isEqualByComparingTo(BigDecimal("2.00"))
        assertThat(lineItem.price).isEqualByComparingTo(BigDecimal("99.99"))
    }

    @Test
    fun `should generate unique guid for each line item`() {
        val order = OrderEntity(
            organizationId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            customerName = "Test Customer",
            customerLat = 40.7128,
            customerLon = -74.0060,
            deliveryLat = 40.7589,
            deliveryLon = -73.9851,
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30
        )

        val lineItems = (1..3).map {
            LineItemEntity(
                order = order,
                name = "Item $it",
                quantity = it,
                weight = BigDecimal("$it.00"),
                volume = BigDecimal("1.00"),
                price = BigDecimal("10.00")
            )
        }

        val guids = lineItems.map { it.guid }.toSet()
        assertThat(guids).hasSize(3)
    }
}
