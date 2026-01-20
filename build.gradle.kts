plugins {
    id("java")
    id("io.freefair.lombok") version "9.1.0"
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

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
