package com.example.cobrowsing.routes.websockets

import com.example.cobrowsing.converters.MessageConverter
import com.example.cobrowsing.converters.SessionConverter
import com.example.cobrowsing.extensions.mapper
import com.example.cobrowsing.extensions.toUUID
import com.example.cobrowsing.plugins.CustomUserPrincipal
import com.example.cobrowsing.routes.websockets.dto.ReceivedMessageDto
import com.example.cobrowsing.routes.websockets.dto.SessionCommandEvent
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

        val (userId, myEnd, otherEnd) = call.principal<CustomUserPrincipal>()?.run {
            operatorByChatId[chatId] = this@webSocket
            Triple(id, operatorByChatId, clientByChatId)
        } ?: kotlin.run {
            clientByChatId[chatId] = this@webSocket
            Triple(null, clientByChatId, operatorByChatId)
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
                otherEnd[chatId]?.sendSerialized(message)

                save(message)
            }
        } catch (e: ClosedReceiveChannelException) {
            println(e.stackTraceToString())
        } catch (e: Throwable) {
            println(e.stackTraceToString())
        } catch (e: java.lang.Exception) {
            println(e.stackTraceToString())
        } finally {
            myEnd.remove(chatId)
            close(CloseReason(CloseReason.Codes.NORMAL, "finally closed"))
            println("Removing user!")
        }
    }

    webSocket("/sessions/{sessionId}") {
        val sessionId = call.parameters["sessionId"]?.toUUID()
            ?: throw BadRequestException("Не указан id сессии")

        suspend fun client(map: Map<UUID, WebSocketServerSession>, frame: Frame) {
            if (frame !is Frame.Text) return

            map[sessionId]?.send(frame)
                ?: sendSerialized(SessionCommandEvent.DISCONNECTED)

            sessionEventService.create(CreateSessionEventArgument(frame.readText()))
        }

        suspend fun operator(map: Map<UUID, WebSocketServerSession>, frame: Frame) {
            if (frame !is Frame.Text) return

            map[sessionId]?.send(frame.readText())
        }

        val (myEnd, otherEnd, execute) = call.principal<CustomUserPrincipal>()
            ?.run {
                operatorBySessionId[sessionId] = this@webSocket
                Triple(
                    operatorBySessionId,
                    clientBySessionId,
                    suspend() { frame: Frame -> operator(clientBySessionId, frame) })
            } ?: kotlin.run {
            clientBySessionId[sessionId] = this@webSocket
            Triple(
                clientBySessionId,
                operatorBySessionId,
                suspend() { frame: Frame -> client(operatorBySessionId, frame) })
        }

        try {
            for (frame in incoming) {
                execute(frame)
            }
        } catch (e: ClosedReceiveChannelException) {
            println(e.stackTraceToString())
        } catch (e: Throwable) {
            println(e.stackTraceToString())
        } catch (e: java.lang.Exception) {
            println(e.stackTraceToString())
        } finally {
            otherEnd[sessionId]?.sendSerialized(SessionCommandEvent.DISCONNECTED)
            myEnd.remove(sessionId)

            close(CloseReason(CloseReason.Codes.NORMAL, "finally closed"))
            println("Removing user!")
        }
    }
}


suspend fun <T1> suspend(block: suspend (T1) -> Unit): suspend (T1) -> Unit {
    return block
}
