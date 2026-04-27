package com.example.vrp.workflow.activity

import com.example.vrp.shared.AuditLogPort
import org.springframework.stereotype.Component

@Component
class EmitAuditActivityImpl(private val auditLogPort: AuditLogPort) : EmitAuditActivity {
    override fun emit(
        organizationId: String,
        action: String,
        entityId: String,
        details: Map<String, String>,
    ) {
        auditLogPort.log(organizationId, action, entityId, details)
    }
}
