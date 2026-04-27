package com.vrp.api.controller

import com.vrp.api.dto.JobResponse
import com.vrp.api.dto.SubmitJobRequest
import com.vrp.api.mapper.ApiDtoMapper
import com.vrp.api.security.JwtOrgIdExtractor
import com.vrp.application.usecase.GetJobStatusUseCase
import com.vrp.application.usecase.SubmitJobUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/jobs")
class JobController(
    private val submitJobUseCase: SubmitJobUseCase,
    private val getJobStatusUseCase: GetJobStatusUseCase,
    private val dtoMapper: ApiDtoMapper,
    private val jwtOrgIdExtractor: JwtOrgIdExtractor
) {

    @PostMapping
    fun submitJob(
        @RequestBody request: SubmitJobRequest,
        authentication: Authentication
    ): ResponseEntity<JobResponse> {
        val organizationId = jwtOrgIdExtractor.extractOrganizationId(authentication)

        val job = submitJobUseCase.execute(organizationId, request.orderIds, request.vehicleIds)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(dtoMapper.toJobResponse(job))
    }

    @GetMapping("/{id}")
    fun getJobStatus(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<JobResponse> {
        val organizationId = jwtOrgIdExtractor.extractOrganizationId(authentication)

        val job = getJobStatusUseCase.execute(id, organizationId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(dtoMapper.toJobResponse(job))
    }
}
