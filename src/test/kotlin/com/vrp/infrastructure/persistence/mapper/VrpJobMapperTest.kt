package com.vrp.infrastructure.persistence.mapper

import com.vrp.domain.model.JobStatus
import com.vrp.domain.model.VrpJob
import com.vrp.infrastructure.persistence.entity.OrderEntity
import com.vrp.infrastructure.persistence.entity.VehicleEntity
import com.vrp.infrastructure.persistence.entity.VrpJobEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class VrpJobMapperTest {
    private val mapper = VrpJobMapper()

    @Test
    fun `should map entity to domain`() {
        val jobId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()

        val entity =
            VrpJobEntity(
                organizationId = organizationId,
                status = JobStatus.COMPLETED,
                hardScore = 0,
                softScore = -1000,
                createdAt = LocalDateTime.now(),
                startedAt = LocalDateTime.now(),
                completedAt = LocalDateTime.now()
            )

        // Add at least one order
        entity.orders.add(
            OrderEntity(
                organizationId = organizationId,
                customerId = UUID.randomUUID(),
                customerName = "Test Customer",
                customerLat = 40.7128,
                customerLon = -74.0060,
                deliveryLat = 40.7589,
                deliveryLon = -73.9851,
                timeWindowStart = LocalDateTime.now(),
                timeWindowEnd = LocalDateTime.now().plusHours(2),
                serviceDurationMinutes = 30
            )
        )

        // Add at least one vehicle
        entity.vehicles.add(
            VehicleEntity(
                organizationId = organizationId,
                name = "Vehicle 1",
                licensePlate = "ABC123",
                weightCapacity = BigDecimal("1000.00"),
                volumeCapacity = BigDecimal("50.00"),
                startLat = 40.7128,
                startLon = -74.0060,
                endLat = 40.7128,
                endLon = -74.0060,
                availableFrom = LocalTime.of(8, 0),
                availableUntil = LocalTime.of(18, 0)
            )
        )

        val domain = mapper.toDomain(entity)

        assertThat(domain.id).isEqualTo(entity.id)
        assertThat(domain.organizationId).isEqualTo(organizationId)
        assertThat(domain.status).isEqualTo(JobStatus.COMPLETED)
        assertThat(domain.hardScore).isEqualTo(0)
        assertThat(domain.softScore).isEqualTo(-1000)
        assertThat(domain.orderIds).hasSize(1)
        assertThat(domain.vehicleIds).hasSize(1)
    }

    @Test
    fun `should map domain to entity`() {
        val jobId = UUID.randomUUID()
        val organizationId = UUID.randomUUID()

        val domain =
            VrpJob(
                id = jobId,
                organizationId = organizationId,
                status = JobStatus.PENDING,
                orderIds = listOf(UUID.randomUUID(), UUID.randomUUID()),
                vehicleIds = listOf(UUID.randomUUID()),
                hardScore = null,
                softScore = null,
                errorMessage = null,
                createdAt = LocalDateTime.now(),
                startedAt = null,
                completedAt = null
            )

        val entity = mapper.toEntity(domain)

        assertThat(entity.id).isNotNull()
        assertThat(entity.status).isEqualTo(JobStatus.PENDING)
        assertThat(entity.hardScore).isNull()
    }
}
