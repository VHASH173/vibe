package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeYellowGold

/**
 * Overlay de Chat y Barra de Acción Inferior idéntica a la interfaz oficial de TikTok Live para celulares.
 */
@Composable
fun LiveChatOverlay(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onUserClicked: (String) -> Unit,
    onOpenGiftSheet: () -> Unit = {},
    onSendQuickRose: () -> Unit = {},
    onShareClicked: () -> Unit = {},
    onMultiGuestClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    ) {
        // 1. Área de Chat y Mensajes (Alineada abajo a la izquierda)
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(230.dp)
                .padding(horizontal = 12.dp)
        ) {
            // Notificación del sistema de filtrado de comentarios de TikTok Live
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎵", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Algunos comentarios de este LIVE se filtraron para proteger la experiencia de la comunidad.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(
                state = listState,
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { msg ->
                    TikTokChatBubbleItem(
                        message = msg,
                        onUserClicked = { onUserClicked(msg.senderName) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Barra de Acción Inferior Oficial de TikTok Live Mobile
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Campo "Escribe algo..." (Cápsula oscura en la izquierda)
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Escribe algo...",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText.trim())
                            inputText = ""
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Black.copy(alpha = 0.55f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.55f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(26.dp),
                trailingIcon = {
                    if (inputText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onSendMessage(inputText.trim())
                                inputText = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = VibePrimaryNeon,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("viewer_chat_input")
            )

            // Botón Multi-Guest / Interacción (👥)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable { onMultiGuestClicked() }
                    .testTag("multi_guest_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Multi-Guest",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Botón Rápido de Rosa (🌹)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable { onSendQuickRose() }
                    .testTag("quick_rose_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌹", fontSize = 22.sp)
            }

            // Botón de Cofre / Caja de Regalo TikTok (🎁) con badge de monedas
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(VibeSecondaryPink, Color(0xFFE11D48))
                        )
                    )
                    .clickable { onOpenGiftSheet() }
                    .testTag("open_gifts_sheet_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Enviar Regalo",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                // Badge "1" en la esquina inferior derecha
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF27273A))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "1",
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Botón de Compartir (↗)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable { onShareClicked() }
                    .testTag("share_live_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartir",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TikTokChatBubbleItem(
    message: ChatMessage,
    onUserClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable { onUserClicked() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar circular del usuario
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF374151)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "👤", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.width(6.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message.senderName,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(4.dp))
                // Badge de nivel/anfitrión estilo TikTok (🎯 N.º 1)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE11D48))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "🎯 N.º 1",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Text(
                text = message.message,
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}
