package com.vazdautsan.drwebtesttask.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vazdautsan.drwebtesttask.domain.AppLauncher
import com.vazdautsan.drwebtesttask.domain.InstalledAppsRepository
import com.vazdautsan.drwebtesttask.presentation.navigation.AppDetailsDestination
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: InstalledAppsRepository,
    private val appLauncher: AppLauncher,
) : ViewModel() {

    private val packageName = savedStateHandle.toRoute<AppDetailsDestination>().packageName

    private val _state = MutableStateFlow(AppDetailsState())
    val state: StateFlow<AppDetailsState> = _state.asStateFlow()

    private val _effects = Channel<AppDetailsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        load()
    }

    fun onIntent(intent: AppDetailsIntent) {
        when (intent) {
            AppDetailsIntent.Retry -> load()
            AppDetailsIntent.OpenAppClicked -> openApp()
            AppDetailsIntent.BackClicked -> sendEffect(AppDetailsEffect.NavigateBack)
        }
    }

    private fun load() {
        _state.value = AppDetailsState()
        viewModelScope.launch {
            try {
                val details = repository.getAppDetails(packageName)
                _state.update {
                    it.copy(
                        isLoading = false,
                        details = details,
                        canLaunch = appLauncher.canLaunch(packageName),
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.update { it.copy(isLoading = false, isError = true) }
                return@launch
            }
            calculateChecksum()
        }
    }

    private suspend fun calculateChecksum() {
        val checksum = try {
            ChecksumState.Ready(repository.getApkChecksum(packageName))
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            ChecksumState.Failed
        }
        _state.update { it.copy(checksum = checksum) }
    }

    private fun openApp() {
        if (!appLauncher.launch(packageName)) {
            sendEffect(AppDetailsEffect.ShowLaunchError)
        }
    }

    private fun sendEffect(effect: AppDetailsEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
