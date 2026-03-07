package dev.efelleto.diogenes.core.model

import kotlinx.serialization.Serializable


// entity representing the database structure (MongoDB).
@Serializable
data class License(
    val key: String,
    val productId: String,
    val discordId: String? = null,
    var hwid: String? = null,
    var lastIp: String? = null,
    val active: Boolean = true
)


// DTO sent from the plugin (SDK) to the server.
@Serializable
data class LicenseRequest(
    val key: String,
    val productId: String,
    val hwid: String,
    val jarSha256: String
)


// DTO sent from the server back to the plugin (SDK).
@Serializable
data class LicenseResponse(
    val isAuthorized: Boolean,
    val message: String,
    val productName: String? = null,
    val buyerName: String? = null
)