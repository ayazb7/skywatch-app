package com.skywatch.skywatch_app.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import org.koin.core.Koin
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier

/**
 * CompositionLocal for Koin instance
 */
val LocalKoin = compositionLocalOf<Koin> {
    error("Koin instance not provided. Make sure to initialize Koin and wrap your app with ProvideKoin.")
}

/**
 * Inject a dependency using Koin for Compose Multiplatform
 */
@Composable
inline fun <reified T : Any> koinInject(
    qualifier: Qualifier? = null,
    noinline parameters: (() -> ParametersHolder)? = null
): T {
    val koin = LocalKoin.current
    return remember(qualifier) {
        koin.get<T>(qualifier, parameters)
    }
}

/**
 * Provides Koin instance to the composition tree
 */
@Composable
fun ProvideKoin(
    koin: Koin = koinInstance ?: error("Koin not initialized. Call initKoin() before using ProvideKoin."),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalKoin provides koin) {
        content()
    }
}

