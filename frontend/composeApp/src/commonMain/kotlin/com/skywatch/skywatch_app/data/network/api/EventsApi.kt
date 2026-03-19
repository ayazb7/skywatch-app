package com.skywatch.skywatch_app.data.network.api

import com.skywatch.skywatch_app.data.network.dto.EventDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Thin wrapper around the `/api/events` backend endpoints.
 *
 * All methods are suspend functions that propagate exceptions
 * upward — the repository / ViewModel layer is responsible for
 * error handling.
 */
class EventsApi(private val client: HttpClient) {

    /**
     * Fetch events, optionally filtered by [date] (format: `YYYY-MM-DD`).
     */
    suspend fun getEvents(
        limit: Int = 50,
        offset: Int = 0,
        date: String? = null
    ): List<EventDto> {
        return client.get("/api/events") {
            parameter("limit", limit)
            parameter("offset", offset)
            date?.let { parameter("date", it) }
        }.body()
    }

    /**
     * Fetch a single event by its [id].
     */
    suspend fun getEvent(id: String): EventDto {
        return client.get("/api/events/$id").body()
    }

    /**
     * Delete an event by its [id].
     */
    suspend fun deleteEvent(id: String) {
        client.delete("/api/events/$id")
    }

    /**
     * Fetch the most recent events (for live HUD).
     */
    suspend fun getLatestEvents(limit: Int = 5): List<EventDto> {
        return client.get("/api/events") {
            parameter("limit", limit)
            parameter("offset", 0)
        }.body()
    }
}
