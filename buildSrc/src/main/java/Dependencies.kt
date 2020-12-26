object Versions {
    const val kotlin = "1.4.21"
    const val kotlinCoroutines = "1.4.2-native-mt"
    const val ktor = "1.4.0"
    const val kotlinxSerialization = "1.0.0-RC"
    const val kotlinxSerializationRuntime = "0.20.0"
    const val koin = "3.0.0-alpha-4"
    const val kermit = "0.1.8"
    const val exposed = "0.28.1"
    const val logback = "1.2.3"
    const val shadow = "6.1.0"
    const val junit = "4.13"
    const val hikari = "3.4.5"
    const val mysqlConnector = "8.0.22"
}

object Deps {
    const val kermit = "co.touchlab:kermit:${Versions.kermit}"
}

object Test {
    const val junit = "junit:junit:${Versions.junit}"
}

object Koin {
    const val core = "org.koin:koin-core:${Versions.koin}"
    const val ktor = "org.koin:koin-ktor:${Versions.koin}"
    const val test = "org.koin:koin-test:${Versions.koin}"
}

object Ktor {
    const val serverCore = "io.ktor:ktor-server-core:${Versions.ktor}"
    const val serverNetty = "io.ktor:ktor-server-netty:${Versions.ktor}"

    const val serialization = "io.ktor:ktor-serialization:${Versions.ktor}"
    const val websockets = "io.ktor:ktor-websockets:${Versions.ktor}"

    const val authCore = "io.ktor:ktor-auth:${Versions.ktor}"
    const val authJwt = "io.ktor:ktor-auth-jwt:${Versions.ktor}"

    const val clientCore = "io.ktor:ktor-client-core:${Versions.ktor}"
    const val clientJson = "io.ktor:ktor-client-json:${Versions.ktor}"
    const val clientApache = "io.ktor:ktor-client-apache:${Versions.ktor}"
    const val clientJs = "io.ktor:ktor-client-js:${Versions.ktor}"

    const val clientLogging = "io.ktor:ktor-client-logging:${Versions.ktor}"
    const val clientSerialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"
}

object Serialization {
    const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerialization}"
}

object Coroutines {
    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
}

object Logback {
    const val classic = "ch.qos.logback:logback-classic:${Versions.logback}"
}

object Exposed {
    const val core = "org.jetbrains.exposed:exposed-core:${Versions.exposed}"
    const val jbdc = "org.jetbrains.exposed:exposed-jdbc:${Versions.exposed}"
    const val dao = "org.jetbrains.exposed:exposed-dao:${Versions.exposed}"
    const val time = "org.jetbrains.exposed:exposed-java-time:${Versions.exposed}"
}

object Hikari {
    const val core = "com.zaxxer:HikariCP:${Versions.hikari}"
}

object MysqlConnector {
    const val core = "mysql:mysql-connector-java:${Versions.mysqlConnector}"
}