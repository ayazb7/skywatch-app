package com.skywatch.skywatch_app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform