plugins {
    `java-library`
    id("io.freefair.lombok")
    `maven-publish`
}

group = "xyz.bobkinn.indigoi18n"
version = rootProject.version
description = "IndigoI18n localization library core that also contains String localization"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    api("net.xyzsd.plurals:cldr-plural-rules:41")
    api(project(":codegen"))
    annotationProcessor(project(":codegen"))
}

tasks.test {
    useJUnitPlatform()
    // jvmArgs("-XX:StartFlightRecording=filename=recording.jfr,dumponexit=true,settings=profile")
}
