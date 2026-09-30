package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.media3.common.MediaItem
import com.example.model.Gift
import com.example.model.GiftAnimationType
import com.example.model.GiftCatalog
import com.example.model.WebmAssetMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Gestor de efectos y caché en memoria para WebM transparentes y retroalimentación háptica.
 * Pre-calienta los assets en un pool para evitar caídas de fotogramas (frame drops)
 * la primera vez que un espectador envía un regalo o se abre un cofre.
 */
object GiftEffectManager {
    private const val TAG = "GiftEffectManager"

    // Pool de bytes en memoria de los assets WebM
    private val assetBytePool = ConcurrentHashMap<String, ByteArray>()

    // Pool de instancias pre-construidas de MediaItem
    private val mediaItemPool = ConcurrentHashMap<String, MediaItem>()

    private var isPreloaded = false

    /**
     * Pre-carga todos los assets WebM en un pool en memoria en segundo plano
     */
    fun prewarmAllAssets(context: Context, scope: CoroutineScope) {
        if (isPreloaded) return
        isPreloaded = true

        scope.launch(Dispatchers.IO) {
            val assetList = listOf(
                "webm/energia.webm",
                "webm/tormenta_red.webm",
                "webm/corona_dorada.webm",
                "webm/cofre_magia.webm",
                "webm/lluvia_rosas.webm",
                "webm/estrella_nova.webm",
                "webm/trofeo_diamante.webm",
                "webm/fuego_aura.webm",
                "webm/cyber_cat.webm",
                "webm/microfono_neon.webm",
                "webm/holograma_visor.webm"
            )

            for (path in assetList) {
                try {
                    val fullUriString = "asset:///$path"
                    // Pre-generar y cachear MediaItem
                    mediaItemPool[fullUriString] = MediaItem.fromUri(Uri.parse(fullUriString))

                    // Leer bytes a memoria en el pool si existe el archivo
                    try {
                        context.assets.open(path).use { stream ->
                            val bytes = stream.readBytes()
                            assetBytePool[path] = bytes
                            Log.d(TAG, "Asset WebM pre-cacheado en pool: $path (${bytes.size} bytes)")
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "Registro de asset en pool listo para reproducción: $path")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error en prewarm de asset: $path", e)
                }
            }
        }
    }

    /**
     * Obtiene el MediaItem optimizado desde el pool de caché
     */
    fun getMediaItem(uriString: String): MediaItem {
        return mediaItemPool.getOrPut(uriString) {
            MediaItem.fromUri(Uri.parse(uriString))
        }
    }

    /**
     * Dispara retroalimentación háptica (Vibrator) sutil y rítmica adaptada a la magnitud
     * del regalo premium para sincronizarse con las animaciones de pantalla completa WebM.
     */
    fun triggerGiftHaptic(context: Context, gift: Gift) {
        val isPremium = gift.coinCost >= 500 ||
                gift.animationType == GiftAnimationType.VIBE_ROCKET ||
                gift.animationType == GiftAnimationType.GALAXY_DRAGON ||
                gift.animationType == GiftAnimationType.DIAMOND_TROPHY ||
                gift.animationType == GiftAnimationType.GOLDEN_CROWN ||
                gift.animationType == GiftAnimationType.CYBER_CAT

        if (!isPremium) return

        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when (gift.animationType) {
                        GiftAnimationType.GALAXY_DRAGON -> {
                            // Patrón cósmico épico (vibraciones crecientes sincronizadas con el dragón)
                            val timings = longArrayOf(0, 40, 70, 60, 80, 100, 120, 180)
                            val amplitudes = intArrayOf(0, 80, 0, 140, 0, 200, 0, 255)
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                        }
                        GiftAnimationType.VIBE_ROCKET -> {
                            // Impulso de cohete supersónico (aceleración continua)
                            val timings = longArrayOf(0, 50, 60, 90, 80, 150)
                            val amplitudes = intArrayOf(0, 110, 0, 180, 0, 240)
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                        }
                        GiftAnimationType.DIAMOND_TROPHY,
                        GiftAnimationType.GOLDEN_CROWN -> {
                            // Doble pulso elegante
                            val timings = longArrayOf(0, 60, 90, 80)
                            val amplitudes = intArrayOf(0, 130, 0, 190)
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                        }
                        else -> {
                            // Pulso sutil de impacto
                            vibrator.vibrate(VibrationEffect.createOneShot(80, 160))
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 40, 60, 90), -1)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo emitir vibración háptica: ${e.message}")
        }
    }
}
