plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.freefair.lombok")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

group = "xyz.bobkinn"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

val shade = configurations.create("shade")

dependencies {
    shade(project(":gson")) {
        isTransitive = false
    }
    implementation(project(":gson"))
    implementation(project(":paper:paper-adventure"))
    shade(project(":paper:paper-adventure")) {
        exclude(module = "paper-api")
        exclude(group = "net.kyori")
        exclude(module = "annotations")
        exclude(group = "xyz.bobkinn.indigoi18n", module = "codegen")
    }
    paperweight.paperDevBundle(property("paper-version") as String)
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"

    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks {
    runServer {
        minecraftVersion("1.20.4")
    }

    shadowJar {
        configurations = listOf(project.configurations.getByName("shade"))
        archiveClassifier.set("") // replaces normal jar
    }

//    assemble {
//        dependsOn(project.tasks.reobfJar)
//    }

    build {
        dependsOn(shadowJar)
    }
}
