package com.vrp.application.usecase

import com.vrp.domain.model.Location
import com.vrp.domain.model.Trip
import com.vrp.domain.model.Visit
import com.vrp.domain.port.TripRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class GetTripUseCaseTest {

    private lateinit var tripRepository: TripRepository
    private lateinit var useCase: GetTripUseCase

    @BeforeEach
    fun setup() {
        tripRepository = mockk()
        useCase = GetTripUseCase(tripRepository)
    }

    @Test
    fun `should get trip by id and organization id`() {
        val organizationId = UUID.randomUUID()
        val tripId = UUID.randomUUID()
        val trip = createTrip(tripId = tripId, organizationId = organizationId)

        every { tripRepository.findById(tripId, organizationId) } returns trip

        val result = useCase.execute(tripId, organizationId)

        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(tripId)
        assertThat(result?.organizationId).isEqualTo(organizationId)
        verify { tripRepository.findById(tripId, organizationId) }
    }

    @Test
    fun `should return null when trip not found`() {
        val organizationId = UUID.randomUUID()
        val tripId = UUID.randomUUID()

        every { tripRepository.findById(tripId, organizationId) } returns null

        val result = useCase.execute(tripId, organizationId)

        assertThat(result).isNull()
        verify { tripRepository.findById(tripId, organizationId) }
    }

    @Test
    fun `should get trips by job id`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()
        val trips = listOf(
            createTrip(jobId = jobId, organizationId = organizationId),
            createTrip(jobId = jobId, organizationId = organizationId)
        )

        every { tripRepository.findAllByJobId(jobId, organizationId) } returns trips

        val results = useCase.getByJobId(jobId, organizationId)

        assertThat(results).hasSize(2)
        assertThat(results.all { it.jobId == jobId }).isTrue()
        assertThat(results.all { it.organizationId == organizationId }).isTrue()
        verify { tripRepository.findAllByJobId(jobId, organizationId) }
    }

    @Test
    fun `should return empty list when no trips found for job`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()

        every { tripRepository.findAllByJobId(jobId, organizationId) } returns emptyList()

        val results = useCase.getByJobId(jobId, organizationId)

        assertThat(results).isEmpty()
        verify { tripRepository.findAllByJobId(jobId, organizationId) }
    }

    private fun createTrip(
        tripId: UUID = UUID.randomUUID(),
        organizationId: UUID = UUID.randomUUID(),
        jobId: UUID = UUID.randomUUID()
    ): Trip {
        return Trip(
            id = tripId,
            organizationId = organizationId,
            jobId = jobId,
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
}
