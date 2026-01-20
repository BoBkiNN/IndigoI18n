plugins {
    `java-library`
    `maven-publish`
}

group = "xyz.bobkinn.indigoi18n"
version = rootProject.version

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
            artifactId = "indigo-i18n-paper-adventure"

            pom {
                name.set(rootProject.name)
                description.set("IndigoI18n localization library paper CommandSender compatibility with adventure overloads")
            }
        }
    }
}