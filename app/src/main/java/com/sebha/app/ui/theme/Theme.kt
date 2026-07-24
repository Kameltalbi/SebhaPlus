package com.sebha.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = SebhaPrimary,
    onPrimary = Color.White,
    secondary = SebhaGold,
    onSecondary = SebhaText,
    background = SebhaBackground,
    onBackground = SebhaText,
    surface = SebhaSurface,
    onSurface = SebhaText,
    onSurfaceVariant = SebhaTextSecondary,
    outline = SebhaProgressTrack,
    surfaceContainerHigh = SebhaSurface,
    surfaceContainerLow = SebhaBackground
)

private val DarkColors = darkColorScheme(
    primary = SebhaButtonDark,
    onPrimary = SebhaTextDark,
    secondary = SebhaGoldDark,
    onSecondary = SebhaBackgroundDark,
    background = SebhaBackgroundDark,
    onBackground = SebhaTextDark,
    surface = SebhaSurfaceDark,
    onSurface = SebhaTextDark,
    onSurfaceVariant = SebhaTextSecondaryDark,
    outline = SebhaProgressTrackDark,
    surfaceContainerHigh = SebhaSurfaceDark,
    surfaceContainerLow = SebhaBackgroundDark
)

/**
 * Sebha Material 3 theme. Follows system dark mode and keeps status-bar
 * icon contrast aligned with the background.
 */
@Composable
fun SebhaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SebhaTypography,
        content = content
    )
}
