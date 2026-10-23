package com.vazdautsan.drwebtesttask.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vazdautsan.drwebtesttask.domain.InstalledAppsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppListViewModel(
    private val repository: InstalledAppsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AppListState())
    val state: StateFlow<AppListState> = _state.asStateFlow()

    private val _effects = Channel<AppListEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadApps()
    }

    fun onIntent(intent: AppListIntent) {
        when (intent) {
            AppListIntent.Retry -> loadApps()
            is AppListIntent.AppClicked -> viewModelScope.launch {
                _effects.send(AppListEffect.OpenDetails(intent.packageName))
            }
        }
    }

    private fun loadApps() {
        _state.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            try {
                val apps = repository.getInstalledApps()
                _state.update { it.copy(isLoading = false, apps = apps) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }
}
