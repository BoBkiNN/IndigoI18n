plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok")
}

group = "xyz.bobkinn.indigoi18n"
version = rootProject.version
description = "IndigoI18n localization library paper CommandSender compatibility with adventure overloads"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    api(project(":paper"))
    api(project(":adventure"))

    implementation("io.papermc.paper:paper-api:${property("paper-version")}")

    implementation("net.kyori:adventure-api:${property("adventure-version")}")
    implementation("net.kyori:adventure-text-serializer-legacy:${property("adventure-version")}")
    implementation("net.kyori:adventure-text-minimessage:${property("adventure-version")}")

    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:2.85.2")
    testImplementation("org.slf4j:slf4j-simple:2.0.17")
    testImplementation(project(":gson"))
}
