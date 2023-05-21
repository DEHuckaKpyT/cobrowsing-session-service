package com.example.cobrowsing.routes.chat.dto

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import java.util.*


/**
 * Created on 21.05.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class PathChatIdDto(
    @PathParam("chat id") val chatId: UUID
)
