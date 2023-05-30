package com.example.cobrowsing.routes.session.dto

import java.time.LocalDateTime
import java.util.*


/**
 * Created on 29.05.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class SessionDto(

    val id: UUID,
    val finished: Boolean,
    val createdDate: LocalDateTime
)
