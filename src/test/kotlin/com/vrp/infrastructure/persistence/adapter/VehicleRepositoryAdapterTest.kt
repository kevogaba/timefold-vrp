package com.vrp.infrastructure.persistence.adapter

import com.vrp.domain.model.*
import com.vrp.infrastructure.persistence.entity.VehicleEntity
import com.vrp.infrastructure.persistence.mapper.VehicleMapper
import com.vrp.infrastructure.persistence.repository.VehicleJpaRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID

class VehicleRepositoryAdapterTest {

    private lateinit var jpaRepository: VehicleJpaRepository
    private lateinit var mapper: VehicleMapper
    private lateinit var adapter: VehicleRepositoryAdapter

    @BeforeEach
    fun setup() {
        jpaRepository = mockk()
        mapper = VehicleMapper()
        adapter = VehicleRepositoryAdapter(jpaRepository, mapper)
    }

    @Test
    fun `should save vehicle successfully`() {
        val vehicle = createVehicle()
        val entity = mapper.toEntity(vehicle)

        every { jpaRepository.save(any()) } returns entity

        val result = adapter.save(vehicle)

        assertThat(result.id).isNotNull()
        verify { jpaRepository.save(any()) }
    }

    @Test
    fun `should find vehicle by id and organizationId`() {
        val organizationId = UUID.randomUUID()
        val vehicleId = UUID.randomUUID()
        val entity = createVehicleEntity(organizationId = organizationId)

        every { jpaRepository.findByIdAndOrganizationId(vehicleId, organizationId) } returns entity

        val result = adapter.findById(vehicleId, organizationId)

        assertThat(result).isNotNull
        assertThat(result?.organizationId).isEqualTo(organizationId)
        verify { jpaRepository.findByIdAndOrganizationId(vehicleId, organizationId) }
    }

    @Test
    fun `should return null when vehicle not found`() {
        val organizationId = UUID.randomUUID()
        val vehicleId = UUID.randomUUID()

        every { jpaRepository.findByIdAndOrganizationId(vehicleId, organizationId) } returns null

        val result = adapter.findById(vehicleId, organizationId)

        assertThat(result).isNull()
    }

    @Test
    fun `should find all vehicles by organizationId`() {
        val organizationId = UUID.randomUUID()
        val entities = listOf(
            createVehicleEntity(organizationId = organizationId),
            createVehicleEntity(organizationId = organizationId)
        )

        every { jpaRepository.findAllByOrganizationId(organizationId) } returns entities

        val results = adapter.findAllByOrganizationId(organizationId)

        assertThat(results).hasSize(2)
        assertThat(results.all { it.organizationId == organizationId }).isTrue()
        verify { jpaRepository.findAllByOrganizationId(organizationId) }
    }

    @Test
    fun `should find all vehicles by ids and organizationId`() {
        val organizationId = UUID.randomUUID()
        val ids = listOf(UUID.randomUUID(), UUID.randomUUID())
        val entities = ids.map { createVehicleEntity(organizationId = organizationId) }

        every { jpaRepository.findAllByIdInAndOrganizationId(ids, organizationId) } returns entities

        val results = adapter.findAllByIds(ids, organizationId)

        assertThat(results).hasSize(2)
        verify { jpaRepository.findAllByIdInAndOrganizationId(ids, organizationId) }
    }

    @Test
    fun `should delete vehicle by id and organizationId`() {
        val organizationId = UUID.randomUUID()
        val vehicleId = UUID.randomUUID()

        every { jpaRepository.deleteByIdAndOrganizationId(vehicleId, organizationId) } just Runs

        adapter.deleteById(vehicleId, organizationId)

        verify { jpaRepository.deleteByIdAndOrganizationId(vehicleId, organizationId) }
    }

    private fun createVehicle(): Vehicle {
        return Vehicle(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            name = "Test Vehicle",
            licensePlate = "ABC123",
            weightCapacity = BigDecimal("1000.0"),
            volumeCapacity = BigDecimal("50.0"),
            startLocation = Location(40.7128, -74.0060),
            endLocation = Location(40.7128, -74.0060),
            availableFrom = LocalTime.of(8, 0),
            availableUntil = LocalTime.of(18, 0),
            driver = null,
            costPerKm = BigDecimal("0.50")
        )
    }

    private fun createVehicleEntity(organizationId: UUID = UUID.randomUUID()): VehicleEntity {
        return VehicleEntity(
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
}
