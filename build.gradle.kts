plugins {
    application
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "1.9.25"
    id("io.ktor.plugin") version Versions.ktor
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
    implementation("io.prometheus:prometheus-metrics-core:${Versions.prometheus}")
    implementation("io.prometheus:prometheus-metrics-instrumentation-jvm:${Versions.prometheus}")
    implementation("io.prometheus:prometheus-metrics-exporter-httpserver:${Versions.prometheus}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinx_serialization}")

}

application {
    mainClass.set("core.ApplicationKt")
}
