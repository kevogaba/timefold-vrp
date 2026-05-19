package com.vrp.solver.mapper

import com.vrp.domain.model.Customer
import com.vrp.domain.model.LineItem
import com.vrp.domain.model.Location
import com.vrp.domain.model.Order
import com.vrp.domain.model.Vehicle
import com.vrp.solver.distance.EuclideanDistanceCalculator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class SolverDomainMapperTest {
    private val distanceCalculator = EuclideanDistanceCalculator()
    private val mapper = SolverDomainMapper(distanceCalculator)

    @Test
    fun `should convert domain models to solver solution`() {
        val jobId = UUID.randomUUID()
        val orders = listOf(createOrder())
        val vehicles = listOf(createVehicle())

        val solution = mapper.toSolverSolution(jobId, orders, vehicles)

        assertThat(solution.jobId).isEqualTo(jobId)
        assertThat(solution.visits).hasSize(1)
        assertThat(solution.vehicles).hasSize(1)
        assertThat(solution.score).isNull()
    }

    @Test
    fun `should create visits from orders with correct properties`() {
        val order =
            createOrder(
                deliveryLocation = Location(40.7589, -73.9851),
                lineItems =
                    listOf(
                        LineItem(
                            id = UUID.randomUUID(),
                            name = "Item 1",
                            quantity = 2,
                            weight = BigDecimal("10.0"),
                            volume = BigDecimal("2.0"),
                            price = BigDecimal("50.00")
                        )
                    )
            )

        val solution = mapper.toSolverSolution(UUID.randomUUID(), listOf(order), listOf(createVehicle()))

        val visit = solution.visits.first()
        assertThat(visit.orderId).isEqualTo(order.id)
        assertThat(visit.location).isEqualTo(order.deliveryLocation)
        assertThat(visit.demandWeight).isEqualByComparingTo(BigDecimal("20.0")) // 2 * 10.0
        assertThat(visit.demandVolume).isEqualByComparingTo(BigDecimal("4.0")) // 2 * 2.0
        assertThat(visit.isDelivery).isTrue()
        assertThat(visit.isPickup).isFalse()
    }

    @Test
    fun `should create solver vehicles with correct properties`() {
        val vehicle =
            createVehicle(
                name = "Van 1",
                weightCapacity = BigDecimal("1000.0"),
                volumeCapacity = BigDecimal("50.0"),
                startLocation = Location(40.7128, -74.0060),
                endLocation = Location(40.7589, -73.9851)
            )

        val solution = mapper.toSolverSolution(UUID.randomUUID(), listOf(createOrder()), listOf(vehicle))

        val solverVehicle = solution.vehicles.first()
        assertThat(solverVehicle.id).isEqualTo(vehicle.id)
        assertThat(solverVehicle.name).isEqualTo("Van 1")
        assertThat(solverVehicle.weightCapacity).isEqualByComparingTo(BigDecimal("1000.0"))
        assertThat(solverVehicle.volumeCapacity).isEqualByComparingTo(BigDecimal("50.0"))
        assertThat(solverVehicle.startLocation).isEqualTo(Location(40.7128, -74.0060))
        assertThat(solverVehicle.endLocation).isEqualTo(Location(40.7589, -73.9851))
        assertThat(solverVehicle.visits).isEmpty()
    }

    @Test
    fun `should handle multiple orders and vehicles`() {
        val orders =
            listOf(
                createOrder(),
                createOrder(),
                createOrder()
            )
        val vehicles =
            listOf(
                createVehicle(name = "Van 1"),
                createVehicle(name = "Van 2")
            )

        val solution = mapper.toSolverSolution(UUID.randomUUID(), orders, vehicles)

        assertThat(solution.visits).hasSize(3)
        assertThat(solution.vehicles).hasSize(2)
    }

    @Test
    fun `should convert solver solution to domain trips`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()

        val solution =
            mapper.toSolverSolution(
                jobId,
                listOf(createOrder()),
                listOf(createVehicle())
            )

        // Assign visit to vehicle (simulate solver result)
        val vehicle = solution.vehicles.first()
        vehicle.visits.add(solution.visits.first())

        val trips = mapper.toTrips(solution, organizationId, jobId)

        assertThat(trips).hasSize(1)
        val trip = trips.first()
        assertThat(trip.organizationId).isEqualTo(organizationId)
        assertThat(trip.jobId).isEqualTo(jobId)
        assertThat(trip.vehicleId).isEqualTo(vehicle.id)
        assertThat(trip.visits).hasSize(1)
        assertThat(trip.totalDistanceMeters).isGreaterThan(0L)
        assertThat(trip.totalDurationMinutes).isGreaterThan(0)
    }

    @Test
    fun `should filter out vehicles with no visits when converting to trips`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()

        val solution =
            mapper.toSolverSolution(
                jobId,
                listOf(createOrder()),
                listOf(createVehicle(), createVehicle())
            )

        // Assign visit to only first vehicle
        solution.vehicles[0].visits.add(solution.visits.first())
        // Second vehicle has no visits

        val trips = mapper.toTrips(solution, organizationId, jobId)

        assertThat(trips).hasSize(1) // Only vehicle with visits
    }

    @Test
    fun `should calculate visit sequence numbers correctly`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()

        val solution =
            mapper.toSolverSolution(
                jobId,
                listOf(createOrder(), createOrder(), createOrder()),
                listOf(createVehicle())
            )

        val vehicle = solution.vehicles.first()
        vehicle.visits.addAll(solution.visits)

        val trips = mapper.toTrips(solution, organizationId, jobId)

        val trip = trips.first()
        assertThat(trip.visits).hasSize(3)
        assertThat(trip.visits[0].sequenceNumber).isEqualTo(0)
        assertThat(trip.visits[1].sequenceNumber).isEqualTo(1)
        assertThat(trip.visits[2].sequenceNumber).isEqualTo(2)
    }

    @Test
    fun `should calculate total distance including return to end location`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()

        val startLocation = Location(40.7128, -74.0060)
        val visitLocation = Location(40.7589, -73.9851)
        val endLocation = Location(40.7128, -74.0060)

        val vehicle =
            createVehicle(
                startLocation = startLocation,
                endLocation = endLocation
            )

        val order = createOrder(deliveryLocation = visitLocation)

        val solution = mapper.toSolverSolution(jobId, listOf(order), listOf(vehicle))
        solution.vehicles
            .first()
            .visits
            .add(solution.visits.first())

        val trips = mapper.toTrips(solution, organizationId, jobId)

        val trip = trips.first()

        // Distance should include:
        // 1. Start -> Visit
        // 2. Visit -> End
        val expectedDistance =
            distanceCalculator.distanceBetween(startLocation, visitLocation) +
                distanceCalculator.distanceBetween(visitLocation, endLocation)

        assertThat(trip.totalDistanceMeters).isEqualTo(expectedDistance)
    }

    @Test
    fun `should calculate total duration including travel and service time`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()

        val order = createOrder(serviceDurationMinutes = 30)
        val solution = mapper.toSolverSolution(jobId, listOf(order), listOf(createVehicle()))
        solution.vehicles
            .first()
            .visits
            .add(solution.visits.first())

        val trips = mapper.toTrips(solution, organizationId, jobId)

        val trip = trips.first()

        // Duration should include both travel time and service time (30 min)
        assertThat(trip.totalDurationMinutes).isGreaterThanOrEqualTo(30)
    }

    private fun createOrder(
        deliveryLocation: Location = Location(40.7589, -73.9851),
        serviceDurationMinutes: Int = 30,
        lineItems: List<LineItem> =
            listOf(
                LineItem(
                    id = UUID.randomUUID(),
                    name = "Test Item",
                    quantity = 1,
                    weight = BigDecimal("10.0"),
                    volume = BigDecimal("1.0"),
                    price = BigDecimal("100.00")
                )
            )
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
            pickupLocation = null,
            deliveryLocation = deliveryLocation,
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(4),
            serviceDurationMinutes = serviceDurationMinutes,
            priority = 0,
            notes = null,
            createdAt = LocalDateTime.now()
        )

    private fun createVehicle(
        name: String = "Test Vehicle",
        weightCapacity: BigDecimal = BigDecimal("1000.0"),
        volumeCapacity: BigDecimal = BigDecimal("50.0"),
        startLocation: Location = Location(40.7128, -74.0060),
        endLocation: Location = Location(40.7128, -74.0060)
    ): Vehicle =
        Vehicle(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            name = name,
            licensePlate = "ABC123",
            weightCapacity = weightCapacity,
            volumeCapacity = volumeCapacity,
            startLocation = startLocation,
            endLocation = endLocation,
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            driver = null,
            costPerKm = BigDecimal("0.50")
        )
}
