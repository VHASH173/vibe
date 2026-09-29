package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeTextSecondary
import com.example.ui.theme.VibeYellowGold

/**
 * Real-time Chat Overlay component for the live stream screen.
 * Renders real-time incoming messages, VIP/gift announcements, and provides
 * a rich input box with quick reaction emojis.
 */
@Composable
fun LiveChatOverlay(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onUserClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val quickReactions = listOf("🔥", "👏", "❤️", "💎", "🚀", "🎉", "👑", "⚡")

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Message Stream Area (max height ~220dp with gradient fade-out at top)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .padding(horizontal = 12.dp)
        ) {
            LazyColumn(
                state = listState,
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        onUserClicked = { onUserClicked(msg.senderName) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Emoji Reaction Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(quickReactions) { emoji ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                        .clickable { onSendMessage(emoji) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Message Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Escribe un comentario en vivo...",
                        color = VibeTextSecondary,
                        fontSize = 12.sp
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
                    focusedBorderColor = VibePrimaryNeon,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color.Black.copy(alpha = 0.7f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    if (inputText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onSendMessage(inputText.trim())
                                inputText = ""
                            },
                            modifier = Modifier.testTag("viewer_chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = VibePrimaryNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("viewer_chat_input")
            )
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    onUserClicked: () -> Unit
) {
    if (message.isDonation) {
        // Special Gift Donation Notification Card
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            VibeSecondaryPink.copy(alpha = 0.9f),
                            VibeAccentPurple.copy(alpha = 0.85f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = VibeYellowGold.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable { onUserClicked() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = VibeYellowGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message.senderName,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "envió ${message.giftName ?: "Regalo"} 🎁",
                        color = VibeYellowGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                if (message.creatorShareUsd > 0) {
                    Text(
                        text = "+$${String.format("%.2f", message.creatorShareUsd)} al creador (75%)",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    } else {
        // Regular Live Chat Bubble
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.55f))
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onUserClicked() }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge / Icon
            if (message.senderName.contains("Host", ignoreCase = true) || message.senderName.startsWith("@streamer")) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(VibeSecondaryPink)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(text = "HOST", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(5.dp))
            } else if (message.senderName.length % 3 == 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(VibeYellowGold.copy(alpha = 0.3f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(text = "VIP", color = VibeYellowGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(5.dp))
            }

            Text(
                text = "${message.senderName}: ",
                color = VibePrimaryNeon,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = message.message,
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}
