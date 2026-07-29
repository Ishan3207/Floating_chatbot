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
import com.floatingcompanion.ui.theme.FloatingCompanionTheme
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.system.exitProcess

import com.sun.jna.platform.win32.WinDef.HWND

interface User32 : StdCallLibrary {
    companion object {
        val INSTANCE: User32 = Native.load("user32", User32::class.java)
    }
    
    fun SetWindowDisplayAffinity(hWnd: HWND, dwAffinity: Int): Boolean
}

fun main() = application {
    var isInitialized by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var initError by remember { mutableStateOf<String?>(null) }
    var isVisible by remember { mutableStateOf(true) }
    val settings by SettingsManager.settings.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                KCEF.init(
                    builder = {
                        installDir(File("kcef-bundle"))
                        progress { 
                            onDownloading { 
                                downloadProgress = it 
                                println("Downloading KCEF: $it") 
                            }
                            onInitialized { 
                                println("KCEF Initialized") 
                                isInitialized = true
                            }
                        }
                        settings {
                            cachePath = File("kcef-cache").absolutePath
                            persistSessionCookies = true
                        }
                    },
                    onError = { 
                        it?.printStackTrace() 
                        initError = it?.message ?: "Unknown KCEF initialization error"
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                initError = e.message
            }
            
            HotkeyManager.initialize(settings.hotkeyCode, settings.hotkeyModifiers) {
                isVisible = !isVisible
            }
        }
    }
    
    LaunchedEffect(settings.hotkeyCode, settings.hotkeyModifiers) {
        if (isInitialized) {
            HotkeyManager.updateHotkey(settings.hotkeyCode, settings.hotkeyModifiers)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            HotkeyManager.shutdown()
            try {
                KCEF.disposeBlocking()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val windowState = rememberWindowState(
        size = DpSize(600.dp, 600.dp),
        position = WindowPosition(Alignment.Center)
    )
    var isAlwaysOnTop by remember { mutableStateOf(false) }

    if (isVisible) {
        Window(
            onCloseRequest = { isVisible = false },
            state = windowState,
            undecorated = true,
            transparent = true,
            resizable = true,
            alwaysOnTop = isAlwaysOnTop,
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

            FloatingCompanionTheme(provider = settings.selectedProvider) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isInitialized) {
                        androidx.compose.foundation.layout.Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            androidx.compose.material3.Text(
                                "Downloading Engine...", 
                                color = Color.White
                            )
                            androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
                            androidx.compose.material3.LinearProgressIndicator(
                                progress = downloadProgress / 100f
                            )
                            if (initError != null) {
                                androidx.compose.material3.Text(
                                    "Error: $initError", 
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }
                        }
                    } else {
                        MainScreen(
                            onClose = { isVisible = false },
                            settings = settings,
                            windowState = windowState,
                            isAlwaysOnTop = isAlwaysOnTop,
                            onToggleAlwaysOnTop = { isAlwaysOnTop = !isAlwaysOnTop }
                        )
                    }
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
