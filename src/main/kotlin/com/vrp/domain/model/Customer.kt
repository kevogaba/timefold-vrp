package com.vrp.domain.model

import java.util.UUID

/**
 * Customer information for an order.
 */
data class Customer(
    val id: UUID,
    val organizationId: UUID,
    val name: String,
    val phoneNumber: String?,
    val email: String?,
    val location: Location
) {
    init {
        require(name.isNotBlank()) { "Customer name cannot be blank" }
    }
}
