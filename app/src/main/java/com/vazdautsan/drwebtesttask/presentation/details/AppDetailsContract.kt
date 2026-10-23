package com.vazdautsan.drwebtesttask.presentation.details

import com.vazdautsan.drwebtesttask.domain.AppDetails

data class AppDetailsState(
    val isLoading: Boolean = true,
    val details: AppDetails? = null,
    val canLaunch: Boolean = false,
    val checksum: ChecksumState = ChecksumState.Calculating,
    val isError: Boolean = false,
)

sealed interface ChecksumState {
    data object Calculating : ChecksumState
    data class Ready(val value: String) : ChecksumState
    data object Failed : ChecksumState
}

sealed interface AppDetailsIntent {
    data object Retry : AppDetailsIntent
    data object OpenAppClicked : AppDetailsIntent
    data object BackClicked : AppDetailsIntent
}

sealed interface AppDetailsEffect {
    data object NavigateBack : AppDetailsEffect
    data object ShowLaunchError : AppDetailsEffect
}
