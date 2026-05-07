package com.example.playlistmaker.playlist.ui.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.ui.components.PlaylistGridItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistViewModel = koinViewModel(),
    onPlaylistClick: (Playlist) -> Unit,
    onCreateClick: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadPlaylists() }

    val playlists by viewModel.playlists.observeAsState(emptyList())

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(top = 24.dp, bottom = 24.dp),
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Новый плейлист", fontSize = 14.sp)
            }
            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .align(TopCenter)
                            .padding(top = 106.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_empty_search_note_120),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp)
                        )
                        Text(
                            text = "Ваши плейлисты",
                            fontSize = 19.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = Fixed(2),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = playlists,
                        key = { playlist -> playlist.id },
                        contentType = { "playlist" }
                    ) { playlist ->
                        PlaylistGridItem(playlist = playlist, onClick = onPlaylistClick)
                    }
                }
            }
        }
    }
}
