package com.vrp.api.controller

import com.vrp.api.dto.TripResponse
import com.vrp.api.mapper.ApiDtoMapper
import com.vrp.api.security.JwtOrgIdExtractor
import com.vrp.application.usecase.GetTripUseCase
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/trips")
class TripController(
    private val getTripUseCase: GetTripUseCase,
    private val dtoMapper: ApiDtoMapper,
    private val jwtOrgIdExtractor: JwtOrgIdExtractor
) {

    @GetMapping("/{id}")
    fun getTrip(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<TripResponse> {
        val organizationId = jwtOrgIdExtractor.extractOrganizationId(authentication)

        val trip = getTripUseCase.execute(id, organizationId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(dtoMapper.toTripResponse(trip))
    }

    @GetMapping
    fun getTripsByJob(
        @RequestParam jobId: UUID,
        authentication: Authentication
    ): ResponseEntity<List<TripResponse>> {
        val organizationId = jwtOrgIdExtractor.extractOrganizationId(authentication)

        val trips = getTripUseCase.getByJobId(jobId, organizationId)

        return ResponseEntity.ok(trips.map { dtoMapper.toTripResponse(it) })
    }
}
