package com.example.cobrowsing.routes.session.dto

import com.papsign.ktor.openapigen.annotations.Request
import java.util.*


/**
 * Created on 26.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
@Request("Создание сессии")
data class CreateSessionDto(

    val chatId: UUID
)
