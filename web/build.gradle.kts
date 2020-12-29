plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-js"))

    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")

    implementation("org.jetbrains:kotlin-react:17.0.0-pre.133-kotlin-1.4.21")
    implementation("org.jetbrains:kotlin-react-dom:17.0.0-pre.133-kotlin-1.4.21")
    //implementation("org.jetbrains:kotlin-css:1.0.0-pre.133-kotlin-1.4.21")
    implementation("org.jetbrains:kotlin-react-router-dom:5.2.0-pre.133-kotlin-1.4.21")
    implementation(npm("react", "17.0.0"))
    implementation(npm("react-dom", "17.0.0"))
    //implementation(npm("kotlin-css", "1.0.0-pre.133-kotlin-1.4.21"))
    //implementation(npm("react-router-dom", "5.2.0-pre.133-kotlin-1.4.21"))
    implementation("io.ktor:ktor-client-js:${Versions.ktor}")
    implementation(Serialization.core)
    //implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinxSerializationRuntime}")
    implementation(project(":domain"))
}

version = "1.0"


kotlin {
    js {
        binaries.executable()
        useCommonJs()
        nodejs()
        browser {
            compilations.all {
                kotlinOptions {
                    metaInfo = true
                    sourceMap = true
                    sourceMapEmbedSources = "always"
                    moduleKind = "commonjs"
                    main = "call"
                    version = "1.0"
                }
            }
        }
    }
}