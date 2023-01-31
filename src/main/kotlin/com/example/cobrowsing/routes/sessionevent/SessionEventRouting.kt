package com.example.cobrowsing.routes.sessionevent

import com.example.cobrowsing.converters.SessionEventConverter
import com.example.cobrowsing.routes.sessionevent.dto.SessionEventDto
import com.example.cobrowsing.routes.sessionevent.dto.SessionEventListParams
import com.example.cobrowsing.service.sessionevent.SessionEventService
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.ktor.server.routing.*
import org.mapstruct.factory.Mappers


/**
 * Created on 27.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Routing.sessionEventRouting() = apiRouting {
    route("session-events") {

        val sessionEventService = SessionEventService()
        val sessionEventConverter: SessionEventConverter = Mappers.getMapper(SessionEventConverter::class.java)

        route("list").get<SessionEventListParams, List<SessionEventDto>>(
            info("Получить список событий сессии")
        ) { params ->
            sessionEventConverter.toSearchSessionEventArgument(params)
                .let { sessionEventService.list(it) }
                .let { sessionEventConverter.toSessionEventDto(it) }
                .let { respond(it) }
        }
        route("/contents/list").get<SessionEventListParams, List<String>>(
            info("Получить список событий сессии")
        ) { params ->
            sessionEventConverter.toSearchSessionEventArgument(params)
                .let { sessionEventService.list(it) }
                .map { it.content }
                .let { respond(it) }
        }
    }
}