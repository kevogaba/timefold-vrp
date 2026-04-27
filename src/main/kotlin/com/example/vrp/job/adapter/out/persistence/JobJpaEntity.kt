package com.example.vrp.job.adapter.out.persistence

import com.example.vrp.job.domain.JobStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "jobs")
@EntityListeners(AuditingEntityListener::class)
class JobJpaEntity(
    @Id val id: String,
    @Column(nullable = false) val organizationId: String,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var status: JobStatus,
    @Column(nullable = false) val submittedAt: Instant,
    var completedAt: Instant? = null,
    var errorMessage: String? = null,
    @Version val version: Long = 0,
    @CreatedDate @Column(nullable = false, updatable = false) var createdAt: Instant? = null,
    @LastModifiedDate @Column(nullable = false) var updatedAt: Instant? = null,
)
