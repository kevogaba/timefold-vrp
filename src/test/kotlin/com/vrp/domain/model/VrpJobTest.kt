package com.vrp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class VrpJobTest {

    @Test
    fun `should create pending job`() {
        val id = UUID.randomUUID()
        val organizationId = UUID.randomUUID()
        val orderIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val vehicleIds = listOf(UUID.randomUUID())

        val job = VrpJob(
            id = id,
            organizationId = organizationId,
            status = JobStatus.PENDING,
            orderIds = orderIds,
            vehicleIds = vehicleIds
        )

        assertThat(job.id).isEqualTo(id)
        assertThat(job.organizationId).isEqualTo(organizationId)
        assertThat(job.status).isEqualTo(JobStatus.PENDING)
        assertThat(job.orderIds).isEqualTo(orderIds)
        assertThat(job.vehicleIds).isEqualTo(vehicleIds)
        assertThat(job.hardScore).isNull()
        assertThat(job.softScore).isNull()
        assertThat(job.errorMessage).isNull()
        assertThat(job.startedAt).isNull()
        assertThat(job.completedAt).isNull()
    }

    @Test
    fun `should create completed job with scores`() {
        val job = VrpJob(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            status = JobStatus.COMPLETED,
            orderIds = listOf(UUID.randomUUID()),
            vehicleIds = listOf(UUID.randomUUID()),
            hardScore = 0,
            softScore = -1000,
            startedAt = LocalDateTime.now().minusMinutes(10),
            completedAt = LocalDateTime.now()
        )

        assertThat(job.status).isEqualTo(JobStatus.COMPLETED)
        assertThat(job.hardScore).isEqualTo(0)
        assertThat(job.softScore).isEqualTo(-1000)
        assertThat(job.startedAt).isNotNull()
        assertThat(job.completedAt).isNotNull()
    }

    @Test
    fun `should create failed job with error message`() {
        val errorMessage = "Solver timeout"

        val job = VrpJob(
            id = UUID.randomUUID(),
            organizationId = UUID.randomUUID(),
            status = JobStatus.FAILED,
            orderIds = listOf(UUID.randomUUID()),
            vehicleIds = listOf(UUID.randomUUID()),
            errorMessage = errorMessage,
            startedAt = LocalDateTime.now().minusMinutes(5),
            completedAt = LocalDateTime.now()
        )

        assertThat(job.status).isEqualTo(JobStatus.FAILED)
        assertThat(job.errorMessage).isEqualTo(errorMessage)
        assertThat(job.hardScore).isNull()
        assertThat(job.softScore).isNull()
    }

    @Test
    fun `should fail when orderIds is empty`() {
        assertThatThrownBy {
            VrpJob(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                status = JobStatus.PENDING,
                orderIds = emptyList(),
                vehicleIds = listOf(UUID.randomUUID())
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Job must include at least one order")
    }

    @Test
    fun `should fail when vehicleIds is empty`() {
        assertThatThrownBy {
            VrpJob(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                status = JobStatus.PENDING,
                orderIds = listOf(UUID.randomUUID()),
                vehicleIds = emptyList()
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Job must include at least one vehicle")
    }
}
