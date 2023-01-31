package com.example.cobrowsing.service.message

import com.example.cobrowsing.models.Chats
import com.example.cobrowsing.models.Message
import com.example.cobrowsing.models.Messages
import com.example.cobrowsing.plugins.execute
import com.example.cobrowsing.plugins.read
import com.example.cobrowsing.service.message.argument.CreateMessageArgument
import com.example.cobrowsing.service.message.argument.SearchMessageArgument
import org.jetbrains.exposed.dao.id.EntityID


/**
 * Created on 28.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
class MessageService {

    suspend fun create(argument: CreateMessageArgument): Message = execute {
        Message.new {
            chatId = EntityID(argument.chatId, Chats)
            text = argument.text
            authorId = argument.authorId

            argument.type?.let { type = it }
        }
    }

    suspend fun list(argument: SearchMessageArgument): List<Message> = read {
        Message.find {
            Messages.chatId eq argument.chatId
        }.toList()
    }
}