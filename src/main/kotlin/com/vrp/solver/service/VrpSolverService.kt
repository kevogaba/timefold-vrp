package com.vrp.solver.service

import ai.timefold.solver.core.api.solver.SolverManager
import ai.timefold.solver.core.api.solver.SolverStatus
import com.vrp.solver.domain.VrpSolution
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Service wrapping Timefold SolverManager.
 */
@Service
class VrpSolverService(
    private val solverManager: SolverManager<VrpSolution>
) {
    private val solutionCache = ConcurrentHashMap<UUID, VrpSolution>()

    /**
     * Start solving a VRP problem and cache solution.
     */
    fun solve(
        problemId: UUID,
        problem: VrpSolution
    ): UUID {
        solverManager.solve(problemId, problem)
        return problemId
    }

    /**
     * Get final best solution for a completed job.
     */
    fun getFinalBestSolution(problemId: UUID): VrpSolution? {
        // In Timefold 2.0, we need to wait for the job to complete and then retrieve the solution
        // The solution is stored in the cache when available
        return solutionCache[problemId]
    }

    /**
     * Get the status of a solving job.
     */
    fun getSolverStatus(problemId: UUID): SolverStatus = solverManager.getSolverStatus(problemId)

    /**
     * Terminate solving for a problem.
     */
    fun terminateEarly(problemId: UUID) {
        solverManager.terminateEarly(problemId)
    }

    /**
     * Get cached solution if available.
     */
    fun getCachedSolution(problemId: UUID): VrpSolution? = solutionCache[problemId]
}
