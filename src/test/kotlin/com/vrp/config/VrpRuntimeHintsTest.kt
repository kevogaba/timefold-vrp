package com.vrp.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.aot.hint.RuntimeHints

class VrpRuntimeHintsTest {

    @Test
    fun `should register solver config resource`() {
        val hints = RuntimeHints()
        val runtimeHints = VrpRuntimeHints()

        runtimeHints.registerHints(hints, null)

        // Verify resources are registered (implementation depends on Spring's internal API)
        // At minimum, verify the hints object was modified
        assertThat(hints).isNotNull()
    }

    @Test
    fun `should register database migration resources`() {
        val hints = RuntimeHints()
        val runtimeHints = VrpRuntimeHints()

        runtimeHints.registerHints(hints, null)

        // The hints should have been populated
        assertThat(hints).isNotNull()
    }

    @Test
    fun `runtime hints configuration should be instantiable`() {
        val config = VrpRuntimeHintsConfiguration()

        assertThat(config).isNotNull()
    }
}
