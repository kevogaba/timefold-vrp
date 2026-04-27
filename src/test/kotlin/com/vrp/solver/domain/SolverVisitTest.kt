package com.vrp.solver.domain

import com.vrp.domain.model.Location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class SolverVisitTest {

    @Test
    fun `should create solver visit with all properties`() {
        val id = UUID.randomUUID()
        val orderId = UUID.randomUUID()
        val timeWindowStart = LocalDateTime.now()
        val timeWindowEnd = timeWindowStart.plusHours(2)

        val visit = SolverVisit(
            id = id,
            orderId = orderId,
            location = Location(40.7589, -73.9851),
            demandWeight = BigDecimal("10.0"),
            demandVolume = BigDecimal("2.0"),
            timeWindowStart = timeWindowStart,
            timeWindowEnd = timeWindowEnd,
            serviceDurationMinutes = 30,
            isPickup = false,
            isDelivery = true
        )

        assertThat(visit.id).isEqualTo(id)
        assertThat(visit.orderId).isEqualTo(orderId)
        assertThat(visit.location).isEqualTo(Location(40.7589, -73.9851))
        assertThat(visit.demandWeight).isEqualByComparingTo(BigDecimal("10.0"))
        assertThat(visit.demandVolume).isEqualByComparingTo(BigDecimal("2.0"))
        assertThat(visit.serviceDurationMinutes).isEqualTo(30)
        assertThat(visit.isPickup).isFalse()
        assertThat(visit.isDelivery).isTrue()
    }

    @Test
    fun `should default to delivery visit`() {
        val visit = SolverVisit(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            location = Location(40.7589, -73.9851),
            demandWeight = BigDecimal("10.0"),
            demandVolume = BigDecimal("2.0"),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30
        )

        assertThat(visit.isPickup).isFalse()
        assertThat(visit.isDelivery).isTrue()
    }

    @Test
    fun `should support pickup visits`() {
        val visit = SolverVisit(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            location = Location(40.7589, -73.9851),
            demandWeight = BigDecimal("10.0"),
            demandVolume = BigDecimal("2.0"),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30,
            isPickup = true,
            isDelivery = false
        )

        assertThat(visit.isPickup).isTrue()
        assertThat(visit.isDelivery).isFalse()
    }

    @Test
    fun `should support data class equality`() {
        val id = UUID.randomUUID()
        val orderId = UUID.randomUUID()
        val timeWindowStart = LocalDateTime.of(2024, 1, 1, 10, 0)
        val timeWindowEnd = timeWindowStart.plusHours(2)

        val visit1 = SolverVisit(
            id = id,
            orderId = orderId,
            location = Location(40.7589, -73.9851),
            demandWeight = BigDecimal("10.0"),
            demandVolume = BigDecimal("2.0"),
            timeWindowStart = timeWindowStart,
            timeWindowEnd = timeWindowEnd,
            serviceDurationMinutes = 30
        )

        val visit2 = SolverVisit(
            id = id,
            orderId = orderId,
            location = Location(40.7589, -73.9851),
            demandWeight = BigDecimal("10.0"),
            demandVolume = BigDecimal("2.0"),
            timeWindowStart = timeWindowStart,
            timeWindowEnd = timeWindowEnd,
            serviceDurationMinutes = 30
        )

        assertThat(visit1).isEqualTo(visit2)
    }

    @Test
    fun `should support data class copy`() {
        val visit = SolverVisit(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            location = Location(40.7589, -73.9851),
            demandWeight = BigDecimal("10.0"),
            demandVolume = BigDecimal("2.0"),
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(2),
            serviceDurationMinutes = 30
        )

        val newWeight = BigDecimal("20.0")
        val copiedVisit = visit.copy(demandWeight = newWeight)

        assertThat(copiedVisit.demandWeight).isEqualByComparingTo(newWeight)
        assertThat(copiedVisit.id).isEqualTo(visit.id)
        assertThat(copiedVisit.orderId).isEqualTo(visit.orderId)
    }
}
