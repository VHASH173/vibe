package com.example.model

enum class GiftAnimationType {
    ROSE_BURST,
    NEON_SHADES,
    HOLO_VISOR,
    GOLDEN_CROWN,
    VIBE_ROCKET,
    GALAXY_DRAGON,
    STARBURST_NOVA,
    DIAMOND_TROPHY,
    NEON_MIC,
    CYBER_CAT,
    AURA_FIRE
}

data class Gift(
    val id: String,
    val name: String,
    val emoji: String,
    val coinCost: Int,
    val animationType: GiftAnimationType,
    val description: String,
    val tag: String = "Branded"
) {
    // 100 coins = $1.00 USD
    val usdValue: Double get() = coinCost / 100.0

    // VibeStream Fair Commission: 75% to Creator, 25% to Platform
    val creatorShareCoins: Int get() = (coinCost * 0.75).toInt()
    val platformShareCoins: Int get() = coinCost - creatorShareCoins

    val creatorShareUsd: Double get() = usdValue * 0.75
    val platformShareUsd: Double get() = usdValue * 0.25
}

object GiftCatalog {
    val allGifts = listOf(
        Gift(
            id = "gift_rose",
            name = "Rosa Neón",
            emoji = "🌹",
            coinCost = 10,
            animationType = GiftAnimationType.ROSE_BURST,
            description = "Lluvia de pétalos holográficos",
            tag = "Popular"
        ),
        Gift(
            id = "gift_glasses",
            name = "Gafas Cyber",
            emoji = "🕶️",
            coinCost = 50,
            animationType = GiftAnimationType.NEON_SHADES,
            description = "Estilo retro-futurista en pantalla",
            tag = "Vibe"
        ),
        Gift(
            id = "gift_visor",
            name = "Visor Holo",
            emoji = "🥽",
            coinCost = 150,
            animationType = GiftAnimationType.HOLO_VISOR,
            description = "Efecto AR de interfaz sci-fi",
            tag = "AR"
        ),
        Gift(
            id = "gift_fire",
            name = "Fuego Fénix",
            emoji = "🔥",
            coinCost = 300,
            animationType = GiftAnimationType.AURA_FIRE,
            description = "Llamas ardientes de energía pura",
            tag = "Energía"
        ),
        Gift(
            id = "gift_crown",
            name = "Corona de Oro",
            emoji = "👑",
            coinCost = 500,
            animationType = GiftAnimationType.GOLDEN_CROWN,
            description = "Brillo y realeza para el streamer",
            tag = "Realeza"
        ),
        Gift(
            id = "gift_mic",
            name = "Micrófono Neón",
            emoji = "🎙️",
            coinCost = 750,
            animationType = GiftAnimationType.NEON_MIC,
            description = "Ondas sonoras fluorescentes",
            tag = "Música"
        ),
        Gift(
            id = "gift_rocket",
            name = "Cohete Vibe",
            emoji = "🚀",
            coinCost = 1000,
            animationType = GiftAnimationType.VIBE_ROCKET,
            description = "Despegue supersónico con pantalla completa",
            tag = "Épico"
        ),
        Gift(
            id = "gift_cat",
            name = "VTuber Cat",
            emoji = "🐱",
            coinCost = 1500,
            animationType = GiftAnimationType.CYBER_CAT,
            description = "Orejitas holográficas y estrellas",
            tag = "VTuber"
        ),
        Gift(
            id = "gift_diamond",
            name = "Trofeo Diamante",
            emoji = "💎",
            coinCost = 2500,
            animationType = GiftAnimationType.DIAMOND_TROPHY,
            description = "Destellos de diamantes resplandecientes",
            tag = "VIP"
        ),
        Gift(
            id = "gift_dragon",
            name = "Dragón Galáctico",
            emoji = "🐉",
            coinCost = 5000,
            animationType = GiftAnimationType.GALAXY_DRAGON,
            description = "Toma total de pantalla mística y cósmica",
            tag = "Legendario"
        )
    )
}

