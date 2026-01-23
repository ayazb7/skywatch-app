package com.skywatch.skywatch_app.di

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

var koinInstance: Koin? = null

fun initKoin(appDeclaration: KoinAppDeclaration = {}): Koin {
    val koinApplication = startKoin {
        appDeclaration()
        modules(appModule)
    }
    koinInstance = koinApplication.koin
    return koinApplication.koin
}

fun initKoin(): Koin = initKoin {}

