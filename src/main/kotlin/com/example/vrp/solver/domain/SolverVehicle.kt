package com.example.vrp.solver.domain

import com.example.vrp.shared.Capacity
import com.example.vrp.shared.Location
import com.example.vrp.shared.WorkingHours

data class SolverVehicle(
    val id: String,
    val depotLocation: Location,
    val capacity: Capacity,
    val workingHours: WorkingHours,
)
