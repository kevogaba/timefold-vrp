package com.vrp.infrastructure.persistence.entity

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener::class)
data class OrderEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false, unique = true)
    val guid: UUID = UuidCreator.getTimeOrderedEpoch(),
    @Column(nullable = false)
    val organizationId: UUID,
    @Column(nullable = false)
    val customerId: UUID,
    @Column(nullable = false)
    val customerName: String,
    val customerPhone: String? = null,
    val customerEmail: String? = null,
    @Column(nullable = false)
    val customerLat: Double,
    @Column(nullable = false)
    val customerLon: Double,
    val pickupLat: Double? = null,
    val pickupLon: Double? = null,
    @Column(nullable = false)
    val deliveryLat: Double,
    @Column(nullable = false)
    val deliveryLon: Double,
    @Column(nullable = false)
    val timeWindowStart: LocalDateTime,
    @Column(nullable = false)
    val timeWindowEnd: LocalDateTime,
    @Column(nullable = false)
    val serviceDurationMinutes: Int,
    @Column(nullable = false)
    val priority: Int = 0,
    @Column(columnDefinition = "TEXT")
    val notes: String? = null,
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val lineItems: MutableList<LineItemEntity> = mutableListOf(),
    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
