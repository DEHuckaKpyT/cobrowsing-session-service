package com.example.cobrowsing.routes.chatmessage.dto

import com.example.cobrowsing.models.enums.MessageType
import java.time.LocalDateTime
import java.util.*


/**
 * Created on 30.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class MessageListDto(

    val chatId: UUID,
    val text: String,
    val type: MessageType,
    val authorId: UUID?,
    val createdDate: LocalDateTime
)
