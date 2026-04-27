package com.example.vrp.config

import com.example.vrp.shared.AuditLogPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LoggingAuditAdapter : AuditLogPort {
    private val log = LoggerFactory.getLogger(LoggingAuditAdapter::class.java)

    override fun log(organizationId: String, action: String, entityId: String, details: Map<String, Any>) {
        log.info(
            "AUDIT orgId={} action={} entityId={} details={}",
            organizationId, action, entityId, details,
        )
    }
}
