pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

}


rootProject.name = "Piskvorky"

enableFeaturePreview("GRADLE_METADATA")

include(":domain")
include(":web")
include(":server")