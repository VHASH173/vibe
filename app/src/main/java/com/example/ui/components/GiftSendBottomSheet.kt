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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeCommissionBlue
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeSurface
import com.example.ui.theme.VibeSurfaceElevated
import com.example.ui.theme.VibeSurfaceVariant
import com.example.ui.theme.VibeTextPrimary
import com.example.ui.theme.VibeTextSecondary
import com.example.ui.theme.VibeYellowGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftSendBottomSheet(
    coinsBalance: Int,
    streamerName: String,
    availableGifts: List<GiftModel>,
    onDismiss: () -> Unit,
    onSendGift: (GiftModel) -> Unit,
    onOpenCoinStore: () -> Unit,
    onSendTreasureBox: ((totalCoins: Int, maxWinners: Int) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val haptic = LocalHapticFeedback.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Regalos, 1 = Cofres
    var selectedGift by remember(availableGifts) {
        mutableStateOf(availableGifts.firstOrNull())
    }

    // Treasure box configurations
    var selectedTreasureCoins by remember { mutableIntStateOf(100) }
    var selectedTreasureWinners by remember { mutableIntStateOf(10) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VibeSurface,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("gift_bottom_sheet")
        ) {
            // Header with User Balance & Top-Up
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = if (selectedTab == 0) "Enviar Regalo a $streamerName" else "Lanzar Cofre del Tesoro",
                        color = VibeTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Tu Saldo: $coinsBalance monedas",
                        color = VibeYellowGold,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }

                // Buy Coins button
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenCoinStore()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VibeSurfaceElevated,
                        contentColor = VibeYellowGold
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .wrapContentWidth()
                        .border(1.dp, VibeYellowGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .testTag("open_coin_store_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Recargar Monedas",
                            tint = VibeYellowGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Recargar",
                            color = VibeYellowGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab Selector: "Regalos 🎁" vs "Cofres 🧰"
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = VibeSurfaceVariant,
                contentColor = VibePrimaryNeon,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = VibePrimaryNeon,
                        height = 2.5.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedTab = 0
                    },
                    text = {
                        Text(
                            text = "Regalos 🎁",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedTab == 0) VibePrimaryNeon else VibeTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedTab = 1
                    },
                    text = {
                        Text(
                            text = "Cofres 🧰",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedTab == 1) VibeYellowGold else VibeTextSecondary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // ==========================================
                // REGALOS ANIMADOS TAB
                // ==========================================
                // Fair 75/25 Platform Transparency Callout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VibeSurfaceVariant)
                        .border(1.dp, VibeSuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = VibeSuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Compromiso de Comisión Justa VibeStream:",
                            color = VibeSuccessGreen,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "El 75% va directo a la billetera de $streamerName. VibeStream retiene solo 25%.",
                            color = VibeTextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dynamic Gift Grid loaded from Firebase Firestore
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(2.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(230.dp)
                ) {
                    items(availableGifts, key = { it.id }) { gift ->
                        val isSelected = selectedGift?.id == gift.id
                        val canAfford = coinsBalance >= gift.diamond

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) VibeSurfaceElevated else VibeSurfaceVariant)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) VibePrimaryNeon else Color.Transparent,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedGift = gift
                                }
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                                .testTag("gift_item_${gift.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Icono: Cárgalo usando Coil AsyncImage
                                AsyncImage(
                                    model = gift.storageIcon.ifEmpty { gift.picture },
                                    contentDescription = gift.name,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                // Nombre del regalo
                                Text(
                                    text = gift.name,
                                    color = VibeTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                // Costo en monedas (diamantes)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${gift.diamond} 🪙",
                                        color = if (canAfford) VibeYellowGold else Color.Red,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selected Gift Details & Send Button
                selectedGift?.let { gift ->
                    val canAfford = coinsBalance >= gift.diamond
                    val usdValue = gift.diamond * 0.01
                    val creatorShareUsd = usdValue * 0.75

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(VibeSurfaceElevated)
                            .border(1.dp, if (canAfford) VibePrimaryNeon.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = gift.storageIcon.ifEmpty { gift.picture },
                            contentDescription = gift.name,
                            modifier = Modifier.size(40.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${gift.name} (${gift.diamond} 🪙)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Gana creador: +$${String.format("%.2f", creatorShareUsd)} USD (75%)",
                                color = VibeSuccessGreen,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (canAfford) {
                                    onSendGift(gift)
                                    onDismiss()
                                } else {
                                    onOpenCoinStore()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canAfford) VibePrimaryNeon else VibeSecondaryPink
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            modifier = Modifier
                                .height(42.dp)
                                .wrapContentWidth()
                                .testTag("confirm_send_gift_button")
                        ) {
                            Text(
                                text = if (canAfford) "Enviar 🎁" else "Recargar",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            } else {
                // ==========================================
                // COFRES DEL TESORO (TREASURE BOX) TAB
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Explanatory Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(VibeSurfaceVariant)
                            .border(1.dp, VibeYellowGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🧰", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cofre del Tesoro para la Audiencia",
                                color = VibeYellowGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Inicia una cuenta regresiva de 60s en vivo para que los espectadores compitan por las monedas.",
                                color = VibeTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    // 1. Selector de Monedas a Regalar
                    Text(
                        text = "1. Selecciona las monedas totales a repartir:",
                        color = VibeTextPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(100, 500, 1000).forEach { coins ->
                            val isSelected = selectedTreasureCoins == coins
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) VibeYellowGold else VibeSurfaceElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) VibeYellowGold else Color.White.copy(alpha = 0.1f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedTreasureCoins = coins
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$coins 🪙",
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // 2. Selector de Ganadores
                    Text(
                        text = "2. Cantidad de espectadores que podrán ganar:",
                        color = VibeTextPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10, 50, 100).forEach { winners ->
                            val isSelected = selectedTreasureWinners == winners
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) VibePrimaryNeon else VibeSurfaceElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) VibePrimaryNeon else Color.White.copy(alpha = 0.1f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedTreasureWinners = winners
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$winners personas",
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val canAffordTreasure = coinsBalance >= selectedTreasureCoins
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (canAffordTreasure) {
                                onSendTreasureBox?.invoke(selectedTreasureCoins, selectedTreasureWinners)
                                onDismiss()
                            } else {
                                onOpenCoinStore()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAffordTreasure) VibeYellowGold else VibeSecondaryPink
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("launch_treasure_box_button")
                    ) {
                        Text(
                            text = if (canAffordTreasure) "Lanzar Cofre 🧰 ($selectedTreasureCoins 🪙)" else "Recargar Saldo Necesario",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
