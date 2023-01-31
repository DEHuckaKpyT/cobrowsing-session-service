package com.example.cobrowsing.routes.sessionevent.dto

import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import java.util.*


/**
 * Created on 27.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class SessionEventListParams(

    @QueryParam("Идентификатор сессии") val sessionId: UUID?
)
