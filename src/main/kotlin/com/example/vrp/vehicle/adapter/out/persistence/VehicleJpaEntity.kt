package com.example.vrp.vehicle.adapter.out.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.time.LocalTime

@Entity
@Table(name = "vehicles")
@EntityListeners(AuditingEntityListener::class)
class VehicleJpaEntity(
    @Id val id: String,
    @Column(nullable = false) val organizationId: String,
    @Column(nullable = false) val licensePlate: String,
    @Column(nullable = false) val capacityWeightKg: Double,
    @Column(nullable = false) val capacityVolumeLiters: Double,
    @Column(nullable = false) val driverId: String,
    @Column(nullable = false) val driverName: String,
    @Column(nullable = false) val driverLicense: String,
    @Column(nullable = false) val workStart: LocalTime,
    @Column(nullable = false) val workEnd: LocalTime,
    @Version val version: Long = 0,
    @CreatedDate @Column(nullable = false, updatable = false) var createdAt: Instant? = null,
    @LastModifiedDate @Column(nullable = false) var updatedAt: Instant? = null,
)
