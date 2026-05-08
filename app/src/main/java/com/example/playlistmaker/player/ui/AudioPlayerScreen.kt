package com.example.playlistmaker.player.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.playlistmaker.R
import com.example.playlistmaker.player.service.AudioPlayerService
import com.example.playlistmaker.search.ui.models.TrackUI
import com.example.playlistmaker.ui.components.PlaylistBottomSheetItem
import com.google.gson.Gson
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun AudioPlayerScreen(
    trackJson: String,
    navController: NavController,
    onBack: () -> Unit,
    onNavigateToCreatePlaylist: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = koinViewModel<AudioPlayerViewModel>()

    val track = remember(trackJson) { Gson().fromJson(trackJson, TrackUI::class.java) }

    LaunchedEffect(track) {
        viewModel.setCurrentTrack(track)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            putExtra(AudioPlayerService.EXTRA_TRACK, track)
        }
        viewModel.bindService(context, intent)
        onDispose { viewModel.unbindService(context) }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.onUiVisible()
                Lifecycle.Event.ON_STOP -> viewModel.onUiHidden()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    val playerState by viewModel.state.observeAsState()
    val isFavorite by viewModel.isFavorite.observeAsState(false)
    val playlists by viewModel.playlists.observeAsState(emptyList())

    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val addResult by viewModel.addToPlaylistResult.observeAsState()
    var lastHandledResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    LaunchedEffect(addResult) {
        val result = addResult ?: return@LaunchedEffect
        if (result == lastHandledResult) return@LaunchedEffect
        lastHandledResult = result
        val (success, name) = result
        if (success) {
            showBottomSheet = false
            Toast.makeText(context, context.getString(R.string.added_to_playlist, name), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, context.getString(R.string.track_already_in_playlist, name), Toast.LENGTH_SHORT).show()
        }
    }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val createdPlaylistId by (savedStateHandle?.getLiveData<Int>("created_playlist_id")
        ?: MutableLiveData()).observeAsState()
    LaunchedEffect(createdPlaylistId) {
        val id = createdPlaylistId ?: return@LaunchedEffect
        viewModel.addTrackToPlaylistById(id)
        savedStateHandle?.remove<Int>("created_playlist_id")
    }

    val playTimeText = when (val s = playerState) {
        is AudioPlayerState.Playing -> formatPosition(s.position)
        is AudioPlayerState.Paused -> formatPosition(s.position)
        else -> "00:00"
    }

    val isPlaying = playerState is AudioPlayerState.Playing

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back_16),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlideImage(
                model = track.getArtworkUrl512(),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 26.dp)
                    .size(312.dp),
                contentScale = ContentScale.Crop,
            ) {
                it.placeholder(R.drawable.ic_placeholder_45)
                    .error(R.drawable.ic_placeholder_45)
                    .transform(com.bumptech.glide.load.resource.bitmap.RoundedCorners(
                        (8 * context.resources.displayMetrics.density).toInt()
                    ))
            }

            Text(
                text = track.trackName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .width(312.dp)
                    .padding(top = 24.dp)
            )

            Text(
                text = track.artistName,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .width(312.dp)
                    .padding(top = 12.dp)
            )

            Row(
                modifier = Modifier
                    .width(312.dp)
                    .padding(top = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        viewModel.loadPlaylists()
                        showBottomSheet = true
                    },
                    modifier = Modifier
                        .size(51.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(69.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_button_23),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(23.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.playPause() },
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (isPlaying) R.drawable.ic_pause_button_100
                            else R.drawable.ic_play_button_100
                        ),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(100.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.onFavoriteClicked() },
                    modifier = Modifier
                        .size(51.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(69.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(
                            if (isFavorite) R.drawable.ic_like_button_filled_25
                            else R.drawable.ic_like_button_25
                        ),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Text(
                text = playTimeText,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 30.dp)
            ) {
                InfoRow(label = stringResource(R.string.duration), value = track.getFormattedTime())

                if (!track.collectionName.isNullOrEmpty()) {
                    InfoRow(label = stringResource(R.string.album), value = track.collectionName)
                }

                if (!track.releaseDate.isNullOrEmpty()) {
                    val year = track.releaseDate.take(4)
                    InfoRow(label = stringResource(R.string.year), value = year)
                }

                InfoRow(label = stringResource(R.string.genre), value = track.primaryGenreName)
                InfoRow(label = stringResource(R.string.country), value = track.country)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            Text(
                text = stringResource(R.string.add_to_playlist_title),
                fontSize = 19.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    showBottomSheet = false
                    onNavigateToCreatePlaylist()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = stringResource(R.string.new_playlist),
                    fontSize = 14.sp
                )
            }

            LazyColumn {
                items(playlists) { playlist ->
                    PlaylistBottomSheetItem(
                        playlist = playlist,
                        onClick = { viewModel.addTrackToPlaylist(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun formatPosition(positionMs: Int): String {
    val totalSeconds = positionMs / 1000
    return String.format(Locale.US, "%02d:%02d", totalSeconds / 60, totalSeconds % 60)
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(0.3f)
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End
            )
        }
    }
}
