package dev.efelleto.diogenes.server.config

import dev.efelleto.diogenes.server.Logger
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream

data class ServerConfig(
    val port: Int,
    val mongoUri: String,
    val botToken: String
)

object ConfigManager {
    private val configFile = File("config.yml")

    // Verifies if config.yml exists
    // Returns 'false' if file was created (first run)
    fun setup(): Boolean {
        if (!configFile.exists()) {
            configFile.writeText("""
                server:
                  port: 8080
                  mongo-uri: "mongodb://localhost:27017"
                
                discord:
                  bot-token: "YOUR_BOT_TOKEN_HERE"
            """.trimIndent())

            Logger.info("CONFIG", "File created at: ${configFile.absolutePath}")
            Logger.info("CONFIG", "Please fill config.yml and restart the server.")
            return false
        }

        return true
    }

    fun loadServerConfig(): ServerConfig {
        val yaml = Yaml()
        val inputStream = FileInputStream(configFile)
        val data = yaml.load<Map<String, Any>>(inputStream)
        inputStream.close()

        val server = data["server"] as Map<*, *>
        val discord = data["discord"] as Map<*, *>

        return ServerConfig(
            port = (server["port"] as? Int) ?: 8080,
            mongoUri = server["mongo-uri"].toString(),
            botToken = discord["bot-token"].toString()
        )
    }
}