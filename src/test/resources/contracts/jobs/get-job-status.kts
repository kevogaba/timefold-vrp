import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    description = "Should get job status successfully"

    request {
        method = GET
        url = url("/api/v1/jobs/550e8400-e29b-41d4-a716-446655440000")
        headers {
            header("Authorization", "Bearer mock-jwt-token")
        }
    }

    response {
        status = OK
        headers {
            contentType = "application/json"
        }
        body = """
            {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "status": "$(anyOf('PENDING', 'RUNNING', 'COMPLETED', 'FAILED'))",
                "hardScore": "$(anyNumber())",
                "softScore": "$(anyNumber())",
                "errorMessage": null,
                "createdAt": "$(anyDateTime())",
                "completedAt": "$(anyDateTime())"
            }
        """.trimIndent()
    }
}
