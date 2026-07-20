package com.floatingcompanion

import androidx.compose.foundation.background
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
import com.floatingcompanion.ui.theme.DraculaBackground
import com.floatingcompanion.ui.theme.FloatingCompanionTheme
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.system.exitProcess

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

    if (isVisible) {
        Window(
            onCloseRequest = { isVisible = false },
            state = rememberWindowState(
                size = DpSize(600.dp, 600.dp),
                position = WindowPosition(Alignment.TopEnd)
            ),
            undecorated = true,
            transparent = true,
            resizable = true,
            alwaysOnTop = true,
            title = "Floating Companion"
        ) {
            FloatingCompanionTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DraculaBackground.copy(alpha = settings.windowOpacity)),
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
                            settings = settings
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
