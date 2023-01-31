package com.example.cobrowsing.routes.chat

import com.example.cobrowsing.plugins.ValueDto
import com.example.cobrowsing.service.chat.ChatService
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.ktor.server.routing.*
import java.util.*


/**
 * Created on 30.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Routing.chatRouting() = apiRouting {
    route("/chats") {

        val chatService = ChatService()

        route("/create").post<Unit, ValueDto<UUID>, Unit>(
            info("Создать чат")
        ) { _, _ ->
            chatService.create()
                .let { ValueDto(it.id.value) }
                .let { respond(it) }
        }
    }
}