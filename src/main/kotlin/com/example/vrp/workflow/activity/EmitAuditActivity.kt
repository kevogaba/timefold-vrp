package com.example.vrp.workflow.activity

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface EmitAuditActivity {
    @ActivityMethod
    fun emit(organizationId: String, action: String, entityId: String, details: Map<String, String>)
}
