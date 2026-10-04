package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentDefault,
    onPrimary = BgDark,
    primaryContainer = SurfaceCard,
    onPrimaryContainer = TextPrimary,
    secondary = ColorReflective,
    onSecondary = BgDark,
    background = BgDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun MoodWaveTheme(
    accentColor: Color = AccentDefault,
    content: @Composable () -> Unit
) {
    val dynamicScheme = DarkColorScheme.copy(
        primary = accentColor,
        secondary = accentColor
    )

    MaterialTheme(
        colorScheme = dynamicScheme,
        typography = Typography,
        content = content
    )
}
