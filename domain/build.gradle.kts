plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("org.openapi.generator") version "5.0.0"
}
val moduleVersion = version

kotlin {

    jvm()

    js {
        browser {
            compilations.all {
                kotlinOptions {
                    version = moduleVersion
                }
            }
        }
    }


    sourceSets {

        sourceSets["commonMain"].kotlin.srcDirs("$rootDir/domain/api/src/commonMain")
        sourceSets["commonMain"].dependencies {
            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}") {
                isForce = true
            }

            // Ktor
            implementation(Ktor.clientCore)
            implementation(Ktor.clientJson)
            implementation(Ktor.clientLogging)
            implementation(Ktor.clientSerialization)

            // Kotlinx Serialization
            implementation(Serialization.core)

            // SQL Delight
            //implementation(SqlDelight.runtime)
            //implementation(SqlDelight.coroutineExtensions)

            // koin
            api(Koin.core)

            // kermit
            api(Deps.kermit)
        }
        sourceSets["commonTest"].dependencies {
        }

        sourceSets["jvmMain"].dependencies {
            implementation(Ktor.clientApache)
            implementation(Ktor.slf4j)
        }

        sourceSets["jsMain"].dependencies {
            implementation(Ktor.clientJs)
        }
    }
}

openApiGenerate {
    generatorName.set("kotlin")
    library.set("multiplatform")

    globalProperties.set(mapOf(
        "models" to ""
        //"apis" to "",
        //"supportingFiles" to ""
    ))
    inputSpec.set("$rootDir/domain/api/specs/swagger.yaml")
    outputDir.set("$rootDir/domain/api")
    modelPackage.set("cz.martinforejt.piskvorky.api.model")
    configOptions.set(
        mapOf(
            "dateLibrary" to "string"
        )
    )
}

task("clearOpenApiFiles") {
    delete("$rootDir/domain/api/src/commonTest")
    delete("$rootDir/domain/api/src/iosTest")
    delete("$rootDir/domain/api/src/jsTest")
    delete("$rootDir/domain/api/src/jvmTest")
    delete("$rootDir/domain/api/src/commonMain/kotlin/org/*")
}

tasks.getByName("openApiGenerate").dependsOn("clearOpenApiFiles")
