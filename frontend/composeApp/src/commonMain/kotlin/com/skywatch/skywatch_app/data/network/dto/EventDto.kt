@file:Suppress("DEPRECATION")
package com.skywatch.skywatch_app.data.network.dto

import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

/**
 * Data-transfer object that mirrors the backend `EventResponse` JSON shape.
 */
@Serializable
data class EventDto(
    val id: String,
    val description: String,
    val timestamp: String,
    val type: String,
    val conversation: String? = null,
    @SerialName("video_url") val videoUrl: String? = null,
    @SerialName("screenshot_url") val screenshotUrl: String? = null,
    @SerialName("is_threat") val isThreat: Boolean = false,
    @SerialName("threat_confidence") val threatConfidence: String = "Unknown",
    @SerialName("threat_explanation") val threatExplanation: String? = null,
    @SerialName("matched_face_id") val matchedFaceId: Int? = null,
    @SerialName("matched_face_name") val matchedFaceName: String? = null,
    @SerialName("matched_face_image_url") val matchedFaceImageUrl: String? = null
)

/**
 * Map a backend DTO to the domain [TimelineEvent].
 */
@OptIn(ExperimentalTime::class)
fun EventDto.toDomain(): TimelineEvent {
    val eventType = try {
        EventType.valueOf(type)
    } catch (_: Exception) {
        EventType.OTHER
    }

    val parsedTimestamp = try {
        Instant.parse(timestamp)
    } catch (_: Exception) {
        null
    }

    return TimelineEvent(
        id = id,
        description = description,
        timestamp = parsedTimestamp,
        type = eventType,
        isThreat = isThreat,
        threatConfidence = threatConfidence,
        threatExplanation = threatExplanation,
        screenshotUrl = screenshotUrl,
        matchedFaceId = matchedFaceId,
        matchedFaceName = matchedFaceName,
        matchedFaceImageUrl = matchedFaceImageUrl
    )
}
