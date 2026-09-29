package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.webrtc.WebRtcStats
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeSurface
import com.example.ui.theme.VibeSurfaceElevated
import com.example.ui.theme.VibeTextPrimary
import com.example.ui.theme.VibeTextSecondary
import com.example.ui.theme.VibeYellowGold

@Composable
fun LiveKitQualityPill(
    stats: WebRtcStats,
    modifier: Modifier = Modifier
) {
    var showDiagnosticsDialog by remember { mutableStateOf(false) }

    // Color code: Green < 45ms, Yellow 45-120ms, Red > 120ms
    val (statusColor, qualityLabel) = when {
        stats.latencyMs < 45 -> Pair(VibeSuccessGreen, "Excelente")
        stats.latencyMs < 120 -> Pair(VibeYellowGold, "Buena")
        else -> Pair(Color.Red, "Inestable")
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_ping")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )

    Row(
        modifier = modifier
            .height(30.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.65f))
            .border(1.dp, statusColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .clickable { showDiagnosticsDialog = true }
            .padding(horizontal = 8.dp, vertical = 0.dp)
            .testTag("livekit_quality_pill"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pulsing status dot
        Box(
            modifier = Modifier
                .size(7.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(statusColor)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "${stats.latencyMs}ms",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Spacer(modifier = Modifier.width(3.dp))

        Text(
            text = "LiveKit",
            color = VibePrimaryNeon,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }

    // Diagnostics Dialog
    if (showDiagnosticsDialog) {
        AlertDialog(
            onDismissRequest = { showDiagnosticsDialog = false },
            containerColor = VibeSurface,
            icon = {
                Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = VibePrimaryNeon, modifier = Modifier.size(32.dp))
            },
            title = {
                Text(
                    text = "Telemetría WebRTC (LiveKit SFU)",
                    color = VibeTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DiagnosticRow(label = "Estado de Red", value = qualityLabel, valueColor = statusColor)
                    DiagnosticRow(label = "Latencia RTT", value = "${stats.latencyMs} ms", valueColor = statusColor)
                    DiagnosticRow(label = "Bitrate de Video", value = "${stats.bitrateKbps} kbps", valueColor = Color.White)
                    DiagnosticRow(label = "Fotogramas por Segundo", value = "${stats.fps} FPS", valueColor = Color.White)
                    DiagnosticRow(label = "Pérdida de Paquetes", value = "${stats.packetLossPercent}%", valueColor = VibeSuccessGreen)
                    DiagnosticRow(label = "Resolución", value = stats.resolution, valueColor = VibeTextSecondary)
                    DiagnosticRow(label = "Protocolo de Códec", value = stats.protocol, valueColor = VibeTextSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDiagnosticsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VibePrimaryNeon),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Cerrar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = VibeTextSecondary, fontSize = 12.sp)
        Text(text = value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
