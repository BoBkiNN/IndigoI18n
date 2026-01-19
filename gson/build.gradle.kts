plugins {
    `java-library`
    id("io.freefair.lombok")
    `maven-publish`
}

group = "xyz.bobkinn.indigoi18n"
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core"))
    api("com.google.code.gson:gson:2.13.2")
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
            artifactId = "indigo-i18n-gson"

            pom {
                name.set(rootProject.name)
                description.set("IndigoI18n localization library JSON support using Gson")
            }
        }
    }
}