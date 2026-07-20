package com.floatingcompanion.models

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val selectedProvider: String = "ChatGPT",
    val hotkeyCode: Int = 57, // NativeKeyEvent.VC_SPACE
    val hotkeyModifiers: Int = 2, // NativeKeyEvent.CTRL_L_MASK
    val windowOpacity: Float = 0.95f,
    val windowScale: Float = 1.0f
)
