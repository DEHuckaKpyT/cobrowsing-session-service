package com.example.cobrowsing.converters

import com.example.cobrowsing.models.Message
import com.example.cobrowsing.routes.chatmessage.dto.CreateChatMessageDto
import com.example.cobrowsing.routes.chatmessage.dto.MessageListDto
import com.example.cobrowsing.routes.websockets.dto.ReceivedMessageDto
import com.example.cobrowsing.service.message.argument.CreateMessageArgument
import org.mapstruct.Mapper
import java.util.*


/**
 * Created on 30.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
@Mapper
interface MessageConverter {

    fun toCreateMessageArgument(chatId: UUID, dto: CreateChatMessageDto): CreateMessageArgument

//    fun toCreateMessageArgument(chatId: UUID, dto: ReceivedMessageDto): CreateMessageArgument

    fun toCreateMessageArgument(chatId: UUID, authorId: UUID?, dto: ReceivedMessageDto): CreateMessageArgument

    fun toMessageListDto(message: List<Message>): List<MessageListDto>
}