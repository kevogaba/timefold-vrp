package com.vrp.infrastructure.temporal.activity.impl

import ai.timefold.solver.core.api.score.HardSoftScore
import ai.timefold.solver.core.api.solver.SolverStatus
import com.vrp.domain.model.*
import com.vrp.domain.port.OrderRepository
import com.vrp.domain.port.TripRepository
import com.vrp.domain.port.VehicleRepository
import com.vrp.infrastructure.temporal.activity.SolverResult
import com.vrp.solver.domain.VrpSolution
import com.vrp.solver.service.VrpSolverService
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class FetchOrdersActivityImplTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var activity: FetchOrdersActivityImpl

    @BeforeEach
    fun setup() {
        orderRepository = mockk()
        activity = FetchOrdersActivityImpl(orderRepository)
    }

    @Test
    fun `should fetch orders by ids and organization id`() {
        val organizationId = UUID.randomUUID()
        val orderIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val orders = orderIds.map { createOrder(id = it, organizationId = organizationId) }

        every { orderRepository.findAllByIds(orderIds, organizationId) } returns orders

        val result = activity.fetchOrders(organizationId, orderIds)

        assertThat(result).hasSize(2)
        assertThat(result.map { it.id }).containsExactlyInAnyOrderElementsOf(orderIds)
        verify { orderRepository.findAllByIds(orderIds, organizationId) }
    }

    @Test
    fun `should return empty list when no orders found`() {
        val organizationId = UUID.randomUUID()
        val orderIds = listOf(UUID.randomUUID())

        every { orderRepository.findAllByIds(orderIds, organizationId) } returns emptyList()

        val result = activity.fetchOrders(organizationId, orderIds)

        assertThat(result).isEmpty()
    }

    private fun createOrder(
        id: UUID = UUID.randomUUID(),
        organizationId: UUID = UUID.randomUUID()
    ): Order =
        Order(
            id = id,
            organizationId = organizationId,
            customer = createCustomer(organizationId),
            pickupLocation = null,
            deliveryLocation = Location(40.7589, -73.9851),
            lineItems =
                listOf(
                    LineItem(
                        id = UUID.randomUUID(),
                        name = "Item",
                        quantity = 1,
                        weight = BigDecimal("10"),
                        volume = BigDecimal("1"),
                        price = BigDecimal("100")
                    )
                ),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30
        )

    private fun createCustomer(organizationId: UUID): Customer =
        Customer(
            id = UUID.randomUUID(),
            organizationId = organizationId,
            name = "Test Customer",
            phoneNumber = "+1234567890",
            email = "test@example.com",
            location = Location(40.7589, -73.9851)
        )
}

class FetchVehiclesActivityImplTest {
    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var activity: FetchVehiclesActivityImpl

    @BeforeEach
    fun setup() {
        vehicleRepository = mockk()
        activity = FetchVehiclesActivityImpl(vehicleRepository)
    }

    @Test
    fun `should fetch vehicles by ids and organization id`() {
        val organizationId = UUID.randomUUID()
        val vehicleIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val vehicles = vehicleIds.map { createVehicle(id = it, organizationId = organizationId) }

        every { vehicleRepository.findAllByIds(vehicleIds, organizationId) } returns vehicles

        val result = activity.fetchVehicles(organizationId, vehicleIds)

        assertThat(result).hasSize(2)
        assertThat(result.map { it.id }).containsExactlyInAnyOrderElementsOf(vehicleIds)
        verify { vehicleRepository.findAllByIds(vehicleIds, organizationId) }
    }

    @Test
    fun `should return empty list when no vehicles found`() {
        val organizationId = UUID.randomUUID()
        val vehicleIds = listOf(UUID.randomUUID())

        every { vehicleRepository.findAllByIds(vehicleIds, organizationId) } returns emptyList()

        val result = activity.fetchVehicles(organizationId, vehicleIds)

        assertThat(result).isEmpty()
    }

    private fun createVehicle(
        id: UUID = UUID.randomUUID(),
        organizationId: UUID = UUID.randomUUID()
    ): Vehicle =
        Vehicle(
            id = id,
            organizationId = organizationId,
            name = "Test Vehicle",
            licensePlate = "ABC-123",
            weightCapacity = BigDecimal("1000"),
            volumeCapacity = BigDecimal("50"),
            startLocation = Location(40.7128, -74.0060),
            endLocation = Location(40.7128, -74.0060),
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            driver = null,
            costPerKm = BigDecimal("0.50")
        )
}

class PersistSolutionActivityImplTest {
    private lateinit var tripRepository: TripRepository
    private lateinit var activity: PersistSolutionActivityImpl

    @BeforeEach
    fun setup() {
        tripRepository = mockk()
        activity = PersistSolutionActivityImpl(tripRepository)
    }

    @Test
    fun `should persist trips from solver result`() {
        val jobId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()
        val trips = listOf(createTrip(), createTrip())
        val result = SolverResult(hardScore = 0, softScore = -1000, trips = trips)

        every { tripRepository.saveAll(trips) } returns trips

        activity.persist(jobId, organizationId, result)

        verify { tripRepository.saveAll(trips) }
    }

    @Test
    fun `should handle empty trip list`() {
        val jobId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()
        val result = SolverResult(hardScore = 0, softScore = 0, trips = emptyList())

        every { tripRepository.saveAll(emptyList()) } returns emptyList()

        activity.persist(jobId, organizationId, result)

        verify { tripRepository.saveAll(emptyList()) }
    }

