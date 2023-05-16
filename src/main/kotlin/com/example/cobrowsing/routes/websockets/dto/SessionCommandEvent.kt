package com.example.cobrowsing.routes.websockets.dto


/**
 * Created on 11.05.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class SessionCommandEvent(

    val content: String? = null,
    val command: SessionCommandType
) {

    companion object {
        val CONNECTED = SessionCommandEvent(command = SessionCommandType.CONNECTED)
        val DISCONNECTED = SessionCommandEvent(command = SessionCommandType.DISCONNECTED)
    }
}

enum class SessionCommandType {
    CONNECTED,
    DISCONNECTED,
    CLOSE,
    TRANSPORT,
}
