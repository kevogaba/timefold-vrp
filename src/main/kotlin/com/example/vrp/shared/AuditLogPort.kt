package com.example.vrp.shared

interface AuditLogPort {
    fun log(organizationId: String, action: String, entityId: String, details: Map<String, Any> = emptyMap())
}
