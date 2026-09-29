package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeYellowGold
import kotlin.math.roundToInt
import kotlin.random.Random

data class FloatingHeart(
    val id: Long = System.currentTimeMillis() + Random.nextLong(1000),
    val startX: Float = Random.nextFloat(), // 0f to 1f
    val color: Color = listOf(VibeSecondaryPink, VibePrimaryNeon, VibeYellowGold, VibeOrangeHot, Color.Red).random(),
    val sizeDp: Int = Random.nextInt(24, 38)
)

@Composable
fun FloatingHeartsOverlay(
    hearts: List<FloatingHeart>,
    onHeartCompleted: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        hearts.forEach { heart ->
            SingleFloatingHeart(
                heart = heart,
                onComplete = { onHeartCompleted(heart.id) }
            )
        }
    }
}

@Composable
private fun SingleFloatingHeart(
    heart: FloatingHeart,
    onComplete: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val wobble = remember { Random.nextFloat() * 60f - 30f }
    val endWobble = remember { Random.nextFloat() * 100f - 50f }

    LaunchedEffect(heart.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
        onComplete()
    }

    val currentProgress = progress.value
    val alpha = (1f - currentProgress).coerceIn(0f, 1f)
    val scale = (0.5f + currentProgress * 0.9f).coerceAtMost(1.3f)

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = heart.color,
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = ((heart.startX * 700f) + (wobble * (1f - currentProgress)) + (endWobble * currentProgress)).roundToInt(),
                        y = (-currentProgress * 1100f).roundToInt()
                    )
                }
                .size(heart.sizeDp.dp)
                .graphicsLayer {
                    this.alpha = alpha
                    this.scaleX = scale
                    this.scaleY = scale
                    this.rotationZ = wobble * (1f - currentProgress)
                }
        )
    }
}
