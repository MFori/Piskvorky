plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
}

version = "1.0"

kotlin {

    jvm()

    js {
        browser {
            compilations.all {
                kotlinOptions {
                    version = "1.0"
                }
            }
        }
    }


    sourceSets {

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
            // api(Koin.test)

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
