package com.example.cobrowsing.models

import com.example.cobrowsing.models.enums.MessageType
import com.example.cobrowsing.models.enums.MessageType.TEXT
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
object Messages : UUIDTable("message") {

    val chatId = reference("chat_id", Chats)
    val text = text("text")
    val type = enumerationByName<MessageType>("type", 255).default(TEXT)
    val authorId = uuid("author_id").nullable()
    val createdDate = datetime("created_date").defaultExpression(CurrentDateTime)
}

class Message(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Message>(Messages)

    var chatId by Messages.chatId
    var text by Messages.text
    var type by Messages.type
    var authorId by Messages.authorId

    val createdDate by Messages.createdDate
    val chat by Chat referencedOn Messages.chatId
}