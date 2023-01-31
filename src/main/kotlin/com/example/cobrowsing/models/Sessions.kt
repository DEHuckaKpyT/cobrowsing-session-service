package com.example.cobrowsing.models

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*


/**
 * Created on 25.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
object Sessions : UUIDTable("session") {

    val chatId = reference("chat_id", Chats)
    val finished = bool("finished").default(false)
    val createdDate = datetime("created_date").defaultExpression(CurrentDateTime)
}

class Session(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Session>(Sessions)

    var chatId by Messages.chatId
    var finished by Sessions.finished

    val createdDate by Sessions.createdDate
    var chat by Chat referencedOn Sessions.chatId
}