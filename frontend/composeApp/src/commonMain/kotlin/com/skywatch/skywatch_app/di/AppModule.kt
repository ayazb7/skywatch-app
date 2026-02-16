package com.skywatch.skywatch_app.di

import com.skywatch.skywatch_app.data.repository.FamiliarFaceRepositoryImpl
import com.skywatch.skywatch_app.data.repository.MediaRepositoryImpl
import com.skywatch.skywatch_app.data.repository.TimelineRepositoryImpl
import com.skywatch.skywatch_app.data.repository.VideoFeedRepositoryImpl
import com.skywatch.skywatch_app.domain.repository.FamiliarFaceRepository
import com.skywatch.skywatch_app.domain.repository.MediaRepository
import com.skywatch.skywatch_app.domain.repository.TimelineRepository
import com.skywatch.skywatch_app.domain.repository.VideoFeedRepository
import com.skywatch.skywatch_app.viewmodel.ConfigureAIViewModel
import com.skywatch.skywatch_app.viewmodel.HomeViewModel
import com.skywatch.skywatch_app.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<CoroutineDispatcher>(qualifier = named("MainDispatcher")) {
        Dispatchers.Main
    }

    factory<CoroutineScope> {
        val dispatcher: CoroutineDispatcher = get(named("MainDispatcher"))
        CoroutineScope(SupervisorJob() + dispatcher)
    }

    // Repositories
    singleOf(::TimelineRepositoryImpl) bind TimelineRepository::class
    singleOf(::MediaRepositoryImpl) bind MediaRepository::class
    singleOf(::VideoFeedRepositoryImpl) bind VideoFeedRepository::class
    singleOf(::FamiliarFaceRepositoryImpl) bind FamiliarFaceRepository::class

    // ViewModels
    factoryOf(::HomeViewModel)
    factoryOf(::SettingsViewModel)
    factoryOf(::ConfigureAIViewModel)
}

