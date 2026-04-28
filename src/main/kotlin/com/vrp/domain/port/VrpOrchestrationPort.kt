package com.vrp.domain.port

import com.vrp.domain.model.VrpJob

/**
 * Port for VRP workflow orchestration.
 */
interface VrpOrchestrationPort {
    /**
     * Start a VRP solve workflow.
     */
    fun startSolveWorkflow(job: VrpJob): String

    /**
     * Query the status of a running workflow.
     */
    fun queryWorkflowStatus(workflowId: String): String?

    /**
     * Cancel a running workflow.
     */
    fun cancelWorkflow(workflowId: String)
}
