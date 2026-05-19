package com.vrp.infrastructure.persistence.entity

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "vehicles")
@EntityListeners(AuditingEntityListener::class)
data class VehicleEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false, unique = true)
    val guid: UUID = UuidCreator.getTimeOrderedEpoch(),
    @Column(nullable = false)
    val organizationId: UUID,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val licensePlate: String,
    @Column(nullable = false, precision = 10, scale = 2)
    val weightCapacity: BigDecimal,
    @Column(nullable = false, precision = 10, scale = 2)
    val volumeCapacity: BigDecimal,
    @Column(nullable = false)
    val startLat: Double,
    @Column(nullable = false)
    val startLon: Double,
    @Column(nullable = false)
    val endLat: Double,
    @Column(nullable = false)
    val endLon: Double,
    @Column(nullable = false)
    val availableFrom: LocalTime,
    @Column(nullable = false)
    val availableUntil: LocalTime,
    val driverId: UUID? = null,
    val driverName: String? = null,
    val driverLicense: String? = null,
    val driverPhone: String? = null,
    val driverEmail: String? = null,
    @Column(nullable = false, precision = 10, scale = 2)
    val costPerKm: BigDecimal = BigDecimal.ZERO,
    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
