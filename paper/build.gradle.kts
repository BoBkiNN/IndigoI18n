plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok")
}

group = "xyz.bobkinn.indigoi18n"
version = rootProject.version
description = "IndigoI18n localization library paper CommandSender compatibility and new argument converters"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    api(project(":core"))
    compileOnly("io.papermc.paper:paper-api:${property("paper-version")}")

    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.88.1")
    testImplementation(project(":gson"))
}
