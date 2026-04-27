package com.example.vrp.vehicle.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface VehicleJpaRepository : JpaRepository<VehicleJpaEntity, String> {
    fun findAllByOrganizationId(organizationId: String): List<VehicleJpaEntity>
}
