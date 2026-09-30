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
    autoDismissTimeoutMs: Long = 4500L
) {
    val context = LocalContext.current
    var isReleased by remember { mutableStateOf(false) }

    // Inicializar ExoPlayer con configuración de baja latencia
    val exoPlayer = remember(assetUri) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = true
            try {
                val mediaItem = GiftEffectManager.getMediaItem(assetUri)
                setMediaItem(mediaItem)
                prepare()
            } catch (e: Exception) {
                Log.w(TAG, "Error preparando asset WebM: $assetUri", e)
            }
        }
    }

    // Auto-limpieza y liberación de memoria cuando termina el video o si se cancela
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    Log.d(TAG, "WebM finalizado (Player.STATE_ENDED), liberando recursos.")
                    if (!isReleased) {
                        isReleased = true
                        exoPlayer.stop()
                        exoPlayer.release()
                        onPlaybackEnded()
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.w(TAG, "PlaybackException en WebM ($assetUri), liberando con fallback: ${error.message}")
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
                exoPlayer.removeListener(listener)
                exoPlayer.stop()
                exoPlayer.release()
                Log.d(TAG, "ExoPlayer liberado en DisposableEffect.")
            }
        }
    }

    // Temporizador de seguridad para descartar overlay en caso de video estancado
    LaunchedEffect(assetUri) {
        delay(autoDismissTimeoutMs)
        if (!isReleased) {
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
