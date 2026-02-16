package com.skywatch.skywatch_app

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.skywatch.skywatch_app.di.ProvideKoin
import com.skywatch.skywatch_app.presentation.screens.AddFamiliarFaceScreen
import com.skywatch.skywatch_app.presentation.screens.ConfigureAIScreen
import com.skywatch.skywatch_app.presentation.screens.FamiliarFacesScreen
import com.skywatch.skywatch_app.presentation.screens.HomeScreen
import com.skywatch.skywatch_app.presentation.screens.SettingsScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable
private data object Home : NavKey

@Serializable
private data object Settings : NavKey

@Serializable
private data object ConfigureAI : NavKey

@Serializable
private data object FamiliarFaces : NavKey

@Serializable
private data object AddFamiliarFace : NavKey

private val navSavedStateConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Home::class, Home.serializer())
            subclass(Settings::class, Settings.serializer())
            subclass(ConfigureAI::class, ConfigureAI.serializer())
            subclass(FamiliarFaces::class, FamiliarFaces.serializer())
            subclass(AddFamiliarFace::class, AddFamiliarFace.serializer())
        }
    }
}

@Composable
@Preview
fun App() {
    val backStack = rememberNavBackStack(navSavedStateConfig, Home)

    MaterialTheme {
        ProvideKoin {
            NavDisplay(
                backStack = backStack,
                entryProvider = entryProvider {
                    entry<Home> {
                        HomeScreen(
                            onNavigateToSettings = {
                                backStack.add(Settings)
                            }
                        )
                    }
                    entry<Settings> {
                        SettingsScreen(
                            onNavigateBack = {
                                backStack.remove(Settings)
                            },
                            onNavigateToConfigureAI = {
                                backStack.add(ConfigureAI)
                            }
                        )
                    }
                    entry<ConfigureAI> {
                        ConfigureAIScreen(
                            onNavigateBack = {
                                backStack.remove(ConfigureAI)
                            },
                            onNavigateToFamiliarFaces = {
                                backStack.add(FamiliarFaces)
                            }
                        )
                    }
                    entry<FamiliarFaces> {
                        FamiliarFacesScreen(
                            onNavigateBack = {
                                backStack.remove(FamiliarFaces)
                            },
                            onNavigateToAddFamiliarFace = {
                                backStack.add(AddFamiliarFace)
                            }
                        )
                    }
                    entry<AddFamiliarFace> {
                        AddFamiliarFaceScreen(
                            onNavigateBack = {
                                backStack.remove(AddFamiliarFace)
                            },
                            onSaveSuccess = {
                                backStack.remove(AddFamiliarFace)
                            }
                        )
                    }
                }
            )
        }
    }
}