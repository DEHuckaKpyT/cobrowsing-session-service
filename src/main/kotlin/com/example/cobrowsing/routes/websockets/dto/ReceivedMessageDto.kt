package com.example.cobrowsing.routes.websockets.dto

import com.example.cobrowsing.models.enums.MessageType
import com.example.cobrowsing.models.enums.MessageType.TEXT
import java.util.*


/**
 * Created on 03.02.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class ReceivedMessageDto(

    val text: String,
    val type: MessageType = TEXT,
    var authorId: UUID?
)
