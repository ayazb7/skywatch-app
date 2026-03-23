package com.skywatch.skywatch_app.data.repository

import com.skywatch.skywatch_app.data.network.ApiConfig
import com.skywatch.skywatch_app.data.network.api.FacesApi
import com.skywatch.skywatch_app.data.network.dto.toDomain
import com.skywatch.skywatch_app.domain.model.FamiliarFace
import com.skywatch.skywatch_app.domain.repository.FamiliarFaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay

/**
 * API-backed implementation of [FamiliarFaceRepository].
 *
 * Maintains a local [StateFlow] cache that is refreshed
 * from the backend on every write operation (add / delete).
 */
class FamiliarFaceRepositoryImpl(
    private val facesApi: FacesApi
) : FamiliarFaceRepository {

    private val _faces = MutableStateFlow<List<FamiliarFace>>(emptyList())

    override fun getFamiliarFaces(): Flow<List<FamiliarFace>> = _faces.asStateFlow()

    /** Fetch the latest list from the backend and update the cache. */
    suspend fun refresh() {
        val dtos = facesApi.getFaces()
        _faces.value = dtos.map { it.toDomain(ApiConfig.BASE_URL) }
    }

    override suspend fun addFamiliarFace(face: FamiliarFace) {
        var lastException: Exception? = null
        for (attempt in 1..3) {
            try {
                facesApi.uploadFace(
                    name = face.name,
                    category = face.category,
                    imageBytes = face.imageData
                )
                // Re-fetch to keep local cache in sync with backend
                refresh()
                return
            } catch (e: Exception) {
                lastException = e
                // Only delay if we have more attempts left
                if (attempt < 3) kotlinx.coroutines.delay(500L * attempt)
            }
        }
        throw lastException ?: Exception("Failed to upload face after 3 attempts")
    }

    override suspend fun deleteFamiliarFace(id: String) {
        facesApi.deleteFace(id)
        // Re-fetch to keep local cache in sync with backend
        refresh()
    }

    override suspend fun getFamiliarFaceById(id: String): FamiliarFace? {
        return _faces.value.find { it.id == id }
    }
}
