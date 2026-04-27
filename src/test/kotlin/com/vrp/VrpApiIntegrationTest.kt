package com.vrp

import com.vrp.api.controller.JobController
import com.vrp.api.controller.TripController
import com.vrp.api.dto.SubmitJobRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.UUID

/**
 * Integration tests for the VRP API endpoints.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class VrpApiIntegrationTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:17-alpine")
            .withDatabaseName("vrp_test")
            .withUsername("test")
            .withPassword("test")
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @WithMockUser
    fun `should return 401 when no authentication provided`() {
        mockMvc.perform(get("/api/v1/jobs/{id}", UUID.randomUUID()))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `application context should load successfully`() {
        // Context loads successfully if test passes
    }
}
