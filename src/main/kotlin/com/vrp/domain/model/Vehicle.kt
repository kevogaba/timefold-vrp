package com.vrp.domain.model

import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID

/**
 * Vehicle available for routing.
 */
data class Vehicle(
    val id: UUID,
    val organizationId: UUID,
    val name: String,
    val licensePlate: String,
    val weightCapacity: BigDecimal, // kg
    val volumeCapacity: BigDecimal, // cubic meters
    val startLocation: Location,
    val endLocation: Location,
    val availableFrom: LocalTime,
    val availableUntil: LocalTime,
    val driver: Driver?,
    val costPerKm: BigDecimal = BigDecimal.ZERO
) {
    init {
        require(name.isNotBlank()) { "Vehicle name cannot be blank" }
        require(licensePlate.isNotBlank()) { "License plate cannot be blank" }
        require(weightCapacity > BigDecimal.ZERO) { "Weight capacity must be positive" }
        require(volumeCapacity > BigDecimal.ZERO) { "Volume capacity must be positive" }
        require(availableUntil.isAfter(availableFrom)) { "Available until must be after available from" }
        require(costPerKm >= BigDecimal.ZERO) { "Cost per km cannot be negative" }
    }
}
