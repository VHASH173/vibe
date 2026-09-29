package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeYellowGold
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private data class BurstParticle(
    val id: Long,
    val angleRad: Float,
    val distance: Float,
    val color: Color,
    val size: Float
)

/**
 * Interactive Floating Like Button with Heart-Burst Animation.
 * When clicked, it animates a spring bounce and triggers a radial burst of neon heart particles
 * while notifying the parent callback to spawn stream floating hearts.
 */
@Composable
fun FloatingLikeButton(
    onLikeClicked: () -> Unit,
    modifier: Modifier = Modifier,
    likeCount: Int? = null
) {
    val scope = rememberCoroutineScope()
    val buttonScale = remember { Animatable(1f) }
    val burstParticles = remember { mutableStateListOf<BurstParticle>() }
    var burstTrigger by remember { mutableIntStateOf(0) }

    val burstAnim = remember { Animatable(0f) }

    fun triggerBurst() {
        onLikeClicked()
        burstParticles.clear()
        val count = 10
        val colors = listOf(VibeSecondaryPink, VibePrimaryNeon, VibeYellowGold, VibeOrangeHot, Color(0xFFFF2D55))
        for (i in 0 until count) {
            val angle = (i.toFloat() / count) * 2f * Math.PI.toFloat() + (Random.nextFloat() * 0.4f - 0.2f)
            val dist = Random.nextFloat() * 55f + 40f
            burstParticles.add(
                BurstParticle(
                    id = System.currentTimeMillis() + i,
                    angleRad = angle,
                    distance = dist,
                    color = colors.random(),
                    size = Random.nextFloat() * 8f + 10f
                )
            )
        }

        scope.launch {
            // Button bounce
            buttonScale.animateTo(1.4f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            buttonScale.animateTo(1.0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        }

        scope.launch {
            burstAnim.snapTo(0f)
            burstAnim.animateTo(1f, animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing))
            burstParticles.clear()
        }
    }

    Box(
        modifier = modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        // Radial Burst Particles Overlay
        if (burstParticles.isNotEmpty()) {
            val progress = burstAnim.value
            val alpha = (1f - progress).coerceIn(0f, 1f)
            val scale = (0.4f + progress * 0.9f)

            burstParticles.forEach { particle ->
                val px = (cos(particle.angleRad) * particle.distance * progress)
                val py = (sin(particle.angleRad) * particle.distance * progress)

                Box(
                    modifier = Modifier
                        .offset { IntOffset(px.roundToInt(), py.roundToInt()) }
                        .graphicsLayer {
                            this.alpha = alpha
                            this.scaleX = scale
                            this.scaleY = scale
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = particle.color,
                        modifier = Modifier.size(particle.size.dp)
                    )
                }
            }
        }

        // Main Like Button
        Box(
            modifier = Modifier
                .scale(buttonScale.value)
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            VibeSecondaryPink.copy(alpha = 0.9f),
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(VibeSecondaryPink, VibePrimaryNeon)
                    ),
                    shape = CircleShape
                )
                .shadow(12.dp, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    triggerBurst()
                }
                .testTag("viewer_heart_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Dar corazón",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        // Small Like Counter Badge if provided
        likeCount?.let { count ->
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VibeSecondaryPink)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (count > 999) "${count / 1000}k" else "$count",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
