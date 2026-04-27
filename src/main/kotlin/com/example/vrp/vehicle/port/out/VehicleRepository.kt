package com.example.vrp.vehicle.port.out

import com.example.vrp.vehicle.domain.Vehicle

interface VehicleRepository {
    fun findById(id: String): Vehicle?
    fun findAllByOrganizationId(organizationId: String): List<Vehicle>
    fun save(vehicle: Vehicle): Vehicle
}
