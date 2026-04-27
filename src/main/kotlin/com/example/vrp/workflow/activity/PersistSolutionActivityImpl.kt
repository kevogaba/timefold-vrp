package com.example.vrp.workflow.activity

import com.example.vrp.solver.domain.VrpSolution
import com.example.vrp.solver.mapper.SolverOutputMapper
import com.example.vrp.trip.port.out.TripRepository
import org.springframework.stereotype.Component

@Component
class PersistSolutionActivityImpl(
    private val tripRepository: TripRepository,
    private val outputMapper: SolverOutputMapper,
) : PersistSolutionActivity {
    override fun persistSolution(jobId: String, organizationId: String, solution: VrpSolution) {
        val trips = outputMapper.toTrips(solution, jobId, organizationId)
        trips.forEach { tripRepository.save(it) }
    }
}
