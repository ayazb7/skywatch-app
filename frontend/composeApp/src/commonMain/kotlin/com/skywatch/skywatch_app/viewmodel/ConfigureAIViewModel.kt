package com.skywatch.skywatch_app.viewmodel

import com.skywatch.skywatch_app.domain.model.FamiliarFace
import com.skywatch.skywatch_app.domain.repository.FamiliarFaceRepository
import com.skywatch.skywatch_app.data.repository.FamiliarFaceRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * ViewModel for the Configure AI / Familiar Faces screens.
 *
 * Manages the faces list and add/delete operations via the repository,
 * exposing a single immutable [ConfigureAIUiState] for the UI to observe.
 */
class ConfigureAIViewModel(
    private val familiarFaceRepository: FamiliarFaceRepository,
    private val viewModelScope: CoroutineScope
) : AutoCloseable {
    
    private val _uiState = MutableStateFlow(ConfigureAIUiState())
    val uiState: StateFlow<ConfigureAIUiState> = _uiState.asStateFlow()
    
    init {
        observeFamiliarFaces()
        loadFaces()
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

    /** Trigger initial / manual refresh from the backend. */
    fun loadFaces() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // FamiliarFaceRepositoryImpl exposes refresh()
                (familiarFaceRepository as? FamiliarFaceRepositoryImpl)?.refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load faces"
                )
            }
        }
    }
    
    /**
     * Add a new familiar face — uploads the image bytes to the backend.
     */
    @OptIn(ExperimentalUuidApi::class)
    fun addFamiliarFace(name: String, category: String, imageData: ByteArray) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                val face = FamiliarFace(
                    id = Uuid.random().toString(),
                    name = name,
                    category = category,
                    imageData = imageData
                )
                familiarFaceRepository.addFamiliarFace(face)
                _uiState.value = _uiState.value.copy(isSaving = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to upload face"
                )
            }
        }
    }
    
    /**
     * Delete a familiar face by ID.
     */
    fun deleteFamiliarFace(id: String) {
        viewModelScope.launch {
            try {
                familiarFaceRepository.deleteFamiliarFace(id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete face"
                )
            }
        }
    }
    
    override fun close() {
        viewModelScope.cancel()
    }
}

/**
 * Immutable UI state for the Configure AI / Familiar Faces screens.
 */
data class ConfigureAIUiState(
    val familiarFaces: List<FamiliarFace> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null
)
