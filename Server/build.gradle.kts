plugins {
    id("java")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" // Versão estável
    id("org.jetbrains.kotlin.jvm")
    id("com.gradleup.shadow") version "9.3.2"
    id("maven-publish")
}

group = "dev.efelleto"
version = "1.0.15"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":Core"))

    implementation("net.dv8tion:JDA:6.3.1")
    implementation("org.litote.kmongo:kmongo:4.11.0")

    implementation("com.charleskorn.kaml:kaml:0.60.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.yaml:snakeyaml:2.2")

    implementation("org.fusesource.jansi:jansi:2.4.0")

    val ktor_version = "2.3.12"
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    implementation("ch.qos.logback:logback-classic:1.4.14")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.shadowJar {
    archiveClassifier.set("") // file name

    manifest {
        attributes["Main-Class"] = "dev.efelleto.diogenes.server.MainKt"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}