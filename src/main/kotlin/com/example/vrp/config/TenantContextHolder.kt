package com.example.vrp.config

import org.springframework.stereotype.Component

@Component
class TenantContextHolder {
    private val tenantId = ThreadLocal<String?>()

    fun set(id: String) { tenantId.set(id) }
    fun get(): String? = tenantId.get()
    fun clear() { tenantId.remove() }
}
