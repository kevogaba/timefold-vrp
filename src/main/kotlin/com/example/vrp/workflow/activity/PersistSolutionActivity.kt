package com.example.vrp.workflow.activity

import com.example.vrp.solver.domain.VrpSolution
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface PersistSolutionActivity {
    @ActivityMethod
    fun persistSolution(jobId: String, organizationId: String, solution: VrpSolution)
}
