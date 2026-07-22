package com.floatingcompanion.services

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import java.util.logging.Level
import java.util.logging.Logger

object HotkeyManager : NativeKeyListener {
    private var onHotkeyPressed: (() -> Unit)? = null
    private var currentKeyCode: Int = NativeKeyEvent.VC_SPACE
    private var currentModifiers: Int = NativeKeyEvent.CTRL_L_MASK
    
    var isListeningMode: Boolean = false
    var onKeyCaptured: ((code: Int, modifiers: Int) -> Unit)? = null

    fun initialize(keyCode: Int, modifiers: Int, onTrigger: () -> Unit) {
        currentKeyCode = keyCode
        currentModifiers = modifiers
        onHotkeyPressed = onTrigger
        
        val logger = Logger.getLogger(GlobalScreen::class.java.`package`.name)
        logger.level = Level.OFF
        logger.useParentHandlers = false
        
        try {
            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook()
            }
            GlobalScreen.addNativeKeyListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun updateHotkey(keyCode: Int, modifiers: Int) {
        currentKeyCode = keyCode
        currentModifiers = modifiers
    }

    override fun nativeKeyPressed(e: NativeKeyEvent) {
        if (isListeningMode) {
            if (e.keyCode == NativeKeyEvent.VC_SHIFT || 
                e.keyCode == NativeKeyEvent.VC_CONTROL || 
                e.keyCode == NativeKeyEvent.VC_ALT || 
                e.keyCode == NativeKeyEvent.VC_META) {
                return
            }
            onKeyCaptured?.invoke(e.keyCode, e.modifiers)
            isListeningMode = false
            return
        }
        
        val isCorrectModifiers = if (currentModifiers == 0) {
            e.modifiers == 0
        } else {
            (e.modifiers and currentModifiers) == currentModifiers
        }
        
        if (e.keyCode == currentKeyCode && isCorrectModifiers) {
            onHotkeyPressed?.invoke()
        }
    }
    
    override fun nativeKeyReleased(e: NativeKeyEvent) {}
    override fun nativeKeyTyped(e: NativeKeyEvent) {}
    
    fun shutdown() {
        try {
            GlobalScreen.removeNativeKeyListener(this)
            if (GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.unregisterNativeHook()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
