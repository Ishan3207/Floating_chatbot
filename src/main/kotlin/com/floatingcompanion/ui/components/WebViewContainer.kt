package com.floatingcompanion.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun WebViewContainer(
    provider: String,
    modifier: Modifier = Modifier
) {
    val url = when (provider) {
        "ChatGPT" -> "https://chatgpt.com/"
        "Claude" -> "https://claude.ai/"
        "Google Gemini" -> "https://gemini.google.com/"
        "Local AI" -> "http://localhost:11434/" // Placeholder
        else -> "https://chatgpt.com/"
    }
    
    val state = rememberWebViewState(url)
    val navigator = com.multiplatform.webview.web.rememberWebViewNavigator()

    // Removed CSS injection for stable app
    
    WebView(
        state = state,
        modifier = modifier.fillMaxSize(),
        navigator = navigator
    )
}
