package com.example.vrp.order.port.out

import com.example.vrp.order.domain.Order

interface OrderRepository {
    fun findById(id: String): Order?
    fun findAllByOrganizationId(organizationId: String): List<Order>
    fun save(order: Order): Order
}
