package com.example.service

import android.content.Context
import android.util.Log
import com.example.model.Gift
import com.example.model.GiftAnimationType
import com.example.model.GiftModel
import com.example.model.WebmAssetMapper
import com.example.ui.components.GiftEffectManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Evento procesado por el puente de regalos
 */
data class GiftBridgeEvent(
    val giftModel: GiftModel,
    val giftCount: Int,
    val webmUri: String?,
    val hasWebmAnimation: Boolean,
    val iconUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Servicio que al recibir un evento de regalo (giftId: Int, giftCount: Int):
 * 1. Llama a FirebaseGiftRepository.getGiftById(giftId).
 * 2. Usa WebmAssetMapper ya existente para decidir si hay .webm asignado al regalo.
 * 3. Si hay .webm -> llama a GiftEffectManager para disparar la animación + háptico.
 * 4. Si no hay .webm -> solo muestra el icono .webp del campo storageIcon.
 */
class GiftBridgeService(
    private val context: Context,
    private val repository: FirebaseGiftRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    companion object {
        private const val TAG = "GiftBridgeService"
    }

    private val _activeGiftEvent = MutableStateFlow<GiftBridgeEvent?>(null)
    val activeGiftEvent: StateFlow<GiftBridgeEvent?> = _activeGiftEvent.asStateFlow()

    /**
     * Procesa la recepción de un evento de regalo y activa la animación WebM a pantalla completa
     */
    fun processGiftEvent(giftId: Long, giftCount: Int = 1) {
        scope.launch {
            // 1. Obtener datos de Firestore a través del repositorio
            val giftModel = repository.getGiftById(giftId) ?: GiftModel(
                id = giftId,
                name = "Regalo #$giftId",
                diamond = (giftCount * 10).toLong(),
                type = 1L,
                storageIcon = "",
                picture = ""
            )

            // 2. Usar WebmAssetMapper con fallback dinámico por valor/tipo para garantizar WebM siempre
            val animationType = resolveAnimationType(giftModel) ?: if (giftModel.diamond > 100L) {
                GiftAnimationType.GALAXY_DRAGON
            } else if (giftModel.diamond > 20L) {
                GiftAnimationType.HOLO_VISOR
            } else {
                GiftAnimationType.VIBE_ROCKET
            }

            val webmUri = WebmAssetMapper.resolveWebmUriForGift(giftModel)
            val iconUrl = giftModel.storageIcon.ifEmpty { giftModel.picture }

            // 3. Disparar animación en pantalla + retroalimentación háptica
            Log.d(TAG, "Reproduciendo WebM para Regalo ${giftModel.name} ($giftId) -> $webmUri")

            val gift = Gift(
                id = "gift_${giftModel.id}",
                name = giftModel.name,
                emoji = "🎁",
                coinCost = if (giftModel.diamond > 0L) giftModel.diamond.toInt() else 500,
                animationType = animationType,
                description = giftModel.name
            )
            GiftEffectManager.triggerGiftHaptic(context, gift)

            _activeGiftEvent.value = GiftBridgeEvent(
                giftModel = giftModel,
                giftCount = giftCount,
                webmUri = webmUri,
                hasWebmAnimation = true,
                iconUrl = iconUrl
            )
        }
    }

    fun processGiftEvent(giftId: Int, giftCount: Int = 1) = processGiftEvent(giftId.toLong(), giftCount)

    fun clearActiveGiftEvent() {
        _activeGiftEvent.value = null
    }

    private fun resolveAnimationType(giftModel: GiftModel): GiftAnimationType? {
        val nameLower = giftModel.name.lowercase()
        return when {
            giftModel.type == 2L || nameLower.contains("rocket") || nameLower.contains("cohete") || giftModel.id == 5655L ->
                GiftAnimationType.VIBE_ROCKET
            giftModel.type == 3L || nameLower.contains("dragon") || nameLower.contains("dragón") || giftModel.id == 5827L ->
                GiftAnimationType.GALAXY_DRAGON
            nameLower.contains("crown") || nameLower.contains("corona") || giftModel.id == 6001L ->
                GiftAnimationType.GOLDEN_CROWN
            nameLower.contains("rose") || nameLower.contains("rosa") || giftModel.id == 5269L ->
                GiftAnimationType.ROSE_BURST
            nameLower.contains("star") || nameLower.contains("estrella") || nameLower.contains("nova") ->
                GiftAnimationType.STARBURST_NOVA
            nameLower.contains("diamond") || nameLower.contains("diamante") || nameLower.contains("trophy") || giftModel.id == 6120L ->
                GiftAnimationType.DIAMOND_TROPHY
            nameLower.contains("fire") || nameLower.contains("fuego") || giftModel.id == 6380L ->
                GiftAnimationType.AURA_FIRE
            nameLower.contains("cat") || nameLower.contains("gato") || giftModel.id == 6250L ->
                GiftAnimationType.CYBER_CAT
            nameLower.contains("mic") ->
                GiftAnimationType.NEON_MIC
            nameLower.contains("visor") || nameLower.contains("shades") || giftModel.id == 5590L ->
                GiftAnimationType.HOLO_VISOR
            else -> null
        }
    }
}
