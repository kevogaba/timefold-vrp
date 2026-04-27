package com.vrp.application.usecase

import com.vrp.domain.model.Trip
import com.vrp.domain.port.TripRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetTripUseCase(
    private val tripRepository: TripRepository
) {

    fun execute(tripId: UUID, organizationId: UUID): Trip? {
        return tripRepository.findById(tripId, organizationId)
    }

    fun getByJobId(jobId: UUID, organizationId: UUID): List<Trip> {
        return tripRepository.findAllByJobId(jobId, organizationId)
    }
}
