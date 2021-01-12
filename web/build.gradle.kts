plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-js"))

    implementation(Js.html)

    implementation(React.core)
    implementation(React.dom)
    implementation(React.router)
    implementation(npm("react", "17.0.0"))
    implementation(npm("react-dom", "17.0.0"))
    implementation(Ktor.clientJs)
    implementation(Serialization.core)
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