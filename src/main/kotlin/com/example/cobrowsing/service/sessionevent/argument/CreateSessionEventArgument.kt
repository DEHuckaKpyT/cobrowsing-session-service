package com.example.cobrowsing.service.sessionevent.argument

import java.util.*


/**
 * Created on 27.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class CreateSessionEventArgument(

    val sessionId: UUID,
    val content: String,
)
