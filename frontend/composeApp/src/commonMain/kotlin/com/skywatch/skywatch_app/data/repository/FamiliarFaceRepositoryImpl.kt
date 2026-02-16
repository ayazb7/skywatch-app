package com.skywatch.skywatch_app.data.repository

import com.skywatch.skywatch_app.domain.model.FamiliarFace
import com.skywatch.skywatch_app.domain.repository.FamiliarFaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory implementation of FamiliarFaceRepository.
 * Stores familiar faces in a MutableStateFlow for reactive updates.
 * This is a temporary implementation until backend endpoints are available.
 */
class FamiliarFaceRepositoryImpl : FamiliarFaceRepository {
    
    private val _familiarFaces = MutableStateFlow<List<FamiliarFace>>(emptyList())
    
    override fun getFamiliarFaces(): Flow<List<FamiliarFace>> {
        return _familiarFaces.asStateFlow()
    }
    
    override suspend fun addFamiliarFace(face: FamiliarFace) {
        _familiarFaces.update { currentList ->
            currentList + face
        }
    }
    
    override suspend fun deleteFamiliarFace(id: String) {
        _familiarFaces.update { currentList ->
            currentList.filter { it.id != id }
        }
    }
    
    override suspend fun getFamiliarFaceById(id: String): FamiliarFace? {
        return _familiarFaces.value.find { it.id == id }
    }
}

