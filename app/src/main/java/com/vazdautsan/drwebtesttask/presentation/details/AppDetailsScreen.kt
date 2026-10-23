package com.vazdautsan.drwebtesttask.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vazdautsan.drwebtesttask.R
import com.vazdautsan.drwebtesttask.domain.AppDetails
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppDetailsRoute(
    onBack: () -> Unit,
    viewModel: AppDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val launchErrorMessage = stringResource(R.string.app_details_launch_error)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AppDetailsEffect.NavigateBack -> onBack()
                AppDetailsEffect.ShowLaunchError -> snackbarHostState.showSnackbar(launchErrorMessage)
            }
        }
    }

    AppDetailsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDetailsScreen(
    state: AppDetailsState,
    snackbarHostState: SnackbarHostState,
    onIntent: (AppDetailsIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_details_title)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AppDetailsIntent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            val details = state.details
            when {
                state.isLoading -> CircularProgressIndicator()
                state.isError || details == null -> ErrorContent(onRetry = { onIntent(AppDetailsIntent.Retry) })
                else -> DetailsContent(
                    details = details,
                    checksum = state.checksum,
                    canLaunch = state.canLaunch,
                    onOpenApp = { onIntent(AppDetailsIntent.OpenAppClicked) },
                )
            }
        }
    }
}

@Composable
private fun DetailsContent(
    details: AppDetails,
    checksum: ChecksumState,
    canLaunch: Boolean,
    onOpenApp: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SelectionContainer {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoRow(stringResource(R.string.app_details_name), details.name)
                InfoRow(
                    label = stringResource(R.string.app_details_version),
                    value = stringResource(
                        R.string.app_details_version_value,
                        details.versionName ?: stringResource(R.string.unknown),
                        details.versionCode,
                    ),
                )
                InfoRow(stringResource(R.string.app_details_package), details.packageName)
                InfoRow(
                    label = stringResource(R.string.app_details_checksum),
                    value = when (checksum) {
                        ChecksumState.Calculating -> stringResource(R.string.app_details_checksum_calculating)
                        ChecksumState.Failed -> stringResource(R.string.app_details_checksum_failed)
                        is ChecksumState.Ready -> checksum.value
                    },
                )
            }
        }
        Button(
            onClick = onOpenApp,
            enabled = canLaunch,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                stringResource(
                    if (canLaunch) R.string.app_details_open else R.string.app_details_not_launchable,
                ),
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(stringResource(R.string.app_details_error))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}
