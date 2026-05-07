package com.example.playlistmaker.playlist.ui.details

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.mappers.toUI
import com.example.playlistmaker.search.ui.models.TrackUI
import com.example.playlistmaker.ui.components.PlaylistBottomSheetItem
import com.example.playlistmaker.ui.components.TrackItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PlaylistDetailsScreen(
    playlistId: Int,
    viewModel: PlaylistDetailsViewModel = koinViewModel(),
    onTrackClick: (TrackUI) -> Unit,
    onBack: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.observeAsState()
    val playlistDeleted by viewModel.playlistDeleted.observeAsState()

    var showMenuSheet by remember { mutableStateOf(false) }
    var trackToDelete by remember { mutableStateOf<Track?>(null) }
    var showDeletePlaylistDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadPlaylist(playlistId) }

    LaunchedEffect(playlistDeleted) {
        if (playlistDeleted == true) onBack()
    }

    fun sharePlaylist() {
        val text = viewModel.getShareText() ?: return
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(sendIntent, null))
    }

    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(),
        sheetPeekHeight = 266.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetContent = {
            when (val s = state) {
                is PlaylistDetailsState.Content -> {
                    if (s.tracks.isEmpty()) {
                        Text(
                            text = "В плейлисте нет треков",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 19.sp
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(s.tracks, key = { it.trackId }) { track ->
                                TrackItem(
                                    track = track.toUI(),
                                    onClick = { onTrackClick(it) },
                                    onLongClick = { trackToDelete = track }
                                )
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE6E8EB))
        ) {
            when (val s = state) {
                is PlaylistDetailsState.Loading -> CircularProgressIndicator(Modifier.align(Center))
                is PlaylistDetailsState.Content -> {
                    Column(Modifier.fillMaxWidth()) {
                        GlideImage(
                            model = s.playlist.imagePath,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        ) {
                            it.placeholder(R.drawable.ic_placeholder_45)
                                .error(R.drawable.ic_placeholder_45)
                        }
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE6E8EB))
                                .padding(horizontal = 16.dp)
                                .padding(top = 24.dp)
                        ) {
                            Text(
                                text = s.playlist.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1B22)
                            )
                            if (s.playlist.description.isNotEmpty()) {
                                Text(
                                    text = s.playlist.description,
                                    fontSize = 18.sp,
                                    color = Color(0xFF1A1B22),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${s.totalDurationMinutes} минут", fontSize = 18.sp, color = Color(0xFF1A1B22))
                                Icon(
                                    painter = painterResource(R.drawable.ic_item_track_point_13),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(13.dp),
                                    tint = Color(0xFF1A1B22)
                                )
                                Text("${s.playlist.trackCount} треков", fontSize = 18.sp, color = Color(0xFF1A1B22))
                            }
                            Row(Modifier.padding(top = 16.dp)) {
                                IconButton(onClick = {
                                    if (viewModel.hasTracksToShare()) sharePlaylist()
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_share_24),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = Color(0xFF1A1B22)
                                    )
                                }
                                IconButton(
                                    onClick = { showMenuSheet = true },
                                    modifier = Modifier.padding(start = 16.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_more_vert_24),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = Color(0xFF1A1B22)
                                    )
                                }
                            }
                        }
                    }

                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
                else -> Unit
            }
        }
    }

    if (showMenuSheet) {
        val content = state as? PlaylistDetailsState.Content
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            content?.playlist?.let { playlist ->
                PlaylistBottomSheetItem(playlist = playlist, onClick = {})
            }
            Text(
                text = "Поделиться",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showMenuSheet = false
                        sharePlaylist()
                    }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                fontSize = 16.sp
            )
            Text(
                text = "Редактировать",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showMenuSheet = false
                        onEditClick(playlistId)
                    }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                fontSize = 16.sp
            )
            Text(
                text = "Удалить плейлист",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showMenuSheet = false
                        showDeletePlaylistDialog = true
                    }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                fontSize = 16.sp
            )
        }
    }

    trackToDelete?.let { track ->
        AlertDialog(
            onDismissRequest = { trackToDelete = null },
            title = { Text("Хотите удалить трек?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeTrack(track.trackId)
                    trackToDelete = null
                }) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { trackToDelete = null }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showDeletePlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePlaylistDialog = false },
            title = { Text("Хотите удалить плейлист?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePlaylist()
                    showDeletePlaylistDialog = false
                }) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePlaylistDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
