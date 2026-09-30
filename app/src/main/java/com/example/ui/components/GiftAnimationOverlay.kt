package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gift
import com.example.model.GiftAnimationType
import com.example.model.WebmAssetMapper
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeCommissionBlue
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeSurface
import com.example.ui.theme.VibeYellowGold
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

data class ActiveGiftAnimation(
    val gift: Gift,
    val senderName: String,
    val creatorShareUsd: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun GiftAnimationOverlay(
    activeGift: ActiveGiftAnimation?,
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeGift == null) return

    val gift = activeGift.gift
    val rocketProgress = remember { Animatable(0f) }
    val dragonProgress = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.2f) }

    // Mapeo automático de asset WebM transparente con canal alfa (ExoPlayer Media3)
    val webmAssetUri = remember(gift.animationType) {
        WebmAssetMapper.getGiftWebmUri(gift.animationType)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    LaunchedEffect(activeGift.timestamp) {
        // Pre-cargar assets en pool para prevenir caídas de FPS
        GiftEffectManager.prewarmAllAssets(context, scope)

        // Disparo de vibración háptica sincronizada con el impacto visual del regalo premium
        GiftEffectManager.triggerGiftHaptic(context, gift)

        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
        if (gift.animationType == GiftAnimationType.VIBE_ROCKET) {
            rocketProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(2200, easing = FastOutSlowInEasing)
            )
        } else if (gift.animationType == GiftAnimationType.GALAXY_DRAGON) {
            dragonProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(2600, easing = LinearEasing)
            )
        }
        delay(2200)
        onAnimationFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Capa de video WebM con canal alfa a pantalla completa (superpuesta sobre LiveKitVideoRenderer)
        TransparentWebmPlayer(
            assetUri = webmAssetUri,
            onPlaybackEnded = onAnimationFinished,
            modifier = Modifier.fillMaxSize()
        )

        // Specific Gift Particle & Visual Effects
        when (gift.animationType) {
            GiftAnimationType.VIBE_ROCKET -> {
                val rY = (1f - rocketProgress.value) * 1200f - 400f
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, rY.roundToInt()) }
                        .size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🚀",
                        fontSize = 110.sp,
                        modifier = Modifier
                            .rotate(-25f)
                            .shadow(24.dp, shape = CircleShape)
                    )
                }
            }

            GiftAnimationType.GALAXY_DRAGON -> {
                val dX = (dragonProgress.value * 900f) - 450f
                val dRot = (dragonProgress.value * 45f) - 20f
                Box(
                    modifier = Modifier
                        .offset { IntOffset(dX.roundToInt(), 0) }
                        .size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🐉",
                        fontSize = 130.sp,
                        modifier = Modifier.rotate(dRot)
                    )
                }
            }

            GiftAnimationType.GOLDEN_CROWN -> {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(scaleAnim.value),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 110.sp)
                }
            }

            GiftAnimationType.ROSE_BURST -> {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(scaleAnim.value),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌹", fontSize = 100.sp)
                }
            }

            GiftAnimationType.NEON_SHADES -> {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(scaleAnim.value),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🕶️", fontSize = 105.sp)
                }
            }

            GiftAnimationType.HOLO_VISOR -> {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(scaleAnim.value),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🥽", fontSize = 105.sp)
                }
            }

            GiftAnimationType.AURA_FIRE -> {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(scaleAnim.value),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔥", fontSize = 115.sp)
                }
            }

            GiftAnimationType.NEON_MIC -> {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(scaleAnim.value),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎙️", fontSize = 110.sp)
                }
            }

            GiftAnimationType.CYBER_CAT -> {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(scaleAnim.value),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐱", fontSize = 115.sp)
                }
            }

            GiftAnimationType.DIAMOND_TROPHY -> {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(scaleAnim.value),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💎", fontSize = 120.sp)
                }
            }

            GiftAnimationType.STARBURST_NOVA -> {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(scaleAnim.value),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⭐", fontSize = 115.sp)
                }
            }
        }

        // Top Gift Notification Banner with 75% creator share highlight
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VibeSurface.copy(alpha = 0.95f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(listOf(VibePrimaryNeon, VibeSecondaryPink, VibeYellowGold)),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .shadow(16.dp, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = gift.emoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${activeGift.senderName} envió ${gift.name}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Valor: ${gift.coinCost} monedas ($${String.format("%.2f", gift.usdValue)} USD)",
                                color = VibeYellowGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Transparent 75% vs 25% Revenue Split Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(VibeSuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Creador recibe (75%): +$${String.format("%.2f", activeGift.creatorShareUsd)} USD",
                                color = VibeSuccessGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "VibeStream (25%)",
                            color = VibeCommissionBlue,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
