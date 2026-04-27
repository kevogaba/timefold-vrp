package com.vrp.infrastructure.persistence

import com.vrp.domain.model.*
import com.vrp.infrastructure.persistence.repository.OrderJpaRepository
import com.vrp.infrastructure.persistence.repository.VehicleJpaRepository
import com.vrp.infrastructure.persistence.adapter.OrderRepositoryAdapter
import com.vrp.infrastructure.persistence.adapter.VehicleRepositoryAdapter
import com.vrp.infrastructure.persistence.mapper.OrderMapper
import com.vrp.infrastructure.persistence.mapper.VehicleMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(OrderMapper::class, VehicleMapper::class, OrderRepositoryAdapter::class, VehicleRepositoryAdapter::class)
@ActiveProfiles("test")
class PersistenceIntegrationTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:17-alpine")
            .withDatabaseName("vrp_test")
            .withUsername("test")
            .withPassword("test")
    }

    @Autowired
    private lateinit var orderRepository: OrderRepositoryAdapter

    @Autowired
    private lateinit var vehicleRepository: VehicleRepositoryAdapter

    @Test
    fun `should save and retrieve order`() {
        val organizationId = UUID.randomUUID()
        val customer = Customer(
            id = UUID.randomUUID(),
            organizationId = organizationId,
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
            organizationId = organizationId,
            customer = customer,
            lineItems = listOf(lineItem),
            pickupLocation = null,
            deliveryLocation = Location(37.8044, -122.2712),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30
        )

        val saved = orderRepository.save(order)
        assertNotNull(saved)
        assertEquals(order.id, saved.id)

        val retrieved = orderRepository.findById(order.id, organizationId)
        assertNotNull(retrieved)
        assertEquals(order.customer.name, retrieved.customer.name)
        assertEquals(1, retrieved.lineItems.size)
    }

    @Test
    fun `should save and retrieve vehicle`() {
        val organizationId = UUID.randomUUID()
        val driver = Driver(
            id = UUID.randomUUID(),
            organizationId = organizationId,
            name = "Test Driver",
            licenseNumber = "DL12345",
            phoneNumber = "9876543210",
            email = "driver@example.com"
        )

        val vehicle = Vehicle(
            id = UUID.randomUUID(),
            organizationId = organizationId,
            name = "Vehicle 1",
            licensePlate = "ABC-1234",
            weightCapacity = BigDecimal("1000.00"),
            volumeCapacity = BigDecimal("500.00"),
            startLocation = Location(37.7749, -122.4194),
            endLocation = Location(37.7749, -122.4194),
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            driver = driver,
            costPerKm = BigDecimal("2.50")
        )

        val saved = vehicleRepository.save(vehicle)
        assertNotNull(saved)
        assertEquals(vehicle.id, saved.id)

        val retrieved = vehicleRepository.findById(vehicle.id, organizationId)
        assertNotNull(retrieved)
        assertEquals(vehicle.name, retrieved.name)
        assertEquals(vehicle.driver?.name, retrieved.driver?.name)
    }

    @Test
    fun `should filter by organization ID`() {
        val org1 = UUID.randomUUID()
        val org2 = UUID.randomUUID()

        val customer1 = Customer(
            id = UUID.randomUUID(),
            organizationId = org1,
            name = "Customer Org 1",
            phoneNumber = null,
            email = null,
            location = Location(0.0, 0.0)
        )

        val order1 = Order(
            id = UUID.randomUUID(),
            organizationId = org1,
            customer = customer1,
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
            deliveryLocation = Location(1.0, 1.0),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(1),
            serviceDurationMinutes = 30
        )

        orderRepository.save(order1)

        val org1Orders = orderRepository.findAllByOrganizationId(org1)
        val org2Orders = orderRepository.findAllByOrganizationId(org2)

        assertEquals(1, org1Orders.size)
        assertEquals(0, org2Orders.size)
    }
}
