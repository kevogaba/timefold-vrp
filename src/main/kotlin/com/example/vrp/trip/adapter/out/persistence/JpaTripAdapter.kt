package com.example.vrp.trip.adapter.out.persistence

import com.example.vrp.trip.domain.Trip
import com.example.vrp.trip.port.out.TripRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JpaTripAdapter(private val repo: TripJpaRepository) : TripRepository {

    override fun findById(id: String): Trip? = repo.findById(id).orElse(null)?.toDomain()

    override fun findAllByJobId(jobId: String): List<Trip> =
        repo.findAllByJobId(jobId).map { it.toDomain() }

    override fun save(trip: Trip): Trip = repo.save(trip.toEntity()).toDomain()

    private fun TripJpaEntity.toDomain() = Trip(
        id = id,
        jobId = jobId,
        organizationId = organizationId,
        vehicleId = vehicleId,
        visits = emptyList(), // visits stored separately
        totalDistanceKm = totalDistanceKm,
        createdAt = createdAt ?: Instant.now(),
    )

    private fun Trip.toEntity() = TripJpaEntity(
        id = id,
        jobId = jobId,
        organizationId = organizationId,
        vehicleId = vehicleId,
        totalDistanceKm = totalDistanceKm,
    )
}
