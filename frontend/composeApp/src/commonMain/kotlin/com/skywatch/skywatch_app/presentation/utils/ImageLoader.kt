package com.skywatch.skywatch_app.presentation.utils

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import com.preat.peekaboo.image.picker.toImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Composable helper that asynchronously downloads an image from [url]
 * and converts the raw bytes to an [ImageBitmap].
 *
 * Returns `null` while loading or on failure.
 */
@Composable
fun rememberImageFromUrl(url: String): ImageBitmap? {
    var bitmap by remember(url) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        bitmap = try {
            val client = HttpClient()
            val bytes = withContext(Dispatchers.Default) {
                client.get(url).readRawBytes()
            }
            client.close()
            bytes.toImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    return bitmap
}
