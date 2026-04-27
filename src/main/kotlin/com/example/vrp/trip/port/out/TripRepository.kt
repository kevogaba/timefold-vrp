package com.example.vrp.trip.port.out

import com.example.vrp.trip.domain.Trip

interface TripRepository {
    fun findById(id: String): Trip?
    fun findAllByJobId(jobId: String): List<Trip>
    fun save(trip: Trip): Trip
}
