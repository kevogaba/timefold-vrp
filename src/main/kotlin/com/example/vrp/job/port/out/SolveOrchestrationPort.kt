package com.example.vrp.job.port.out

interface SolveOrchestrationPort {
    fun startSolving(jobId: String, organizationId: String, orderIds: List<String>)
}
