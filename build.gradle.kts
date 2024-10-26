plugins {
    application
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")
    implementation("io.github.cdimascio:dotenv-kotlin:${Versions.dotenv}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")
    implementation("io.ktor:ktor-server-core:${Versions.ktor}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
    implementation("io.ktor:ktor-server-content-negotiation:${Versions.ktor}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinx_serialization}")

}

application {
    mainClass.set("core.ApplicationKt")
}
