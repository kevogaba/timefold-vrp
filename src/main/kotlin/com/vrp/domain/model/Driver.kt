package com.vrp.domain.model

import java.util.UUID

/**
 * Driver assigned to a vehicle.
 */
data class Driver(
    val id: UUID,
    val organizationId: UUID,
    val name: String,
    val licenseNumber: String,
    val phoneNumber: String?,
    val email: String?
) {
    init {
        require(name.isNotBlank()) { "Driver name cannot be blank" }
        require(licenseNumber.isNotBlank()) { "License number cannot be blank" }
    }
}
