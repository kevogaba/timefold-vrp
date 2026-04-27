package com.vrp.solver

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier
import com.vrp.domain.model.Location
import com.vrp.solver.constraints.VrpConstraintProvider
import com.vrp.solver.domain.SolverVehicle
import com.vrp.solver.domain.SolverVisit
import com.vrp.solver.domain.VrpSolution
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

/**
 * Comprehensive solver integration tests.
 */
class VrpSolverIntegrationTest {

    private lateinit var constraintVerifier: ConstraintVerifier<VrpConstraintProvider, VrpSolution>

    @BeforeEach
    fun setup() {
        constraintVerifier = ConstraintVerifier.build(
            VrpConstraintProvider(),
            VrpSolution::class.java,
            SolverVehicle::class.java
        )
    }

    @Test
    fun `should not penalize vehicle with capacity within limits`() {
        val vehicle = createVehicle(
            weightCapacity = BigDecimal("100.00"),
            volumeCapacity = BigDecimal("100.00")
        )

        val visit = createVisit(
            demandWeight = BigDecimal("50.00"),
            demandVolume = BigDecimal("40.00")
        )

        vehicle.visits.add(visit)

        constraintVerifier.verifyThat(VrpConstraintProvider::vehicleWeightCapacity)
            .given(vehicle)
            .penalizesBy(0)

        constraintVerifier.verifyThat(VrpConstraintProvider::vehicleVolumeCapacity)
            .given(vehicle)
            .penalizesBy(0)
    }

    @Test
    fun `should penalize vehicle exceeding weight capacity`() {
        val vehicle = createVehicle(
            weightCapacity = BigDecimal("100.00"),
            volumeCapacity = BigDecimal("100.00")
        )

        val visit1 = createVisit(demandWeight = BigDecimal("60.00"))
        val visit2 = createVisit(demandWeight = BigDecimal("60.00"))

        vehicle.visits.add(visit1)
        vehicle.visits.add(visit2)

        constraintVerifier.verifyThat(VrpConstraintProvider::vehicleWeightCapacity)
            .given(vehicle)
            .penalizesBy(1)
    }

    @Test
    fun `should penalize vehicle exceeding volume capacity`() {
        val vehicle = createVehicle(
            weightCapacity = BigDecimal("100.00"),
            volumeCapacity = BigDecimal("100.00")
        )

        val visit1 = createVisit(demandVolume = BigDecimal("60.00"))
        val visit2 = createVisit(demandVolume = BigDecimal("60.00"))

        vehicle.visits.add(visit1)
        vehicle.visits.add(visit2)

        constraintVerifier.verifyThat(VrpConstraintProvider::vehicleVolumeCapacity)
            .given(vehicle)
            .penalizesBy(1)
    }

    @Test
    fun `should calculate distance penalty correctly`() {
        val vehicle = createVehicle()
        val visit1 = createVisit(location = Location(0.0, 0.0))
        val visit2 = createVisit(location = Location(1.0, 1.0))

        vehicle.visits.add(visit1)
        vehicle.visits.add(visit2)

        constraintVerifier.verifyThat(VrpConstraintProvider::minimizeTotalDistance)
            .given(vehicle)
            .penalizes()
    }

    private fun createVehicle(
        weightCapacity: BigDecimal = BigDecimal("1000.00"),
        volumeCapacity: BigDecimal = BigDecimal("1000.00")
    ): SolverVehicle {
        return SolverVehicle(
            id = UUID.randomUUID(),
            name = "Test Vehicle",
            weightCapacity = weightCapacity,
            volumeCapacity = volumeCapacity,
            startLocation = Location(0.0, 0.0),
            endLocation = Location(0.0, 0.0),
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            visits = mutableListOf()
        )
    }

    private fun createVisit(
        location: Location = Location(1.0, 1.0),
        demandWeight: BigDecimal = BigDecimal("10.00"),
        demandVolume: BigDecimal = BigDecimal("10.00")
    ): SolverVisit {
        return SolverVisit(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            location = location,
            demandWeight = demandWeight,
            demandVolume = demandVolume,
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 15
        )
    }
}
