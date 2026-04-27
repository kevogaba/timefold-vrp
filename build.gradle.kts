plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spring.cloud.contract)
    alias(libs.plugins.kover)
    alias(libs.plugins.graalvm.native)
}

group = "com.vrp"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.opentelemetry)
    developmentOnly(libs.spring.boot.devtools)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    // Jackson
    implementation(libs.jackson.module.kotlin)

    // Timefold Solver
    implementation(libs.timefold.solver.spring.boot.starter)
    implementation(libs.timefold.solver.core)

    // Temporal
    implementation(libs.temporal.spring.boot.starter)
    implementation(libs.temporal.sdk)

    // Spring Modulith
    implementation(libs.spring.modulith.starter.core)
    implementation(libs.spring.modulith.starter.jpa)
    implementation(libs.spring.modulith.events.api)

    // Sentry
    implementation(libs.sentry.spring.boot.starter)
    implementation(libs.sentry.logback)

    // Springdoc OpenAPI
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // Database
    implementation(libs.postgresql)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)

    // Logging
    implementation(libs.logback.classic)
    implementation(libs.logback.encoder)

    // Testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.timefold.solver.test)
    testImplementation(libs.temporal.testing)
    testImplementation(libs.spring.modulith.starter.test)
    testImplementation(libs.spring.cloud.contract.verifier)
    testImplementation(libs.spring.cloud.contract.stub.runner)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.assertj.core)
}

tasks {
    test {
        useJUnitPlatform()
        finalizedBy(koverHtmlReport, koverXmlReport)
    }

    bootJar {
        archiveFileName.set("${project.name}.jar")
    }
}

// Kover coverage configuration
kover {
    reports {
        total {
            html {
                onCheck = true
                htmlDir = layout.buildDirectory.dir("reports/kover/html")
            }
            xml {
                onCheck = true
                xmlFile = layout.buildDirectory.file("reports/kover/report.xml")
            }
            verify {
                onCheck = true
                rule {
                    minBound(80)
                }
                rule("Domain and Application packages") {
                    filters {
                        includes {
                            packages("com.vrp.domain.*", "com.vrp.application.*")
                        }
                    }
                    minBound(90)
                }
            }
        }
    }
}

// Spring Cloud Contract configuration
contracts {
    testFramework = org.springframework.cloud.contract.verifier.config.TestFramework.JUNIT5
    baseClassForTests = "com.vrp.ContractTestBase"
    contractsDirectory = file("src/test/resources/contracts")
}

// GraalVM Native configuration
graalvmNative {
    binaries {
        named("main") {
            javaLauncher = javaToolchains.launcherFor {
                languageVersion = JavaLanguageVersion.of(25)
            }
            buildArgs.add("--initialize-at-build-time=org.slf4j")
            buildArgs.add("-H:+ReportExceptionStackTraces")
        }
    }
}
