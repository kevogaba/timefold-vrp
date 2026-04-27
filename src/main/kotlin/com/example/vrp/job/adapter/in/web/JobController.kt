package com.example.vrp.job.adapter.`in`.web

import com.example.vrp.job.port.`in`.QueryJobUseCase
import com.example.vrp.job.port.`in`.SubmitJobUseCase
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/jobs")
class JobController(
    private val submitJobUseCase: SubmitJobUseCase,
    private val queryJobUseCase: QueryJobUseCase,
) {

    @PostMapping
    fun submitJob(
        @RequestBody request: SubmitJobRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<JobResponse> {
        val organizationId = jwt.getClaimAsString("org_id")
            ?: return ResponseEntity.badRequest().build()
        val job = submitJobUseCase.submit(organizationId, request.orderIds)
        return ResponseEntity
            .created(URI.create("/api/v1/jobs/${job.id}"))
            .body(JobResponse.from(job))
    }

    @GetMapping("/{id}")
    fun getJob(
        @PathVariable id: String,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<JobResponse> {
        val job = queryJobUseCase.getById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(JobResponse.from(job))
    }

    @GetMapping
    fun listJobs(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<List<JobResponse>> {
        val organizationId = jwt.getClaimAsString("org_id")
            ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(
            queryJobUseCase.listByOrganization(organizationId).map { JobResponse.from(it) }
        )
    }
}
