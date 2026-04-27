package com.vrp.application.usecase

import com.vrp.domain.model.JobStatus
import com.vrp.domain.model.VrpJob
import com.vrp.domain.port.VrpJobRepository
import com.vrp.domain.port.VrpOrchestrationPort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class SubmitJobUseCase(
    private val jobRepository: VrpJobRepository,
    private val orchestrationPort: VrpOrchestrationPort
) {

    fun execute(organizationId: UUID, orderIds: List<UUID>, vehicleIds: List<UUID>): VrpJob {
        require(orderIds.isNotEmpty()) { "Order IDs cannot be empty" }
        require(vehicleIds.isNotEmpty()) { "Vehicle IDs cannot be empty" }

        // Create job
        val job = VrpJob(
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
