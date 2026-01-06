package com.skywatch.skywatch_app.di

import com.skywatch.skywatch_app.data.repository.SkyWatchRepositoryImpl
import com.skywatch.skywatch_app.domain.repository.SkyWatchRepository
import com.skywatch.skywatch_app.presentation.viewmodel.HomeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    singleOf(::SkyWatchRepositoryImpl) bind SkyWatchRepository::class
    factoryOf(::HomeViewModel)
}

