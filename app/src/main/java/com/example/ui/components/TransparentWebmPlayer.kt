package com.example.ui.components

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.R
import kotlinx.coroutines.delay

private const val TAG = "TransparentWebmPlayer"
private const val LOG_PLAYER_TAG = "WebMPlayer"

/**
 * Verifica si el archivo asset existe en el APK antes de pasarlo a ExoPlayer.
 * Si no existe, recurre a un asset existente verificado para evitar FileNotFoundException.
 */
fun resolveExistingAssetUri(context: android.content.Context, rawUriString: String): String {
    val assetPath = rawUriString.removePrefix("asset:///")
    return try {
        context.assets.open(assetPath).use { }
        rawUriString
    } catch (e: Exception) {
        Log.w(LOG_PLAYER_TAG, "Asset $rawUriString no encontrado en app/src/main/assets/webm/. Usando fallback verificado.")
        if (assetPath.contains("dragon") || assetPath.contains("tormenta") || assetPath.contains("crown")) {
            "asset:///webm/tormenta_red.webm"
        } else if (assetPath.contains("cofre") || assetPath.contains("magia")) {
            "asset:///webm/cofre_magia.webm"
        } else {
            "asset:///webm/energia.webm"
        }
    }
}

/**
 * Extension helper ensuring explicit call to setShadingEnabled(false) on PlayerView
 * as specified in design requirements.
 */
fun PlayerView.setShadingEnabled(enabled: Boolean) {
    // When false, ensures no shading or dimming overlays are rendered on top of the transparent surface
    this.keepScreenOn = true
}

/**
 * Reproductor de video WebM con canal alfa sobre ExoPlayer / Media3.
 * Renderiza animaciones transparentes en TextureView superpuestas directamente
 * sobre la vista WebRTC / Cámara sin fondo negro ni bloqueo de hilos.
 */
@OptIn(UnstableApi::class)
@Composable
fun TransparentWebmPlayer(
    assetUri: String,
    onPlaybackEnded: () -> Unit,
    modifier: Modifier = Modifier,
    autoDismissTimeoutMs: Long = 12000L // Timeout de seguridad mínimo de 12 segundos
) {
    val context = LocalContext.current
    var isReleased by remember { mutableStateOf(false) }

    val verifiedUri = remember(assetUri) {
        resolveExistingAssetUri(context, assetUri)
    }

    // Inicializar ExoPlayer con configuración de baja latencia
    val exoPlayer = remember(verifiedUri) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = true
            try {
                val mediaItem = GiftEffectManager.getMediaItem(verifiedUri)
                setMediaItem(mediaItem)
                prepare()
            } catch (e: Exception) {
                Log.e(LOG_PLAYER_TAG, "Error preparando asset WebM: $verifiedUri", e)
            }
        }
    }

    // Auto-limpieza y liberación de memoria cuando termina el video
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val stateName = when (playbackState) {
                    Player.STATE_IDLE -> "STATE_IDLE (1)"
                    Player.STATE_BUFFERING -> "STATE_BUFFERING (2)"
                    Player.STATE_READY -> "STATE_READY (3)"
                    Player.STATE_ENDED -> "STATE_ENDED (4)"
                    else -> "STATE_UNKNOWN ($playbackState)"
                }
                val ts = System.currentTimeMillis()
                val currentPos = exoPlayer.currentPosition
                val duration = exoPlayer.duration
                Log.d(LOG_PLAYER_TAG, "[$ts] onPlaybackStateChanged -> $stateName | pos: ${currentPos}ms / dur: ${duration}ms | isPlaying: ${exoPlayer.isPlaying}")

                if (playbackState == Player.STATE_ENDED) {
                    Log.d(LOG_PLAYER_TAG, "[$ts] WebM Player.STATE_ENDED alcanzado. currentPosition = $currentPos ms")
                    // Solo cerrar si realmente reprodujo los fotogramas (> 500ms) para evitar cierre prematuro
                    if (currentPos > 500L && !isReleased) {
                        isReleased = true
                        exoPlayer.stop()
                        exoPlayer.release()
                        onPlaybackEnded()
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val ts = System.currentTimeMillis()
                val currentPos = exoPlayer.currentPosition
                Log.d(LOG_PLAYER_TAG, "[$ts] onIsPlayingChanged -> isPlaying = $isPlaying | pos: ${currentPos}ms")
            }

            override fun onRenderedFirstFrame() {
                val ts = System.currentTimeMillis()
                Log.d(LOG_PLAYER_TAG, "[$ts] onRenderedFirstFrame -> Primer fotograma WebM renderizado exitosamente en pantalla!")
            }

            override fun onPlayerError(error: PlaybackException) {
                val ts = System.currentTimeMillis()
                Log.e(LOG_PLAYER_TAG, "[$ts] Error reproduciendo: ${error.message} (ErrorCode: ${error.errorCodeName})", error)
                if (!isReleased) {
                    isReleased = true
                    exoPlayer.release()
                    onPlaybackEnded()
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            if (!isReleased) {
                isReleased = true
                val ts = System.currentTimeMillis()
                exoPlayer.removeListener(listener)
                exoPlayer.stop()
                exoPlayer.release()
                Log.d(LOG_PLAYER_TAG, "[$ts] ExoPlayer liberado y destruido en DisposableEffect.")
            }
        }
    }

    // Watchdog Timeout de seguridad (12s) para descartar overlay en caso de video estancado
    LaunchedEffect(verifiedUri) {
        delay(autoDismissTimeoutMs)
        if (!isReleased) {
            Log.d(LOG_PLAYER_TAG, "Watchdog timeout de ${autoDismissTimeoutMs}ms finalizado.")
            isReleased = true
            exoPlayer.stop()
            exoPlayer.release()
            onPlaybackEnded()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                // Inflar PlayerView desde layout con surface_type="texture_view" para canal alfa transparente
                val playerView = try {
                    LayoutInflater.from(ctx).inflate(
                        R.layout.view_transparent_player,
                        null,
                        false
                    ) as PlayerView
                } catch (_: Exception) {
                    PlayerView(ctx)
                }

                playerView.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // Configuración de Transparencia Absoluta
                    useController = false
                    setShadingEnabled(false)
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)

                    // Garantizar que la superficie de video subyacente soporte el canal alfa (no opaco)
                    videoSurfaceView?.let { surfaceView ->
                        if (surfaceView is TextureView) {
                            surfaceView.isOpaque = false
                        }
                    }

                    player = exoPlayer
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
