package com.example.cobrowsing.routes.chat

import com.example.cobrowsing.converters.MessageConverter
import com.example.cobrowsing.plugins.ValueDto
import com.example.cobrowsing.routes.chat.dto.PathChatIdDto
import com.example.cobrowsing.routes.chatmessage.dto.MessageListDto
import com.example.cobrowsing.service.chat.ChatService
import com.example.cobrowsing.service.message.MessageService
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.mapstruct.factory.Mappers
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
        val messageService = MessageService()
        val messageConverter: MessageConverter = Mappers.getMapper(MessageConverter::class.java)

        route("/create").post<Unit, ValueDto<UUID>, Unit>(
            info("Создать чат")
        ) { _, _ ->
            chatService.create()
                .let { ValueDto(it.id.value) }
                .let { respond(it) }
        }

        route("last-messages").get<Unit, List<MessageListDto>>(
            info("Последние сообщения для каждого чата")
        ) {
            messageService.lastMessagesByChat()
                .let { messageConverter.toMessageListDto(it) }
                .let { respond(it) }
        }

        route("{chatId}/is-sharing-accepted").get<PathChatIdDto, ValueDto<Boolean>>(
            info("Можно делиться страницей")
        ) { params ->
            chatService.isSharingAccepted(params.chatId)
                .let { ValueDto(it) }
                .let { respond(it) }
        }

        route("{chatId}/accept-sharing").post<PathChatIdDto, Unit, Unit>(
            info("Разрешить делиться страницей")
        ) { params, _ ->
            chatService.acceptSharing(params.chatId)
                .let { this@post.pipeline.call.response.status(HttpStatusCode.OK) }
        }
    }
}