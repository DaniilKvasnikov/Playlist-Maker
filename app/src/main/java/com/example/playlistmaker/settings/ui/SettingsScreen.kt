package com.example.playlistmaker.settings.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.livedata.observeAsState
import com.example.playlistmaker.R
import org.koin.androidx.compose.koinViewModel

private val ThumbSize = 18.dp
private val TrackWidth = 32.dp
private val TrackHeight = 12.dp

@Composable
private fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkedThumbColor: Color,
    checkedTrackColor: Color,
) {
    val uncheckedThumbColor = MaterialTheme.colorScheme.outline
    val uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant

    val transition = updateTransition(checked, label = "switch")
    val thumbOffset by transition.animateDp(label = "thumbOffset") { isChecked ->
        if (isChecked) TrackWidth - ThumbSize else 0.dp
    }
    val currentThumbColor by transition.animateColor(label = "thumbColor") { isChecked ->
        if (isChecked) checkedThumbColor else uncheckedThumbColor
    }
    val currentTrackColor by transition.animateColor(label = "trackColor") { isChecked ->
        if (isChecked) checkedTrackColor else uncheckedTrackColor
    }

    Box(
        modifier = Modifier
            .size(width = TrackWidth, height = ThumbSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(width = TrackWidth, height = TrackHeight)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(50))
                .background(currentTrackColor)
        )
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(ThumbSize)
                .shadow(2.dp, CircleShape)
                .background(currentThumbColor, CircleShape)
        )
    }
}

@Composable
private fun SettingsContent(
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit = {},
    onShare: () -> Unit = {},
    onSupport: () -> Unit = {},
    onTerms: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val textColor = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1B22)
    val iconTint = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFFAEAFB4)
    val switchCheckedThumb = if (isDarkTheme) Color(0xFF3772E7) else Color(0xFF00D6C3)
    val switchCheckedTrack = if (isDarkTheme) Color(0xFF9FBBF3) else Color(0xFF76EAE0)

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.dark_theme),
                fontSize = 16.sp,
                color = textColor
            )
            CustomSwitch(
                checked = isDarkTheme,
                onCheckedChange = onThemeToggle,
                checkedThumbColor = switchCheckedThumb,
                checkedTrackColor = switchCheckedTrack
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onShare)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(R.string.text_share), fontSize = 16.sp, color = textColor)
            Icon(
                painter = painterResource(R.drawable.ic_share_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSupport)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(R.string.text_support), fontSize = 16.sp, color = textColor)
            Icon(
                painter = painterResource(R.drawable.ic_support_20),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onTerms)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(R.string.text_documents), fontSize = 16.sp, color = textColor)
            Icon(
                painter = painterResource(R.drawable.ic_documents_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
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
