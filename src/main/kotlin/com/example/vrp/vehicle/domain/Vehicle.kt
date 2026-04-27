package com.example.vrp.vehicle.domain

import com.example.vrp.shared.Capacity
import com.example.vrp.shared.WorkingHours

data class Vehicle(
    val id: String,
    val organizationId: String,
    val licensePlate: String,
    val capacity: Capacity,
    val driver: Driver,
    val workingHours: WorkingHours,
)
