package com.example.cobrowsing.plugins

import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.util.*


/**
 * Created on 03.02.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Application.configureAuthentication() {
    install(Authentication) {
        bearer("bearer-auth") {
            realm = "custom realm text"
            authenticate { tokenCredential ->
                if (tokenCredential.token == "abc123") {
                    CustomUserPrincipal(
                        UUID.fromString("00000000-0000-0000-0001-000000000001"),
                        "DEHucka"
                    )
                } else {
                    null
                }
            }
            authHeader { call ->
                call.request.parseAuthorizationHeader()
                    ?: call.parameters["access_token"]?.let {
                        HttpAuthHeader.Single("Bearer", it)
                    }
            }
        }
    }
}

data class CustomUserPrincipal(

    val id: UUID,
    val username: String
) : Principal