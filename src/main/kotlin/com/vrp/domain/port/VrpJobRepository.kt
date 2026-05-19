package com.vrp.domain.port

import com.vrp.domain.model.JobStatus
import com.vrp.domain.model.VrpJob
import java.util.UUID

/**
 * Repository port for VrpJob domain model.
 */
interface VrpJobRepository {
    fun save(job: VrpJob): VrpJob

    fun findById(
        id: UUID,
        organizationId: UUID
    ): VrpJob?

    fun findAllByOrganizationId(organizationId: UUID): List<VrpJob>

    fun updateStatus(
        id: UUID,
        status: JobStatus,
        organizationId: UUID
    ): VrpJob?

    fun updateWithScore(
        id: UUID,
        status: JobStatus,
        hardScore: Int,
        softScore: Int,
        organizationId: UUID
    ): VrpJob?
}
