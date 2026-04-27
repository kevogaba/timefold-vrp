package com.example.vrp.workflow

data class VrpSolveRequest(
    val jobId: String,
    val organizationId: String,
    val orderIds: List<String>,
)
