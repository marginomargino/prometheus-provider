package core

import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.receive

fun main() {

    val dotenv = dotenv {
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }

    val accessToken = dotenv["ACCESS_TOKEN"] ?: error("ACCESS_TOKEN not set in .env")


    embeddedServer(Netty, port = 8080) {
        install(createRouteScopedPlugin("BearerAuthPlugin") {
            onCall { call ->
                val authHeader = call.request.headers["Authorization"]

                if (authHeader == null
                    || !authHeader.startsWith("Bearer ")
                    || authHeader.removePrefix("Bearer ") != accessToken
                ) {
                    call.respond(HttpStatusCode.NotFound)
                    return@onCall
                }
            }
        })

        install(ContentNegotiation) {
            json()
        }
        routing {
            post("/sensors") {
                log.info(call.receive<String>())
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }.start(wait = true)
}
