package com.example.cobrowsing.routes.session

import com.example.cobrowsing.converters.SessionConverter
import com.example.cobrowsing.converters.SessionEventConverter
import com.example.cobrowsing.routes.session.dto.CreateSessionDto
import com.example.cobrowsing.service.sessionevent.SessionEventService
import com.example.cobrowsing.service.session.SessionService
import com.example.cobrowsing.service.sessionevent.argument.CreateSessionEventArgument
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.mapstruct.factory.Mappers
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * Created on 24.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Routing.sessionRouting() = apiRouting {

    val sessionService = SessionService()
    val sessionEventService = SessionEventService()
    val sessionConverter: SessionConverter = Mappers.getMapper(SessionConverter::class.java)
    val connectionsMap = ConcurrentHashMap<UUID, DefaultWebSocketSession>()

    route("/sessions") {
        route("/create").post<Unit, Unit, CreateSessionDto>(
            info("Создать сессию")
        ) { _, body ->
            sessionConverter.toCreateSessionArgument(body)
                .let { sessionService.create(it) }
                .let { this@post.pipeline.call.response.status(HttpStatusCode.OK) }
        }

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
}