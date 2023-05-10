package com.example.cobrowsing.service.message

import com.example.cobrowsing.models.Chats
import com.example.cobrowsing.models.Message
import com.example.cobrowsing.models.Messages
import com.example.cobrowsing.models.enums.MessageType.TEXT
import com.example.cobrowsing.plugins.execute
import com.example.cobrowsing.plugins.read
import com.example.cobrowsing.service.message.argument.CreateMessageArgument
import com.example.cobrowsing.service.message.argument.SearchMessageArgument
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


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

    suspend fun lastMessagesByChat(): List<Message> = read {
        val slice = Messages.slice(
            customDistinctOn(Messages.chatId),
            *(Messages.columns).toTypedArray()
        ).select(
            Messages.type.eq(TEXT)
        ).orderBy(
            Messages.chatId to SortOrder.ASC,
            Messages.createdDate to SortOrder.DESC
        )

        Message.wrapRows(slice).toList()
    }
}

fun customDistinctOn(vararg expressions: Expression<*>): CustomFunction<Boolean?> = customBooleanFunction(
    functionName = "DISTINCT ON",
    postfix = " TRUE",
    params = expressions
)

fun customBooleanFunction(
    functionName: String, postfix: String = "", vararg params: Expression<*>
): CustomFunction<Boolean?> =
    object : CustomFunction<Boolean?>(functionName, BooleanColumnType(), *params) {
        override fun toQueryBuilder(queryBuilder: QueryBuilder) {
            super.toQueryBuilder(queryBuilder)
            if (postfix.isNotEmpty()) {
                queryBuilder.append(postfix)
            }
        }
    }