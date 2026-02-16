pluginManagement {
    repositories {
        maven("https://jitpack.io")
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "io.github.bobkinn.sonatype-publisher") {
                useModule("com.github.BoBkiNN:sonatype-publisher:${requested.version}")
            }
        }
    }
}

rootProject.name = "IndigoI18n"
include("core")
include("gson")
include("codegen")
include("adventure")
include("paper")
include("paper:paper-adventure")
include("examples")
include("examples:test-plugin")
findProject(":examples:test-plugin")?.name = "test-plugin"
