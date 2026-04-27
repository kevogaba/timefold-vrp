package com.example.vrp.order.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface OrderJpaRepository : JpaRepository<OrderJpaEntity, String> {
    fun findAllByOrganizationId(organizationId: String): List<OrderJpaEntity>
}
