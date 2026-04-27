package com.example.vrp.workflow

import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface VrpSolveWorkflow {
    @WorkflowMethod
    fun solve(request: VrpSolveRequest)
}
