package com.vrp.infrastructure.temporal.config

import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.serviceclient.WorkflowServiceStubsOptions
import io.temporal.worker.Worker
import io.temporal.worker.WorkerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TemporalConfig {
    @Value("\${temporal.target:localhost:7233}")
    private lateinit var temporalTarget: String

    @Value("\${temporal.namespace:default}")
    private lateinit var namespace: String

    @Value("\${temporal.task-queue:vrp-task-queue}")
    private lateinit var taskQueue: String

    @Bean
    fun workflowServiceStubs(): WorkflowServiceStubs =
        WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(temporalTarget)
                .build()
        )

    @Bean
    fun workflowClient(serviceStubs: WorkflowServiceStubs): WorkflowClient =
        WorkflowClient.newInstance(
            serviceStubs,
            WorkflowClientOptions
                .newBuilder()
                .setNamespace(namespace)
                .build()
        )

    @Bean
    fun workerFactory(workflowClient: WorkflowClient): WorkerFactory = WorkerFactory.newInstance(workflowClient)

    @Bean
    fun worker(workerFactory: WorkerFactory): Worker = workerFactory.newWorker(taskQueue)

    @Bean
    fun taskQueue(): String = taskQueue
}
