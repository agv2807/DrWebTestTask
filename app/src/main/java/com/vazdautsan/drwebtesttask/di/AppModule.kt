package com.vazdautsan.drwebtesttask.di

import com.vazdautsan.drwebtesttask.data.AndroidAppLauncher
import com.vazdautsan.drwebtesttask.data.InstalledAppsRepositoryImpl
import com.vazdautsan.drwebtesttask.domain.AppLauncher
import com.vazdautsan.drwebtesttask.domain.InstalledAppsRepository
import com.vazdautsan.drwebtesttask.presentation.details.AppDetailsViewModel
import com.vazdautsan.drwebtesttask.presentation.list.AppListViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::InstalledAppsRepositoryImpl) { bind<InstalledAppsRepository>() }
    singleOf(::AndroidAppLauncher) { bind<AppLauncher>() }
    viewModelOf(::AppListViewModel)
    viewModelOf(::AppDetailsViewModel)
}
