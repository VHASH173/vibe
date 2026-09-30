package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gift
import com.example.model.GiftAnimationType
import com.example.model.WebmAssetMapper
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeYellowGold
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

data class ActiveGiftAnimation(
    val gift: Gift,
    val senderName: String,
    val creatorShareUsd: Double,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Overlay de animación cinematográfica de Regalos en Vivo estilo TikTok Live limpio y sin sobrecargas.
 * Reproduce el video WebM transparente en pantalla completa junto con la figura animada de impacto en la parte inferior.
 */
@Composable
fun GiftAnimationOverlay(
    activeGift: ActiveGiftAnimation?,
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeGift == null) return

    val gift = activeGift.gift
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    // Animadores de entrada
    val entranceScale = remember { Animatable(0f) }
    val heroOffsetY = remember { Animatable(250f) }
    val auraScale = remember { Animatable(0.5f) }

    // Mapeo dinámico de asset WebM transparente con canal alfa (Media3)
    val webmAssetUri = remember(gift.animationType, gift.coinCost, gift.name) {
        WebmAssetMapper.getGiftWebmUriByValue(
            diamond = gift.coinCost.toLong(),
            name = gift.name
        )
    }

    // Animación continua de impacto sutil
    val infiniteTransition = rememberInfiniteTransition(label = "hero_pulse")
    val heroPulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroPulse"
    )

    val stompShake by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stompShake"
    )

    LaunchedEffect(activeGift.timestamp) {
        // Pre-cargar assets en pool para prevenir caídas de FPS
        GiftEffectManager.prewarmAllAssets(context, scope)

        // Disparo de vibración háptica sincronizada
        GiftEffectManager.triggerGiftHaptic(context, gift)

        // Entrada explosiva
        entranceScale.animateTo(1.15f, animationSpec = tween(240, easing = FastOutSlowInEasing))
        entranceScale.animateTo(1.0f, animationSpec = tween(160, easing = FastOutSlowInEasing))
        heroOffsetY.animateTo(0f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        auraScale.animateTo(1.3f, animationSpec = tween(500))

        // Duración en pantalla limpia (5 segundos)
        delay(5000)

        // Salida
        entranceScale.animateTo(0.1f, animationSpec = tween(250))
        onAnimationFinished()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. Capa de video WebM con canal alfa a pantalla completa
        TransparentWebmPlayer(
            assetUri = webmAssetUri,
            onPlaybackEnded = onAnimationFinished,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Aura de energía de impacto bajo el personaje
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
                .size(280.dp)
                .scale(auraScale.value * heroPulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            VibeYellowGold.copy(alpha = 0.35f),
                            VibeOrangeHot.copy(alpha = 0.20f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 3. Criatura / Personaje 3D Gigante en la mitad inferior
        val nameLower = gift.name.lowercase()
        val isGorilla = nameLower.contains("gorila") || nameLower.contains("gorilla") || nameLower.contains("mono") || nameLower.contains("beast")
        val isDragon = gift.animationType == GiftAnimationType.GALAXY_DRAGON || nameLower.contains("dragon") || nameLower.contains("dragón")
        val isRocket = gift.animationType == GiftAnimationType.VIBE_ROCKET || nameLower.contains("cohete") || nameLower.contains("rocket")
        val isCrown = gift.animationType == GiftAnimationType.GOLDEN_CROWN || nameLower.contains("corona") || nameLower.contains("crown")
        val isRose = gift.animationType == GiftAnimationType.ROSE_BURST || nameLower.contains("rosa") || nameLower.contains("rose")
        val isTrophy = gift.animationType == GiftAnimationType.DIAMOND_TROPHY || nameLower.contains("trofeo") || nameLower.contains("diamond")
        val isFire = gift.animationType == GiftAnimationType.AURA_FIRE || nameLower.contains("fuego") || nameLower.contains("fire")
        val isCat = gift.animationType == GiftAnimationType.CYBER_CAT || nameLower.contains("gato") || nameLower.contains("cat")

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 110.dp)
                .offset { IntOffset(stompShake.roundToInt(), heroOffsetY.value.roundToInt()) }
                .scale(entranceScale.value * heroPulse),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when {
                    isGorilla -> Text("🦍", fontSize = 160.sp, modifier = Modifier.shadow(28.dp, CircleShape))
                    isDragon -> Text("🐉", fontSize = 160.sp, modifier = Modifier.rotate(-10f + stompShake).shadow(28.dp, CircleShape))
                    isRocket -> Text("🚀", fontSize = 150.sp, modifier = Modifier.rotate(-25f).shadow(28.dp, CircleShape))
                    isCrown -> Text("👑", fontSize = 155.sp, modifier = Modifier.shadow(28.dp, CircleShape))
                    isRose -> Text("🌹", fontSize = 150.sp, modifier = Modifier.shadow(28.dp, CircleShape))
                    isTrophy -> Text("💎", fontSize = 150.sp, modifier = Modifier.shadow(28.dp, CircleShape))
                    isFire -> Text("🔥", fontSize = 155.sp, modifier = Modifier.shadow(28.dp, CircleShape))
                    isCat -> Text("🐱", fontSize = 150.sp, modifier = Modifier.shadow(28.dp, CircleShape))
                    else -> Text(gift.emoji.ifEmpty { "🎁" }, fontSize = 150.sp, modifier = Modifier.shadow(28.dp, CircleShape))
                }

                // Etiqueta limpia del regalo
                Box(
                    modifier = Modifier
                        .offset(y = (-8).dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = gift.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
