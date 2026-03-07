package dev.efelleto.diogenes.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductModel(
    val id: String,
    val displayName: String,
    val roleId: String
)