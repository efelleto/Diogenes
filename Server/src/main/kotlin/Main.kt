package dev.efelleto.diogenes.server

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
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
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.origin
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

// Cores ANSI para Terminal
private const val RESET = "\u001B[0m"
private const val LIGHT_BLUE = "\u001B[36m"
private const val DARK_BLUE = "\u001B[34m"
private const val WHITE = "\u001B[37m"
private const val RED = "\u001B[31m"
private const val GRAY = "\u001B[90m"
private const val BOLD = "\u001B[1m"
private const val SEPARATOR = "\u001B[90m__________________________________________________________________________________\u001B[0m"

// Global logging handler for formatted terminal output.
object Logger {
    private const val RESET = "\u001B[0m"
    private const val LIGHT_BLUE = "\u001B[36m"
    private const val WHITE = "\u001B[37m"
    private const val RED = "\u001B[31m"

    fun info(prefix: String, message: String) {
        println("$WHITE[$LIGHT_BLUE INFO $WHITE] $LIGHT_BLUE$prefix: $WHITE$message$RESET")
    }

    fun error(prefix: String, message: String) {
        println("$WHITE[$RED ERROR $WHITE] $RED$prefix: $WHITE$message$RESET")
    }
}


fun main() {

    if (System.getProperty("os.name").lowercase().contains("win")) {
        org.fusesource.jansi.AnsiConsole.systemInstall()
    }

//  Silence library debug logs (Netty, Mongo, JDA)
    val root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
    root.level = Level.ERROR

    // Configuration Setup
    if (!ConfigManager.setup()) return
    val config = ConfigManager.loadServerConfig()

    showBanner("1.0.11") // visual

    // Service Initialization
    try {
        logInfo("DATABASE", "Connecting to MongoDB...")
        MongoService.init(config.mongoUri)

        logInfo("JDA", "Starting Discord Bot Manager...")
        BotManager.start(config.botToken)

        logInfo("SERVER", "Booting Ktor engine on port ${config.port}...")
    } catch (e: Exception) {
        logError("FATAL", "Failed to initialize services: ${e.message}")
        return
    }

    // Ktor API Server
    embeddedServer(Netty, port = config.port) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                serializeNulls()
            }
        }

        routing {
            get("/") {
                call.respondText("[+] Diogenes API is Running!", ContentType.Text.Plain)
            }

            post("/v1/validate") {
                try {
                    val request = call.receive<LicenseRequest>()
                    val clientIp = call.request.origin.remoteHost

                    val response = LicenseService.validate(request, clientIp)

                    // Validation log
                    if (response.isAuthorized) {
                        logInfo("AUTH", "Success: ${request.key} | IP: $clientIp")
                    } else {
                        logInfo("AUTH", "Denied: ${request.key} | Reason: ${response.message}")
                    }

                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    logError("API", "Invalid request received from ${call.request.origin.remoteHost}")
                    call.respond(HttpStatusCode.BadRequest, "Invalid request format.")
                }
            }
        }
    }.start(wait = true)
}

// Auxiliary Log functions
private fun logInfo(prefix: String, message: String) {
    println("$WHITE[$LIGHT_BLUE INFO $WHITE] $LIGHT_BLUE$prefix: $WHITE$message$RESET")
}

private fun logError(prefix: String, message: String) {
    println("$WHITE[$RED ERROR $WHITE] $RED$prefix: $WHITE$message$RESET")
}

private fun showBanner(version: String) {
    println(SEPARATOR)
    println("""
$LIGHT_BLUE    _____^_
$LIGHT_BLUE   |    |    \
$LIGHT_BLUE    \   /  ^ |                        
$LIGHT_BLUE   / \_/   0  \                        $DARK_BLUE$BOLD Diogenes $version
$LIGHT_BLUE  /            \      $GRAY"The most beautiful thing in the world is freedom of speech"
$LIGHT_BLUE /    ____      0
$LIGHT_BLUE/      /  \___ _/
    """.trimIndent())
    println(SEPARATOR)
    println("")
}
