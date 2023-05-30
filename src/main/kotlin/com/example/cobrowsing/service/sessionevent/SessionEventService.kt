package com.example.cobrowsing.service.sessionevent

import com.example.cobrowsing.models.SessionEvent
import com.example.cobrowsing.models.SessionEvents
import com.example.cobrowsing.models.Sessions
import com.example.cobrowsing.plugins.execute
import com.example.cobrowsing.plugins.read
import com.example.cobrowsing.service.sessionevent.argument.CreateSessionEventArgument
import com.example.cobrowsing.service.sessionevent.argument.SearchSessionEventArgument
import org.jetbrains.exposed.dao.id.EntityID


/**
 * Created on 27.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
class SessionEventService {

    suspend fun create(argument: CreateSessionEventArgument): SessionEvent = execute {
        SessionEvent.new {
            sessionId = EntityID(argument.sessionId, Sessions)
            content = argument.content
        }
    }

    suspend fun list(searchArgument: SearchSessionEventArgument): List<SessionEvent> = read {
        SessionEvent.find {
            SessionEvents.sessionId eq searchArgument.sessionId
        }.toList()
    }
}