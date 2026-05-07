package com.example.playlistmaker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.models.TrackUI

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun TrackItem(
    track: TrackUI,
    onClick: (TrackUI) -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: ((TrackUI) -> Unit)? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(61.dp)
            .combinedClickable(
                onClick = { onClick(track) },
                onLongClick = onLongClick?.let { { it(track) } }
            )
            .padding(start = 13.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = track.artworkUrl100,
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
                text = track.trackName,
                fontSize = 16.sp,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artistName,
                    fontSize = 11.sp,
                    color = subtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_item_track_point_13),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(13.dp),
                    tint = subtitleColor
                )
                Text(
                    text = track.getFormattedTime(),
                    fontSize = 11.sp,
                    color = subtitleColor,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward_24),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = subtitleColor
        )
    }
}
