package com.floatingcompanion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = HidMeBackground,
    surface = HidMeBackground,
    onBackground = HidMeForeground,
    onSurface = HidMeForeground,
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
