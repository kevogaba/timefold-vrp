package com.vrp.infrastructure.persistence.adapter

import com.vrp.domain.model.JobStatus
import com.vrp.domain.model.VrpJob
import com.vrp.domain.port.VrpJobRepository
import com.vrp.infrastructure.persistence.mapper.VrpJobMapper
import com.vrp.infrastructure.persistence.repository.OrderJpaRepository
import com.vrp.infrastructure.persistence.repository.VehicleJpaRepository
import com.vrp.infrastructure.persistence.repository.VrpJobJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Component
@Transactional
class VrpJobRepositoryAdapter(
    private val jpaRepository: VrpJobJpaRepository,
    private val orderJpaRepository: OrderJpaRepository,
    private val vehicleJpaRepository: VehicleJpaRepository,
    private val mapper: VrpJobMapper
) : VrpJobRepository {
    override fun save(job: VrpJob): VrpJob {
        val entity = mapper.toEntity(job)

        // Load and attach orders and vehicles
        val orders = orderJpaRepository.findAllByIdInAndOrganizationId(job.orderIds, job.organizationId)
        val vehicles = vehicleJpaRepository.findAllByIdInAndOrganizationId(job.vehicleIds, job.organizationId)

        entity.orders.addAll(orders)
        entity.vehicles.addAll(vehicles)

        val saved = jpaRepository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun findById(
        id: UUID,
        organizationId: UUID
    ): VrpJob? =
        jpaRepository
            .findByIdAndOrganizationId(id, organizationId)
            ?.let { mapper.toDomain(it) }

    override fun findAllByOrganizationId(organizationId: UUID): List<VrpJob> =
        jpaRepository
            .findAllByOrganizationId(organizationId)
            .map { mapper.toDomain(it) }

    override fun updateStatus(
        id: UUID,
        status: JobStatus,
        organizationId: UUID
    ): VrpJob? {
        val entity = jpaRepository.findByIdAndOrganizationId(id, organizationId) ?: return null
        entity.status = status

        when (status) {
            JobStatus.RUNNING -> entity.startedAt = LocalDateTime.now()
            JobStatus.COMPLETED, JobStatus.FAILED, JobStatus.CANCELLED -> entity.completedAt = LocalDateTime.now()
            else -> {}
        }

        val saved = jpaRepository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun updateWithScore(
        id: UUID,
        status: JobStatus,
        hardScore: Int,
        softScore: Int,
        organizationId: UUID
    ): VrpJob? {
        val entity = jpaRepository.findByIdAndOrganizationId(id, organizationId) ?: return null
        entity.status = status
        entity.hardScore = hardScore
        entity.softScore = softScore
        entity.completedAt = LocalDateTime.now()

        val saved = jpaRepository.save(entity)
        return mapper.toDomain(saved)
    }
}
