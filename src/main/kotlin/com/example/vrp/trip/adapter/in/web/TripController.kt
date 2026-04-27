package com.example.vrp.trip.adapter.`in`.web

import com.example.vrp.trip.domain.Trip
import com.example.vrp.trip.port.out.TripRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/trips")
class TripController(private val tripRepository: TripRepository) {

    @GetMapping("/{id}")
    fun getTrip(@PathVariable id: String): ResponseEntity<Trip> =
        tripRepository.findById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @GetMapping("/job/{jobId}")
    fun getTripsByJob(@PathVariable jobId: String): ResponseEntity<List<Trip>> =
        ResponseEntity.ok(tripRepository.findAllByJobId(jobId))
}
