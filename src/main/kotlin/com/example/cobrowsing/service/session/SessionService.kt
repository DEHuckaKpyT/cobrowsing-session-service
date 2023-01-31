package com.example.cobrowsing.service.session

import com.example.cobrowsing.models.Chat
import com.example.cobrowsing.models.Session
import com.example.cobrowsing.plugins.execute
import com.example.cobrowsing.service.session.argument.CreateSessionArgument


/**
 * Created on 25.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
class SessionService {

    suspend fun create(argument: CreateSessionArgument): Session = execute {
        Session.new {
//            chatId = EntityID(argument.chatId, Chats)
            chat = Chat[argument.chatId]
        }
    }
}