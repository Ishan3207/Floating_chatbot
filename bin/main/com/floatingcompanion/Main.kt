package com.floatingcompanion

import androidx.compose.foundation.background
import androidx.compose.ui.awt.ComposeWindow
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.win32.StdCallLibrary
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.floatingcompanion.services.HotkeyManager
import com.floatingcompanion.services.SettingsManager
import com.floatingcompanion.ui.screens.MainScreen
import com.floatingcompanion.ui.theme.HidMeBackground
import com.floatingcompanion.ui.theme.FloatingCompanionTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.sun.jna.platform.win32.WinDef.HWND

interface User32 : StdCallLibrary {
    companion object {
        val INSTANCE: User32 = Native.load("user32", User32::class.java)
    }
    
    fun SetWindowDisplayAffinity(hWnd: HWND, dwAffinity: Int): Boolean
}

fun main() = application {
    var isVisible by remember { mutableStateOf(true) }
    val settings by SettingsManager.settings.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            HotkeyManager.initialize(settings.hotkeyCode, settings.hotkeyModifiers) {
                isVisible = !isVisible
            }
        }
    }
    
    LaunchedEffect(settings.hotkeyCode, settings.hotkeyModifiers) {
        HotkeyManager.updateHotkey(settings.hotkeyCode, settings.hotkeyModifiers)
    }

    DisposableEffect(Unit) {
        onDispose {
            HotkeyManager.shutdown()
        }
    }

    if (isVisible) {
        Window(
            onCloseRequest = { isVisible = false },
            state = rememberWindowState(
                size = DpSize(808.dp, 406.dp),
                position = WindowPosition(Alignment.Center)
            ),
            undecorated = true,
            transparent = true,
            resizable = true,
            alwaysOnTop = true,
            title = "Floating Companion"
        ) {
            val composeWindow = window as? ComposeWindow
            
            LaunchedEffect(composeWindow) {
                if (composeWindow != null) {
                    try {
                        val hwndNative = HWND(Pointer(Native.getWindowID(composeWindow)))
                        val user32Platform = com.sun.jna.platform.win32.User32.INSTANCE
                        
                        val hwndParent = user32Platform.GetAncestor(hwndNative, 1) // GA_PARENT
                        val hwndRoot = user32Platform.GetAncestor(hwndNative, 2) // GA_ROOT
                        val hwndRootOwner = user32Platform.GetAncestor(hwndNative, 3) // GA_ROOTOWNER
                        
                        val hwndsToTry = listOfNotNull(hwndNative, hwndRootOwner, hwndRoot, hwndParent).filter { it.pointer != null }.distinctBy { it.pointer }
                        
                        val wdaExcludeFromCapture = 0x00000011
                        val wdaMonitor = 0x00000001
                        
                        for (hwnd in hwndsToTry) {
                            var success = User32.INSTANCE.SetWindowDisplayAffinity(hwnd, wdaExcludeFromCapture)
                            println("SetWindowDisplayAffinity (0x11) for HWND $hwnd: $success")
                            if (!success) {
                                success = User32.INSTANCE.SetWindowDisplayAffinity(hwnd, wdaMonitor)
                                println("SetWindowDisplayAffinity (0x01) for HWND $hwnd: $success")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            FloatingCompanionTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(HidMeBackground.copy(alpha = settings.windowOpacity)),
                    contentAlignment = Alignment.Center
                ) {
                    MainScreen(
                        onClose = { isVisible = false },
                        settings = settings
                    )
                }
            }
        }
    }

    Tray(
        icon = TrayIcon,
        menu = {
            Item("Toggle Window", onClick = { isVisible = !isVisible })
            Item("Exit", onClick = { exitApplication() })
        }
    )
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(24f, 24f)
    override fun DrawScope.onDraw() {
        drawCircle(color = Color.DarkGray)
        drawCircle(color = Color.White, radius = size.minDimension / 3)
    }
}
