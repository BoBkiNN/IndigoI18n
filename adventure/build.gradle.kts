plugins {
    `java-library`
    id("io.freefair.lombok")
}

group = "xyz.bobkinn.indigoi18n"
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core"))
    implementation("net.kyori:adventure-api:${property("adventure-version")}")
    implementation("net.kyori:adventure-text-serializer-legacy:${property("adventure-version")}")
    implementation("net.kyori:adventure-text-minimessage:${property("adventure-version")}")
}

tasks.test {
    useJUnitPlatform()
}