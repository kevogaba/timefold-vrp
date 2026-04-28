package com.vrp.infrastructure.persistence.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class AuditLogEntityTest {
    @Test
    fun `should create audit log with required fields`() {
        val organizationId = UUID.randomUUID()
        val entityId = UUID.randomUUID()

        val auditLog =
            AuditLogEntity(
                organizationId = organizationId,
                entityType = "Order",
                entityId = entityId,
                action = "CREATE"
            )

        assertThat(auditLog.id).isNotNull()
        assertThat(auditLog.guid).isNotNull()
        assertThat(auditLog.organizationId).isEqualTo(organizationId)
        assertThat(auditLog.entityType).isEqualTo("Order")
        assertThat(auditLog.entityId).isEqualTo(entityId)
        assertThat(auditLog.action).isEqualTo("CREATE")
        assertThat(auditLog.userId).isNull()
        assertThat(auditLog.changes).isNull()
        assertThat(auditLog.createdAt).isNotNull()
    }

    @Test
    fun `should create audit log with user and changes`() {
        val userId = UUID.randomUUID()

        val auditLog =
            AuditLogEntity(
                organizationId = UUID.randomUUID(),
                entityType = "Vehicle",
                entityId = UUID.randomUUID(),
                action = "UPDATE",
                userId = userId,
                changes = """{"licensePlate": {"old": "ABC123", "new": "XYZ789"}}"""
            )

        assertThat(auditLog.userId).isEqualTo(userId)
        assertThat(auditLog.changes).contains("licensePlate")
    }

    @Test
    fun `should generate unique guid for each audit log`() {
        val auditLogs =
            (1..5).map {
                AuditLogEntity(
                    organizationId = UUID.randomUUID(),
                    entityType = "Order",
                    entityId = UUID.randomUUID(),
                    action = "CREATE"
                )
            }

        val guids = auditLogs.map { it.guid }.toSet()
        assertThat(guids).hasSize(5)
    }
}
