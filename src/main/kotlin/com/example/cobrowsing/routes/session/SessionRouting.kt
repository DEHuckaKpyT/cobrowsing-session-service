package com.example.cobrowsing.routes.session

import com.example.cobrowsing.converters.SessionConverter
import com.example.cobrowsing.routes.session.dto.CreateSessionDto
import com.example.cobrowsing.routes.session.dto.FinishSessionDto
import com.example.cobrowsing.routes.session.dto.MaskSettingsDto
import com.example.cobrowsing.routes.session.dto.SessionDto
import com.example.cobrowsing.service.session.SessionService
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
 * Created on 24.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Routing.sessionRouting() = apiRouting {

    val sessionService = SessionService()
    val sessionConverter: SessionConverter = Mappers.getMapper(SessionConverter::class.java)
    val rrwebConfig = this.ktorRoute.application.environment.config.config("rrweb")

    route("/sessions") {
        route("/create").post<Unit, SessionDto, CreateSessionDto>(
            info("Создать сессию")
        ) { _, body ->
            sessionConverter.toCreateSessionArgument(body)
                .let { sessionService.create(it) }
                .let { sessionConverter.toSessionDto(it) }
                .let { respond(it) }
        }

        route("/{id}/finish").post<FinishSessionDto, Unit, Unit>(
            info("Завершить сессию")
        ) { params, _ ->
            sessionService.finish(params.id)
                .let { this@post.pipeline.call.response.status(HttpStatusCode.OK) }
        }

        route("/mask-settings").get<Unit, MaskSettingsDto>(
            info("Настройки маскирования информации")
        ) { _ ->
            respond(
                MaskSettingsDto(
                    maskTextClass = rrwebConfig.property("mask-text-class").getList()
                        .joinToString(separator = "|", transform = { "($it)" }),
                    blockClass = rrwebConfig.property("block-class").getList()
                        .joinToString(separator = "|", transform = { "($it)" }),
                    maskAllInputs = rrwebConfig.property("mask-all-inputs").getString().toBooleanStrict()
                )
            )
        }
    }
}