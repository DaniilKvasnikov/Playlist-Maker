package com.example.playlistmaker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.playlistmaker.R
import com.example.playlistmaker.playlist.domain.models.Playlist

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlaylistBottomSheetItem(
    playlist: Playlist,
    onClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(playlist) }
            .padding(horizontal = 13.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = playlist.imagePath,
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop,
        ) {
            it.placeholder(R.drawable.ic_placeholder_45)
                .error(R.drawable.ic_placeholder_45)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playlist.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${playlist.trackCount} треков",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
