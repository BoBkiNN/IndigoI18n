plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok")
}

group = "xyz.bobkinn.indigoi18n"
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    api(project(":core"))
    implementation("org.spigotmc:spigot-api:${property("spigot-version")}")

    testImplementation("com.github.seeseemelk:MockBukkit-v1.16:1.5.2")
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenLocal") {
            from(components["java"])

            groupId = "io.github.bobkinn"
            artifactId = "indigo-i18n-spigot"

            pom {
                name.set(rootProject.name)
                description.set("IndigoI18n localization library spigot CommandSender compatibility")
            }
        }
    }
}