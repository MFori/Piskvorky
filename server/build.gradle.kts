import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("kotlin-platform-jvm")
    application
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}")

    implementation("io.ktor:ktor-server-core:${Versions.ktor}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
    implementation("io.ktor:ktor-serialization:${Versions.ktor}")

    implementation("ch.qos.logback:logback-classic:${Versions.logback}")
    implementation(Koin.ktor)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerialization}") // JVM dependency
    implementation("io.ktor:ktor-websockets:${Versions.ktor}")

    implementation(project(":domain"))
}

val mMainClassName = "cz.martinforejt.piskvorky.server.ApplicationKt"
application {
    mainClass.set(mMainClassName)
}
project.setProperty("mainClassName", mMainClassName)

tasks.withType<ShadowJar> {
    archiveBaseName.set("piskvorky-server")
    archiveClassifier.set("")
    archiveVersion.set("")
}