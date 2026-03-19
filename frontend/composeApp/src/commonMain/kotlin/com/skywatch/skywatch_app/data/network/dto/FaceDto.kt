package com.skywatch.skywatch_app.data.network.dto

import com.skywatch.skywatch_app.domain.model.FamiliarFace
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data-transfer object that mirrors the backend `FamiliarFaceResponse` JSON shape.
 */
@Serializable
data class FaceDto(
    val id: String,
    val name: String,
    val category: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Map a backend DTO to the domain [FamiliarFace].
 *
 * The [imageUrl] is resolved against the API base URL so that
 * the presentation layer can load it directly.
 */
fun FaceDto.toDomain(baseUrl: String): FamiliarFace {
    val fullImageUrl = imageUrl?.let { url ->
        if (url.startsWith("http")) url else "$baseUrl$url"
    }

    return FamiliarFace(
        id = id,
        name = name,
        category = category ?: "Other",
        imageData = ByteArray(0), // no raw bytes when loaded from network
        imageUrl = fullImageUrl
    )
}
