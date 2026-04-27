package com.vrp.infrastructure.persistence.adapter

import com.vrp.domain.model.*
import com.vrp.infrastructure.persistence.entity.OrderEntity
import com.vrp.infrastructure.persistence.mapper.OrderMapper
import com.vrp.infrastructure.persistence.repository.OrderJpaRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class OrderRepositoryAdapterTest {

    private lateinit var jpaRepository: OrderJpaRepository
    private lateinit var mapper: OrderMapper
    private lateinit var adapter: OrderRepositoryAdapter

    @BeforeEach
    fun setup() {
        jpaRepository = mockk()
        mapper = OrderMapper()
        adapter = OrderRepositoryAdapter(jpaRepository, mapper)
    }

    @Test
    fun `should save order successfully`() {
        val order = createOrder()
        val entity = mapper.toEntity(order)

        every { jpaRepository.save(any()) } returns entity

        val result = adapter.save(order)

        assertThat(result.id).isNotNull()
        verify { jpaRepository.save(any()) }
    }

    @Test
    fun `should find order by id and organizationId`() {
        val organizationId = UUID.randomUUID()
        val orderId = UUID.randomUUID()
        val entity = createOrderEntity(organizationId = organizationId)

        every { jpaRepository.findByIdAndOrganizationId(orderId, organizationId) } returns entity

        val result = adapter.findById(orderId, organizationId)

        assertThat(result).isNotNull
        assertThat(result?.organizationId).isEqualTo(organizationId)
        verify { jpaRepository.findByIdAndOrganizationId(orderId, organizationId) }
    }

    @Test
    fun `should return null when order not found`() {
        val organizationId = UUID.randomUUID()
        val orderId = UUID.randomUUID()

        every { jpaRepository.findByIdAndOrganizationId(orderId, organizationId) } returns null

        val result = adapter.findById(orderId, organizationId)

        assertThat(result).isNull()
    }

    @Test
    fun `should find all orders by organizationId`() {
        val organizationId = UUID.randomUUID()
        val entities = listOf(
            createOrderEntity(organizationId = organizationId),
            createOrderEntity(organizationId = organizationId)
        )

        every { jpaRepository.findAllByOrganizationId(organizationId) } returns entities

        val results = adapter.findAllByOrganizationId(organizationId)

        assertThat(results).hasSize(2)
        assertThat(results.all { it.organizationId == organizationId }).isTrue()
        verify { jpaRepository.findAllByOrganizationId(organizationId) }
    }

    @Test
    fun `should find all orders by ids and organizationId`() {
        val organizationId = UUID.randomUUID()
        val ids = listOf(UUID.randomUUID(), UUID.randomUUID())
        val entities = ids.map { createOrderEntity(organizationId = organizationId) }

        every { jpaRepository.findAllByIdInAndOrganizationId(ids, organizationId) } returns entities

        val results = adapter.findAllByIds(ids, organizationId)

        assertThat(results).hasSize(2)
        verify { jpaRepository.findAllByIdInAndOrganizationId(ids, organizationId) }
    }

    @Test
    fun `should delete order by id and organizationId`() {
        val organizationId = UUID.randomUUID()
        val orderId = UUID.randomUUID()

        every { jpaRepository.deleteByIdAndOrganizationId(orderId, organizationId) } just Runs

        adapter.deleteById(orderId, organizationId)

        verify { jpaRepository.deleteByIdAndOrganizationId(orderId, organizationId) }
    }

    private fun createOrder(): Order {
        return Order(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            customer = Customer(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                name = "Test Customer",
                phoneNumber = "+1234567890",
                email = "test@example.com",
                location = Location(40.7128, -74.0060)
            ),
            lineItems = listOf(
                LineItem(
                    id = UUID.randomUUID(),
                    name = "Test Item",
                    quantity = 1,
                    weight = BigDecimal("10.0"),
                    volume = BigDecimal("1.0"),
                    price = BigDecimal("100.00")
                )
            ),
            pickupLocation = null,
            deliveryLocation = Location(40.7589, -73.9851),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(4),
            serviceDurationMinutes = 30,
            priority = 0,
            notes = null,
            createdAt = LocalDateTime.now()
        )
    }

    private fun createOrderEntity(organizationId: UUID = UUID.randomUUID()): OrderEntity {
        val entity = OrderEntity(
            organizationId = organizationId,
            customerId = UUID.randomUUID(),
            customerName = "Test Customer",
            customerLat = 40.7128,
            customerLon = -74.0060,
            deliveryLat = 40.7589,
            deliveryLon = -73.9851,
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(4),
            serviceDurationMinutes = 30
        )
        // Add at least one line item to satisfy domain validation
        entity.lineItems.add(
            com.vrp.infrastructure.persistence.entity.LineItemEntity(
                order = entity,
                name = "Test Item",
                quantity = 1,
                weight = BigDecimal("10.0"),
                volume = BigDecimal("1.0"),
                price = BigDecimal("100.00")
            )
        )
        return entity
    }
}
