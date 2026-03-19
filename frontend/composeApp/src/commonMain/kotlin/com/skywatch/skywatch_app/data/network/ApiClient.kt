package com.skywatch.skywatch_app.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory for the shared Ktor [HttpClient].
 *
 * A single instance should be created and provided via DI so that
 * connection pools and serialisation config are reused.
 */
object ApiClient {

    fun create(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                }
            )
        }

        defaultRequest {
            url(ApiConfig.BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }
}
