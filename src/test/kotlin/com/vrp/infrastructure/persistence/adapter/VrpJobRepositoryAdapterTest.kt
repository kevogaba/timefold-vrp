package com.vrp.infrastructure.persistence.adapter

import com.vrp.domain.model.JobStatus
import com.vrp.domain.model.VrpJob
import com.vrp.infrastructure.persistence.entity.OrderEntity
import com.vrp.infrastructure.persistence.entity.VehicleEntity
import com.vrp.infrastructure.persistence.entity.VrpJobEntity
import com.vrp.infrastructure.persistence.mapper.VrpJobMapper
import com.vrp.infrastructure.persistence.repository.OrderJpaRepository
import com.vrp.infrastructure.persistence.repository.VehicleJpaRepository
import com.vrp.infrastructure.persistence.repository.VrpJobJpaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class VrpJobRepositoryAdapterTest {
    private lateinit var jpaRepository: VrpJobJpaRepository
    private lateinit var orderJpaRepository: OrderJpaRepository
    private lateinit var vehicleJpaRepository: VehicleJpaRepository
    private lateinit var mapper: VrpJobMapper
    private lateinit var adapter: VrpJobRepositoryAdapter

    @BeforeEach
    fun setup() {
        jpaRepository = mockk()
        orderJpaRepository = mockk()
        vehicleJpaRepository = mockk()
        mapper = VrpJobMapper()
        adapter = VrpJobRepositoryAdapter(jpaRepository, orderJpaRepository, vehicleJpaRepository, mapper)
    }

    @Test
    fun `should save job with orders and vehicles`() {
        val job = createJob()
        val orders = listOf(createOrderEntity())
        val vehicles = listOf(createVehicleEntity())
        val savedEntity = createJobEntity()

        every { orderJpaRepository.findAllByIdInAndOrganizationId(any(), any()) } returns orders
        every { vehicleJpaRepository.findAllByIdInAndOrganizationId(any(), any()) } returns vehicles
        every { jpaRepository.save(any()) } returns savedEntity

        val result = adapter.save(job)

        assertThat(result).isNotNull
        verify { orderJpaRepository.findAllByIdInAndOrganizationId(job.orderIds, job.organizationId) }
        verify { vehicleJpaRepository.findAllByIdInAndOrganizationId(job.vehicleIds, job.organizationId) }
        verify { jpaRepository.save(any()) }
    }

    @Test
    fun `should find job by id and organizationId`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()
        val entity = createJobEntity(organizationId = organizationId)

        every { jpaRepository.findByIdAndOrganizationId(jobId, organizationId) } returns entity

        val result = adapter.findById(jobId, organizationId)

        assertThat(result).isNotNull
        assertThat(result?.organizationId).isEqualTo(organizationId)
        verify { jpaRepository.findByIdAndOrganizationId(jobId, organizationId) }
    }

    @Test
    fun `should return null when job not found`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()

        every { jpaRepository.findByIdAndOrganizationId(jobId, organizationId) } returns null

        val result = adapter.findById(jobId, organizationId)

        assertThat(result).isNull()
    }

    @Test
    fun `should find all jobs by organizationId`() {
        val organizationId = UUID.randomUUID()
        val entities =
            listOf(
                createJobEntity(organizationId = organizationId),
                createJobEntity(organizationId = organizationId)
            )

        every { jpaRepository.findAllByOrganizationId(organizationId) } returns entities

        val results = adapter.findAllByOrganizationId(organizationId)

        assertThat(results).hasSize(2)
        verify { jpaRepository.findAllByOrganizationId(organizationId) }
    }

    @Test
    fun `should update job status to RUNNING`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()
        val entity = createJobEntity(organizationId = organizationId)

        every { jpaRepository.findByIdAndOrganizationId(jobId, organizationId) } returns entity
        every { jpaRepository.save(any()) } returns entity

        val result = adapter.updateStatus(jobId, JobStatus.RUNNING, organizationId)

        assertThat(result).isNotNull
        assertThat(result?.status).isEqualTo(JobStatus.RUNNING)
        assertThat(entity.startedAt).isNotNull()
        verify { jpaRepository.save(entity) }
    }

    @Test
    fun `should update job status to COMPLETED`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()
        val entity = createJobEntity(organizationId = organizationId)

        every { jpaRepository.findByIdAndOrganizationId(jobId, organizationId) } returns entity
        every { jpaRepository.save(any()) } returns entity

        val result = adapter.updateStatus(jobId, JobStatus.COMPLETED, organizationId)

        assertThat(result).isNotNull
        assertThat(result?.status).isEqualTo(JobStatus.COMPLETED)
        assertThat(entity.completedAt).isNotNull()
        verify { jpaRepository.save(entity) }
    }

    @Test
    fun `should update job with score`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()
        val entity = createJobEntity(organizationId = organizationId)

        every { jpaRepository.findByIdAndOrganizationId(jobId, organizationId) } returns entity
        every { jpaRepository.save(any()) } returns entity

        val result =
            adapter.updateWithScore(
                id = jobId,
                status = JobStatus.COMPLETED,
                hardScore = 0,
                softScore = -1000,
                organizationId = organizationId
            )

        assertThat(result).isNotNull
        assertThat(result?.hardScore).isEqualTo(0)
        assertThat(result?.softScore).isEqualTo(-1000)
        assertThat(entity.completedAt).isNotNull()
        verify { jpaRepository.save(entity) }
    }

    @Test
    fun `should return null when updating non-existent job`() {
        val organizationId = UUID.randomUUID()
        val jobId = UUID.randomUUID()

        every { jpaRepository.findByIdAndOrganizationId(jobId, organizationId) } returns null

        val result = adapter.updateStatus(jobId, JobStatus.RUNNING, organizationId)

        assertThat(result).isNull()
        verify(exactly = 0) { jpaRepository.save(any()) }
    }

    private fun createJob(): VrpJob =
        VrpJob(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            status = JobStatus.PENDING,
            orderIds = listOf(UUID.randomUUID()),
            vehicleIds = listOf(UUID.randomUUID()),
            hardScore = null,
            softScore = null,
            errorMessage = null,
            createdAt = LocalDateTime.now(),
            startedAt = null,
            completedAt = null
        )

    private fun createJobEntity(organizationId: UUID = UUID.randomUUID()): VrpJobEntity {
        val entity =
            VrpJobEntity(
                organizationId = organizationId,
                status = JobStatus.PENDING
            )
        // Add required orders and vehicles to satisfy validation
        entity.orders.add(createOrderEntity(organizationId))
        entity.vehicles.add(createVehicleEntity(organizationId))
        return entity
    }

    private fun createOrderEntity(organizationId: UUID = UUID.randomUUID()): OrderEntity =
        OrderEntity(
            organizationId = organizationId,
            customerId = UUID.randomUUID(),
            customerName = "Test Customer",
            customerLat = 40.7128,
            customerLon = -74.0060,
            deliveryLat = 40.7589,
            deliveryLon = -73.9851,
            timeWindowStart = LocalDateTime.now(),
            timeWindowEnd = LocalDateTime.now().plusHours(4),
            serviceDurationMinutes = 30
        )

    private fun createVehicleEntity(organizationId: UUID = UUID.randomUUID()): VehicleEntity =
        VehicleEntity(
            organizationId = organizationId,
            name = "Test Vehicle",
            licensePlate = "ABC123",
            weightCapacity = BigDecimal("1000.0"),
            volumeCapacity = BigDecimal("50.0"),
            startLat = 40.7128,
            startLon = -74.0060,
            endLat = 40.7128,
            endLon = -74.0060,
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0)
        )
}
