package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.GiftModel
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeTextPrimary
import com.example.ui.theme.VibeTextSecondary
import com.example.ui.theme.VibeYellowGold

/**
 * Barra inferior de Regalos Rápidos estilo TikTok Live (Mobile & Web).
 * Despliega los regalos dinámicos de Firestore en un dock horizontal,
 * permitiendo seleccionar y enviar regalos con un solo toque, con botón de "Más ˄" y "Recargar 🪙".
 */
@Composable
fun LiveGiftDockBar(
    availableGifts: List<GiftModel>,
    coinsBalance: Int,
    onSendGift: (GiftModel) -> Unit,
    onOpenFullSheet: () -> Unit,
    onOpenCoinStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    // Regalos destacados o primeros regalos del catálogo
    val dockGifts = remember(availableGifts) {
        if (availableGifts.isNotEmpty()) {
            availableGifts.take(8)
        } else {
            emptyList()
        }
    }

    var selectedGiftId by remember { mutableStateOf<Long?>(null) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(Color(0xE6181926))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Carrusel horizontal de regalos rápidos
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockGifts.forEach { gift ->
                val isSelected = selectedGiftId == gift.id
                val canAfford = coinsBalance >= gift.diamond

                if (isSelected) {
                    // Tarjeta activa elevada con botón "Enviar"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF282A3E))
                            .border(1.5.dp, VibeSecondaryPink, RoundedCornerShape(14.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = gift.storageIcon.ifEmpty { gift.picture },
                                contentDescription = gift.name,
                                modifier = Modifier.size(34.dp)
                            )
                            Text(
                                text = "🪙 ${gift.diamond}",
                                color = VibeYellowGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (canAfford) {
                                        onSendGift(gift)
                                    } else {
                                        onOpenCoinStore()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canAfford) VibeSecondaryPink else Color.Gray
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .height(26.dp)
                                    .testTag("send_dock_gift_${gift.id}")
                            ) {
                                Text(
                                    text = if (canAfford) "Enviar" else "Recargar",
                                    color = Color.White,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    // Item normal en la barra
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedGiftId = gift.id
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("dock_gift_item_${gift.id}"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = gift.storageIcon.ifEmpty { gift.picture },
                            contentDescription = gift.name,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = gift.name,
                            color = VibeTextPrimary,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "🪙 ${gift.diamond}",
                            color = VibeYellowGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Botón "Más ˄" (More)
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onOpenFullSheet()
                    }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .testTag("open_more_gifts_dock_button"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Más regalos",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Más",
                    color = VibeTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // 2. Botón "Recargar 🪙" a la derecha
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onOpenCoinStore()
                }
                .background(Color(0xFF232536))
                .border(1.dp, VibeYellowGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("dock_recharge_button"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🪙", fontSize = 18.sp)
            Text(
                text = "Recargar",
                color = VibeYellowGold,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
