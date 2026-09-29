package com.example.ui.screens.live

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeCommissionBlue
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeYellowGold
import com.example.viewmodel.LiveSummaryData

@Composable
fun LiveSummaryScreen(
    summary: LiveSummaryData,
    onFinishAndGoHome: () -> Unit,
    onOpenDashboard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    BackHandler { onFinishAndGoHome() }

    val mins = summary.durationSeconds / 60
    val secs = summary.durationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", mins, secs)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
            .testTag("live_summary_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Trophy / Check Icon Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(VibePrimaryNeon, VibeSecondaryPink, VibeAccentPurple))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎉", fontSize = 42.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "¡Transmisión Finalizada!",
                color = appColors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = summary.title,
                color = appColors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4 Stats Grid Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.Timer,
                    iconColor = VibePrimaryNeon,
                    value = formattedDuration,
                    label = "Duración",
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    icon = Icons.Default.Group,
                    iconColor = VibeSecondaryPink,
                    value = "${summary.peakViewers}",
                    label = "Espectadores",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.MonetizationOn,
                    iconColor = VibeYellowGold,
                    value = "${summary.totalCoinsReceived} 🪙",
                    label = "Monedas Recibidas",
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    icon = Icons.Default.PersonAdd,
                    iconColor = VibeSuccessGreen,
                    value = "+${summary.newFollowersCount}",
                    label = "Nuevos Seguidores",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 75% Fair Share Breakdown Card (Validated Logic)
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = appColors.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(VibeSuccessGreen, VibePrimaryNeon)),
                        RoundedCornerShape(22.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ganancias de la Sesión",
                            color = appColors.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(VibeSuccessGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "75% Creador Activo",
                                color = VibeSuccessGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Tu Ganancia Neta (75%):", color = appColors.textSecondary, fontSize = 12.sp)
                            Text(
                                text = "+$${String.format("%.2f", summary.creatorEarningsUsd)} USD",
                                color = VibeSuccessGreen,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Comisión VibeStream (25%):", color = appColors.textSecondary, fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%.2f", summary.platformEarningsUsd)} USD",
                                color = VibeCommissionBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(appColors.border)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = VibeSuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Saldo sumado exitosamente a tu Billetera de Creador.",
                            color = appColors.textPrimary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons (Formatted and well-proportioned)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Secondary: View Full Stream Analytics
                OutlinedButton(
                    onClick = onOpenDashboard,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VibePrimaryNeon),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, VibePrimaryNeon.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .testTag("summary_view_analytics_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = VibePrimaryNeon,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ver Métricas y Gráficos del Directo",
                        color = VibePrimaryNeon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }

                // Primary: Return Home
                Button(
                    onClick = onFinishAndGoHome,
                    colors = ButtonDefaults.buttonColors(containerColor = VibePrimaryNeon),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("summary_go_home_button")
                ) {
                    Text(
                        text = "Volver al Inicio",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surface),
        modifier = modifier.border(1.dp, appColors.border, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = appColors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = appColors.textSecondary,
                fontSize = 11.sp
            )
        }
    }
}
