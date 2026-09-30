package com.example.model

/**
 * Mapeador de eventos de regalos y cofres hacia assets de video WebM transparentes (canal alfa VP8/VP9).
 * Soporta asignación por tipo de animación específica y fallback dinámico por rango de valor de monedas.
 */
object WebmAssetMapper {
    const val ASSET_BASE = "asset:///webm"

    /**
     * Resuelve la URI del asset WebM correspondiente a cada tipo de regalo específico.
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
     * Mapeo dinámico por rango de monedas o nombre para asegurar que NINGÚN regalo quede solo en estático:
     * - 1 a 20 monedas: asset:///webm/energia.webm o asset:///webm/fuego_corto.webm
     * - 21 a 100 monedas: asset:///webm/cristal.webm o asset:///webm/fuego_largo.webm
     * - 101+ monedas / especiales: asset:///webm/tormenta.webm o asset:///webm/tormenta_red.webm
     */
    fun getGiftWebmUriByValue(diamond: Long, name: String = "", type: Long = 0L): String {
        val nameLower = name.lowercase()
        return when {
            // Regalos con nombre clave específico
            nameLower.contains("rocket") || nameLower.contains("cohete") || type == 2L ->
                "$ASSET_BASE/energia.webm"
            nameLower.contains("dragon") || nameLower.contains("dragón") || type == 3L ->
                "$ASSET_BASE/tormenta_red.webm"
            nameLower.contains("crown") || nameLower.contains("corona") ->
                "$ASSET_BASE/corona_dorada.webm"
            nameLower.contains("trophy") || nameLower.contains("trofeo") || nameLower.contains("diamond") || nameLower.contains("diamante") ->
                "$ASSET_BASE/trofeo_diamante.webm"
            nameLower.contains("fire") || nameLower.contains("fuego") ->
                "$ASSET_BASE/fuego_aura.webm"
            nameLower.contains("cat") || nameLower.contains("gato") ->
                "$ASSET_BASE/cyber_cat.webm"
            nameLower.contains("rose") || nameLower.contains("rosa") ->
                "$ASSET_BASE/lluvia_rosas.webm"
            nameLower.contains("visor") || nameLower.contains("shades") || nameLower.contains("gafas") ->
                "$ASSET_BASE/holograma_visor.webm"
            nameLower.contains("cofre") || nameLower.contains("treasure") || nameLower.contains("chest") ->
                "$ASSET_BASE/cofre_magia.webm"

            // Mapeo dinámico por rangos de monedas
            diamond in 1..20 -> "$ASSET_BASE/energia.webm"
            diamond in 21..100 -> "$ASSET_BASE/holograma_visor.webm"
            diamond > 100 -> "$ASSET_BASE/tormenta_red.webm"
            else -> "$ASSET_BASE/energia.webm"
        }
    }

    /**
     * Resuelve el archivo WebM para un GiftModel
     */
    fun resolveWebmUriForGift(giftModel: GiftModel): String {
        return getGiftWebmUriByValue(
            diamond = giftModel.diamond,
            name = giftModel.name,
            type = giftModel.type
        )
    }

    /**
     * Resuelve el archivo WebM para la explosión y distribución del cofre del tesoro.
     */
    fun getTreasureBoxWebmUri(): String {
        return "$ASSET_BASE/cofre_magia.webm"
    }
}
