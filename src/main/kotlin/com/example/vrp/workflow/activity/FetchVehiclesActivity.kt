package com.example.vrp.workflow.activity

import com.example.vrp.vehicle.domain.Vehicle
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface FetchVehiclesActivity {
    @ActivityMethod
    fun fetchVehicles(organizationId: String): List<Vehicle>
}
