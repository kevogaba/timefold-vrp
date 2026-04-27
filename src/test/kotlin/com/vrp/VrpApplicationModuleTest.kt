package com.vrp

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter

class VrpApplicationModuleTest {

    private val modules = ApplicationModules.of(VrpApplication::class.java)

    @Test
    fun `verify module structure`() {
        modules.verify()
    }

    @Test
    fun `generate module documentation`() {
        Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml()
    }
}
