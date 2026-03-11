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
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    val ktor_version = "2.3.12"

    api(project(":Core"))

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

// java 8 support
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("")

    relocate("io.ktor", "dev.efelleto.diogenes.libs.ktor")
    relocate("com.google.gson", "dev.efelleto.diogenes.libs.gson")
    relocate("kotlin", "dev.efelleto.diogenes.libs.kotlin")
    relocate("kotlinx", "dev.efelleto.diogenes.libs.kotlinx")
}