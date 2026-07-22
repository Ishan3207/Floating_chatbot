package com.floatingcompanion.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowScope
import androidx.compose.foundation.window.WindowDraggableArea
import com.floatingcompanion.models.AppSettings
import com.floatingcompanion.services.OpenAiApiService
import com.floatingcompanion.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.layout

data class ChatMessage(val text: String, val isUser: Boolean)

// Helper extension to animate position safely outside standard padding flows
fun Modifier.fractionalOffsetCustom(fraction: Float, startX: Float, startY: Float, endX: Float, endY: Float): Modifier = this.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val curX = startX + (endX - startX) * fraction
    val curY = startY + (endY - startY) * fraction
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(curX.toInt(), curY.toInt())
    }
}

@Composable
fun WindowScope.MainScreen(
    onClose: () -> Unit,
    settings: AppSettings
) {
    var showSettings by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var chatMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    val coroutineScope = rememberCoroutineScope()

    if (showSettings) {
        SettingsScreen(onClose = { showSettings = false })
    } else {
        val hasChat = chatMessages.isNotEmpty()
        
        // Progress defines 0.0 for initial state (centered) and 1.0 for active state (top-left / bottom)
        val progress by animateFloatAsState(
            targetValue = if (hasChat) 1f else 0f,
            animationSpec = tween(durationMillis = 600)
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            
            // Header Top Bar - Contains Draggable functionality
            Column(modifier = Modifier.fillMaxWidth()) {
                WindowDraggableArea {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            IconButton(onClick = { showSettings = true }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = HidMeForeground.copy(alpha = 0.8f))
                            }
                            IconButton(onClick = onClose) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = HidMeForeground.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }

            // Big "HIDME" Text Layer
            // It shrinks and goes top-left as progress -> 1f
            // Size: 240sp to 32sp. Let's interpolate
            val fontSize = 240f - (240f - 32f) * progress
            val textOpacity = 0.49f + (1f - 0.49f) * progress
            
            Text(
                text = "HIDME",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Black,
                    color = HidMeForeground.copy(alpha = textOpacity),
                    letterSpacing = (-4).sp
                ),
                maxLines = 1,
                // Interpolate X and Y padding manually to center the text when 0, and align top-left when 1
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fractionalOffsetCustom(
                        progress, 
                        startX = -20f, // Center horizontally visually (slightly offset based on text width, but we'll try absolute padding)
                        startY = 60f,  // Center vertically
                        endX = 24f,    // Target left padding
                        endY = 16f     // Target top padding
                    )
            )

            // Chat Messages area
            if (progress > 0.01f) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 64.dp, bottom = 100.dp, start = 24.dp, end = 24.dp),
                    reverseLayout = true
                ) {
                    items(chatMessages.reversed()) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (msg.isUser) HidMeButtonBackground else Color.Transparent)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    style = TextStyle(
                                        color = if (msg.isUser) HidMeInputText else HidMeForeground,
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }
                    }
                    if (chatMessages.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.padding(bottom = 24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No messages yet.",
                                    color = HidMeForeground.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Text Slot Layer
            // It moves from middle to bottom as progress -> 1f
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fractionalOffsetCustom(
                        progress,
                        startX = 0f, 
                        startY = -175f, // Approx Center Y
                        endX = 0f, 
                        endY = -24f     // Bottom padding 24
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(CircleShape)
                    .background(HidMeInputBackground)
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Attachment action */ }) {
                    Icon(Icons.Default.Attachment, contentDescription = "Attach", tint = HidMeIconColor)
                }
                
                Box(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    if (inputText.isEmpty() && !isLoading) {
                        Text("How may I assist you?", color = HidMeIconColor.copy(alpha = 0.6f))
                    }
                    if (isLoading) {
                        Text("Thinking...", color = HidMeIconColor.copy(alpha = 0.6f))
                    } else {
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            textStyle = TextStyle(color = HidMeInputText, fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val userMsg = inputText
                            chatMessages = chatMessages + ChatMessage(userMsg, true)
                            inputText = ""
                            isLoading = true
                            
                            coroutineScope.launch {
                                val response = OpenAiApiService.sendMessage(settings.apiKey, settings.aiModel, userMsg)
                                chatMessages = chatMessages + ChatMessage(response, false)
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .background(HidMeIconColor.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Send", tint = HidMeIconColor)
                }
            }
        }
    }
}
