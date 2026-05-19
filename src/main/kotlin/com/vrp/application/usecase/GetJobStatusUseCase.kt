package com.vrp.application.usecase

import com.vrp.domain.model.VrpJob
import com.vrp.domain.port.VrpJobRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetJobStatusUseCase(
    private val jobRepository: VrpJobRepository
) {
    @Cacheable(value = ["jobStatus"], key = "#jobId + '-' + #organizationId")
    fun execute(
        jobId: UUID,
        organizationId: UUID
    ): VrpJob? = jobRepository.findById(jobId, organizationId)
}
