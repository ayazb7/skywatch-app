package com.skywatch.skywatch_app.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.toImageBitmap
import com.skywatch.skywatch_app.di.koinInject
import com.skywatch.skywatch_app.domain.model.FamiliarFace
import com.skywatch.skywatch_app.presentation.utils.rememberImageFromUrl
import com.skywatch.skywatch_app.presentation.views.*
import com.skywatch.skywatch_app.viewmodel.ConfigureAIViewModel

@Composable
fun FamiliarFacesScreen(
    viewModel: ConfigureAIViewModel = koinInject(),
    onNavigateBack: () -> Unit = {},
    onNavigateToAddFamiliarFace: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.close()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBackground)
            .safeContentPadding()
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextBlack
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Familiar Faces",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextBlack
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Add people you know so the AI can recognize them.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                    modifier = Modifier.padding(start = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Add Familiar Face button
                Button(
                    onClick = onNavigateToAddFamiliarFace,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GradientBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Familiar Face")
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Error banner
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = RecordRed,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // Familiar faces list
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GradientBlue)
                    }
                } else if (uiState.familiarFaces.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = TextGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No familiar faces added yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextGray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.familiarFaces, key = { it.id }) { face ->
                            FamiliarFaceItem(
                                face = face,
                                onDelete = { viewModel.deleteFamiliarFace(face.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FamiliarFaceItem(
    face: FamiliarFace,
    onDelete: () -> Unit
) {
    // Try URL-based image first, then fall back to raw bytes
    val urlBitmap = face.imageUrl?.let { rememberImageFromUrl(it) }
    val localBitmap: ImageBitmap? = remember(face.imageData) {
        if (face.imageData.isNotEmpty()) {
            try {
                face.imageData.toImageBitmap()
            } catch (_: Exception) {
                null
            }
        } else null
    }
    val imageBitmap = urlBitmap ?: localBitmap

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGrayBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Face image
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BorderGray),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = face.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name and category
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = face.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextBlack
                )
                Text(
                    text = face.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = RecordRed
                )
            }
        }
    }
}
