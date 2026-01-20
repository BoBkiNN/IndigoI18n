plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok")
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
    api(project(":core"))
    implementation("io.papermc.paper:paper-api:${property("paper-version")}")

    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:2.85.2")
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
            artifactId = "indigo-i18n-paper"

            pom {
                name.set(rootProject.name)
                description.set("IndigoI18n localization library paper CommandSender compatibility and new argument converters")
            }
        }
    }
}