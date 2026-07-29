package com.floatingcompanion.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WebViewContainer(
    provider: String,
    modifier: Modifier = Modifier
) {
    val initialUrl = remember {
        when (provider) {
            "ChatGPT" -> "https://chatgpt.com/"
            "Claude" -> "https://claude.ai/"
            "Google Gemini" -> "https://gemini.google.com/"
            else -> "https://chatgpt.com/"
        }
    }
    
    val state = rememberWebViewState(initialUrl)
    val navigator = com.multiplatform.webview.web.rememberWebViewNavigator()
    var zoomLevel by remember { mutableStateOf(0.7f) }

    LaunchedEffect(provider) {
        val newUrl = when (provider) {
            "ChatGPT" -> "https://chatgpt.com/"
            "Claude" -> "https://claude.ai/"
            "Google Gemini" -> "https://gemini.google.com/"
            else -> "https://chatgpt.com/"
        }
        if (!state.lastLoadedUrl.isNullOrEmpty() && !state.lastLoadedUrl!!.startsWith(newUrl)) {
            navigator.loadUrl(newUrl)
        } else if (state.lastLoadedUrl.isNullOrEmpty()) {
            navigator.loadUrl(newUrl)
        }
    }

    LaunchedEffect(zoomLevel, state.loadingState) {
        if (state.loadingState is LoadingState.Finished) {
            navigator.evaluateJavaScript("document.body.style.zoom = '$zoomLevel'")
        }
    }
    
    WebView(
        state = state,
        modifier = modifier
            .fillMaxSize()
            .onPreviewKeyEvent { event ->
                if (event.isCtrlPressed && event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Equals, Key.NumPadAdd -> {
                            zoomLevel = min(2.0f, zoomLevel + 0.1f)
                            true
                        }
                        Key.Minus, Key.NumPadSubtract -> {
                            zoomLevel = max(0.3f, zoomLevel - 0.1f)
                            true
                        }
                        Key.Zero, Key.NumPad0 -> {
                            zoomLevel = 1.0f
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
            .onPointerEvent(PointerEventType.Scroll) { event ->
                if (event.keyboardModifiers.isCtrlPressed) {
                    val delta = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                    if (delta > 0) {
                        zoomLevel = max(0.3f, zoomLevel - 0.1f) // Scroll down to zoom out
                    } else if (delta < 0) {
                        zoomLevel = min(2.0f, zoomLevel + 0.1f) // Scroll up to zoom in
                    }
                    event.changes.forEach { it.consume() }
                }
            },
        navigator = navigator
    )
}
