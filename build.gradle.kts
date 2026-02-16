import xyz.bobkinn.sonatypepublisher.PublishingType
import xyz.bobkinn.sonatypepublisher.sonatypePublish


plugins {
    java
    id("io.freefair.lombok") version "9.1.0"
    `maven-publish`
    signing
    id("io.github.bobkinn.sonatype-publisher") version "2.2.2" apply false
}

group = "xyz.bobkinn.indigoi18n"
version = property("version") as String

val isRelease = findProperty("isRelease") == "true"

if (!isRelease) {
    version = "$version-SNAPSHOT"
}

val commit = runCommand("git rev-parse HEAD")

println("Project version: $version; commit: $commit")

fun runCommand(cmd: String): String {
    val proc = ProcessBuilder(cmd.split(" "))
        .directory(rootDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
    proc.waitFor(10, TimeUnit.SECONDS)
    if (proc.exitValue() != 0) throw RuntimeException("Command exited with non-0 error code")
    return proc.inputStream.bufferedReader().readText().trim()
}

repositories {
    mavenCentral()
}

allprojects {
    apply(plugin = "java")
    dependencies {
        implementation("org.jetbrains:annotations:24.1.0")

        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }
}


subprojects {
    // do not configure examples
    if (path.startsWith(":examples")) return@subprojects

    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "io.github.bobkinn.sonatype-publisher")

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<Javadoc> {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    val p = this@subprojects
    publishing.publications {
        create<MavenPublication>("main") {
            from(components["java"])

            groupId = "io.github.bobkinn"
            artifactId = "indigo-i18n-${p.name}"

            setupPom(p)
        }
    }

    // configuring after evaluation because mavenLocale publication is created at same time
    signing {
        val f = rootProject.file(".gradle/sign_key.asc")
        val signingKey = if (f.isFile) f.readText() else System.getenv("SIGNING_KEY")
        val signingPassword = findProperty("signingPassword") as String?
            ?: System.getenv("SIGNING_PASSWORD")

        gradle.taskGraph.whenReady {
            val hasPublishTask = allTasks.any { it.name.contains("publish", ignoreCase = true) }

            if (isRelease && hasPublishTask) {
                if (signingKey.isNullOrBlank() || signingPassword.isNullOrBlank()) {
                    throw GradleException(
                        "SIGNING_KEY or SIGNING_PASSWORD is not set, required for release publish"
                    )
                }
            }
        }

        if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications["main"])
        }
    }

    sonatypePublish {
        username = providers.environmentVariable("MAVEN_CENTRAL_USERNAME")
            .orElse(providers.gradleProperty("maven_central_username"))

        password = providers.environmentVariable("MAVEN_CENTRAL_PASSWORD")
            .orElse(providers.gradleProperty("maven_central_password"))
        publishingType = PublishingType.USER_MANAGED

        registerMaven(publishing.publications.named("main", MavenPublication::class))
    }
}

fun MavenPublication.setupPom(p: Project) = pom {
    name.set(rootProject.name+"-"+p.name)
    description = provider {
        if (p.description.isNullOrBlank()) {
            logger.error("Missing description for project ${p.name}")
            "IndigoI18n library part '${p.name}'"
        } else {
            p.description!!
        }
    }

    url = "https://github.com/BoBkiNN/IndigoI18n"

    licenses {
        license {
            name = "MIT License"
            url = "https://opensource.org/license/mit"
        }
    }

    developers {
        developer {
            id = "bobkinn"
            name = "BoBkiNN"
            url = "https://github.com/BoBkiNN"
        }
    }

    scm {
        url.set("https://github.com/BoBkiNN/IndigoI18n")
        connection.set("scm:git:git://github.com/BoBkiNN/IndigoI18n.git")
        developerConnection.set("scm:git:ssh://github.com:BoBkiNN/IndigoI18n.git")
    }

    withXml {
        asNode().appendNode("properties")
            .appendNode("commit", commit)
    }
}
