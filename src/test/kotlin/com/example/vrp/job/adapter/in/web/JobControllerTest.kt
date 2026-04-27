package com.example.vrp.job.adapter.`in`.web

import com.example.vrp.job.domain.Job
import com.example.vrp.job.domain.JobStatus
import com.example.vrp.job.port.`in`.QueryJobUseCase
import com.example.vrp.job.port.`in`.SubmitJobUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get
import java.time.Instant

@WebMvcTest(JobController::class)
class JobControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var submitJobUseCase: SubmitJobUseCase

    @Autowired
    lateinit var queryJobUseCase: QueryJobUseCase

    @TestConfiguration
    class Config {
        @Bean fun submitJobUseCase() = mockk<SubmitJobUseCase>()
        @Bean fun queryJobUseCase() = mockk<QueryJobUseCase>()
    }

    @Test
    fun `POST jobs returns 201 with job details`() {
        val job = Job(
            id = "job-1",
            organizationId = "org-1",
            status = JobStatus.SUBMITTED,
            submittedAt = Instant.now(),
        )
        every { submitJobUseCase.submit("org-1", listOf("order-1")) } returns job

        mockMvc.post("/api/v1/jobs") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(SubmitJobRequest(listOf("order-1")))
            with(jwt().jwt { it.claim("org_id", "org-1") })
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `GET jobs returns 404 when not found`() {
        every { queryJobUseCase.getById("missing") } returns null

        mockMvc.get("/api/v1/jobs/missing") {
            with(jwt().jwt { it.claim("org_id", "org-1") })
        }.andExpect {
            status { isNotFound() }
        }
    }
}
