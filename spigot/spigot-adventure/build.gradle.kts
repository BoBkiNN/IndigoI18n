plugins {
    `java-library`
}

group = "xyz.bobkinn.indigoi18n"
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    api(project(":spigot"))
    api(project(":adventure"))

    implementation("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-api:4.25.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.25.0")
    implementation("net.kyori:adventure-text-minimessage:4.25.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}