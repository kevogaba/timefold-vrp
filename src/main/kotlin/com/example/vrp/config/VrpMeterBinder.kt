package com.example.vrp.config

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Component

@Component
class VrpMeterBinder(registry: MeterRegistry) {

    val jobSubmittedCounter: Counter = Counter.builder("vrp.job.submitted")
        .description("Total VRP jobs submitted")
        .register(registry)

    val jobCompletedCounter: Counter = Counter.builder("vrp.job.completed")
        .description("Total VRP jobs successfully completed")
        .register(registry)

    val jobFailedCounter: Counter = Counter.builder("vrp.job.failed")
        .description("Total VRP jobs that failed")
        .register(registry)

    val solverDurationTimer: Timer = Timer.builder("vrp.solver.duration")
        .description("Time taken by the VRP solver")
        .register(registry)
}
