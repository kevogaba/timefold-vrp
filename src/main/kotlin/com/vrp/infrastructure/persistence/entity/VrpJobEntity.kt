package com.vrp.infrastructure.persistence.entity

import com.vrp.domain.model.JobStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "vrp_jobs")
@EntityListeners(AuditingEntityListener::class)
data class VrpJobEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val organizationId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: JobStatus,

    var hardScore: Int? = null,
    var softScore: Int? = null,

    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null,

    @ManyToMany
    @JoinTable(
        name = "job_orders",
        joinColumns = [JoinColumn(name = "job_id")],
        inverseJoinColumns = [JoinColumn(name = "order_id")]
    )
    val orders: MutableSet<OrderEntity> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "job_vehicles",
        joinColumns = [JoinColumn(name = "job_id")],
        inverseJoinColumns = [JoinColumn(name = "vehicle_id")]
    )
    val vehicles: MutableSet<VehicleEntity> = mutableSetOf(),

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    var startedAt: LocalDateTime? = null,
    var completedAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
