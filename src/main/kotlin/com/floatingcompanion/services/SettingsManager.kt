package com.floatingcompanion.services

import com.floatingcompanion.models.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object SettingsManager {
    private val settingsFile = File(System.getProperty("user.home"), ".floating-companion/settings.json")
    
    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    private fun loadSettings(): AppSettings {
        if (!settingsFile.exists()) return AppSettings()
        return try {
            json.decodeFromString<AppSettings>(settingsFile.readText())
        } catch (e: Exception) {
            e.printStackTrace()
            AppSettings()
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
        saveSettings(newSettings)
    }

    private fun saveSettings(appSettings: AppSettings) {
        try {
            settingsFile.parentFile.mkdirs()
            settingsFile.writeText(json.encodeToString(appSettings))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
