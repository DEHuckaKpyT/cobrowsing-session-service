package com.example.cobrowsing.service.sessionevent

import com.example.cobrowsing.models.SessionEvent
import com.example.cobrowsing.models.SessionEvents
import com.example.cobrowsing.plugins.execute
import com.example.cobrowsing.plugins.read
import com.example.cobrowsing.service.sessionevent.argument.CreateSessionEventArgument
import com.example.cobrowsing.service.sessionevent.argument.SearchSessionEventArgument


/**
 * Created on 27.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
class SessionEventService {

    suspend fun create(argument: CreateSessionEventArgument): SessionEvent = execute {
        SessionEvent.new {
            content = argument.content
        }
    }

    suspend fun list(searchArgument: SearchSessionEventArgument): List<SessionEvent> = read {
        SessionEvent.find {
            SessionEvents.id.isNotNull()
        }.toList()
    }
}