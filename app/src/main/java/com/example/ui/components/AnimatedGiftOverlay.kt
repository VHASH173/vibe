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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.model.GiftAnimationType
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeYellowGold
import kotlinx.coroutines.delay

@Composable
fun AnimatedGiftOverlay(
    activeGift: ActiveGiftAnimation?,
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeGift == null) return

    val gift = activeGift.gift

    // Lottie JSON URL mapping for Premium Gifts (with fallback for offline/preview resilience)
    val lottieUrl = remember(gift.animationType) {
        when (gift.animationType) {
            GiftAnimationType.VIBE_ROCKET -> "https://assets10.lottiefiles.com/packages/lf20_m64xbslf.json"
            GiftAnimationType.GALAXY_DRAGON -> "https://assets9.lottiefiles.com/packages/lf20_w51pcehl.json"
            GiftAnimationType.GOLDEN_CROWN -> "https://assets3.lottiefiles.com/packages/lf20_j1adxtyb.json"
            else -> "https://assets2.lottiefiles.com/packages/lf20_u4yrau05.json"
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.Url(lottieUrl))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.0f
    )

    // Auto-dismiss after 4.2 seconds
    LaunchedEffect(activeGift.timestamp) {
        delay(4200)
        onAnimationFinished()
    }

    // Dismiss when Lottie completes if available
    LaunchedEffect(progress) {
        if (progress >= 0.99f && composition != null) {
            delay(400)
            onAnimationFinished()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "gift_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("animated_gift_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Transparent Overlay Canvas with Starburst particle aura
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.45f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        VibeSecondaryPink.copy(alpha = 0.45f),
                        VibePrimaryNeon.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width * 0.75f
                ),
                radius = size.width * 0.75f,
                center = center
            )
        }

        // Lottie Animation Container covering screen with transparent background
        Box(
            modifier = Modifier
                .size(340.dp)
                .scale(pulseScale),
            contentAlignment = Alignment.Center
        ) {
            if (composition != null) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // High-impact graceful fallback with animated 3D emoji burst
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = gift.emoji, fontSize = 110.sp)
                }
            }
        }

        // Celebratory Banner (Top/Center) with Sender & 75% Fair Share Callout
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 110.dp)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                VibeSecondaryPink.copy(alpha = 0.92f),
                                VibeAccentPurple.copy(alpha = 0.92f),
                                VibePrimaryNeon.copy(alpha = 0.92f)
                            )
                        )
                    )
                    .border(2.dp, Color.White, RoundedCornerShape(24.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = gift.emoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${activeGift.senderName} envió un ${gift.name}!",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "+${gift.coinCost} 🪙  (+$${String.format("%.2f", activeGift.creatorShareUsd)} USD para el Creador • 75%)",
                            color = VibeYellowGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
