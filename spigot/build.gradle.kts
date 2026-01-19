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
    api(project(":core"))
    implementation("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}