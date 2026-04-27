package com.vrp.infrastructure.persistence.adapter

import com.vrp.domain.model.Trip
import com.vrp.domain.port.TripRepository
import com.vrp.infrastructure.persistence.mapper.TripMapper
import com.vrp.infrastructure.persistence.repository.TripJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@Transactional
class TripRepositoryAdapter(
    private val jpaRepository: TripJpaRepository,
    private val mapper: TripMapper
) : TripRepository {

    override fun save(trip: Trip): Trip {
        val entity = mapper.toEntity(trip)
        val saved = jpaRepository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun saveAll(trips: List<Trip>): List<Trip> {
        val entities = trips.map { mapper.toEntity(it) }
        val saved = jpaRepository.saveAll(entities)
        return saved.map { mapper.toDomain(it) }
    }

    override fun findById(id: UUID, organizationId: UUID): Trip? {
        return jpaRepository.findByIdAndOrganizationId(id, organizationId)
            ?.let { mapper.toDomain(it) }
    }

    override fun findAllByJobId(jobId: UUID, organizationId: UUID): List<Trip> {
        return jpaRepository.findAllByJobIdAndOrganizationId(jobId, organizationId)
            .map { mapper.toDomain(it) }
    }

    override fun findAllByOrganizationId(organizationId: UUID): List<Trip> {
        return jpaRepository.findAllByOrganizationId(organizationId)
            .map { mapper.toDomain(it) }
    }
}
