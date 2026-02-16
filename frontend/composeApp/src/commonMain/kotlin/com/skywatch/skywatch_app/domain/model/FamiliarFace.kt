package com.skywatch.skywatch_app.domain.model

/**
 * Represents a familiar face that the AI can recognize.
 * 
 * @property id Unique identifier for the familiar face
 * @property name Name of the person
 * @property category Category/group the person belongs to (e.g., "Family", "Friend", "Delivery")
 * @property imageData The face image stored as ByteArray
 * @property embedding Optional ML embedding for face recognition (placeholder for future backend integration)
 */
data class FamiliarFace(
    val id: String,
    val name: String,
    val category: String,
    val imageData: ByteArray,
    val embedding: List<Float>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FamiliarFace

        if (id != other.id) return false
        if (name != other.name) return false
        if (category != other.category) return false
        if (!imageData.contentEquals(other.imageData)) return false
        if (embedding != other.embedding) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + imageData.contentHashCode()
        result = 31 * result + (embedding?.hashCode() ?: 0)
        return result
    }
}

/**
 * Predefined categories for familiar faces.
 */
object FaceCategory {
    const val FAMILY = "Family"
    const val FRIEND = "Friend"
    const val DELIVERY = "Delivery"
    const val SERVICE = "Service"
    const val OTHER = "Other"
    
    val all = listOf(FAMILY, FRIEND, DELIVERY, SERVICE, OTHER)
}

