package com.vrp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class VrpApplication

fun main(args: Array<String>) {
    runApplication<VrpApplication>(*args)
}
