package com.example.cobrowsing.service.chat

import com.example.cobrowsing.models.Chat
import com.example.cobrowsing.plugins.execute


/**
 * Created on 30.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
class ChatService {

    suspend fun create(): Chat = execute {
        Chat.new {
        }
    }
}