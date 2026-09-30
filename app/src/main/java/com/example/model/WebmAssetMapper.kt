package com.example.model

/**
 * Mapeador de eventos de regalos y cofres hacia assets de video WebM transparentes (canal alfa VP8/VP9).
 */
object WebmAssetMapper {
    const val ASSET_BASE = "asset:///webm"

    /**
     * Resuelve la URI del asset WebM correspondiente a cada tipo de regalo.
     * Ejemplo: Vibe Rocket -> energia.webm, Galaxy Dragon -> tormenta_red.webm.
     */
    fun getGiftWebmUri(animationType: GiftAnimationType): String {
        return when (animationType) {
            GiftAnimationType.VIBE_ROCKET -> "$ASSET_BASE/energia.webm"
            GiftAnimationType.GALAXY_DRAGON -> "$ASSET_BASE/tormenta_red.webm"
            GiftAnimationType.GOLDEN_CROWN -> "$ASSET_BASE/corona_dorada.webm"
            GiftAnimationType.ROSE_BURST -> "$ASSET_BASE/lluvia_rosas.webm"
            GiftAnimationType.STARBURST_NOVA -> "$ASSET_BASE/estrella_nova.webm"
            GiftAnimationType.DIAMOND_TROPHY -> "$ASSET_BASE/trofeo_diamante.webm"
            GiftAnimationType.AURA_FIRE -> "$ASSET_BASE/fuego_aura.webm"
            GiftAnimationType.CYBER_CAT -> "$ASSET_BASE/cyber_cat.webm"
            GiftAnimationType.NEON_MIC -> "$ASSET_BASE/microfono_neon.webm"
            GiftAnimationType.NEON_SHADES,
            GiftAnimationType.HOLO_VISOR -> "$ASSET_BASE/holograma_visor.webm"
        }
    }

    /**
     * Resuelve el archivo WebM para la explosión y distribución del cofre del tesoro.
     */
    fun getTreasureBoxWebmUri(): String {
        return "$ASSET_BASE/cofre_magia.webm"
    }
}
