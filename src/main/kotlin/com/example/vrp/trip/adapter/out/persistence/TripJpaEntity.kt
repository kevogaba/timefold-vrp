package com.example.vrp.trip.adapter.out.persistence

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

@Entity
@Table(name = "trips")
@EntityListeners(AuditingEntityListener::class)
class TripJpaEntity(
    @Id val id: String,
    @Column(nullable = false) val jobId: String,
    @Column(nullable = false) val organizationId: String,
    @Column(nullable = false) val vehicleId: String,
    @Column(nullable = false) val totalDistanceKm: Double,
    @Version val version: Long = 0,
    @CreatedDate @Column(nullable = false, updatable = false) var createdAt: Instant? = null,
    @LastModifiedDate @Column(nullable = false) var updatedAt: Instant? = null,
)
