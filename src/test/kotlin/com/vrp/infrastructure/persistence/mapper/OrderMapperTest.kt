package com.vrp.infrastructure.persistence.mapper

import com.vrp.domain.model.*
import com.vrp.infrastructure.persistence.entity.LineItemEntity
import com.vrp.infrastructure.persistence.entity.OrderEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class OrderMapperTest {
    private val mapper = OrderMapper()

    @Test
    fun `should map entity to domain`() {
        val organizationId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val timeWindowStart = LocalDateTime.now()
        val timeWindowEnd = timeWindowStart.plusHours(2)

        val entity =
            OrderEntity(
                organizationId = organizationId,
                customerId = customerId,
                customerName = "Test Customer",
                customerPhone = "+1234567890",
                customerEmail = "test@example.com",
                customerLat = 40.7128,
                customerLon = -74.0060,
                pickupLat = 40.7000,
                pickupLon = -74.0000,
                deliveryLat = 40.7589,
                deliveryLon = -73.9851,
                timeWindowStart = timeWindowStart,
                timeWindowEnd = timeWindowEnd,
                serviceDurationMinutes = 30,
                priority = 5,
                notes = "Test notes"
            )

        // Add at least one line item
        entity.lineItems.add(
            LineItemEntity(
                order = entity,
                name = "Test Product",
                quantity = 1,
                weight = BigDecimal("5.0"),
                volume = BigDecimal("1.0"),
                price = BigDecimal("50.00")
            )
        )

        val domain = mapper.toDomain(entity)

        assertThat(domain.id).isEqualTo(entity.id)
        assertThat(domain.organizationId).isEqualTo(organizationId)
        assertThat(domain.customer.id).isEqualTo(customerId)
        assertThat(domain.customer.name).isEqualTo("Test Customer")
        assertThat(domain.customer.phoneNumber).isEqualTo("+1234567890")
        assertThat(domain.customer.email).isEqualTo("test@example.com")
        assertThat(domain.customer.location.latitude).isEqualTo(40.7128)
        assertThat(domain.customer.location.longitude).isEqualTo(-74.0060)
        assertThat(domain.pickupLocation).isNotNull
        assertThat(domain.pickupLocation?.latitude).isEqualTo(40.7000)
        assertThat(domain.pickupLocation?.longitude).isEqualTo(-74.0000)
        assertThat(domain.deliveryLocation.latitude).isEqualTo(40.7589)
        assertThat(domain.deliveryLocation.longitude).isEqualTo(-73.9851)
        assertThat(domain.timeWindowStart).isEqualTo(timeWindowStart)
        assertThat(domain.timeWindowEnd).isEqualTo(timeWindowEnd)
        assertThat(domain.serviceDurationMinutes).isEqualTo(30)
        assertThat(domain.priority).isEqualTo(5)
        assertThat(domain.notes).isEqualTo("Test notes")
        assertThat(domain.lineItems).hasSize(1)
        assertThat(domain.lineItems[0].name).isEqualTo("Test Product")
    }

    @Test
    fun `should map domain to entity`() {
        val orderId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()
        val customerId = UUID.randomUUID()

        val domain =
            Order(
                id = orderId,
                organizationId = organizationId,
                customer =
                    Customer(
                        id = customerId,
                        organizationId = organizationId,
                        name = "Test Customer",
                        phoneNumber = "+1234567890",
                        email = "test@example.com",
                        location = Location(40.7128, -74.0060)
                    ),
                lineItems =
                    listOf(
                        LineItem(
                            id = UUID.randomUUID(),
                            name = "Product A",
                            quantity = 2,
                            weight = BigDecimal("10.0"),
                            volume = BigDecimal("2.0"),
                            price = BigDecimal("99.99")
                        )
                    ),
                pickupLocation = Location(40.7000, -74.0000),
                deliveryLocation = Location(40.7589, -73.9851),
                timeWindowStart = LocalDateTime.now(),
                timeWindowEnd = LocalDateTime.now().plusHours(2),
                serviceDurationMinutes = 30,
                priority = 5,
                notes = "Test notes",
                createdAt = LocalDateTime.now()
            )

        val entity = mapper.toEntity(domain)

        assertThat(entity.id).isNotNull()
        assertThat(entity.organizationId).isEqualTo(organizationId)
        assertThat(entity.customerId).isEqualTo(customerId)
        assertThat(entity.customerName).isEqualTo("Test Customer")
        assertThat(entity.lineItems).hasSize(1)
        assertThat(entity.lineItems[0].name).isEqualTo("Product A")
    }

    @Test
    fun `should handle null pickup location`() {
        val entity =
            OrderEntity(
                organizationId = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                customerName = "Test",
                customerLat = 40.7128,
                customerLon = -74.0060,
                deliveryLat = 40.7589,
                deliveryLon = -73.9851,
                timeWindowStart = LocalDateTime.now(),
                timeWindowEnd = LocalDateTime.now().plusHours(2),
                serviceDurationMinutes = 30
            )

        // Add at least one line item
        entity.lineItems.add(
            LineItemEntity(
                order = entity,
                name = "Test Product",
                quantity = 1,
                weight = BigDecimal("5.0"),
                volume = BigDecimal("1.0"),
                price = BigDecimal("50.00")
            )
        )

        val domain = mapper.toDomain(entity)

        assertThat(domain.pickupLocation).isNull()
        assertThat(domain.lineItems).hasSize(1)
    }
}
