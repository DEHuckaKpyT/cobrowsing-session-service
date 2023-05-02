package com.example.cobrowsing.models

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*


/**
 * Created on 27.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */

object SessionEvents : UUIDTable("session_event") {

    val content = text("content")
}

class SessionEvent(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SessionEvent>(SessionEvents)

    var content by SessionEvents.content
}