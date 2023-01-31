package com.example.cobrowsing.models

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*


/**
 * Created on 26.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
object Chats : UUIDTable("chat") {

    val createdDate = datetime("created_date").defaultExpression(CurrentDateTime)
}

class Chat(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Chat>(Chats)


    val createdDate by Chats.createdDate
    val messages by Message referrersOn Messages.chatId
    val sessions by Session referrersOn Sessions.chatId
}