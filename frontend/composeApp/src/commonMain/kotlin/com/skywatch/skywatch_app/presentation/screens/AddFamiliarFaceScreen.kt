package com.skywatch.skywatch_app.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import com.skywatch.skywatch_app.di.koinInject
import com.skywatch.skywatch_app.domain.model.FaceCategory
import com.skywatch.skywatch_app.presentation.utils.openAppSettings
import com.skywatch.skywatch_app.presentation.views.*
import com.skywatch.skywatch_app.viewmodel.ConfigureAIViewModel
import kotlinx.coroutines.launch

@Composable
fun AddFamiliarFaceScreen(
    viewModel: ConfigureAIViewModel = koinInject(),
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    var selectedImageData by remember { mutableStateOf<ByteArray?>(null) }
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FaceCategory.FAMILY) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Image picker launcher
    val imagePickerLauncher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                selectedImageData = it
            }
        }
    )
    
    // Camera state
    val cameraState = rememberPeekabooCameraState(
        onCapture = { byteArray ->
            byteArray?.let {
                selectedImageData = it
                showCamera = false
            }
        }
    )
    
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.close()
        }
    }

    // Show error as snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    if (showCamera) {
        // Full screen camera view
        Box(modifier = Modifier.fillMaxSize()) {
            PeekabooCamera(
                state = cameraState,
                modifier = Modifier.fillMaxSize(),
                permissionDeniedContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CardWhite),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Camera Permission Required",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please grant camera access in settings to take photos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { openAppSettings() },
                            colors = ButtonDefaults.buttonColors(containerColor = GradientBlue)
                        ) {
                            Text("Open Settings")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = { showCamera = false }) {
                            Text("Go Back", color = TextGray)
                        }
                    }
                }
            )
            
            // Camera controls overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button
                IconButton(
                    onClick = { showCamera = false },
                    modifier = Modifier
                        .size(48.dp)
                        .background(CardWhite.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextBlack
                    )
                }
                
                // Capture button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { cameraState.capture() },
                        modifier = Modifier
                            .size(72.dp)
                            .background(CardWhite, CircleShape)
                            .border(4.dp, GradientBlue, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture",
                            tint = GradientBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    } else {
        // Main form view
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { _ ->
            AddFamiliarFaceFormContent(
                selectedImageData = selectedImageData,
                name = name,
                onNameChange = { name = it },
                selectedCategory = selectedCategory,
                showCategoryDropdown = showCategoryDropdown,
                onShowCategoryDropdownChange = { showCategoryDropdown = it },
                onCategorySelected = { selectedCategory = it },
                onNavigateBack = onNavigateBack,
                onCameraClick = { showCamera = true },
                onGalleryClick = { imagePickerLauncher.launch() },
                isSaving = uiState.isSaving,
                onSave = {
                    selectedImageData?.let { imageData ->
                        viewModel.addFamiliarFace(name, selectedCategory, imageData)
                        onSaveSuccess()
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFamiliarFaceFormContent(
    selectedImageData: ByteArray?,
    name: String,
    onNameChange: (String) -> Unit,
    selectedCategory: String,
    showCategoryDropdown: Boolean,
    onShowCategoryDropdownChange: (Boolean) -> Unit,
    onCategorySelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    isSaving: Boolean,
    onSave: () -> Unit
) {
    val imageBitmap: ImageBitmap? = remember(selectedImageData) {
        selectedImageData?.let {
            try {
                it.toImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    val canSave = selectedImageData != null && name.isNotBlank() && !isSaving

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
                    .verticalScroll(rememberScrollState())
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
                        text = "Add Familiar Face",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextBlack
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Image selection area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(LightGrayBackground)
                        .border(2.dp, BorderGray, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "Selected face",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = TextGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Select a photo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Camera and Gallery buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCameraClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Camera")
                    }

                    OutlinedButton(
                        onClick = onGalleryClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gallery")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Name") },
                    placeholder = { Text("Enter person's name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = onShowCategoryDropdownChange
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { onShowCategoryDropdownChange(false) }
                    ) {
                        FaceCategory.all.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    onCategorySelected(category)
                                    onShowCategoryDropdownChange(false)
                                },
                                leadingIcon = if (category == selectedCategory) {
                                    { Icon(Icons.Default.Check, contentDescription = null) }
                                } else null
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save button with loading indicator
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GradientBlue
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = CardWhite,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uploading…")
                    } else {
                        Text("Save")
                    }
                }
            }
        }
    }
}
