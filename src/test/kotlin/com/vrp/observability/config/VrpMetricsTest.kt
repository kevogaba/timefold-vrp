package com.vrp.observability.config

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class VrpMetricsTest {
    @Test
    fun `should record business metrics`() {
        val registry = SimpleMeterRegistry()
        val metrics = VrpMetrics(registry)
        val organizationId = UUID.randomUUID()

        metrics.recordJobSubmitted(organizationId)
        assertThat(
            registry
                .get("vrp.job.submitted")
                .tag("organization_id", organizationId.toString())
                .counter()
                .count()
        ).isEqualTo(1.0)

        metrics.recordJobCompleted(organizationId, success = true)
        assertThat(
            registry
                .get("vrp.job.completed")
                .tag("organization_id", organizationId.toString())
                .tag("success", "true")
                .counter()
                .count()
        ).isEqualTo(1.0)

        metrics.recordSolverDuration(organizationId, durationMillis = 250)
        assertThat(
            registry
                .get("vrp.solver.duration.seconds")
                .tag("organization_id", organizationId.toString())
                .timer()
                .count()
        ).isEqualTo(1L)

        metrics.recordSolverScore(organizationId, hardScore = 7, softScore = -42)
        assertThat(
            registry
                .get("vrp.solver.score.hard")
                .tag("organization_id", organizationId.toString())
                .gauge()
                .value()
        ).isEqualTo(7.0)
        assertThat(
            registry
                .get("vrp.solver.score.soft")
                .tag("organization_id", organizationId.toString())
                .gauge()
                .value()
        ).isEqualTo(-42.0)
    }
}
