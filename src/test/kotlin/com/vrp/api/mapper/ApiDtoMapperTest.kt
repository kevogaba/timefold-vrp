package com.vrp.api.mapper

import com.vrp.domain.model.JobStatus
import com.vrp.domain.model.Location
import com.vrp.domain.model.Trip
import com.vrp.domain.model.Visit
import com.vrp.domain.model.VrpJob
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class ApiDtoMapperTest {
    private lateinit var mapper: ApiDtoMapper

    @BeforeEach
    fun setup() {
        mapper = ApiDtoMapper()
    }

    @Test
    fun `should map VrpJob to JobResponse`() {
        val jobId = UUID.randomUUID()
        val createdAt = LocalDateTime.now()
        val completedAt = createdAt.plusMinutes(5)

        val job =
            VrpJob(
                id = jobId,
                organizationId = UUID.randomUUID(),
                orderIds = listOf(UUID.randomUUID()),
                vehicleIds = listOf(UUID.randomUUID()),
                status = JobStatus.COMPLETED,
                hardScore = 0,
                softScore = -1000,
                errorMessage = null,
                createdAt = createdAt,
                startedAt = createdAt.plusSeconds(1),
                completedAt = completedAt
            )

        val response = mapper.toJobResponse(job)

        assertThat(response.id).isEqualTo(jobId)
        assertThat(response.status).isEqualTo("COMPLETED")
        assertThat(response.hardScore).isEqualTo(0)
        assertThat(response.softScore).isEqualTo(-1000)
        assertThat(response.errorMessage).isNull()
        assertThat(response.createdAt).isEqualTo(createdAt)
        assertThat(response.completedAt).isEqualTo(completedAt)
    }

    @Test
    fun `should map pending job to JobResponse`() {
        val job =
            VrpJob(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                orderIds = listOf(UUID.randomUUID()),
                vehicleIds = listOf(UUID.randomUUID()),
                status = JobStatus.PENDING,
                hardScore = null,
                softScore = null,
                errorMessage = null,
                createdAt = LocalDateTime.now(),
                startedAt = null,
                completedAt = null
            )

        val response = mapper.toJobResponse(job)

        assertThat(response.status).isEqualTo("PENDING")
        assertThat(response.hardScore).isNull()
        assertThat(response.softScore).isNull()
        assertThat(response.completedAt).isNull()
    }

    @Test
    fun `should map failed job to JobResponse with error message`() {
        val errorMessage = "Solver timeout"
        val job =
            VrpJob(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                orderIds = listOf(UUID.randomUUID()),
                vehicleIds = listOf(UUID.randomUUID()),
                status = JobStatus.FAILED,
                hardScore = null,
                softScore = null,
                errorMessage = errorMessage,
                createdAt = LocalDateTime.now(),
                startedAt = LocalDateTime.now(),
                completedAt = LocalDateTime.now()
            )

        val response = mapper.toJobResponse(job)

        assertThat(response.status).isEqualTo("FAILED")
        assertThat(response.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `should map Trip to TripResponse`() {
        val tripId = UUID.randomUUID()
        val jobId = UUID.randomUUID()
        val vehicleId = UUID.randomUUID()
        val orderId1 = UUID.randomUUID()
        val orderId2 = UUID.randomUUID()

        val trip =
            Trip(
                id = tripId,
                organizationId = UUID.randomUUID(),
                jobId = jobId,
                vehicleId = vehicleId,
                visits =
                    listOf(
                        Visit(
                            id = UUID.randomUUID(),
                            orderId = orderId1,
                            location = Location(40.7589, -73.9851),
                            arrivalTime = LocalDateTime.of(2024, 1, 1, 10, 0),
                            departureTime = LocalDateTime.of(2024, 1, 1, 10, 30),
                            sequenceNumber = 0
                        ),
                        Visit(
                            id = UUID.randomUUID(),
                            orderId = orderId2,
                            location = Location(40.7128, -74.0060),
                            arrivalTime = LocalDateTime.of(2024, 1, 1, 11, 0),
                            departureTime = LocalDateTime.of(2024, 1, 1, 11, 30),
                            sequenceNumber = 1
                        )
                    ),
                totalDistanceMeters = 15000L,
                totalDurationMinutes = 90,
                createdAt = LocalDateTime.now()
            )

        val response = mapper.toTripResponse(trip)

        assertThat(response.id).isEqualTo(tripId)
        assertThat(response.jobId).isEqualTo(jobId)
        assertThat(response.vehicleId).isEqualTo(vehicleId)
        assertThat(response.totalDistanceMeters).isEqualTo(15000L)
        assertThat(response.totalDurationMinutes).isEqualTo(90)
        assertThat(response.visits).hasSize(2)

        val visit1 = response.visits[0]
        assertThat(visit1.orderId).isEqualTo(orderId1)
        assertThat(visit1.location.latitude).isEqualTo(40.7589)
        assertThat(visit1.location.longitude).isEqualTo(-73.9851)
        assertThat(visit1.sequenceNumber).isEqualTo(0)

        val visit2 = response.visits[1]
        assertThat(visit2.orderId).isEqualTo(orderId2)
        assertThat(visit2.location.latitude).isEqualTo(40.7128)
        assertThat(visit2.location.longitude).isEqualTo(-74.0060)
        assertThat(visit2.sequenceNumber).isEqualTo(1)
    }

    @Test
    fun `should map trip with empty visits list`() {
        val trip =
            Trip(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                jobId = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                visits = emptyList(),
                totalDistanceMeters = 0L,
                totalDurationMinutes = 0,
                createdAt = LocalDateTime.now()
            )

        val response = mapper.toTripResponse(trip)

        assertThat(response.visits).isEmpty()
        assertThat(response.totalDistanceMeters).isEqualTo(0L)
        assertThat(response.totalDurationMinutes).isEqualTo(0)
    }
}
