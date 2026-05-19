package com.vrp.infrastructure.persistence.mapper

import com.vrp.domain.model.Location
import com.vrp.domain.model.Trip
import com.vrp.domain.model.Visit
import com.vrp.infrastructure.persistence.entity.TripEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class TripMapperTest {
    private val mapper = TripMapper()

    @Test
    fun `should map entity to domain`() {
        val tripEntity =
            TripEntity(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                jobId = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                totalDistanceMeters = 15000L,
                totalDurationMinutes = 120,
                createdAt = LocalDateTime.now()
            )

        val domain = mapper.toDomain(tripEntity)

        assertThat(domain.id).isEqualTo(tripEntity.id)
        assertThat(domain.totalDistanceMeters).isEqualTo(15000L)
        assertThat(domain.totalDurationMinutes).isEqualTo(120)
        assertThat(domain.visits).isEmpty()
    }

    @Test
    fun `should map domain to entity`() {
        val tripId = UUID.randomUUID()

        val domain =
            Trip(
                id = tripId,
                organizationId = UUID.randomUUID(),
                jobId = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                visits =
                    listOf(
                        Visit(
                            id = UUID.randomUUID(),
                            orderId = UUID.randomUUID(),
                            location = Location(40.7128, -74.0060),
                            arrivalTime = LocalDateTime.now(),
                            departureTime = LocalDateTime.now().plusMinutes(30),
                            sequenceNumber = 1
                        )
                    ),
                totalDistanceMeters = 15000L,
                totalDurationMinutes = 120,
                createdAt = LocalDateTime.now()
            )

        val entity = mapper.toEntity(domain)

        assertThat(entity.id).isNotNull()
        assertThat(entity.totalDistanceMeters).isEqualTo(15000L)
        assertThat(entity.visits).hasSize(1)
        assertThat(entity.visits[0].sequenceNumber).isEqualTo(1)
    }
}
