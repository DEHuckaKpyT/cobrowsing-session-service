package com.example.cobrowsing.converters

import com.example.cobrowsing.models.Session
import com.example.cobrowsing.routes.session.dto.CreateSessionDto
import com.example.cobrowsing.routes.session.dto.SessionDto
import com.example.cobrowsing.service.session.argument.CreateSessionArgument
import org.mapstruct.Mapper
import org.mapstruct.Mapping


/**
 * Created on 29.12.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
@Mapper
interface SessionConverter {

    @Mapping(target = "copy", ignore = true)
    fun toCreateSessionArgument(createDto: CreateSessionDto): CreateSessionArgument

    @Mapping(target = "id", source = "id.value")
    fun toSessionDto(session: Session): SessionDto
}