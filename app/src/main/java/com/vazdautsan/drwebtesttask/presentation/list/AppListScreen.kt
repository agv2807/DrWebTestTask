package com.vazdautsan.drwebtesttask.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vazdautsan.drwebtesttask.R
import com.vazdautsan.drwebtesttask.domain.InstalledApp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppListRoute(
    onOpenDetails: (packageName: String) -> Unit,
    viewModel: AppListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AppListEffect.OpenDetails -> onOpenDetails(effect.packageName)
            }
        }
    }

    AppListScreen(state = state, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppListScreen(
    state: AppListState,
    onIntent: (AppListIntent) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_list_title)) }) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.isError -> ErrorContent(onRetry = { onIntent(AppListIntent.Retry) })
                else -> AppList(
                    apps = state.apps,
                    onAppClick = { onIntent(AppListIntent.AppClicked(it)) },
                )
            }
        }
    }
}

@Composable
private fun AppList(
    apps: List<InstalledApp>,
    onAppClick: (packageName: String) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(apps, key = { it.packageName }) { app ->
            ListItem(
                headlineContent = { Text(app.name) },
                supportingContent = { Text(app.packageName) },
                modifier = Modifier.clickable { onAppClick(app.packageName) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(stringResource(R.string.app_list_error))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}
