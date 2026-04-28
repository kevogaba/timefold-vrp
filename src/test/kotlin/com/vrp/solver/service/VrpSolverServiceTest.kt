package com.vrp.solver.service

import ai.timefold.solver.core.api.solver.SolverManager
import ai.timefold.solver.core.api.solver.SolverStatus
import com.vrp.solver.domain.VrpSolution
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class VrpSolverServiceTest {
    private lateinit var solverManager: SolverManager<VrpSolution>
    private lateinit var service: VrpSolverService

    @BeforeEach
    fun setup() {
        solverManager = mockk()
        service = VrpSolverService(solverManager)
    }

    @Test
    fun `should start solving and return problem id`() {
        val problemId = UUID.randomUUID()
        val problem = VrpSolution(jobId = problemId)

        every { solverManager.solve(problemId, problem) } returns mockk()

        val result = service.solve(problemId, problem)

        assertThat(result).isEqualTo(problemId)
        verify { solverManager.solve(problemId, problem) }
    }

    @Test
    fun `should get solver status`() {
        val problemId = UUID.randomUUID()

        every { solverManager.getSolverStatus(problemId) } returns SolverStatus.SOLVING_ACTIVE

        val status = service.getSolverStatus(problemId)

        assertThat(status).isEqualTo(SolverStatus.SOLVING_ACTIVE)
        verify { solverManager.getSolverStatus(problemId) }
    }

    @Test
    fun `should terminate early`() {
        val problemId = UUID.randomUUID()

        every { solverManager.terminateEarly(problemId) } returns Unit

        service.terminateEarly(problemId)

        verify { solverManager.terminateEarly(problemId) }
    }

    @Test
    fun `should return null for uncached solution`() {
        val problemId = UUID.randomUUID()

        val solution = service.getCachedSolution(problemId)

        assertThat(solution).isNull()
    }

    @Test
    fun `should return null for final solution when not cached`() {
        val problemId = UUID.randomUUID()

        val solution = service.getFinalBestSolution(problemId)

        assertThat(solution).isNull()
    }

    @Test
    fun `should handle different solver statuses`() {
        val problemId = UUID.randomUUID()

        every { solverManager.getSolverStatus(problemId) } returns SolverStatus.NOT_SOLVING

        val status = service.getSolverStatus(problemId)

        assertThat(status).isEqualTo(SolverStatus.NOT_SOLVING)
    }
}
