package dev.efelleto.diogenes.sdk

import dev.efelleto.diogenes.core.model.LicenseRequest
import dev.efelleto.diogenes.core.security.SecurityUtils
import dev.efelleto.diogenes.sdk.network.DiogenesClient
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

class DiogenesSDK private constructor(
    private val productId: String,
    private val baseUrl: String,
    private val pluginFolder: File
) {

    companion object {
        private val bannerShown = AtomicBoolean(false)
        private val SDK_VERSION get() = DiogenesSDK::class.java.`package`.implementationVersion ?: "1.0.19"

        private const val LIGHT_BLUE = "§3"
        private const val DARK_BLUE = "§9"
        private const val WHITE = "§f"
        private const val RED = "§c"
        private const val GREEN = "§a"
        private const val GRAY = "§7"
        private const val SEPARATOR = "§8__________________________________________________________________________________"

        @JvmStatic
        fun init(plugin: Plugin, productId: String, baseUrl: String, onSuccess: Runnable) {
            val instance = DiogenesSDK(productId, baseUrl, plugin.dataFolder)
            instance.start(plugin, onSuccess)
        }
    }

    private fun start(plugin: Plugin, onSuccess: Runnable) {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            verify().thenAccept { response ->

                // Banner + result printed together on first plugin
                if (bannerShown.compareAndSet(false, true)) {
                    printBannerWithResult(response)
                } else {
                    printResult(response)
                }

                if (response.isAuthorized) {
                    Bukkit.getScheduler().runTask(plugin, onSuccess)
                } else {
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        Bukkit.getPluginManager().disablePlugin(plugin)
                    })
                }
            }.exceptionally {
                if (bannerShown.compareAndSet(false, true)) {
                    printBannerWithResult(SDKResponse(false, "Remote server unreachable."))
                } else {
                    Bukkit.getConsoleSender().sendMessage("$WHITE[$RED ERROR $WHITE] ${RED}AUTH: ${WHITE}Remote server unreachable.")
                }
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    Bukkit.getPluginManager().disablePlugin(plugin)
                })
                null
            }
        }, 10L)
    }

    private fun printBannerWithResult(response: SDKResponse) {
        val statusLine = if (response.isAuthorized) {
            "$WHITE[$LIGHT_BLUE INFO $WHITE] ${LIGHT_BLUE}AUTH: ${WHITE}Successfully $GREEN authenticated. ${LIGHT_BLUE}PRODUCT: ${WHITE}$productId"
        } else {
            "$WHITE[$RED ERROR $WHITE] ${RED}AUTH: ${WHITE}${response.message}"
        }

        val banner = """
            
            $SEPARATOR
             
            $LIGHT_BLUE    _____^_
            $LIGHT_BLUE   |    |    \
            $LIGHT_BLUE    \   /  ^ |                        
            $LIGHT_BLUE   / \_/   0  \                        $DARK_BLUE§lDiogenes $SDK_VERSION
            $LIGHT_BLUE  /            \      $GRAY"The most beautiful thing in the world is freedom of speech"
            $LIGHT_BLUE /    ____      0
            $LIGHT_BLUE/      /  \___ _/
             
            $statusLine
            $SEPARATOR
        """.trimIndent()

        Bukkit.getConsoleSender().sendMessage(banner)
    }

    private fun printResult(response: SDKResponse) {
        if (response.isAuthorized) {
            Bukkit.getConsoleSender().sendMessage("$WHITE[$LIGHT_BLUE INFO $WHITE] ${LIGHT_BLUE}AUTH: ${WHITE}Successfully $GREEN authenticated.")
            Bukkit.getConsoleSender().sendMessage("$WHITE[$LIGHT_BLUE INFO $WHITE] ${LIGHT_BLUE}PRODUCT: ${WHITE}$productId")
        } else {
            Bukkit.getConsoleSender().sendMessage("$WHITE[$RED ERROR $WHITE] ${RED}AUTH: ${WHITE}${response.message}")
        }
    }

    private fun verify(): CompletableFuture<SDKResponse> {
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
        val request = LicenseRequest(key, productId, hwid, "SINOPE_PENDING")

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