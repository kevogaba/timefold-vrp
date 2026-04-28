package com.vrp.solver.domain

import ai.timefold.solver.core.api.score.HardSoftScore
import com.vrp.domain.model.Location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class VrpSolutionTest {
    @Test
    fun `should create solution with no-arg constructor`() {
        val solution = VrpSolution()

        assertThat(solution.vehicles).isEmpty()
        assertThat(solution.visits).isEmpty()
        assertThat(solution.score).isNull()
        assertThat(solution.jobId).isNull()
    }

    @Test
    fun `should create solution with properties`() {
        val jobId = UUID.randomUUID()
        val vehicles = mutableListOf(createVehicle())
        val visits = mutableListOf(createVisit())

        val solution =
            VrpSolution(
                vehicles = vehicles,
                visits = visits,
                score = null,
                jobId = jobId
            )

        assertThat(solution.vehicles).hasSize(1)
        assertThat(solution.visits).hasSize(1)
        assertThat(solution.jobId).isEqualTo(jobId)
    }

    @Test
    fun `should support mutable vehicle list`() {
        val solution = VrpSolution()

        solution.vehicles.add(createVehicle())
        solution.vehicles.add(createVehicle())

        assertThat(solution.vehicles).hasSize(2)
    }

    @Test
    fun `should support mutable visit list`() {
        val solution = VrpSolution()

        solution.visits.add(createVisit())
        solution.visits.add(createVisit())

        assertThat(solution.visits).hasSize(2)
    }

    @Test
    fun `should support score assignment`() {
        val solution = VrpSolution()

        assertThat(solution.score).isNull()

        solution.score = HardSoftScore.of(0, -1000)

        assertThat(solution.score).isNotNull()
        assertThat(solution.score?.hardScore()).isEqualTo(0)
        assertThat(solution.score?.softScore()).isEqualTo(-1000)
    }

    @Test
    fun `should support data class copy`() {
        val originalJobId = UUID.randomUUID()
        val solution = VrpSolution(jobId = originalJobId)

        val newJobId = UUID.randomUUID()
        val copiedSolution = solution.copy(jobId = newJobId)

        assertThat(copiedSolution.jobId).isEqualTo(newJobId)
        assertThat(solution.jobId).isEqualTo(originalJobId)
    }

    private fun createVehicle(): SolverVehicle =
        SolverVehicle(
            id = UUID.randomUUID(),
            name = "Test Vehicle",
            weightCapacity = BigDecimal("1000.0"),
            volumeCapacity = BigDecimal("50.0"),
            startLocation = Location(40.7128, -74.0060),
            endLocation = Location(40.7128, -74.0060),
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0)
        )

    private fun createVisit(): SolverVisit =
        SolverVisit(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            location = Location(40.7589, -73.9851),
            demandWeight = BigDecimal("10.0"),
            demandVolume = BigDecimal("2.0"),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30
        )
}
