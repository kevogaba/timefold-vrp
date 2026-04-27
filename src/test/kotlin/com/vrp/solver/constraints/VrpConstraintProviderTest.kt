package com.vrp.solver.constraints

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier
import com.vrp.domain.model.Location
import com.vrp.solver.domain.SolverVehicle
import com.vrp.solver.domain.SolverVisit
import com.vrp.solver.domain.VrpSolution
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class VrpConstraintProviderTest {

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
    fun `vehicle weight capacity should penalize overloaded vehicles`() {
        val vehicle = SolverVehicle(
            id = UUID.randomUUID(),
            name = "Vehicle 1",
            weightCapacity = BigDecimal("100.00"),
            volumeCapacity = BigDecimal("100.00"),
            startLocation = Location(0.0, 0.0),
            endLocation = Location(0.0, 0.0),
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            visits = mutableListOf()
        )

        val visit1 = SolverVisit(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            location = Location(1.0, 1.0),
            demandWeight = BigDecimal("60.00"),
            demandVolume = BigDecimal("30.00"),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 15
        )

        val visit2 = SolverVisit(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            location = Location(2.0, 2.0),
            demandWeight = BigDecimal("60.00"),
            demandVolume = BigDecimal("30.00"),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 15
        )

        vehicle.visits.add(visit1)
        vehicle.visits.add(visit2)

        constraintVerifier.verifyThat(VrpConstraintProvider::vehicleWeightCapacity)
            .given(vehicle)
            .penalizesBy(1)
    }
}
