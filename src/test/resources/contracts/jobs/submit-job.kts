import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    description = "Should submit a VRP job successfully"

    request {
        method = POST
        url = url("/api/v1/jobs")
        headers {
            contentType = "application/json"
            header("Authorization", "Bearer mock-jwt-token")
        }
        body = """
            {
                "orderIds": ["550e8400-e29b-41d4-a716-446655440000"],
                "vehicleIds": ["650e8400-e29b-41d4-a716-446655440001"]
            }
        """.trimIndent()
    }

    response {
        status = CREATED
        headers {
            contentType = "application/json"
        }
        body = """
            {
                "id": "$(anyUuid())",
                "status": "RUNNING",
                "hardScore": null,
                "softScore": null,
                "errorMessage": null,
                "createdAt": "$(anyDateTime())",
                "completedAt": null
            }
        """.trimIndent()
    }
}
