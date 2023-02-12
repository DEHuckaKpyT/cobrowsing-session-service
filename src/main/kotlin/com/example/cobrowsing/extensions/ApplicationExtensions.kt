package com.example.cobrowsing.extensions

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import java.util.*


/**
 * Created on 14.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
suspend fun ApplicationEngineEnvironmentBuilder.applyConfig(resourcePath: String = "application.conf"): ApplicationConfig {
    val client = HttpClient(Apache)
    val localConfig = ConfigFactory.load(resourcePath)

    suspend fun mergeRemoteConfigs(): Config {
        val configService = localConfig.getString("ktor.deployment.config-service-url")
        val appName = localConfig.getString("ktor.application.name")

        var finalConfig = ConfigFactory.load()
            .withFallback(localConfig)

        listOf("properties", "yml", "yaml", "conf").forEach { type ->
            client.get("$configService/$appName/default/main/$appName.$type")
                .body<String>()
                .let { ConfigFactory.parseString(it) }
                .let { finalConfig = finalConfig.withFallback(it) }
        }

        return finalConfig.resolve()
    }

    return HoconApplicationConfig(localConfig)
//    return try {
//        HoconApplicationConfig(mergeRemoteConfigs())
//    } catch (throwable: Throwable) {
//        log.error("Error while getting configs from config-service", throwable)
//
//        HoconApplicationConfig(localConfig)
//    } finally {
//        client.close()
//    }
}

val mapper: ObjectMapper by lazy { jacksonObjectMapper() }

fun String.toUUID(): UUID = UUID.fromString(this)