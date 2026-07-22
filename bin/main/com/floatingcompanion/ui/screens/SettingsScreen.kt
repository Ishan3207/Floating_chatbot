package com.floatingcompanion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.floatingcompanion.services.HotkeyManager
import com.floatingcompanion.services.SettingsManager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.ui.window.WindowScope

@Composable
fun WindowScope.SettingsScreen(onClose: () -> Unit) {
    val settings by SettingsManager.settings.collectAsState()
    var isListeningForKey by remember { mutableStateOf(false) }
    
    DisposableEffect(isListeningForKey) {
        if (isListeningForKey) {
            HotkeyManager.isListeningMode = true
            HotkeyManager.onKeyCaptured = { code, modifiers ->
                SettingsManager.updateSettings(settings.copy(hotkeyCode = code, hotkeyModifiers = modifiers))
                isListeningForKey = false
            }
        } else {
            HotkeyManager.isListeningMode = false
            HotkeyManager.onKeyCaptured = null
        }
        onDispose {
            HotkeyManager.isListeningMode = false
            HotkeyManager.onKeyCaptured = null
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        WindowDraggableArea {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Settings", style = MaterialTheme.typography.headlineSmall)
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        }
        HorizontalDivider()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("API Key", style = MaterialTheme.typography.titleMedium)
        
        OutlinedTextField(
            value = settings.apiKey,
            onValueChange = { SettingsManager.updateSettings(settings.copy(apiKey = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your OpenAI/Claude API Key") },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("AI Model", style = MaterialTheme.typography.titleMedium)
        
        val models = listOf("gpt-3.5-turbo", "gpt-4", "gpt-4o", "gpt-4-turbo", "claude-3-haiku-20240307", "claude-3-sonnet-20240229", "claude-3-opus-20240229")
        var expandedModel by remember { mutableStateOf(false) }
        
        Box {
            Button(onClick = { expandedModel = true }) {
                Text(settings.aiModel)
            }
            DropdownMenu(
                expanded = expandedModel,
                onDismissRequest = { expandedModel = false }
            ) {
                models.forEach { model ->
                    DropdownMenuItem(
                        text = { Text(model) },
                        onClick = {
                            SettingsManager.updateSettings(settings.copy(aiModel = model))
                            expandedModel = false
                        }
                    )
                }
            }
        }
        
        Text("Global Hotkey", style = MaterialTheme.typography.titleMedium)
        
        Button(
            onClick = { isListeningForKey = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListeningForKey) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isListeningForKey) "Press any key combination..." else "Change Hotkey (Current: ${com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.getKeyText(settings.hotkeyCode)})")
        }
        
        HorizontalDivider()

        Text("Window Opacity: ${(settings.windowOpacity * 100).toInt()}%", style = MaterialTheme.typography.titleMedium)
        
        Slider(
            value = settings.windowOpacity,
            onValueChange = { SettingsManager.updateSettings(settings.copy(windowOpacity = it)) },
            valueRange = 0.1f..1f,
            modifier = Modifier.fillMaxWidth()
        )
        
        HorizontalDivider()

        Spacer(modifier = Modifier.weight(1f))
        
        }
    }
}
