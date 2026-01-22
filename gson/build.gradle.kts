plugins {
    `java-library`
    id("io.freefair.lombok")
    `maven-publish`
}

group = "xyz.bobkinn.indigoi18n"
version = rootProject.version
description = "IndigoI18n localization library JSON support using Gson"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core"))
    api("com.google.code.gson:gson:2.13.2")
}
