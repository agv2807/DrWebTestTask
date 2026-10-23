package com.vazdautsan.drwebtesttask.presentation.list

import com.vazdautsan.drwebtesttask.domain.InstalledApp

data class AppListState(
    val isLoading: Boolean = true,
    val apps: List<InstalledApp> = emptyList(),
    val isError: Boolean = false,
)

sealed interface AppListIntent {
    data object Retry : AppListIntent
    data class AppClicked(val packageName: String) : AppListIntent
}

sealed interface AppListEffect {
    data class OpenDetails(val packageName: String) : AppListEffect
}
