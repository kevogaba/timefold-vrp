import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

contract {
    description = "Should get trip by ID successfully"

    request {
        method = GET
        url = url("/api/v1/trips/750e8400-e29b-41d4-a716-446655440002")
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
                "id": "750e8400-e29b-41d4-a716-446655440002",
                "jobId": "$(anyUuid())",
                "vehicleId": "$(anyUuid())",
                "visits": [
                    {
                        "orderId": "$(anyUuid())",
                        "location": {
                            "latitude": "$(anyDouble())",
                            "longitude": "$(anyDouble())"
                        },
                        "arrivalTime": "$(anyDateTime())",
                        "departureTime": "$(anyDateTime())",
                        "sequenceNumber": 0
                    }
                ],
                "totalDistanceMeters": "$(anyPositiveInt())",
                "totalDurationMinutes": "$(anyPositiveInt())"
            }
        """.trimIndent()
    }
}
