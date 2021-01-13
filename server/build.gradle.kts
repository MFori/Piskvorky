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
    implementation(Ktor.htmlBuilder)

    implementation(Koin.ktor)
    implementation(Logback.classic)

    implementation(Exposed.core)
    implementation(Exposed.jbdc)
    implementation(Exposed.dao)
    implementation(Exposed.time)
    implementation(Hikari.core)
    implementation(MysqlConnector.core)
    implementation(Commons.email)


    implementation(project(":domain"))
}

val mMainClassName = "cz.martinforejt.piskvorky.server.ApplicationKt"
application {
    mainClass.set(mMainClassName)
}
project.setProperty("mainClassName", mMainClassName)

tasks.register<Copy>("copyStatic") {
    from("$projectDir/static") {
        exclude("**/*.scss", "**/*.map")
    }
    into("${rootProject.buildDir}/static")
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("piskvorky-server")
    archiveClassifier.set("")
    archiveVersion.set("")
    dependsOn("copyStatic")
}