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

private val ChatGPTColorScheme = darkColorScheme(
    primary = ChatGptPrimary,
    secondary = ChatGptPrimary,
    background = ChatGptBackground,
    surface = ChatGptSurface,
    onPrimary = ChatGptText,
    onSecondary = ChatGptText,
    onBackground = ChatGptText,
    onSurface = ChatGptText,
)

private val GeminiColorScheme = darkColorScheme(
    primary = GeminiPrimary,
    secondary = GeminiPrimary,
    background = GeminiBackground,
    surface = GeminiSurface,
    onPrimary = GeminiBackground,
    onSecondary = GeminiBackground,
    onBackground = GeminiText,
    onSurface = GeminiText,
)

private val ClaudeColorScheme = darkColorScheme(
    primary = ClaudePrimary,
    secondary = ClaudePrimary,
    background = ClaudeBackground,
    surface = ClaudeSurface,
    onPrimary = ClaudeBackground,
    onSecondary = ClaudeBackground,
    onBackground = ClaudeText,
    onSurface = ClaudeText,
)

@Composable
fun FloatingCompanionTheme(
    provider: String = "ChatGPT",
    content: @Composable () -> Unit
) {
    val colorScheme = when (provider) {
        "ChatGPT" -> ChatGPTColorScheme
        "Google Gemini" -> GeminiColorScheme
        "Claude" -> ClaudeColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
