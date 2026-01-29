plugins {
    id("java")
    id("io.freefair.lombok") version "9.1.0"
    `maven-publish`
    signing
}

group = "xyz.bobkinn.indigoi18n"
version = property("version") as String

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
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

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

    afterEvaluate {
        val p = this@afterEvaluate
        publishing.publications {
            create<MavenPublication>("mavenLocal") {
                from(components["java"])

                groupId = "io.github.bobkinn"
                artifactId = "indigo-i18n-${p.name}"

                pom {
                    name.set(rootProject.name+"-"+p.name)
                    p.description?.let {
                        description = it
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
                }
            }
        }

        signing {
            val f = rootProject.file(".gradle/sign_key.asc")
            val signingKey = if (f.isFile) f.readText() else System.getenv("SIGNING_KEY")
            val signingPassword = findProperty("signingPassword") as String?
                ?: System.getenv("SIGNING_PASSWORD")

            if (signingKey != null && signingPassword != null) {
                useInMemoryPgpKeys(signingKey, signingPassword)
                sign(publishing.publications["mavenLocal"]) // use local for now
            } else {
                logger.warn("Signing key or password not found in environment, skipping signing")
            }
        }
    }

}
