package dev.efelleto.diogenes.sdk

import dev.efelleto.diogenes.core.model.LicenseRequest
import dev.efelleto.diogenes.core.security.SecurityUtils
import dev.efelleto.diogenes.sdk.network.DiogenesClient
import java.io.File
import java.util.concurrent.CompletableFuture

object DiogenesSDK {

    private lateinit var productId: String
    private lateinit var baseUrl: String
    private lateinit var pluginFolder: File

    // Initialize the SDK. Call this at the very beginning of your onEnable
    @JvmStatic
    fun init(productId: String, baseUrl: String, pluginFolder: File) {
        this.productId = productId
        this.baseUrl = baseUrl
        this.pluginFolder = pluginFolder
    }

    // Runs the license validation process
    // It handles file creation, hardware identification and server communication.
    @JvmStatic
    fun verify(): CompletableFuture<SDKResponse> {
        val future = CompletableFuture<SDKResponse>()

        // Ensure plugin directory exists
        if (!pluginFolder.exists()) pluginFolder.mkdirs()

        // Load/create license.yml
        val licenseFile = File(pluginFolder, "license.yml")
        if (!licenseFile.exists()) {
            licenseFile.writeText(
                "# 🏺 Diogenes Licensing System\n" +
                        "# Get your key at: Discord Store\n" +
                        "license-key: 'PASTE-YOUR-KEY-HERE'\n"
            )
            future.complete(SDKResponse(false, "License file created. Please fill it and restart the server."))
            return future
        }

        // Extract key from file
        val key = extractKey(licenseFile)
        if (key.isBlank() || key == "PASTE-YOUR-KEY-HERE") {
            future.complete(SDKResponse(false, "Please provide a valid license key in license.yml."))
            return future
        }

        // Gather Security Data
        val hwid = SecurityUtils.getHWID()
        val jarHash = "SINOPE_PENDING" // This will be handled by Sinope later

        val request = LicenseRequest(key, productId, hwid, jarHash)

        // Async Request to Server
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

// Simple response wrapper for the SDK
data class SDKResponse(val isAuthorized: Boolean, val message: String)