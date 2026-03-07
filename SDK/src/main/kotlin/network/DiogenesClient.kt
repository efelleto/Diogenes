package dev.efelleto.diogenes.sdk.network

import dev.efelleto.diogenes.core.model.LicenseRequest
import dev.efelleto.diogenes.core.model.LicenseResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

object DiogenesClient {

    // Ktor HTTP Client configured with Gson for JSON serialization
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
    }

    /**
     * sends a POST request to the licensing server to validate the credentials.
     * we use a "CompletableFuture" to make it easier for Spigot/Paper developers.
     */

    fun requestValidation(url: String, request: LicenseRequest): CompletableFuture<LicenseResponse> {
        val future = CompletableFuture<LicenseResponse>()

        GlobalScope.launch {
            try {
                val response: HttpResponse = client.post("$url/v1/validate") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

                if (response.status == HttpStatusCode.OK) {
                    future.complete(response.body<LicenseResponse>())
                } else {
                    future.complete(LicenseResponse(false, "Server returned status: ${response.status}"))
                }
            } catch (e: Exception) {
                future.complete(LicenseResponse(false, "Connection failed: ${e.message}"))
            }
        }

        return future
    }
}