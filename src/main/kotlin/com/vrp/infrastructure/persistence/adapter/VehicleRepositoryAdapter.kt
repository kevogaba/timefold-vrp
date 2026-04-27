package com.vrp.infrastructure.persistence.adapter

import com.vrp.domain.model.Vehicle
import com.vrp.domain.port.VehicleRepository
import com.vrp.infrastructure.persistence.mapper.VehicleMapper
import com.vrp.infrastructure.persistence.repository.VehicleJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@Transactional
class VehicleRepositoryAdapter(
    private val jpaRepository: VehicleJpaRepository,
    private val mapper: VehicleMapper
) : VehicleRepository {

    override fun save(vehicle: Vehicle): Vehicle {
        val entity = mapper.toEntity(vehicle)
        val saved = jpaRepository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun findById(id: UUID, organizationId: UUID): Vehicle? {
        return jpaRepository.findByIdAndOrganizationId(id, organizationId)
            ?.let { mapper.toDomain(it) }
    }

    override fun findAllByOrganizationId(organizationId: UUID): List<Vehicle> {
        return jpaRepository.findAllByOrganizationId(organizationId)
            .map { mapper.toDomain(it) }
    }

    override fun findAllByIds(ids: List<UUID>, organizationId: UUID): List<Vehicle> {
        return jpaRepository.findAllByIdInAndOrganizationId(ids, organizationId)
            .map { mapper.toDomain(it) }
    }

    override fun deleteById(id: UUID, organizationId: UUID) {
        jpaRepository.deleteByIdAndOrganizationId(id, organizationId)
    }
}
