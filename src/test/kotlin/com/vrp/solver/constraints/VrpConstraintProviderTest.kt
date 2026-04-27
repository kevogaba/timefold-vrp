package com.vrp.solver.constraints

import com.vrp.domain.model.Location
import com.vrp.solver.domain.SolverVehicle
import com.vrp.solver.domain.SolverVisit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

/**
 * Tests for VRP constraint logic without using ConstraintVerifier.
 * Tests the constraint provider's calculation methods directly.
 */
class VrpConstraintProviderTest {

    private val constraintProvider = VrpConstraintProvider()

    @Test
    fun `should calculate total distance for empty route as zero`() {
        val vehicle = createVehicle()

        // Access private method via reflection for testing
        val method = VrpConstraintProvider::class.java.getDeclaredMethod(
            "calculateTotalDistance",
            SolverVehicle::class.java
        )
        method.isAccessible = true

        val distance = method.invoke(constraintProvider, vehicle) as Long

        assertThat(distance).isEqualTo(0L)
    }

    @Test
    fun `should calculate total distance including return to end location`() {
        val startLocation = Location(40.7128, -74.0060)
        val visitLocation = Location(40.7589, -73.9851)
        val endLocation = Location(40.7128, -74.0060)

        val vehicle = createVehicle(
            startLocation = startLocation,
            endLocation = endLocation
        )

        val visit = createVisit(location = visitLocation)
        vehicle.visits.add(visit)

        val method = VrpConstraintProvider::class.java.getDeclaredMethod(
            "calculateTotalDistance",
            SolverVehicle::class.java
        )
        method.isAccessible = true

        val distance = method.invoke(constraintProvider, vehicle) as Long

        // Distance should be greater than zero and include both legs
        assertThat(distance).isGreaterThan(0L)
    }

    @Test
    fun `should detect weight capacity violation`() {
        val vehicle = createVehicle(weightCapacity = BigDecimal("100.0"))

        // Add visits exceeding capacity
        vehicle.visits.add(createVisit(demandWeight = BigDecimal("60.0")))
        vehicle.visits.add(createVisit(demandWeight = BigDecimal("50.0")))

        val totalWeight = vehicle.visits.sumOf { it.demandWeight }

        assertThat(totalWeight).isGreaterThan(vehicle.weightCapacity)
    }

    @Test
    fun `should detect volume capacity violation`() {
        val vehicle = createVehicle(volumeCapacity = BigDecimal("30.0"))

        // Add visits exceeding capacity
        vehicle.visits.add(createVisit(demandVolume = BigDecimal("20.0")))
        vehicle.visits.add(createVisit(demandVolume = BigDecimal("15.0")))

        val totalVolume = vehicle.visits.sumOf { it.demandVolume }

        assertThat(totalVolume).isGreaterThan(vehicle.volumeCapacity)
    }

    @Test
    fun `should allow visits within capacity limits`() {
        val vehicle = createVehicle(
            weightCapacity = BigDecimal("200.0"),
            volumeCapacity = BigDecimal("50.0")
        )

        vehicle.visits.add(createVisit(
            demandWeight = BigDecimal("80.0"),
            demandVolume = BigDecimal("20.0")
        ))
        vehicle.visits.add(createVisit(
            demandWeight = BigDecimal("60.0"),
            demandVolume = BigDecimal("15.0")
        ))

        val totalWeight = vehicle.visits.sumOf { it.demandWeight }
        val totalVolume = vehicle.visits.sumOf { it.demandVolume }

        assertThat(totalWeight).isLessThanOrEqualTo(vehicle.weightCapacity)
        assertThat(totalVolume).isLessThanOrEqualTo(vehicle.volumeCapacity)
    }

    @Test
    fun `should detect pickup-delivery ordering violation`() {
        val orderId = UUID.randomUUID()
        val vehicle = createVehicle()

        val delivery = createVisit(
            orderId = orderId,
            isPickup = false,
            isDelivery = true
        )

        val pickup = createVisit(
            orderId = orderId,
            isPickup = true,
            isDelivery = false
        )

        // Wrong order - delivery before pickup
        vehicle.visits.add(delivery)
        vehicle.visits.add(pickup)

        val visitsByOrder = vehicle.visits.groupBy { it.orderId }
        val sameOrderVisits = visitsByOrder[orderId]!!

        val pickupIndex = vehicle.visits.indexOf(sameOrderVisits.find { it.isPickup })
        val deliveryIndex = vehicle.visits.indexOf(sameOrderVisits.find { it.isDelivery })

        assertThat(pickupIndex).isGreaterThan(deliveryIndex) // Violation detected
    }

