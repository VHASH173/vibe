package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeYellowGold

enum class FilterType(
    val title: String,
    val description: String,
    val iconEmoji: String,
    val accentColor: Color
) {
    NONE(
        title = "Natural",
        description = "Sin efectos sobre el stream",
        iconEmoji = "✨",
        accentColor = Color.White
    ),
    CYBER_NEON(
        title = "Cyberpunk",
        description = "Partículas de neón cian & magenta flotantes",
        iconEmoji = "⚡",
        accentColor = VibePrimaryNeon
    ),
    VTUBER_KAWAII(
        title = "VTuber Cat",
        description = "Orejas kawaii holográficas y rubor animado",
        iconEmoji = "🐱",
        accentColor = VibeSecondaryPink
    ),
    AR_TECH_HUD(
        title = "AR Visor",
        description = "Retícula sci-fi militar y escaneo biométrico",
        iconEmoji = "🎯",
        accentColor = VibePrimaryNeon
    ),
    FLAME_AURA(
        title = "Fuego Aura",
        description = "Llamas ascendentes y chispas incandescentes",
        iconEmoji = "🔥",
        accentColor = VibeOrangeHot
    ),
    MATRIX_RAIN(
        title = "Matrix Glitch",
        description = "Lluvia de código digital y scanlines hacker",
        iconEmoji = "💻",
        accentColor = VibeSuccessGreen
    ),
    STARDUST_GLOW(
        title = "Polvo Estelar",
        description = "Destellos dorados y constelaciones cósmicas",
        iconEmoji = "🌟",
        accentColor = VibeYellowGold
    )
}
