package com.floatingcompanion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.floatingcompanion.services.HotkeyManager
import com.floatingcompanion.services.SettingsManager
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent

@Composable
fun SettingsScreen(onClose: () -> Unit) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        
        HorizontalDivider()
        
        Text("AI Provider", style = MaterialTheme.typography.titleMedium)
        
        val providers = listOf("ChatGPT", "Claude", "Google Gemini")
        var expanded by remember { mutableStateOf(false) }
        
        Box {
            Button(onClick = { expanded = true }) {
                Text(settings.selectedProvider)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                providers.forEach { provider ->
                    DropdownMenuItem(
                        text = { Text(provider) },
                        onClick = {
                            SettingsManager.updateSettings(settings.copy(selectedProvider = provider))
                            expanded = false
                        }
                    )
                }
            }
        }
        
        HorizontalDivider()
        
        Text("Global Hotkey", style = MaterialTheme.typography.titleMedium)
        
        Button(
            onClick = { isListeningForKey = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListeningForKey) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isListeningForKey) "Press any key combination..." else "Change Hotkey (Current: ${NativeKeyEvent.getKeyText(settings.hotkeyCode)})")
        }
        

        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onClose,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Back")
        }
    }
}