    private fun createTrip(): Trip =
        Trip(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            jobId = UUID.randomUUID(),
            vehicleId = UUID.randomUUID(),
            visits =
                listOf(
                    Visit(
                        id = UUID.randomUUID(),
                        orderId = UUID.randomUUID(),
                        location = Location(40.7589, -73.9851),
                        arrivalTime = LocalDateTime.now(),
                        departureTime = LocalDateTime.now().plusMinutes(30),
                        sequenceNumber = 0
                    )
                ),
            totalDistanceMeters = 10000L,
            totalDurationMinutes = 60,
            createdAt = LocalDateTime.now()
        )
}

class RunSolverActivityImplTest {
    private lateinit var solverService: VrpSolverService
    private lateinit var activity: RunSolverActivityImpl

    @BeforeEach
    fun setup() {
        solverService = mockk()
        activity = RunSolverActivityImpl(solverService)
    }

    @Test
    fun `should run solver and return result`() {
        val jobId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()
        val orders = listOf(createOrder())
        val vehicles = listOf(createVehicle())
        val solution = createSolution(jobId)

        every { solverService.solve(jobId, any()) } returns jobId
        every { solverService.getSolverStatus(jobId) } returnsMany
            listOf(
                SolverStatus.SOLVING_ACTIVE,
                SolverStatus.NOT_SOLVING
            )
        every { solverService.getFinalBestSolution(jobId) } returns solution

        val result = activity.runSolver(jobId, organizationId, orders, vehicles)

        assertThat(result.hardScore).isEqualTo(0)
        assertThat(result.softScore).isEqualTo(-1000)
        assertThat(result.trips).isNotEmpty
        verify { solverService.solve(jobId, any()) }
        verify(atLeast = 1) { solverService.getSolverStatus(jobId) }
        verify { solverService.getFinalBestSolution(jobId) }
    }

    @Test
    fun `should throw exception when no solution found`() {
        val jobId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()
        val orders = listOf(createOrder())
        val vehicles = listOf(createVehicle())

        every { solverService.solve(jobId, any()) } returns jobId
        every { solverService.getSolverStatus(jobId) } returns SolverStatus.NOT_SOLVING
        every { solverService.getFinalBestSolution(jobId) } returns null

        assertThatThrownBy {
            activity.runSolver(jobId, organizationId, orders, vehicles)
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("No solution found")
    }

    @Test
    fun `should throw exception when no score calculated`() {
        val jobId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()
        val orders = listOf(createOrder())
        val vehicles = listOf(createVehicle())
        val solutionWithoutScore = VrpSolution(jobId = jobId, score = null)

        every { solverService.solve(jobId, any()) } returns jobId
        every { solverService.getSolverStatus(jobId) } returns SolverStatus.NOT_SOLVING
        every { solverService.getFinalBestSolution(jobId) } returns solutionWithoutScore

        assertThatThrownBy {
            activity.runSolver(jobId, organizationId, orders, vehicles)
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("No score calculated")
    }

    private fun createOrder(): Order {
        val organizationId = UUID.randomUUID()
        return Order(
            id = UUID.randomUUID(),
            organizationId = organizationId,
            customer = createCustomer(organizationId),
            pickupLocation = null,
            deliveryLocation = Location(40.7589, -73.9851),
            lineItems =
                listOf(
                    LineItem(
                        id = UUID.randomUUID(),
                        name = "Item",
                        quantity = 1,
                        weight = BigDecimal("10"),
                        volume = BigDecimal("1"),
                        price = BigDecimal("100")
                    )
                ),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30
        )
    }

    private fun createCustomer(organizationId: UUID): Customer =
        Customer(
            id = UUID.randomUUID(),
            organizationId = organizationId,
            name = "Test Customer",
            phoneNumber = "+1234567890",
            email = "test@example.com",
            location = Location(40.7589, -73.9851)
        )

    private fun createVehicle(): Vehicle =
        Vehicle(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            name = "Test Vehicle",
            licensePlate = "ABC-123",
            weightCapacity = BigDecimal("1000"),
            volumeCapacity = BigDecimal("50"),
            startLocation = Location(40.7128, -74.0060),
            endLocation = Location(40.7128, -74.0060),
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            driver = null,
            costPerKm = BigDecimal("0.50")
        )

    private fun createSolution(jobId: UUID): VrpSolution {
        val vehicle =
            com.vrp.solver.domain.SolverVehicle(
                id = UUID.randomUUID(),
                name = "Vehicle 1",
                weightCapacity = BigDecimal("1000"),
                volumeCapacity = BigDecimal("50"),
                startLocation = Location(40.7128, -74.0060),
                endLocation = Location(40.7128, -74.0060),
                availableFrom = LocalTime.of(8, 0),
                availableUntil = LocalTime.of(18, 0)
            )
        val visit =
            com.vrp.solver.domain.SolverVisit(
                id = UUID.randomUUID(),
                orderId = UUID.randomUUID(),
                location = Location(40.7589, -73.9851),
                demandWeight = BigDecimal("10"),
                demandVolume = BigDecimal("1"),
                timeWindowStart = LocalDateTime.now(),
                timeWindowEnd = LocalDateTime.now().plusHours(2),
                serviceDurationMinutes = 30
            )
        vehicle.visits.add(visit)

        return VrpSolution(
            jobId = jobId,
            vehicles = mutableListOf(vehicle),
            visits = mutableListOf(visit),
            score = HardSoftScore.of(0, -1000)
        )
    }
}
