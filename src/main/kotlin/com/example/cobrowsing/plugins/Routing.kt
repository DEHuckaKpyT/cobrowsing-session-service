package com.example.cobrowsing.plugins

import com.example.cobrowsing.routes.chat.chatRouting
import com.example.cobrowsing.routes.chatmessage.chatMessageRouting
import com.example.cobrowsing.routes.session.sessionRouting
import com.example.cobrowsing.routes.sessionevent.sessionEventRouting
import com.example.cobrowsing.routes.websockets.configureWebSocketsRouting
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        sessionRouting()
        sessionEventRouting()
        chatRouting()
        chatMessageRouting()
        configureWebSocketsRouting()
    }
}

data class ValueDto<ValueT>(
    val value: ValueT
)

data class PathParamDto<ValueT>(
    @PathParam("value") val value: ValueT
)