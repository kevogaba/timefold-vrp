package com.vrp.infrastructure.persistence.mapper

import com.vrp.domain.model.Driver
import com.vrp.domain.model.Location
import com.vrp.domain.model.Vehicle
import com.vrp.infrastructure.persistence.entity.VehicleEntity
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class VehicleMapper {

    fun toDomain(entity: VehicleEntity): Vehicle {
        return Vehicle(
            id = entity.id,
            organizationId = entity.organizationId,
            name = entity.name,
            licensePlate = entity.licensePlate,
            weightCapacity = entity.weightCapacity,
            volumeCapacity = entity.volumeCapacity,
            startLocation = Location(entity.startLat, entity.startLon),
            endLocation = Location(entity.endLat, entity.endLon),
            availableFrom = entity.availableFrom,
            availableUntil = entity.availableUntil,
            driver = if (entity.driverId != null && entity.driverName != null && entity.driverLicense != null) {
                Driver(
                    id = entity.driverId,
                    organizationId = entity.organizationId,
                    name = entity.driverName,
                    licenseNumber = entity.driverLicense,
                    phoneNumber = entity.driverPhone,
                    email = entity.driverEmail
                )
            } else null,
            costPerKm = entity.costPerKm
        )
    }

    fun toEntity(domain: Vehicle): VehicleEntity {
        return VehicleEntity(
            id = domain.id,
            organizationId = domain.organizationId,
            name = domain.name,
            licensePlate = domain.licensePlate,
            weightCapacity = domain.weightCapacity,
            volumeCapacity = domain.volumeCapacity,
            startLat = domain.startLocation.latitude,
            startLon = domain.startLocation.longitude,
            endLat = domain.endLocation.latitude,
            endLon = domain.endLocation.longitude,
            availableFrom = domain.availableFrom,
            availableUntil = domain.availableUntil,
            driverId = domain.driver?.id,
            driverName = domain.driver?.name,
            driverLicense = domain.driver?.licenseNumber,
            driverPhone = domain.driver?.phoneNumber,
            driverEmail = domain.driver?.email,
            costPerKm = domain.costPerKm
        )
    }
}
