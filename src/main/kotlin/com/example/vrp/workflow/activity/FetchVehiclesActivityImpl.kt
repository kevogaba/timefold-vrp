package com.example.vrp.workflow.activity

import com.example.vrp.vehicle.domain.Vehicle
import com.example.vrp.vehicle.port.out.VehicleRepository
import org.springframework.stereotype.Component

@Component
class FetchVehiclesActivityImpl(private val vehicleRepository: VehicleRepository) : FetchVehiclesActivity {
    override fun fetchVehicles(organizationId: String): List<Vehicle> =
        vehicleRepository.findAllByOrganizationId(organizationId)
}
