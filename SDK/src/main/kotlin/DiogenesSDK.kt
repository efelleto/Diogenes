package dev.efelleto.diogenes.sdk

import dev.efelleto.diogenes.core.model.LicenseRequest
import dev.efelleto.diogenes.core.security.SecurityUtils
import dev.efelleto.diogenes.sdk.network.DiogenesClient
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

object DiogenesSDK {

    private lateinit var productId: String
    private lateinit var baseUrl: String
    private lateinit var pluginFolder: File

    private val bannerShown = AtomicBoolean(false)

    private val SDK_VERSION = this.javaClass.`package`.implementationVersion ?: "1.0.17"

    private const val LIGHT_BLUE = "§3"
    private const val DARK_BLUE = "§9"
    private const val WHITE = "§f"
    private const val RED = "§c"
    private const val GREEN = "§a"
    private const val GRAY = "§7"
    private const val SEPARATOR = "§8__________________________________________________________________________________"


    @JvmStatic
    fun init(plugin: Plugin, productId: String, baseUrl: String, onSuccess: Runnable) {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {

            // Core data
            this.productId = productId
            this.baseUrl = baseUrl
            this.pluginFolder = plugin.dataFolder

            // Show unified banner once per server session
            if (bannerShown.compareAndSet(false, true)) {
                showBanner()
            }

            // Execute validation
            verify().thenAccept { response ->
                if (response.isAuthorized) {
                    Bukkit.getConsoleSender().sendMessage("$WHITE[$LIGHT_BLUE INFO $WHITE] ${LIGHT_BLUE}AUTH: ${WHITE}Successfully $GREEN authenticated.")
                    Bukkit.getConsoleSender().sendMessage("$WHITE[$LIGHT_BLUE INFO $WHITE] ${LIGHT_BLUE}PRODUCT: ${WHITE}$productId")

                    // Run success callback on main thread
                    Bukkit.getScheduler().runTask(plugin, onSuccess)
                } else {
                    // Failure Logs
                    Bukkit.getConsoleSender().sendMessage("$WHITE[$LIGHT_BLUE INFO $WHITE] ${RED}AUTH: ${WHITE}${response.message}")
                    Bukkit.getConsoleSender().sendMessage("$WHITE[$LIGHT_BLUE INFO $WHITE] ${RED}AUTH: ${WHITE}Plugin will be disabled.")

                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        Bukkit.getPluginManager().disablePlugin(plugin)
                    })
                }
            }.exceptionally { ex ->
                // Error Log
                Bukkit.getConsoleSender().sendMessage("$WHITE[$LIGHT_BLUE INFO $WHITE] ${RED}ERROR: ${WHITE}Remote server unreachable.")
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    Bukkit.getPluginManager().disablePlugin(plugin)
                })
                null
            }
        }, 10L)
    }

    private fun showBanner() {
        val banner = """
            
            $SEPARATOR
             
            $LIGHT_BLUE    _____^_
            $LIGHT_BLUE   |    |    \
            $LIGHT_BLUE    \   /  ^ |                        
            $LIGHT_BLUE   / \_/   0  \                        $DARK_BLUE§lDiogenes $SDK_VERSION
            $LIGHT_BLUE  /            \      $GRAY"The most beautiful thing in the world is freedom of speech"
            $LIGHT_BLUE /    ____      0
            $LIGHT_BLUE/      /  \___ _/
             
            $SEPARATOR
        """.trimIndent()

        Bukkit.getConsoleSender().sendMessage(banner)
    }

    fun verify(): CompletableFuture<SDKResponse> {
        val future = CompletableFuture<SDKResponse>()

        if (!pluginFolder.exists()) pluginFolder.mkdirs()

        val licenseFile = File(pluginFolder, "license.yml")
        if (!licenseFile.exists()) {
            licenseFile.writeText(
                "# Diogenes Licensing System\n" +
                        "license-key: 'PASTE-YOUR-KEY-HERE'\n"
            )
            future.complete(SDKResponse(false, "License file created. Please fill it and restart the server."))
            return future
        }

        val key = extractKey(licenseFile)
        if (key.isBlank() || key == "PASTE-YOUR-KEY-HERE") {
            future.complete(SDKResponse(false, "Please provide a valid license key in license.yml."))
            return future
        }

        val hwid = SecurityUtils.getHWID()
        val jarHash = "SINOPE_PENDING"

        val request = LicenseRequest(key, productId, hwid, jarHash)

        DiogenesClient.requestValidation(baseUrl, request).thenAccept { response ->
            future.complete(SDKResponse(response.isAuthorized, response.message))
        }.exceptionally { ex ->
            future.complete(SDKResponse(false, "Could not connect to Diogenes Server: ${ex.message}"))
            null
        }

        return future
    }

    private fun extractKey(file: File): String {
        return file.readLines()
            .find { it.startsWith("license-key:") }
            ?.substringAfter(":")
            ?.replace("'", "")
            ?.replace("\"", "")
            ?.trim() ?: ""
    }
}

data class SDKResponse(val isAuthorized: Boolean, val message: String)
