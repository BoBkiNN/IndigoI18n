plugins {
    id("java")
}

group = "xyz.bobkinn.indigoi18n"
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup:javapoet:1.13.0")
}
