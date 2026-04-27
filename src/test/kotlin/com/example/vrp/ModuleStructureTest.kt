package com.example.vrp

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter

class ModuleStructureTest {

    private val modules = ApplicationModules.of(VrpApplication::class.java)

    @Test
    fun `verifies module structure`() {
        modules.verify()
    }

    @Test
    fun `writes module documentation`() {
        Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml()
    }
}
