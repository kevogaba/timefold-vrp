package com.vrp.domain.model

import java.math.BigDecimal
import java.util.UUID

/**
 * Individual item in an order.
 */
data class LineItem(
    val id: UUID,
    val name: String,
    val quantity: Int,
    val weight: BigDecimal, // kg
    val volume: BigDecimal, // cubic meters
    val price: BigDecimal
) {
    init {
        require(name.isNotBlank()) { "Line item name cannot be blank" }
        require(quantity > 0) { "Quantity must be positive" }
        require(weight >= BigDecimal.ZERO) { "Weight cannot be negative" }
        require(volume >= BigDecimal.ZERO) { "Volume cannot be negative" }
        require(price >= BigDecimal.ZERO) { "Price cannot be negative" }
    }
}
