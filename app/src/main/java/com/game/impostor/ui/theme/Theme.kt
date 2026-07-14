package com.game.impostor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SpyColorScheme = darkColorScheme(
    primary = SpyGreen,
    onPrimary = SpyBlack,
    secondary = SpyRed,
    background = SpyBlack,
    surface = SpyGray,
    onBackground = SpyTextWhite,
    onSurface = SpyTextWhite,
)

@Composable
fun ImpostorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SpyColorScheme,
        typography = Typography,
        content = content
    )
}
