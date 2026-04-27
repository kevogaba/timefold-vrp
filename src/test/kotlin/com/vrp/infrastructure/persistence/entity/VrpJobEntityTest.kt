package com.vrp.infrastructure.persistence.entity

import com.vrp.domain.model.JobStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class VrpJobEntityTest {

    @Test
    fun `should create VRP job with required fields`() {
        val organizationId = UUID.randomUUID()

        val job = VrpJobEntity(
            organizationId = organizationId,
            status = JobStatus.PENDING
        )

        assertThat(job.id).isNotNull()
        assertThat(job.guid).isNotNull()
        assertThat(job.organizationId).isEqualTo(organizationId)
        assertThat(job.status).isEqualTo(JobStatus.PENDING)
        assertThat(job.hardScore).isNull()
        assertThat(job.softScore).isNull()
        assertThat(job.errorMessage).isNull()
        assertThat(job.orders).isEmpty()
        assertThat(job.vehicles).isEmpty()
        assertThat(job.startedAt).isNull()
        assertThat(job.completedAt).isNull()
    }

    @Test
    fun `should update job status and scores`() {
        val job = VrpJobEntity(
            organizationId = UUID.randomUUID(),
            status = JobStatus.PENDING
        )

        job.status = JobStatus.COMPLETED
        job.hardScore = 0
        job.softScore = -1000
        job.completedAt = LocalDateTime.now()

        assertThat(job.status).isEqualTo(JobStatus.COMPLETED)
        assertThat(job.hardScore).isEqualTo(0)
        assertThat(job.softScore).isEqualTo(-1000)
        assertThat(job.completedAt).isNotNull()
    }

    @Test
    fun `should handle failed job with error message`() {
        val job = VrpJobEntity(
            organizationId = UUID.randomUUID(),
            status = JobStatus.PENDING
        )

        job.status = JobStatus.FAILED
        job.errorMessage = "Solver failed to find solution"

        assertThat(job.status).isEqualTo(JobStatus.FAILED)
        assertThat(job.errorMessage).isEqualTo("Solver failed to find solution")
    }

    @Test
    fun `should generate unique guid for each job`() {
        val jobs = (1..5).map {
            VrpJobEntity(
                organizationId = UUID.randomUUID(),
                status = JobStatus.PENDING
            )
        }

        val guids = jobs.map { it.guid }.toSet()
        assertThat(guids).hasSize(5)
    }
}
