package com.example.cobrowsing.service.message.argument

import com.example.cobrowsing.models.enums.MessageType
import java.util.*


/**
 * Created on 28.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class CreateMessageArgument(

    val chatId: UUID,
    val text: String,
    val type: MessageType?,
    val authorId: UUID?
)
