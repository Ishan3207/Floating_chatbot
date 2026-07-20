package com.floatingcompanion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DraculaPurple,
    secondary = DraculaCyan,
    background = DraculaBackground,
    surface = DraculaBlack,
    onPrimary = DraculaBackground,
    onSecondary = DraculaBackground,
    onBackground = DraculaForeground,
    onSurface = DraculaForeground,
)

@Composable
fun FloatingCompanionTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
