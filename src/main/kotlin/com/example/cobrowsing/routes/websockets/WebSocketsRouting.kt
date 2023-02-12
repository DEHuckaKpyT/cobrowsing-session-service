package com.example.cobrowsing.routes.websockets

import com.example.cobrowsing.converters.MessageConverter
import com.example.cobrowsing.converters.SessionConverter
import com.example.cobrowsing.extensions.mapper
import com.example.cobrowsing.extensions.toUUID
import com.example.cobrowsing.plugins.CustomUserPrincipal
import com.example.cobrowsing.routes.websockets.dto.ReceivedMessageDto
import com.example.cobrowsing.service.message.MessageService
import com.example.cobrowsing.service.session.SessionService
import com.example.cobrowsing.service.sessionevent.SessionEventService
import com.example.cobrowsing.service.sessionevent.argument.CreateSessionEventArgument
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.mapstruct.factory.Mappers
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * Created on 03.02.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Routing.configureWebSocketsRouting() = authenticate("bearer-auth", strategy = AuthenticationStrategy.Optional) {

    val messageService = MessageService()
    val messageConverter: MessageConverter = Mappers.getMapper(MessageConverter::class.java)
    val sessionService = SessionService()
    val sessionEventService = SessionEventService()
    val sessionConverter: SessionConverter = Mappers.getMapper(SessionConverter::class.java)

    val clientByChatId = ConcurrentHashMap<UUID, WebSocketServerSession>()
    val operatorByChatId = ConcurrentHashMap<UUID, WebSocketServerSession>()

    val clientBySessionId = ConcurrentHashMap<UUID, WebSocketServerSession>()
    val operatorBySessionId = ConcurrentHashMap<UUID, WebSocketServerSession>()

    webSocket("/chats/{chatId}") {
        val chatId = call.parameters["chatId"]?.toUUID()
            ?: throw BadRequestException("Не указан id чата")

        val (userId, map) = call.principal<CustomUserPrincipal>()?.run {
            operatorByChatId[chatId] = this@webSocket
            id to clientByChatId
        } ?: kotlin.run {
            clientByChatId[chatId] = this@webSocket
            null to operatorByChatId
        }

        suspend fun save(message: ReceivedMessageDto) {
            messageConverter.toCreateMessageArgument(chatId, userId, message)
                .let { messageService.create(it) }
        }

        try {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                //receiveDeserialized<ReceivedMessageDto>() - Работает ровно через раз. Понятия не имею, почему!

                val message = mapper.readValue<ReceivedMessageDto>(frame.data)
                message.authorId = userId
                map[chatId]?.sendSerialized(message)

                save(message)
            }
        } catch (e: ClosedReceiveChannelException) {
            println(e.stackTraceToString())
        } catch (e: Throwable) {
            println(e.stackTraceToString())
        } catch (e: java.lang.Exception) {
            println(e.stackTraceToString())
        } finally {
            close(CloseReason(CloseReason.Codes.NORMAL, "finally closed"))
            println("Removing user!")
        }
    }

    val connectionsMap = ConcurrentHashMap<UUID, DefaultWebSocketSession>()
    webSocket("/sessions/{sessionId}/{role}") {
        val sessionId = UUID.fromString(call.parameters["sessionId"]!!)
        val role = call.parameters["role"]!!

        if (role == "operator") {
            connectionsMap[sessionId] = this
            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    send(receivedText)
                }
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                println("onError ${closeReason.await()}")
            } finally {
                println("Removing operator!")
            }
        }

        if (role == "client") {
            val operatorConnection = connectionsMap[sessionId]
            try {
                for (frame in incoming) {
                    operatorConnection!!.send(frame)

                    frame as? Frame.Text ?: continue
                    sessionEventService.create(CreateSessionEventArgument(frame.readText()))
                }
            } catch (e: ClosedReceiveChannelException) {
                send("closing")
                close(CloseReason(CloseReason.Codes.NORMAL, "qwe1"))
                println(e.stackTraceToString())
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                send("closing")
                close(CloseReason(CloseReason.Codes.NORMAL, "qwe2"))
                println(e.stackTraceToString())
                println("onError ${closeReason.await()}")
            } catch (e: java.lang.Exception) {
                send("closing")
                close(CloseReason(CloseReason.Codes.NORMAL, "qwe3"))
                println(e.stackTraceToString())
            } finally {
                send("closing")
                close(CloseReason(CloseReason.Codes.NORMAL, "qwe4"))
                println("Removing user!")
            }
        }
    }
}