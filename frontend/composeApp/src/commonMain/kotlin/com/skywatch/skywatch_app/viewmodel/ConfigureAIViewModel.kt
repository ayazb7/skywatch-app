package com.skywatch.skywatch_app.viewmodel

import com.skywatch.skywatch_app.domain.model.FamiliarFace
import com.skywatch.skywatch_app.domain.repository.FamiliarFaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * ViewModel for the Configure AI screen.
 * Manages familiar faces list and add/delete operations.
 */
class ConfigureAIViewModel(
    private val familiarFaceRepository: FamiliarFaceRepository,
    private val viewModelScope: CoroutineScope
) : AutoCloseable {
    
    private val _uiState = MutableStateFlow(ConfigureAIUiState())
    val uiState: StateFlow<ConfigureAIUiState> = _uiState.asStateFlow()
    
    init {
        observeFamiliarFaces()
    }
    
    private fun observeFamiliarFaces() {
        viewModelScope.launch {
            familiarFaceRepository.getFamiliarFaces().collect { faces ->
                _uiState.value = _uiState.value.copy(
                    familiarFaces = faces,
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Add a new familiar face with the provided details.
     */
    @OptIn(ExperimentalUuidApi::class)
    fun addFamiliarFace(name: String, category: String, imageData: ByteArray) {
        viewModelScope.launch {
            val face = FamiliarFace(
                id = Uuid.random().toString(),
                name = name,
                category = category,
                imageData = imageData
            )
            familiarFaceRepository.addFamiliarFace(face)
        }
    }
    
    /**
     * Delete a familiar face by ID.
     */
    fun deleteFamiliarFace(id: String) {
        viewModelScope.launch {
            familiarFaceRepository.deleteFamiliarFace(id)
        }
    }
    
    override fun close() {
        viewModelScope.cancel()
    }
}

/**
 * UI state for the Configure AI screen.
 */
data class ConfigureAIUiState(
    val familiarFaces: List<FamiliarFace> = emptyList(),
    val isLoading: Boolean = true
)

