package com.example.playlistmaker.search.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.models.TrackUI
import com.example.playlistmaker.ui.components.TrackItem
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.livedata.observeAsState

private const val SEARCH_DEBOUNCE_DELAY_MS = 500L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onTrackClick: (TrackUI) -> Unit
) {
    val query by viewModel.query.observeAsState("")
    val state by viewModel.state.observeAsState(SearchState.None)

    LaunchedEffect(query) {
        delay(SEARCH_DEBOUNCE_DELAY_MS)
        if (query.isNotBlank()) viewModel.searchTracks(query) else viewModel.loadHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.text_search)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchField(
                query = query,
                onQueryChange = { viewModel.setQuery(it) },
                onClear = {
                    viewModel.setQuery("")
                    viewModel.loadHistory()
                }
            )

            Box(modifier = Modifier.weight(1f)) {
                when (val s = state) {
                    is SearchState.Loading -> LoadingContent()
                    is SearchState.Content -> TracksContent(
                        tracks = s.tracks,
                        onTrackClick = { track ->
                            viewModel.saveToHistory(track)
                            onTrackClick(track)
                        }
                    )
                    is SearchState.Empty -> EmptyContent()
                    is SearchState.Error -> ErrorContent(onRetry = { viewModel.searchTracks(query) })
                    is SearchState.History -> HistoryContent(
                        tracks = s.tracks,
                        onTrackClick = onTrackClick,
                        onClearHistory = { viewModel.clearHistory() }
                    )
                    is SearchState.None -> Spacer(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(42.dp)
            .background(
                color = Color(0xFFE6E8EB),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search_icon_16),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.size(8.dp))

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF1A1B22)),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.text_search_edittext),
                        fontSize = 16.sp,
                        color = Color(0xFFAEAFB4)
                    )
                }
                inner()
            }
        )

        if (query.isNotEmpty()) {
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_clear_24),
                    contentDescription = null,
                    tint = Color(0xFFAEAFB4)
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 140.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TracksContent(
    tracks: List<TrackUI>,
    onTrackClick: (TrackUI) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tracks) { track ->
            TrackItem(track = track, onClick = onTrackClick)
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 106.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_empty_search_note_120),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.search_not_found),
                fontSize = 19.sp
            )
        }
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 102.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_connection_error_120),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.problems_with_connection),
                fontSize = 19.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(text = stringResource(R.string.refresh_search))
            }
        }
    }
}

@Composable
private fun HistoryContent(
    tracks: List<TrackUI>,
    onTrackClick: (TrackUI) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.search_history_title),
            fontSize = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tracks) { track ->
                TrackItem(track = track, onClick = onTrackClick)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onClearHistory,
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(text = stringResource(R.string.clear_history))
            }
        }
    }
}
