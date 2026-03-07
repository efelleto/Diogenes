plugins {
    id("java")
    kotlin("jvm")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.3.2"
}

group = "dev.efelleto"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val ktor_version = "3.4.1"

    api(project(":Core"))

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")

    implementation("org.litote.kmongo:kmongo:4.11.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}


tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "dev.efelleto"
            artifactId = "SDK"
            version = "1.0-SNAPSHOT"
        }
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("")

    relocate("io.ktor", "dev.efelleto.diogenes.libs.ktor")
    relocate("com.google.gson", "dev.efelleto.diogenes.libs.gson")
    relocate("kotlin", "dev.efelleto.diogenes.libs.kotlin")
    relocate("kotlinx", "dev.efelleto.diogenes.libs.kotlinx")
}

kotlin {
    jvmToolchain(21)
}