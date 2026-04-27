package com.vrp.infrastructure.persistence.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "audit_log")
@EntityListeners(AuditingEntityListener::class)
data class AuditLogEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val organizationId: UUID,

    @Column(nullable = false)
    val entityType: String,

    @Column(nullable = false)
    val entityId: UUID,

    @Column(nullable = false)
    val action: String,

    val userId: UUID? = null,

    @Column(columnDefinition = "JSONB")
    val changes: String? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
