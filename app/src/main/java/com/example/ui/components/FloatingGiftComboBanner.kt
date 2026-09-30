package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeYellowGold

/**
 * Cápsula Flotante de Regalo y Contador de Combo en Vivo estilo TikTok Live.
 * Se ubica en el lateral izquierdo sobre el video de la transmisión,
 * mostrando el avatar del usuario donante, su nombre, el regalo y el número "x 1" animado en grande.
 */
@Composable
fun FloatingGiftComboBanner(
    activeGift: ActiveGiftAnimation?,
    modifier: Modifier = Modifier
) {
    if (activeGift == null) return

    val scaleAnim = remember { Animatable(0.4f) }
    val comboScale = remember { Animatable(1.8f) }

    LaunchedEffect(activeGift.timestamp) {
        scaleAnim.snapTo(0.4f)
        scaleAnim.animateTo(1f, animationSpec = tween(220, easing = FastOutSlowInEasing))
        comboScale.snapTo(1.8f)
        comboScale.animateTo(1f, animationSpec = tween(200, easing = FastOutSlowInEasing))
    }

    Row(
        modifier = modifier
            .padding(start = 12.dp)
            .scale(scaleAnim.value),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cápsula izquierda: Avatar + Nombre + "envió [Nombre del Regalo]"
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.75f))
                .border(1.dp, VibeSecondaryPink.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Circular del Donante
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(VibeSecondaryPink, VibeOrangeHot))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👤", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = activeGift.senderName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "💫", fontSize = 11.sp)
                }
                Text(
                    text = "envió ${activeGift.gift.name}",
                    color = VibeYellowGold,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Icono / Emoji del Regalo
        Text(
            text = activeGift.gift.emoji.ifEmpty { "🎁" },
            fontSize = 34.sp,
            modifier = Modifier.shadow(8.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Multiplicador de Combo Dinámico Estilo TikTok: "x 1"
        Text(
            text = "x1",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .scale(comboScale.value)
                .shadow(16.dp)
        )
    }
}
