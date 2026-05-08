package com.example.playlistmaker.playlist.ui.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.favorites.ui.FavoritesState
import com.example.playlistmaker.favorites.ui.FavoritesViewModel
import com.example.playlistmaker.search.ui.models.TrackUI
import com.example.playlistmaker.ui.components.TrackItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    onTrackClick: (TrackUI) -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadFavorites() }

    val state by viewModel.state.observeAsState()

    Box(Modifier.fillMaxSize()) {
        when (val s = state) {
            is FavoritesState.Loading -> CircularProgressIndicator(
                modifier = Modifier
                    .align(TopCenter)
                    .padding(top = 140.dp),
                color = MaterialTheme.colorScheme.primary
            )
            is FavoritesState.Content -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp)
            ) {
                items(s.tracks) { track ->
                    TrackItem(track = track, onClick = onTrackClick)
                }
            }
            is FavoritesState.Empty -> Column(
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
                    text = "Медиатека пуста",
                    fontSize = 19.sp,
                    textAlign = TextAlign.Center
                )
            }
            else -> Unit
        }
    }
}
