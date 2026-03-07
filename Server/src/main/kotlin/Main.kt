package dev.efelleto.diogenes.server

import dev.efelleto.diogenes.core.model.LicenseRequest
import dev.efelleto.diogenes.server.config.ConfigManager
import dev.efelleto.diogenes.server.database.MongoService
import dev.efelleto.diogenes.server.discord.BotManager
import dev.efelleto.diogenes.server.service.LicenseService
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    // Initial configuration setup
    if (!ConfigManager.setup()) return

    val config = ConfigManager.loadServerConfig()
    println("[>] Initializing Diogenes Server...")

    // Services Initialization
    MongoService.init(config.mongoUri)
    BotManager.start(config.botToken)

    // Ktor API Server
    embeddedServer(Netty, port = config.port) {

        // JSON serialization
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                serializeNulls()
            }
        }

        routing {
            // Health Check
            get("/") {
                call.respondText("[+] Diogenes API is Running!", ContentType.Text.Plain)
            }

            // Licensing validation endpoint
            post("/v1/validate") {
                try {
                    val request = call.receive<LicenseRequest>()
                    val clientIp = call.request.origin.remoteHost

                    // Let the service handle the logic =D
                    val response = LicenseService.validate(request, clientIp)

                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    // "Safety"
                    call.respond(HttpStatusCode.BadRequest, "Invalid request format.")
                }
            }
        }
    }.start(wait = true)
}