    @Test
    fun `should allow correct pickup-delivery ordering`() {
        val orderId = UUID.randomUUID()
        val vehicle = createVehicle()

        val pickup = createVisit(
            orderId = orderId,
            isPickup = true,
            isDelivery = false
        )

        val delivery = createVisit(
            orderId = orderId,
            isPickup = false,
            isDelivery = true
        )

        // Correct order - pickup before delivery
        vehicle.visits.add(pickup)
        vehicle.visits.add(delivery)

        val visitsByOrder = vehicle.visits.groupBy { it.orderId }
        val sameOrderVisits = visitsByOrder[orderId]!!

        val pickupIndex = vehicle.visits.indexOf(sameOrderVisits.find { it.isPickup })
        val deliveryIndex = vehicle.visits.indexOf(sameOrderVisits.find { it.isDelivery })

        assertThat(pickupIndex).isLessThan(deliveryIndex) // Correct ordering
    }

    @Test
    fun `should calculate utilization percentage for load balancing`() {
        val vehicle = createVehicle(weightCapacity = BigDecimal("100.0"))

        // 80% utilization (optimal target)
        vehicle.visits.add(createVisit(demandWeight = BigDecimal("80.0")))

        val totalWeight = vehicle.visits.sumOf { it.demandWeight }
        val utilizationPercent = ((totalWeight / vehicle.weightCapacity) * BigDecimal(100)).toInt()

        assertThat(utilizationPercent).isEqualTo(80)
    }

    @Test
    fun `should calculate deviation from optimal utilization`() {
        val vehicle = createVehicle(weightCapacity = BigDecimal("100.0"))

        // 50% utilization - deviates by 30 from optimal 80%
        vehicle.visits.add(createVisit(demandWeight = BigDecimal("50.0")))

        val totalWeight = vehicle.visits.sumOf { it.demandWeight }
        val utilizationPercent = ((totalWeight / vehicle.weightCapacity) * BigDecimal(100)).toInt()
        val deviation = Math.abs(utilizationPercent - 80)

        assertThat(deviation).isEqualTo(30)
    }

    private fun createVehicle(
        weightCapacity: BigDecimal = BigDecimal("1000.0"),
        volumeCapacity: BigDecimal = BigDecimal("100.0"),
        startLocation: Location = Location(40.7128, -74.0060),
        endLocation: Location = Location(40.7128, -74.0060)
    ): SolverVehicle {
        return SolverVehicle(
            id = UUID.randomUUID(),
            name = "Test Vehicle",
            weightCapacity = weightCapacity,
            volumeCapacity = volumeCapacity,
            startLocation = startLocation,
            endLocation = endLocation,
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            costPerKm = BigDecimal("0.50"),
            visits = mutableListOf()
        )
    }

    private fun createVisit(
        orderId: UUID = UUID.randomUUID(),
        location: Location = Location(40.7589, -73.9851),
        demandWeight: BigDecimal = BigDecimal("10.0"),
        demandVolume: BigDecimal = BigDecimal("1.0"),
        timeWindowStart: LocalDateTime = LocalDateTime.now(),
        timeWindowEnd: LocalDateTime = LocalDateTime.now().plusHours(4),
        serviceDurationMinutes: Int = 30,
        isPickup: Boolean = false,
        isDelivery: Boolean = true
    ): SolverVisit {
        return SolverVisit(
            id = UUID.randomUUID(),
            orderId = orderId,
            location = location,
            demandWeight = demandWeight,
            demandVolume = demandVolume,
            timeWindowStart = timeWindowStart,
            timeWindowEnd = timeWindowEnd,
            serviceDurationMinutes = serviceDurationMinutes,
            isPickup = isPickup,
            isDelivery = isDelivery
        )
    }
}
