package com.example.ui.screens.inbox

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.VibeCommissionBlue
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeYellowGold

@Composable
fun InboxScreen(
    transactions: List<TransactionEntity>,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
            .padding(horizontal = 16.dp)
            .testTag("inbox_screen")
    ) {
        Spacer(modifier = Modifier.height(44.dp))

        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bandeja de Entrada",
                color = appColors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(appColors.surfaceElevated)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(text = "Todas las actividades", color = appColors.textSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Monetization Notice Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(appColors.surfaceVariant)
                .border(1.dp, VibeSuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VibeSuccessGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = VibeSuccessGreen)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Modelo 75/25 Activo en tu Cuenta",
                    color = VibeSuccessGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cada regalo que recibes en live te entrega el 75% directo. En TikTok sólo recibes el 50%.",
                    color = appColors.textSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Alertas de Donaciones y Actividad",
            color = appColors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Live Transactions from database or Empty State
            if (transactions.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = appColors.textSecondary.copy(alpha = 0.35f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aún no tienes regalos ni transacciones recientes",
                            color = appColors.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                items(transactions) { tx ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = appColors.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, appColors.border, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconColor = if (tx.type == "GIFT_RECEIVED") VibeSuccessGreen else VibeSecondaryPink
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(iconColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.description,
                                    color = appColors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (tx.creatorShareUsd > 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Tu 75%: +$${String.format("%.2f", tx.creatorShareUsd)} USD",
                                            color = VibeSuccessGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "| VibeStream 25%: $${String.format("%.2f", tx.platformShareUsd)}",
                                            color = VibeCommissionBlue,
                                            fontSize = 10.sp
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Valor: $${String.format("%.2f", tx.usdAmount)} USD",
                                        color = VibeYellowGold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Fixed activity alerts
            item {
                ActivityItem(
                    emoji = "👤",
                    title = "@cyber_kiri comenzó a seguirte",
                    subtitle = "Hace 2 horas"
                )
            }
            item {
                ActivityItem(
                    emoji = "❤️",
                    title = "A @marcos_beats le gustó tu stream",
                    subtitle = "Hace 4 horas"
                )
            }
            item {
                ActivityItem(
                    emoji = "⚡",
                    title = "Actualización de Red WebRTC LiveKit",
                    subtitle = "Nueva región us-east conectada con 18ms de ping"
                )
            }
        }
    }
}

@Composable
private fun ActivityItem(
    emoji: String,
    title: String,
    subtitle: String
) {
    val appColors = LocalAppColors.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, appColors.border, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(appColors.surfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = appColors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = appColors.textSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
