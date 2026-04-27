package com.example.vrp.order.domain

import com.example.vrp.shared.Capacity
import com.example.vrp.shared.Money

data class LineItem(
    val id: String,
    val description: String,
    val quantity: Int,
    val unitPrice: Money,
    val demand: Capacity,
) {
    init {
        require(quantity > 0) { "Quantity must be positive" }
    }
}
