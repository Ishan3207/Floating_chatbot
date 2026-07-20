package com.floatingcompanion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import com.floatingcompanion.models.AppSettings
import com.floatingcompanion.ui.components.WebViewContainer

@Composable
fun WindowScope.MainScreen(
    onClose: () -> Unit,
    settings: AppSettings
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
            if (showSettings) {
                SettingsScreen(
                    onClose = { showSettings = false }
                )
            } else {
                WebViewContainer(
                    provider = settings.selectedProvider,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
