package com.example.playlistmaker.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import com.example.playlistmaker.R
import org.koin.androidx.compose.koinViewModel

@Composable
private fun SettingsContent(
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit = {},
    onShare: () -> Unit = {},
    onSupport: () -> Unit = {},
    onTerms: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(R.string.dark_theme))
            Switch(checked = isDarkTheme, onCheckedChange = onThemeToggle)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onShare)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(R.string.text_share))
            Icon(painter = painterResource(R.drawable.ic_share_24), contentDescription = null)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSupport)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(R.string.text_support))
            Icon(painter = painterResource(R.drawable.ic_support_20), contentDescription = null)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onTerms)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(R.string.text_documents))
            Icon(painter = painterResource(R.drawable.ic_documents_24), contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val isDarkTheme by viewModel.isDarkTheme.observeAsState(false)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.text_settings)) })
        }
    ) { paddingValues ->
        SettingsContent(
            isDarkTheme = isDarkTheme ?: false,
            onThemeToggle = { viewModel.toggleTheme(it) },
            onShare = { viewModel.shareApp() },
            onSupport = { viewModel.openSupport() },
            onTerms = { viewModel.openTerms() },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
