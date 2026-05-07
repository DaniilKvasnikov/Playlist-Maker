package com.example.playlistmaker.playlist.ui.edit

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.stringResource
import com.example.playlistmaker.R
import com.example.playlistmaker.playlist.ui.create.CreatePlaylistState
import com.example.playlistmaker.playlist.ui.create.PlaylistFormContent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlaylistScreen(
    playlistId: Int,
    viewModel: EditPlaylistViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadPlaylist(playlistId) }

    val state by viewModel.state.observeAsState(CreatePlaylistState())
    val playlistUpdated by viewModel.playlistUpdated.observeAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { viewModel.setCoverUri(it) } }

    LaunchedEffect(playlistUpdated) {
        if (playlistUpdated == true) onBack()
    }

    fun handleBack() {
        if (viewModel.hasUnsavedData()) showExitDialog = true else onBack()
    }

    BackHandler { handleBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_playlist)) },
                navigationIcon = {
                    IconButton(onClick = { handleBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        PlaylistFormContent(
            state = state,
            buttonLabel = stringResource(R.string.save),
            onCoverClick = {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onNameChange = viewModel::setName,
            onDescriptionChange = viewModel::setDescription,
            onSubmit = viewModel::savePlaylist,
            modifier = Modifier.padding(padding)
        )

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text(stringResource(R.string.finish_creating_playlist_title)) },
                text = { Text(stringResource(R.string.finish_creating_playlist_message)) },
                confirmButton = {
                    TextButton(onClick = { showExitDialog = false; onBack() }) {
                        Text(stringResource(R.string.finish))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}
