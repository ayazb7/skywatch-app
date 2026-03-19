package com.skywatch.skywatch_app.data.network

/**
 * Centralised API configuration.
 *
 * For Android emulator use 10.0.2.2 (maps to host loopback).
 * For iOS simulator or physical device on same network, use
 * the machine's LAN IP or localhost.
 *
 * TODO: make this configurable via build flavours / env.
 */
object ApiConfig {
    /**
     * Change this to your machine's LAN IP when testing on a
     * physical device, e.g. "http://192.168.1.42:8000".
     */
    const val BASE_URL = "http://10.0.2.2:8000"
}
