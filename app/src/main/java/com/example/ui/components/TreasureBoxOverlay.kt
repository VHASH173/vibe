package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WebmAssetMapper
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeSurface
import com.example.ui.theme.VibeSurfaceElevated
import com.example.ui.theme.VibeYellowGold
import com.example.viewmodel.ActiveTreasureBox
import com.example.viewmodel.VibeStreamViewModel
import kotlinx.coroutines.launch

@Composable
fun TreasureBoxOverlay(
    activeBox: ActiveTreasureBox?,
    viewModel: VibeStreamViewModel,
    modifier: Modifier = Modifier
) {
    if (activeBox == null) return

    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var showResultDialog by remember { mutableStateOf(false) }
    var winResult by remember { mutableStateOf<Pair<Boolean, Int>?>(null) }
    var isClaiming by remember { mutableStateOf(false) }
    var isPlayingTreasureWebm by remember { mutableStateOf(false) }

    // Disparar reproducción del asset WebM (cofre_magia.webm) al llegar a cero el temporizador
    LaunchedEffect(activeBox.remainingSeconds) {
        if (activeBox.remainingSeconds == 0 && !activeBox.isClaimed) {
            isPlayingTreasureWebm = true
        }
    }

    // Pulsing bounce animation when ready to open
    val infiniteTransition = rememberInfiniteTransition(label = "treasure_box_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = if (activeBox.isReadyToOpen && !activeBox.isClaimed) 0.95f else 1f,
        targetValue = if (activeBox.isReadyToOpen && !activeBox.isClaimed) 1.14f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val formattedTime = remember(activeBox.remainingSeconds) {
        val mins = activeBox.remainingSeconds / 60
        val secs = activeBox.remainingSeconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    Box(
        modifier = modifier
            .testTag("treasure_box_overlay_container")
    ) {
        // Floating Treasure Box Capsule
        Row(
            modifier = Modifier
                .scale(pulseScale)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (activeBox.isReadyToOpen && !activeBox.isClaimed) {
                        Brush.horizontalGradient(listOf(VibeYellowGold, VibeOrangeHot))
                    } else {
                        Brush.horizontalGradient(listOf(Color(0xFF1E1035), Color(0xFF2A1B4E)))
                    }
                )
                .border(
                    width = if (activeBox.isReadyToOpen) 2.dp else 1.5.dp,
                    brush = Brush.horizontalGradient(
                        if (activeBox.isReadyToOpen && !activeBox.isClaimed) {
                            listOf(Color.White, VibeYellowGold)
                        } else {
                            listOf(VibeOrangeHot, VibeSecondaryPink)
                        }
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .clickable {
                    if (activeBox.isReadyToOpen && !activeBox.isClaimed && !isClaiming) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isPlayingTreasureWebm = true
                        isClaiming = true
                        scope.launch {
                            val result = viewModel.claimTreasureBoxReward()
                            winResult = result
                            showResultDialog = true
                            isClaiming = false
                        }
                    } else if (activeBox.isClaimed) {
                        viewModel.dismissTreasureBox()
                    }
                }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Chest Icon
            Text(
                text = if (activeBox.isReadyToOpen) "🎁" else "🧰",
                fontSize = 20.sp
            )

            Column(horizontalAlignment = Alignment.Start) {
                if (activeBox.isReadyToOpen && !activeBox.isClaimed) {
                    Text(
                        text = "¡ABRIR!",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${activeBox.totalCoins} 🪙",
                        color = Color.Black.copy(alpha = 0.85f),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (activeBox.isClaimed) {
                    Text(
                        text = "Reclamado",
                        color = VibeSuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFFFF4B4B),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = formattedTime,
                            color = Color(0xFFFF4B4B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "${activeBox.totalCoins} 🪙 (${activeBox.maxWinners})",
                        color = VibeYellowGold,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Reproducción de video WebM con canal alfa (cofre_magia.webm) superpuesto al activarse
        if (isPlayingTreasureWebm) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                TransparentWebmPlayer(
                    assetUri = WebmAssetMapper.getTreasureBoxWebmUri(),
                    onPlaybackEnded = { isPlayingTreasureWebm = false },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Result AlertDialog with Neon Gradient Border
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = {
                showResultDialog = false
                viewModel.dismissTreasureBox()
            },
            containerColor = VibeSurface,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .border(
                    width = 2.dp,
                    brush = Brush.horizontalGradient(
                        listOf(VibePrimaryNeon, VibeSecondaryPink, VibeYellowGold)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(4.dp),
            title = {
                Text(
                    text = if (winResult?.first == true) "¡Cofre Abierto! 🎉" else "Cofre Vacío 😢",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (winResult?.first == true) "👑" else "📦",
                        fontSize = 54.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (winResult?.first == true) {
                        val won = winResult?.second ?: 0
                        Text(
                            text = "¡Ganaste $won Monedas! 🎉",
                            color = VibeYellowGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Se han añadido automáticamente a tu Billetera de VibeStream.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "El cofre se vació antes de que lo abrieras 😢",
                            color = Color(0xFFFF5252),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "¡Participa en el próximo cofre que lancen los creadores!",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResultDialog = false
                        viewModel.dismissTreasureBox()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VibePrimaryNeon),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¡Entendido!",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        )
    }
}
