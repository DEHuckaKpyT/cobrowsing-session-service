package com.example.cobrowsing

import com.example.cobrowsing.extensions.applyConfig
import com.example.cobrowsing.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.tomcat.*
import kotlinx.coroutines.runBlocking

suspend fun main(args: Array<String>): Unit {
    embeddedServer(Tomcat, environment = applicationEngineEnvironment {
        runBlocking {
            config = applyConfig()
        }

        connector {
            host = config.property("ktor.deployment.host").getString()
            port = config.property("ktor.deployment.port").getString().toInt()
        }
    }).start(wait = true)
}

@Suppress("unused")
fun Application.module() {
//    configureEurekaClient()
    configureSerialization()
    configureWebSockets()
    configureStatusPages()
    configureSwagger()
    configureAuthentication()
    configureRouting()
    configureCORS()
    configureDatabase()

    enableShutdownUrl()
}
