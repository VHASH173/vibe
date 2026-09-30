package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.webrtc.WebRtcStats
import com.example.ui.theme.VibeSecondaryPink

/**
 * Encabezado Superior Oficial de TikTok Live para celulares:
 * - Píldora del Creador: Avatar, Nombre (@streamer), Me gusta (♥ 849) y botón "+ Seguir"
 * - Sub-píldoras: "🔥 Clasificación diaria" y "Liga de poder 🏆"
 * - Espectadores y botón de cerrar "✕"
 */
@Composable
fun StreamerHeaderOverlay(
    streamerName: String,
    streamerAvatar: String,
    category: String,
    viewerCount: Int,
    isFollowing: Boolean,
    webRtcStats: WebRtcStats?,
    onFollowToggle: () -> Unit,
    onStreamerProfileClicked: () -> Unit,
    onReportClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        // Fila Principal Superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Píldora del Creador en la Izquierda
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable { onStreamerProfileClicked() }
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar del Streamer
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF374151)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = streamerAvatar.ifEmpty { "👤" }, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Nombre y Likes
                Column(
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = streamerName.removePrefix("@"),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "♥ ${viewerCount * 3 + 120}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }

                // Botón "+ Seguir"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isFollowing) Color.Gray else Color(0xFFFE2C55))
                        .clickable { onFollowToggle() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("follow_streamer_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFollowing) "Siguiendo" else "+ Seguir",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Derecha: Avatares de espectadores y Botón Cerrar (✕)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Insignias de Top Espectadores
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👑", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$viewerCount",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón Cerrar (✕)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable { onCloseClicked() }
                        .testTag("close_stream_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar Live",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Sub-píldoras de TikTok Live: "🔥 Clasificación diaria" y "Liga de poder 🏆"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "🔥 Clasificación diaria",
                    color = Color.White,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Liga de poder 🏆",
                    color = Color.White,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
