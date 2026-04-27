package com.vrp.domain.port

import com.vrp.domain.model.Order
import java.util.UUID

/**
 * Repository port for Order domain model.
 */
interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: UUID, organizationId: UUID): Order?
    fun findAllByOrganizationId(organizationId: UUID): List<Order>
    fun findAllByIds(ids: List<UUID>, organizationId: UUID): List<Order>
    fun deleteById(id: UUID, organizationId: UUID)
}
