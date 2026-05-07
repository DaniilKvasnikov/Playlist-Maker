package com.example.playlistmaker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1B22),
    onSurface = Color(0xFF1A1B22),
    onSurfaceVariant = Color(0xFFAEAFB4),
    primary = Color(0xFF3772E7),
    onPrimary = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE6E8EB),
    secondary = Color(0xFF1A1B22),
)

private val DarkColorScheme = darkColorScheme(
    background = Color(0xFF1A1B22),
    surface = Color(0xFF1A1B22),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFFFFFFF),
    primary = Color(0xFF3772E7),
    onPrimary = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFFFFFFF),
    secondary = Color(0xFFFFFFFF),
)

@Composable
fun AppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content,
    )
}
