package com.example.playlistmaker.playlist.ui.create

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.playlistmaker.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlaylistFormContent(
    state: CreatePlaylistState,
    buttonLabel: String,
    onCoverClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .padding(top = 26.dp)
                .size(312.dp)
                .align(CenterHorizontally)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onCoverClick() },
            contentAlignment = Alignment.Center
        ) {
            if (state.coverUri != null) {
                GlideImage(
                    model = state.coverUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_placeholder_45),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.Unspecified
                )
            }
        }

        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Название") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = { Text("Описание") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            singleLine = true
        )

        Spacer(Modifier.weight(1f))

        val enabled = state.name.isNotBlank()
        Button(
            onClick = onSubmit,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 17.dp)
                .padding(bottom = 32.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3772E7),
                disabledContainerColor = Color(0xFFAEAFB4)
            )
        ) {
            Text(buttonLabel, fontSize = 16.sp, color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistScreen(
    viewModel: CreatePlaylistViewModel = koinViewModel(),
    onBack: () -> Unit,
    onCreated: (Int) -> Unit
) {
    val state by viewModel.state.observeAsState(CreatePlaylistState())
    val playlistCreated by viewModel.playlistCreated.observeAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { viewModel.setCoverUri(it) } }

    fun handleBack() {
        if (viewModel.hasUnsavedData()) showExitDialog = true else onBack()
    }

    BackHandler { handleBack() }

    LaunchedEffect(playlistCreated) {
        playlistCreated?.let { (name, id) ->
            Toast.makeText(context, "Плейлист $name создан", Toast.LENGTH_SHORT).show()
            onCreated(id.toInt())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новый плейлист") },
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
            buttonLabel = "Создать",
            onCoverClick = {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onNameChange = viewModel::setName,
            onDescriptionChange = viewModel::setDescription,
            onSubmit = viewModel::createPlaylist,
            modifier = Modifier.padding(padding)
        )

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Завершить создание плейлиста?") },
                text = { Text("Все несохранённые данные будут потеряны") },
                confirmButton = {
                    TextButton(onClick = { showExitDialog = false; onBack() }) {
                        Text("Завершить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}
