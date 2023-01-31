package com.example.cobrowsing.routes.chatmessage.dto

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import java.util.*


/**
 * Created on 30.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class SearchChatMessagesParams(

    @PathParam("Идентификатор чата") val chatId: UUID
)
