package com.example.vrp.order.domain

import com.example.vrp.shared.TimeWindow
import java.time.Instant

data class Order(
    val id: String,
    val organizationId: String,
    val customer: Customer,
    val lineItems: List<LineItem>,
    val deliveryWindow: TimeWindow,
    val createdAt: Instant = Instant.now(),
) {
    init {
        require(lineItems.isNotEmpty()) { "Order must have at least one line item" }
    }
}
