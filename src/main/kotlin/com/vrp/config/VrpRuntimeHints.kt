package com.vrp.config

import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints

/**
 * Registers runtime hints for AOT processing and native image compilation.
 * This is required to ensure resources like solverConfig.xml are available at runtime.
 */
class VrpRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        // Register Timefold Solver configuration XML as a resource
        hints.resources().registerPattern("solverConfig.xml")

        // Register database migration scripts
        hints.resources().registerPattern("db/migration/*.sql")
    }
}

@Configuration
@ImportRuntimeHints(VrpRuntimeHints::class)
class VrpRuntimeHintsConfiguration
