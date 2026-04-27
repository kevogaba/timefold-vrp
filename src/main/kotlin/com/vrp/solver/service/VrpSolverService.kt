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
    private val solverManager: SolverManager<VrpSolution, UUID>
) {

    private val solutionCache = ConcurrentHashMap<UUID, VrpSolution>()

    /**
     * Start solving a VRP problem.
     */
    fun solve(
        problemId: UUID,
        problem: VrpSolution,
        onSolutionFound: (VrpSolution) -> Unit
    ) {
        solverManager.solveAndListen(
            problemId,
            { problem },
            { solution -> onSolutionFound(solution) },
            { finalSolution ->
                solutionCache[problemId] = finalSolution
                onSolutionFound(finalSolution)
            }
        )
    }

    /**
     * Get the status of a solving job.
     */
    fun getSolverStatus(problemId: UUID): SolverStatus {
        return solverManager.getSolverStatus(problemId)
    }

    /**
     * Terminate solving for a problem.
     */
    fun terminateEarly(problemId: UUID) {
        solverManager.terminateEarly(problemId)
    }

    /**
     * Get cached solution if available.
     */
    fun getCachedSolution(problemId: UUID): VrpSolution? {
        return solutionCache[problemId]
    }
}
