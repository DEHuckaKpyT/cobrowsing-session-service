package com.example.cobrowsing.plugins

import io.ktor.server.application.*
import io.ktor.server.engine.*


/**
 * Created on 14.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Application.enableShutdownUrl(enabled: Boolean = true) {
    if (!enabled) return

    install(ShutDownUrl.ApplicationCallPlugin) {
        shutDownUrl = this@enableShutdownUrl.environment.config.property("ktor.deployment.shutdown-url").getString()
        exitCodeSupplier = { 0 }
    }
}