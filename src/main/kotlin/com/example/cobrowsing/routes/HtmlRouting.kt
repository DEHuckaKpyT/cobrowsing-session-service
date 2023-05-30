package com.example.cobrowsing.routes

import io.ktor.server.http.content.*
import io.ktor.server.routing.*


/**
 * Created on 30.05.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Routing.htmlRouting() {

    static {
        resource("/", "static/admin.html")
        resource("/admin", "static/admin.html")
        resource("/replayer", "static/replayer.html")
        resource("/js/general.js", "static/js/general.js")
        resource("/js/replayer.js", "static/js/replayer.js")
    }
}