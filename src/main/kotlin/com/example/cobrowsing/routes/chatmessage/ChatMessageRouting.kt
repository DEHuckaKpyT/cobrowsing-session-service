package com.example.cobrowsing.routes.chatmessage

import com.example.cobrowsing.converters.MessageConverter
import com.example.cobrowsing.routes.chatmessage.dto.CreateChatMessageDto
import com.example.cobrowsing.routes.chatmessage.dto.CreateChatMessageParams
import com.example.cobrowsing.routes.chatmessage.dto.MessageListDto
import com.example.cobrowsing.routes.chatmessage.dto.SearchChatMessagesParams
import com.example.cobrowsing.service.message.MessageService
import com.example.cobrowsing.service.message.argument.SearchMessageArgument
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


/**
 * Created on 30.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Routing.chatMessageRouting() = apiRouting {
    route("/chats/{chatId}/messages") {

        val messageService = MessageService()
        val messageConverter: MessageConverter = Mappers.getMapper(MessageConverter::class.java)

        route("/create").post<CreateChatMessageParams, Unit, CreateChatMessageDto>(
            info("Создать сообщение")
        ) { (chatId), createDto ->
            messageConverter.toCreateMessageArgument(chatId, createDto)
                .let { messageService.create(it) }
                .let { this@post.pipeline.call.response.status(HttpStatusCode.OK) }
        }

        route("/list").get<SearchChatMessagesParams, List<MessageListDto>>(
            info("Получить сообщения чата")
        ) { (chatId) ->
            messageService.list(SearchMessageArgument(chatId))
                .let { messageConverter.toMessageListDto(it) }
                .let { respond(it) }
        }
    }
}