rootProject.name = "timefold-vrp"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven {
            url = uri("https://maven.pkg.github.com/detekt/detekt")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: "detekt"
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "io.gitlab.arturbosch.detekt") {
                useModule("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
