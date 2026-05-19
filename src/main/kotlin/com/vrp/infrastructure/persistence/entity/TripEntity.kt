package com.vrp.infrastructure.persistence.entity

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "trips")
@EntityListeners(AuditingEntityListener::class)
data class TripEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false, unique = true)
    val guid: UUID = UuidCreator.getTimeOrderedEpoch(),
    @Column(nullable = false)
    val organizationId: UUID,
    @Column(nullable = false)
    val jobId: UUID,
    @Column(nullable = false)
    val vehicleId: UUID,
    @OneToMany(mappedBy = "trip", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sequenceNumber ASC")
    val visits: MutableList<VisitEntity> = mutableListOf(),
    @Column(nullable = false)
    val totalDistanceMeters: Long,
    @Column(nullable = false)
    val totalDurationMinutes: Int,
    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
