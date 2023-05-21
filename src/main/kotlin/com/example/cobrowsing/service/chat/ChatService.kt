package com.example.cobrowsing.service.chat

import com.example.cobrowsing.models.Chat
import com.example.cobrowsing.plugins.execute
import com.example.cobrowsing.plugins.read
import java.util.*


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

    suspend fun isSharingAccepted(chatId: UUID): Boolean = read {
        Chat[chatId].sharingAccepted
    }

    suspend fun acceptSharing(chatId: UUID) = execute {
        Chat[chatId].sharingAccepted = true
    }
}