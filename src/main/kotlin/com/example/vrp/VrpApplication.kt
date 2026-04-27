package com.example.vrp

import com.example.vrp.solver.domain.SolverVehicle
import com.example.vrp.solver.domain.SolverVisit
import com.example.vrp.solver.domain.VrpSolution
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
@ImportRuntimeHints(VrpRuntimeHints::class)
class VrpApplication

fun main(args: Array<String>) {
    runApplication<VrpApplication>(*args)
}

class VrpRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerType(VrpSolution::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
        hints.reflection().registerType(SolverVehicle::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
        hints.reflection().registerType(SolverVisit::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
    }
}
