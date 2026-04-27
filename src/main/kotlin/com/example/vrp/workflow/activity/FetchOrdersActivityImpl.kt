package com.example.vrp.workflow.activity

import com.example.vrp.order.domain.Order
import com.example.vrp.order.port.out.OrderRepository
import org.springframework.stereotype.Component

@Component
class FetchOrdersActivityImpl(private val orderRepository: OrderRepository) : FetchOrdersActivity {
    override fun fetchOrders(organizationId: String, orderIds: List<String>): List<Order> =
        orderIds.mapNotNull { orderRepository.findById(it) }
}
