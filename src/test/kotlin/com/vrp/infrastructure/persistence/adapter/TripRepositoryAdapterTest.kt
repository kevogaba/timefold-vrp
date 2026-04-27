package com.vrp.infrastructure.persistence.adapter

import com.vrp.domain.model.*
import com.vrp.infrastructure.persistence.entity.TripEntity
import com.vrp.infrastructure.persistence.mapper.TripMapper
import com.vrp.infrastructure.persistence.repository.TripJpaRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class TripRepositoryAdapterTest {

    private lateinit var jpaRepository: TripJpaRepository
    private lateinit var mapper: TripMapper
    private lateinit var adapter: TripRepositoryAdapter

    @BeforeEach
    fun setup() {
        jpaRepository = mockk()
        mapper = TripMapper()
        adapter = TripRepositoryAdapter(jpaRepository, mapper)
    }

    @Test
    fun `should save trip successfully`() {
        val trip = createTrip()
        val entity = mapper.toEntity(trip)

        every { jpaRepository.save(any()) } returns entity

        val result = adapter.save(trip)

        assertThat(result.id).isNotNull()
        verify { jpaRepository.save(any()) }
    }

    @Test
    fun `should find trip by id and organizationId`() {
        val organizationId = UUID.randomUUID()
        val tripId = UUID.randomUUID()
        val entity = createTripEntity(organizationId = organizationId)

        every { jpaRepository.findByIdAndOrganizationId(tripId, organizationId) } returns entity

        val result = adapter.findById(tripId, organizationId)

        assertThat(result).isNotNull
        assertThat(result?.organizationId).isEqualTo(organizationId)
        verify { jpaRepository.findByIdAndOrganizationId(tripId, organizationId) }
    }

    @Test
    fun `should return null when trip not found`() {
        val organizationId = UUID.randomUUID()
        val tripId = UUID.randomUUID()

        every { jpaRepository.findByIdAndOrganizationId(tripId, organizationId) } returns null

        val result = adapter.findById(tripId, organizationId)

        assertThat(result).isNull()
    }

    @Test
    fun `should find all trips by jobId and organizationId`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()
        val entities = listOf(
            createTripEntity(organizationId = organizationId, jobId = jobId),
            createTripEntity(organizationId = organizationId, jobId = jobId)
        )

        every { jpaRepository.findAllByJobIdAndOrganizationId(jobId, organizationId) } returns entities

        val results = adapter.findAllByJobId(jobId, organizationId)

        assertThat(results).hasSize(2)
        assertThat(results.all { it.jobId == jobId }).isTrue()
        verify { jpaRepository.findAllByJobIdAndOrganizationId(jobId, organizationId) }
    }

    @Test
    fun `should find all trips by organizationId`() {
        val organizationId = UUID.randomUUID()
        val entities = listOf(
            createTripEntity(organizationId = organizationId),
            createTripEntity(organizationId = organizationId)
        )

        every { jpaRepository.findAllByOrganizationId(organizationId) } returns entities

        val results = adapter.findAllByOrganizationId(organizationId)

        assertThat(results).hasSize(2)
        verify { jpaRepository.findAllByOrganizationId(organizationId) }
    }

    private fun createTrip(): Trip {
        return Trip(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            jobId = UUID.randomUUID(),
            vehicleId = UUID.randomUUID(),
            visits = listOf(
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

    private fun createTripEntity(
        organizationId: UUID = UUID.randomUUID(),
        jobId: UUID = UUID.randomUUID()
    ): TripEntity {
        return TripEntity(
            organizationId = organizationId,
            jobId = jobId,
            vehicleId = UUID.randomUUID(),
            totalDistanceMeters = 10000L,
            totalDurationMinutes = 60
        )
    }
}
