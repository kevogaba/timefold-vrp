package com.example.vrp.trip.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface TripJpaRepository : JpaRepository<TripJpaEntity, String> {
    fun findAllByJobId(jobId: String): List<TripJpaEntity>
}
