package com.skywatch.skywatch_app.domain.repository

import com.skywatch.skywatch_app.domain.model.FamiliarFace
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing familiar faces.
 * Currently uses in-memory storage, but designed for future backend integration.
 */
interface FamiliarFaceRepository {
    /**
     * Get all familiar faces as a Flow for reactive updates.
     */
    fun getFamiliarFaces(): Flow<List<FamiliarFace>>
    
    /**
     * Add a new familiar face.
     * @param face The familiar face to add
     */
    suspend fun addFamiliarFace(face: FamiliarFace)
    
    /**
     * Delete a familiar face by its ID.
     * @param id The ID of the familiar face to delete
     */
    suspend fun deleteFamiliarFace(id: String)
    
    /**
     * Get a familiar face by its ID.
     * @param id The ID of the familiar face
     * @return The familiar face if found, null otherwise
     */
    suspend fun getFamiliarFaceById(id: String): FamiliarFace?
}

