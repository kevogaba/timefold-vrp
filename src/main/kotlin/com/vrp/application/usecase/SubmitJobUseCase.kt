package com.vrp.application.usecase

import com.vrp.domain.model.JobStatus
import com.vrp.domain.model.VrpJob
import com.vrp.domain.port.VrpJobRepository
import com.vrp.domain.port.VrpOrchestrationPort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

/**
 * Use case for submitting a new Vehicle Routing Problem (VRP) optimization job.
 *
 * This use case handles the submission of a VRP job by:
 * 1. Validating input parameters (orders and vehicles must not be empty)
 * 2. Creating and persisting a VrpJob entity with PENDING status
 * 3. Starting a Temporal workflow to execute the optimization asynchronously
 * 4. Updating the job status to RUNNING once the workflow is started
 *
 * The actual optimization is performed asynchronously by the Temporal workflow,
 * which fetches data, runs the Timefold solver, and persists the results.
 *
 * @property jobRepository Repository for managing VRP job persistence
 * @property orchestrationPort Port for interacting with the Temporal workflow engine
 *
 * @see VrpJob
 * @see com.vrp.infrastructure.temporal.workflow.VrpSolveWorkflow
 */
@Service
class SubmitJobUseCase(
    private val jobRepository: VrpJobRepository,
    private val orchestrationPort: VrpOrchestrationPort
) {
    /**
     * Submits a new VRP optimization job for the specified organization.
     *
     * Creates a pending job, starts the Temporal workflow for asynchronous solving,
     * and returns the job with RUNNING status.
     *
     * @param organizationId The UUID of the organization submitting the job (for multi-tenancy)
     * @param orderIds List of order UUIDs to be included in the routing optimization
     * @param vehicleIds List of vehicle UUIDs available for the routing
     * @return The created VrpJob entity with RUNNING status
     * @throws IllegalArgumentException if orderIds or vehicleIds are empty
     * @throws IllegalStateException if the job cannot be transitioned to RUNNING status
     */
    fun execute(
        organizationId: UUID,
        orderIds: List<UUID>,
        vehicleIds: List<UUID>
    ): VrpJob {
        require(orderIds.isNotEmpty()) { "Order IDs cannot be empty" }
        require(vehicleIds.isNotEmpty()) { "Vehicle IDs cannot be empty" }

        // Create job
        val job =
            VrpJob(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                status = JobStatus.PENDING,
                orderIds = orderIds,
                vehicleIds = vehicleIds,
                createdAt = LocalDateTime.now()
            )

        // Save job
        val savedJob = jobRepository.save(job)

        // Start workflow
        orchestrationPort.startSolveWorkflow(savedJob)

        // Update status to RUNNING
        return jobRepository.updateStatus(savedJob.id, JobStatus.RUNNING, organizationId)
            ?: savedJob
    }
}
