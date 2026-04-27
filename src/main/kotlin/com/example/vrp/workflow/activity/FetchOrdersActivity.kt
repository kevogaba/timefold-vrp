package com.example.vrp.workflow.activity

import com.example.vrp.order.domain.Order
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface FetchOrdersActivity {
    @ActivityMethod
    fun fetchOrders(organizationId: String, orderIds: List<String>): List<Order>
}
