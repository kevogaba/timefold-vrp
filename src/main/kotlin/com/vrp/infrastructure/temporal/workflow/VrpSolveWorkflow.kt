package com.vrp.infrastructure.temporal.workflow

import io.temporal.workflow.QueryMethod
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.util.UUID

/**
 * VRP solve workflow interface.
 */
@WorkflowInterface
interface VrpSolveWorkflow {
    @WorkflowMethod
    fun solve(input: VrpJobInput): VrpJobResult

    @QueryMethod
    fun getStatus(): String
}

data class VrpJobInput(
    val jobId: UUID,
    val organizationId: UUID,
    val orderIds: List<UUID>,
    val vehicleIds: List<UUID>
)

data class VrpJobResult(
    val jobId: UUID,
    val success: Boolean,
    val hardScore: Int?,
    val softScore: Int?,
    val errorMessage: String?
)
