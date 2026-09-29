package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.webrtc.WebRtcStats
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSurfaceElevated
import com.example.ui.theme.VibeTextSecondary

/**
 * Top Overlay Component that displays the streamer's name, avatar, category,
 * real-time viewer count, follow toggle, WebRTC quality, report flag, and close button.
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
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Streamer Profile & Live Viewer Pill (Responsive without pushing right controls)
        Row(
            modifier = Modifier
                .weight(1f, fill = false)
                .clip(RoundedCornerShape(26.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(VibePrimaryNeon.copy(alpha = 0.5f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .clickable { onStreamerProfileClicked() }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(VibePrimaryNeon, VibeSecondaryPink)
                        )
                    )
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(VibeSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(text = streamerAvatar, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Name + Viewer Count
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = streamerName,
                    color = Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pulsing Red Dot
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(VibeSecondaryPink)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$viewerCount espectadores",
                        color = VibeTextSecondary,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Follow Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isFollowing) Color.White.copy(alpha = 0.15f)
                        else VibeSecondaryPink
                    )
                    .clickable { onFollowToggle() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("follow_streamer_button")
            ) {
                Text(
                    text = if (isFollowing) "Siguiendo" else "+ Seguir",
                    color = Color.White,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        // Right side: WebRTC Quality Pill, Flag Report, and Close Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // LiveKit Quality Pill
            LiveKitQualityPill(stats = webRtcStats ?: WebRtcStats())

            // Flag / Report button
            IconButton(
                onClick = onReportClicked,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .testTag("report_streamer_flag_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Reportar Streamer",
                    tint = VibeSecondaryPink,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Close stream button
            IconButton(
                onClick = onCloseClicked,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .testTag("close_viewer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Salir",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
