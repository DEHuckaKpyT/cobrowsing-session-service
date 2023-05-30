package com.example.cobrowsing.routes.session.dto

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import java.util.*


/**
 * Created on 29.05.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class FinishSessionDto(

    @PathParam("session id") val id: UUID
)
