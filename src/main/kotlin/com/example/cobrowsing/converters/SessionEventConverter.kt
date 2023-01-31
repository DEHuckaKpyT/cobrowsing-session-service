package com.example.cobrowsing.converters

import com.example.cobrowsing.models.SessionEvent
import com.example.cobrowsing.routes.sessionevent.dto.SessionEventDto
import com.example.cobrowsing.routes.sessionevent.dto.SessionEventListParams
import com.example.cobrowsing.service.sessionevent.argument.SearchSessionEventArgument
import org.mapstruct.Mapper
import org.mapstruct.Mapping


/**
 * Created on 27.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
@Mapper
interface SessionEventConverter {

//    @Mapping(target = "createdDate", expression = "java(dto.getTimestamp().toLocalDateTime())")
//    fun toCreateSessionEventArgument(dto: SessionEventReceiveDto, content: String): CreateSessionEventArgument

    @Mapping(target = "copy", ignore = true)
    fun toSearchSessionEventArgument(dto: SessionEventListParams): SearchSessionEventArgument

    @Mapping(target = "copy", ignore = true)
    fun toSessionEventDto(sessionEvent: SessionEvent): SessionEventDto

    fun toSessionEventDto(sessionEvents: List<SessionEvent>): List<SessionEventDto>
}