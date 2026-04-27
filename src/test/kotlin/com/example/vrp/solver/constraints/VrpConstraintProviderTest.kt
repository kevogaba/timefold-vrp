package com.example.vrp.solver.constraints

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier
import com.example.vrp.shared.Capacity
import com.example.vrp.shared.Location
import com.example.vrp.shared.TimeWindow
import com.example.vrp.shared.WorkingHours
import com.example.vrp.solver.domain.SolverVehicle
import com.example.vrp.solver.domain.SolverVisit
import com.example.vrp.solver.domain.VrpSolution
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.LocalTime

class VrpConstraintProviderTest {

    private val constraintVerifier = ConstraintVerifier.build(
        VrpConstraintProvider(),
        VrpSolution::class.java,
        SolverVisit::class.java,
    )

    private val depot = Location("Depot", 0.0, 0.0)
    private val vehicle = SolverVehicle(
        id = "v1",
        depotLocation = depot,
        capacity = Capacity(100.0, 500.0),
        workingHours = WorkingHours(LocalTime.of(8, 0), LocalTime.of(18, 0)),
    )

    @Test
    fun `weight capacity not exceeded when within limit`() {
        val visit = SolverVisit(
            id = "visit-1",
            orderId = "order-1",
            location = Location("A", 1.0, 1.0),
            timeWindow = TimeWindow(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(4),
            ),
            demandWeightKg = 50.0,
        ).also { it.vehicle = vehicle }

        constraintVerifier.verifyThat(VrpConstraintProvider::vehicleCapacityWeight)
            .given(visit)
            .penalizesBy(0)
    }

    @Test
    fun `weight capacity constraint fires when exceeded`() {
        val visit1 = SolverVisit(
            id = "visit-1",
            orderId = "order-1",
            location = Location("A", 1.0, 1.0),
            timeWindow = TimeWindow(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(4),
            ),
            demandWeightKg = 80.0,
        ).also { it.vehicle = vehicle }

        val visit2 = SolverVisit(
            id = "visit-2",
            orderId = "order-2",
            location = Location("B", 2.0, 2.0),
            timeWindow = TimeWindow(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(4),
            ),
            demandWeightKg = 40.0,
        ).also { it.vehicle = vehicle }

        constraintVerifier.verifyThat(VrpConstraintProvider::vehicleCapacityWeight)
            .given(visit1, visit2)
            .penalizes()
    }
}
