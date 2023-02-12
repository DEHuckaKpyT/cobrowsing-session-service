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
    val sessionConverter: SessionConverter = Mappers.getMapper(SessionConverter::class.java)

    route("/sessions") {
        route("/create").post<Unit, Unit, CreateSessionDto>(
            info("Создать сессию")
        ) { _, body ->
            sessionConverter.toCreateSessionArgument(body)
                .let { sessionService.create(it) }
                .let { this@post.pipeline.call.response.status(HttpStatusCode.OK) }
        }
    }
}