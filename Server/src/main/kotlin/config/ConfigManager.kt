package dev.efelleto.diogenes.server.config

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths

data class ServerConfig(
    val port: Int,
    val mongoUri: String,
    val botToken: String,
    val adminRoleId: String,
    val logChannelId: String
)

object ConfigManager {
    private val configPath = Paths.get("diogenes-config")
    private val serverConfigFile = File("diogenes-config/server/config.yml")
    private val discordMessagesFile = File("diogenes-config/discord/messages.yml")

    // verifies if the folder structure exists
    // return 'false' if created files
    fun setup(): Boolean {
        var firstRun = false

        if (!Files.exists(configPath)) {
            println("[-] Creating Diogenes configuration structure...")
            Files.createDirectories(Paths.get("diogenes-config/server"))
            Files.createDirectories(Paths.get("diogenes-config/discord"))
            firstRun = true
        }

        if (!serverConfigFile.exists()) {
            serverConfigFile.writeText("""
                server:
                  port: 8080
                  mongo-uri: "mongodb://localhost:27017"
                
                discord:
                  bot-token: "YOUR_BOT_TOKEN_HERE"
                  admin-role-id: "0000000000"
                  log-channel-id: "0000000000"
            """.trimIndent())
            firstRun = true
        }

        if (!discordMessagesFile.exists()) {
            discordMessagesFile.writeText("""
                auth-success: "✅ License verified! Welcome back, %user%."
                auth-failed: "❌ Access Denied: %reason%"
                hwid-alert: "🚨 Security Alert: Unauthorized HWID detected for key %key%!"
            """.trimIndent())
            firstRun = true
        }

        if (firstRun) {
            println("\n[!] Config folder created on: ${configPath.toAbsolutePath()}")
            println("[!] Please fill the information in ./diogenes-config/server/config.yml and restart.")
            return false
        }

        return true
    }

    fun loadServerConfig(): ServerConfig {
        val yaml = Yaml()
        val inputStream = FileInputStream(serverConfigFile)

        val rawData = yaml.load(inputStream)
        val data = rawData as Map<*, *>

        inputStream.close()

        val server = data["server"] as Map<*, *>
        val discord = data["discord"] as Map<*, *>

        return ServerConfig(
            port = (server["port"] as? Int) ?: 8080,
            mongoUri = server["mongo-uri"].toString(),
            botToken = discord["bot-token"].toString(),
            adminRoleId = discord["admin-role-id"].toString(),
            logChannelId = discord["log-channel-id"].toString()
        )
    }
}