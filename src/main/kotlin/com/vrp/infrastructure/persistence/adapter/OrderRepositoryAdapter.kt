package com.vrp.infrastructure.persistence.adapter

import com.vrp.domain.model.Order
import com.vrp.domain.port.OrderRepository
import com.vrp.infrastructure.persistence.mapper.OrderMapper
import com.vrp.infrastructure.persistence.repository.OrderJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@Transactional
class OrderRepositoryAdapter(
    private val jpaRepository: OrderJpaRepository,
    private val mapper: OrderMapper
) : OrderRepository {

    override fun save(order: Order): Order {
        val entity = mapper.toEntity(order)
        val saved = jpaRepository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun findById(id: UUID, organizationId: UUID): Order? {
        return jpaRepository.findByIdAndOrganizationId(id, organizationId)
            ?.let { mapper.toDomain(it) }
    }

    override fun findAllByOrganizationId(organizationId: UUID): List<Order> {
        return jpaRepository.findAllByOrganizationId(organizationId)
            .map { mapper.toDomain(it) }
    }

    override fun findAllByIds(ids: List<UUID>, organizationId: UUID): List<Order> {
        return jpaRepository.findAllByIdInAndOrganizationId(ids, organizationId)
            .map { mapper.toDomain(it) }
    }

    override fun deleteById(id: UUID, organizationId: UUID) {
        jpaRepository.deleteByIdAndOrganizationId(id, organizationId)
    }
}
