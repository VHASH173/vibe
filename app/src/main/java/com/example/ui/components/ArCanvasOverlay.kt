package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.FilterType
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeYellowGold
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 60 FPS Canvas-based Real-time Video AR Filter & Particle System
 * Superimposed directly on Broadcaster Camera or Viewer Stream
 */
@Composable
fun ArCanvasOverlay(
    filterType: FilterType,
    modifier: Modifier = Modifier
) {
    if (filterType == FilterType.NONE) return

    val infiniteTransition = rememberInfiniteTransition(label = "ar_filter")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Pre-calculate stable particle seeds
    val particleCount = 40
    val seeds = remember {
        List(particleCount) {
            Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 8f + 3f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f

        when (filterType) {
            FilterType.CYBER_NEON -> {
                // Floating neon dust particles
                seeds.forEachIndexed { index, (sx, sy, r) ->
                    val py = ((sy + time * (0.3f + index % 3 * 0.2f)) % 1f) * height
                    val px = (sx * width + sin((time + index) * 3.14f) * 30f).coerceIn(0f, width)
                    val color = if (index % 2 == 0) VibePrimaryNeon.copy(alpha = 0.6f) else VibeSecondaryPink.copy(alpha = 0.6f)
                    drawCircle(color = color, radius = r, center = Offset(px, py))
                }

                // Pulsing Cyber Grid Rings
                val pulseRadius = 140f + sin(time * 6.28f) * 20f
                drawCircle(
                    color = VibePrimaryNeon.copy(alpha = 0.4f),
                    radius = pulseRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 3f)
                )

                // Corner neon brackets
                val cornerLen = 50f
                val margin = 40f
                // Top-left
                drawLine(VibePrimaryNeon, Offset(margin, margin), Offset(margin + cornerLen, margin), strokeWidth = 4f)
                drawLine(VibePrimaryNeon, Offset(margin, margin), Offset(margin, margin + cornerLen), strokeWidth = 4f)
                // Top-right
                drawLine(VibeSecondaryPink, Offset(width - margin, margin), Offset(width - margin - cornerLen, margin), strokeWidth = 4f)
                drawLine(VibeSecondaryPink, Offset(width - margin, margin), Offset(width - margin, margin + cornerLen), strokeWidth = 4f)
                // Bottom-left
                drawLine(VibeSecondaryPink, Offset(margin, height - margin), Offset(margin + cornerLen, height - margin), strokeWidth = 4f)
                drawLine(VibeSecondaryPink, Offset(margin, height - margin), Offset(margin, height - margin - cornerLen), strokeWidth = 4f)
                // Bottom-right
                drawLine(VibePrimaryNeon, Offset(width - margin, height - margin), Offset(width - margin - cornerLen, height - margin), strokeWidth = 4f)
                drawLine(VibePrimaryNeon, Offset(width - margin, height - margin), Offset(width - margin, height - margin - cornerLen), strokeWidth = 4f)
            }

            FilterType.VTUBER_KAWAII -> {
                // Animated Holographic Anime Cat Ears (Head overlay)
                val earTopY = centerY * 0.45f
                val earBaseY = centerY * 0.65f
                val leftEarX = centerX - 120f
                val rightEarX = centerX + 120f
                val earBounce = sin(time * 12.56f) * 8f

                // Left Ear
                val leftPath = Path().apply {
                    moveTo(leftEarX - 60f, earBaseY)
                    lineTo(leftEarX - 30f, earTopY + earBounce)
                    lineTo(leftEarX + 40f, earBaseY)
                    close()
                }
                drawPath(path = leftPath, color = VibeSecondaryPink.copy(alpha = 0.75f))
                drawPath(path = leftPath, color = Color.White, style = Stroke(width = 4f))

                // Left Ear Inner Pink
                val leftInner = Path().apply {
                    moveTo(leftEarX - 45f, earBaseY - 10f)
                    lineTo(leftEarX - 30f, earTopY + earBounce + 25f)
                    lineTo(leftEarX + 20f, earBaseY - 10f)
                    close()
                }
                drawPath(path = leftInner, color = Color.White.copy(alpha = 0.85f))

                // Right Ear
                val rightPath = Path().apply {
                    moveTo(rightEarX - 40f, earBaseY)
                    lineTo(rightEarX + 30f, earTopY + earBounce)
                    lineTo(rightEarX + 60f, earBaseY)
                    close()
                }
                drawPath(path = rightPath, color = VibeSecondaryPink.copy(alpha = 0.75f))
                drawPath(path = rightPath, color = Color.White, style = Stroke(width = 4f))

                // Right Ear Inner Pink
                val rightInner = Path().apply {
                    moveTo(rightEarX - 20f, earBaseY - 10f)
                    lineTo(rightEarX + 30f, earTopY + earBounce + 25f)
                    lineTo(rightEarX + 45f, earBaseY - 10f)
                    close()
                }
                drawPath(path = rightInner, color = Color.White.copy(alpha = 0.85f))

                // Kawaii Blushes (Cheeks)
                val cheekY = centerY * 0.95f
                val blushAlpha = 0.45f + sin(time * 6.28f) * 0.15f
                drawCircle(
                    color = VibeSecondaryPink.copy(alpha = blushAlpha),
                    radius = 28f,
                    center = Offset(centerX - 130f, cheekY)
                )
                drawCircle(
                    color = VibeSecondaryPink.copy(alpha = blushAlpha),
                    radius = 28f,
                    center = Offset(centerX + 130f, cheekY)
                )

                // Floating sparkle stars
                seeds.take(15).forEachIndexed { index, (sx, sy, r) ->
                    val py = ((sy - time * 0.2f + 1f) % 1f) * height
                    val px = sx * width
                    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = r * 0.7f, center = Offset(px, py))
                }
            }

            FilterType.AR_TECH_HUD -> {
                // Futuristic Military / Sci-Fi Targeting System
                val reticleRadius = 180f
                drawCircle(
                    color = VibePrimaryNeon.copy(alpha = 0.5f),
                    radius = reticleRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 2.5f)
                )

                // Rotating targeting arcs
                val angleOffset = time * 360f
                drawArc(
                    color = VibePrimaryNeon,
                    startAngle = angleOffset,
                    sweepAngle = 70f,
                    useCenter = false,
                    topLeft = Offset(centerX - reticleRadius - 20f, centerY - reticleRadius - 20f),
                    size = Size((reticleRadius + 20f) * 2f, (reticleRadius + 20f) * 2f),
                    style = Stroke(width = 5f)
                )
                drawArc(
                    color = VibeSecondaryPink,
                    startAngle = angleOffset + 180f,
                    sweepAngle = 70f,
                    useCenter = false,
                    topLeft = Offset(centerX - reticleRadius - 20f, centerY - reticleRadius - 20f),
                    size = Size((reticleRadius + 20f) * 2f, (reticleRadius + 20f) * 2f),
                    style = Stroke(width = 5f)
                )

                // Crosshairs
                drawLine(VibePrimaryNeon.copy(alpha = 0.7f), Offset(centerX - 40f, centerY), Offset(centerX + 40f, centerY), strokeWidth = 2f)
                drawLine(VibePrimaryNeon.copy(alpha = 0.7f), Offset(centerX, centerY - 40f), Offset(centerX, centerY + 40f), strokeWidth = 2f)

                // Tech scanline sweeping
                val scanlineY = (time * height)
                drawLine(
                    color = VibePrimaryNeon.copy(alpha = 0.35f),
                    start = Offset(0f, scanlineY),
                    end = Offset(width, scanlineY),
                    strokeWidth = 3f
                )
            }

            FilterType.FLAME_AURA -> {
                // Rising Fire Embers
                seeds.forEachIndexed { index, (sx, sy, r) ->
                    val py = ((sy - time * (0.5f + (index % 4) * 0.2f) + 1f) % 1f) * height
                    val wobble = sin((time * 8f + index)) * 25f
                    val px = (sx * width + wobble).coerceIn(0f, width)
                    val color = when (index % 3) {
                        0 -> VibeOrangeHot.copy(alpha = 0.7f)
                        1 -> VibeYellowGold.copy(alpha = 0.8f)
                        else -> VibeSecondaryPink.copy(alpha = 0.6f)
                    }
                    drawCircle(color = color, radius = r * 1.3f, center = Offset(px, py))
                }

                // Flame glow gradient at bottom edge
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, VibeOrangeHot.copy(alpha = 0.35f)),
                        startY = height * 0.7f,
                        endY = height
                    ),
                    topLeft = Offset(0f, height * 0.7f),
                    size = Size(width, height * 0.3f)
                )
            }

            FilterType.MATRIX_RAIN -> {
                // Matrix Digital Glyphs Rain
                val columns = 16
                val colWidth = width / columns
                for (col in 0 until columns) {
                    val speed = 0.4f + (col % 5) * 0.15f
                    val headY = ((time * speed + (col * 0.17f)) % 1f) * height
                    for (drop in 0..6) {
                        val dy = headY - drop * 22f
                        if (dy in 0f..height) {
                            val alpha = (1f - (drop / 7f)).coerceIn(0.1f, 0.9f)
                            val color = if (drop == 0) Color.White else VibeSuccessGreen.copy(alpha = alpha)
                            drawCircle(color = color, radius = 5f, center = Offset(col * colWidth + colWidth / 2f, dy))
                        }
                    }
                }
            }

            FilterType.STARDUST_GLOW -> {
                // Cosmic Stardust & Constellations
                val starPoints = seeds.map { (sx, sy, r) ->
                    val twinkle = 0.4f + sin(time * 10f + sx * 15f) * 0.3f
                    val px = sx * width
                    val py = sy * height
                    drawCircle(
                        color = VibeYellowGold.copy(alpha = twinkle),
                        radius = r,
                        center = Offset(px, py)
                    )
                    Offset(px, py)
                }

                // Connect a few constellations
                for (i in 0 until 8 step 2) {
                    val p1 = starPoints[i]
                    val p2 = starPoints[i + 1]
                    drawLine(
                        color = VibeYellowGold.copy(alpha = 0.25f),
                        start = p1,
                        end = p2,
                        strokeWidth = 1.5f
                    )
                }
            }

            FilterType.NONE -> {}
        }
    }
}
