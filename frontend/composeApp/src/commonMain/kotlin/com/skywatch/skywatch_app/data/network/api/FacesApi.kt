package com.skywatch.skywatch_app.data.network.api

import com.skywatch.skywatch_app.data.network.dto.FaceDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

/**
 * Thin wrapper around the `/api/faces` backend endpoints.
 */
class FacesApi(private val client: HttpClient) {

    /**
     * Fetch all stored familiar faces.
     */
    suspend fun getFaces(
        limit: Int = 50,
        offset: Int = 0
    ): List<FaceDto> {
        return client.get("/api/faces") {
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }

    /**
     * Upload a new familiar face via multipart form data.
     *
     * Matches the backend endpoint that expects:
     * - `name` (form field)
     * - `category` (form field, optional)
     * - `image` (file upload)
     */
    suspend fun uploadFace(
        name: String,
        category: String,
        imageBytes: ByteArray
    ): FaceDto {
        return client.submitFormWithBinaryData(
            url = "/api/faces",
            formData = formData {
                append("name", name)
                append("category", category)
                append(
                    "image",
                    imageBytes,
                    Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"face.jpg\"")
                    }
                )
            }
        ).body()
    }

    /**
     * Delete a familiar face by its [id].
     */
    suspend fun deleteFace(id: String) {
        client.delete("/api/faces/$id")
    }
}
