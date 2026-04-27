package com.example.vrp.order.adapter.out.persistence

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
@Table(name = "orders")
@EntityListeners(AuditingEntityListener::class)
class OrderJpaEntity(
    @Id val id: String,
    @Column(nullable = false) val organizationId: String,
    @Column(nullable = false) val customerId: String,
    @Column(nullable = false) val customerName: String,
    @Column(nullable = false) val customerEmail: String,
    @Column(nullable = false) val customerAddress: String,
    @Column(nullable = false) val customerLatitude: Double,
    @Column(nullable = false) val customerLongitude: Double,
    @Column(nullable = false) val deliveryWindowStart: Instant,
    @Column(nullable = false) val deliveryWindowEnd: Instant,
    @Version val version: Long = 0,
    @CreatedDate @Column(nullable = false, updatable = false) var createdAt: Instant? = null,
    @LastModifiedDate @Column(nullable = false) var updatedAt: Instant? = null,
)
