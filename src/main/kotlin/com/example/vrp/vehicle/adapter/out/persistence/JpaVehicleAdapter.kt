package com.example.vrp.vehicle.adapter.out.persistence

import com.example.vrp.shared.Capacity
import com.example.vrp.shared.WorkingHours
import com.example.vrp.vehicle.domain.Driver
import com.example.vrp.vehicle.domain.Vehicle
import com.example.vrp.vehicle.port.out.VehicleRepository
import org.springframework.stereotype.Component

@Component
class JpaVehicleAdapter(private val repo: VehicleJpaRepository) : VehicleRepository {

    override fun findById(id: String): Vehicle? = repo.findById(id).orElse(null)?.toDomain()

    override fun findAllByOrganizationId(organizationId: String): List<Vehicle> =
        repo.findAllByOrganizationId(organizationId).map { it.toDomain() }

    override fun save(vehicle: Vehicle): Vehicle = repo.save(vehicle.toEntity()).toDomain()

    private fun VehicleJpaEntity.toDomain() = Vehicle(
        id = id,
        organizationId = organizationId,
        licensePlate = licensePlate,
        capacity = Capacity(capacityWeightKg, capacityVolumeLiters),
        driver = Driver(driverId, driverName, driverLicense),
        workingHours = WorkingHours(workStart, workEnd),
    )

    private fun Vehicle.toEntity() = VehicleJpaEntity(
        id = id,
        organizationId = organizationId,
        licensePlate = licensePlate,
        capacityWeightKg = capacity.weightKg,
        capacityVolumeLiters = capacity.volumeLiters,
        driverId = driver.id,
        driverName = driver.name,
        driverLicense = driver.licenseNumber,
        workStart = workingHours.start,
        workEnd = workingHours.end,
    )
}
