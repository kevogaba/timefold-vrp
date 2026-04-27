plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dep.mgmt)
    alias(libs.plugins.graalvm.native)
    alias(libs.plugins.kover)
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(25) }
}

repositories { mavenCentral() }

extra["sentryVersion"]         = "8.27.0"
extra["springModulithVersion"] = "2.0.6"
extra["timefoldSolverVersion"] = "2.0.0"

dependencies {
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    // OTEL
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")

    // Timefold
    implementation("ai.timefold.solver:timefold-solver-spring-boot-starter")

    // Temporal
    implementation(libs.temporal.spring.boot)

    // Sentry
    implementation("io.sentry:sentry-spring-boot-starter-jakarta")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring Modulith
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-jpa")
    runtimeOnly("org.springframework.modulith:spring-modulith-actuator")
    runtimeOnly("org.springframework.modulith:spring-modulith-observability")
    runtimeOnly("org.springframework.modulith:spring-modulith-runtime")

    // OpenAPI
    implementation(libs.springdoc.openapi.webmvc)

    // Dev tools (excluded from production)
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation(libs.mockk)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("ai.timefold.solver:timefold-solver-bom:${property("timefoldSolverVersion")}")
        mavenBom("io.sentry:sentry-bom:${property("sentryVersion")}")
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ── GraalVM native image ──────────────────────────────────────────────────────
graalvmNative {
    binaries {
        named("main") {
            // Spring Boot AOT handles reflection hints; only add custom ones here if needed
        }
    }
    metadataRepository { enabled.set(true) }
}

// ── Kover coverage ────────────────────────────────────────────────────────────
kover {
    reports {
        filters {
            excludes {
                classes(
                    "*ApplicationKt",
                    "*.config.*",
                    "*.adapter.out.persistence.*Entity",
                    "*.adapter.out.persistence.*Repository",
                    "*.domain.events.*",
                    "*.adapter.in.web.*Request",
                    "*.adapter.in.web.*Response",
                    "*.adapter.in.web.*Dto",
                )
            }
        }
        total {
            xml { onCheck.set(true) }
            html { onCheck.set(true) }
        }
        verify {
            rule {
                minBound(80)
            }
            rule {
                filters {
                    includes {
                        packages(
                            "com.example.vrp.*.domain",
                            "com.example.vrp.*.application",
                        )
                    }
                }
                minBound(90)
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.koverVerify)
}
