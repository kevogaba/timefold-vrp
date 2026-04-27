package com.vrp.observability.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Custom VRP business metrics.
 */
@Component
class VrpMetrics(
    private val meterRegistry: MeterRegistry
) {

    fun recordJobSubmitted(organizationId: UUID) {
        meterRegistry.counter(
            "vrp.job.submitted",
            "organization_id", organizationId.toString()
        ).increment()
    }

    fun recordJobCompleted(organizationId: UUID, success: Boolean) {
        meterRegistry.counter(
            "vrp.job.completed",
            "organization_id", organizationId.toString(),
            "success", success.toString()
        ).increment()
    }

    fun recordSolverDuration(organizationId: UUID, durationMillis: Long) {
        meterRegistry.timer(
            "vrp.solver.duration.seconds",
            "organization_id", organizationId.toString()
        ).record(durationMillis, TimeUnit.MILLISECONDS)
    }

    fun recordSolverScore(organizationId: UUID, hardScore: Int, softScore: Int) {
        meterRegistry.gauge(
            "vrp.solver.score.hard",
            listOf(io.micrometer.core.instrument.Tag.of("organization_id", organizationId.toString())),
            hardScore
        )
        meterRegistry.gauge(
            "vrp.solver.score.soft",
            listOf(io.micrometer.core.instrument.Tag.of("organization_id", organizationId.toString())),
            softScore
        )
    }
}
