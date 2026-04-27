package com.vrp.infrastructure.persistence.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class OrderEntityTest {

    @Test
    fun `should create order entity with default values`() {
        val organizationId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val timeWindowStart = LocalDateTime.now()
        val timeWindowEnd = timeWindowStart.plusHours(2)

        val order = OrderEntity(
            organizationId = organizationId,
            customerId = customerId,
            customerName = "Test Customer",
            customerLat = 40.7128,
            customerLon = -74.0060,
            deliveryLat = 40.7589,
            deliveryLon = -73.9851,
            timeWindowStart = timeWindowStart,
            timeWindowEnd = timeWindowEnd,
            serviceDurationMinutes = 30
        )

        assertThat(order.id).isNotNull()
        assertThat(order.guid).isNotNull()
        assertThat(order.organizationId).isEqualTo(organizationId)
        assertThat(order.customerId).isEqualTo(customerId)
        assertThat(order.customerName).isEqualTo("Test Customer")
        assertThat(order.priority).isEqualTo(0)
        assertThat(order.lineItems).isEmpty()
        assertThat(order.createdAt).isNotNull()
        assertThat(order.updatedAt).isNotNull()
    }

    @Test
    fun `should create order with all optional fields`() {
        val order = OrderEntity(
            organizationId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            customerName = "Test Customer",
            customerPhone = "+1234567890",
            customerEmail = "test@example.com",
            customerLat = 40.7128,
            customerLon = -74.0060,
            pickupLat = 40.7000,
            pickupLon = -74.0000,
            deliveryLat = 40.7589,
            deliveryLon = -73.9851,
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30,
            priority = 5,
            notes = "Special delivery instructions"
        )

        assertThat(order.customerPhone).isEqualTo("+1234567890")
        assertThat(order.customerEmail).isEqualTo("test@example.com")
        assertThat(order.pickupLat).isEqualTo(40.7000)
        assertThat(order.pickupLon).isEqualTo(-74.0000)
        assertThat(order.priority).isEqualTo(5)
        assertThat(order.notes).isEqualTo("Special delivery instructions")
    }

    @Test
    fun `should generate unique guid for each order`() {
        val orders = (1..10).map {
            OrderEntity(
                organizationId = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                customerName = "Customer $it",
                customerLat = 40.7128,
                customerLon = -74.0060,
                deliveryLat = 40.7589,
                deliveryLon = -73.9851,
                timeWindowStart = LocalDateTime.now(),
                timeWindowEnd = LocalDateTime.now().plusHours(2),
                serviceDurationMinutes = 30
            )
        }

        val guids = orders.map { it.guid }.toSet()
        assertThat(guids).hasSize(10)
    }
}
