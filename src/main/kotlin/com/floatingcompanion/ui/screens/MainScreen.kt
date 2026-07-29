package com.floatingcompanion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.WindowPlacement
import com.floatingcompanion.models.AppSettings
import com.floatingcompanion.ui.components.WebViewContainer

@Composable
fun WindowScope.MainScreen(
    onClose: () -> Unit,
    settings: AppSettings,
    windowState: WindowState,
    isAlwaysOnTop: Boolean,
    onToggleAlwaysOnTop: () -> Unit
) {
    var showSettings by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        WindowDraggableArea {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Floating Companion", 
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
                
                Row {
                    IconButton(onClick = onToggleAlwaysOnTop) {
                        val icon = if (isAlwaysOnTop) Icons.Default.PushPin else Icons.Outlined.PushPin
                        Icon(icon, contentDescription = "Always on top", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { windowState.isMinimized = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minimize", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { 
                        if (windowState.placement == WindowPlacement.Maximized) {
                            windowState.placement = WindowPlacement.Floating
                        } else {
                            windowState.placement = WindowPlacement.Maximized
                        }
                    }) {
                        val icon = if (windowState.placement == WindowPlacement.Maximized) Icons.Default.FullscreenExit else Icons.Default.Fullscreen
                        Icon(icon, contentDescription = "Maximize", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { showSettings = !showSettings }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            WebViewContainer(
                provider = settings.selectedProvider,
                modifier = Modifier.fillMaxSize()
            )
            
            if (showSettings) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    SettingsScreen(
                        onClose = { showSettings = false }
                    )
                }
            }
        }
    }
}
