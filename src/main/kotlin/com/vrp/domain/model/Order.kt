package com.vrp.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Order representing a delivery or pickup request.
 */
data class Order(
    val id: UUID,
    val organizationId: UUID,
    val customer: Customer,
    val lineItems: List<LineItem>,
    val pickupLocation: Location?,
    val deliveryLocation: Location,
    val timeWindowStart: LocalDateTime,
    val timeWindowEnd: LocalDateTime,
    val serviceDurationMinutes: Int,
    val priority: Int = 0,
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(lineItems.isNotEmpty()) { "Order must have at least one line item" }
        require(timeWindowEnd.isAfter(timeWindowStart)) { "Time window end must be after start" }
        require(serviceDurationMinutes >= 0) { "Service duration cannot be negative" }
        require(priority >= 0) { "Priority cannot be negative" }
    }

    /**
     * Total weight of all line items.
     */
    val totalWeight: BigDecimal
        get() = lineItems.sumOf { it.weight * it.quantity.toBigDecimal() }

    /**
     * Total volume of all line items.
     */
    val totalVolume: BigDecimal
        get() = lineItems.sumOf { it.volume * it.quantity.toBigDecimal() }

    /**
     * Total value of the order.
     */
    val totalValue: BigDecimal
        get() = lineItems.sumOf { it.price * it.quantity.toBigDecimal() }
}
