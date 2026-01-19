plugins {
    `java-library`
    `maven-publish`
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

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
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