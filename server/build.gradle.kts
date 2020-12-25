import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("kotlin-platform-jvm")
    application
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(Coroutines.core)
    implementation(Serialization.core)

    implementation(Ktor.serverCore)
    implementation(Ktor.serverNetty)
    implementation(Ktor.serialization)
    implementation(Ktor.websockets)
    implementation(Ktor.authCore)
    implementation(Ktor.authJwt)

    implementation(Koin.ktor)
    implementation(Logback.classic)

    implementation(Exposed.core)
    implementation(Exposed.jbdc)
    implementation(Exposed.dao)
    implementation(Hikari.core)
    implementation(MysqlConnector.core)

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