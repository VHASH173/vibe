package com.example.data.webrtc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class WebRtcStats(
    val isConnected: Boolean = true,
    val connectionState: String = "WebRTC Conectado (LiveKit SFU)",
    val bitrateKbps: Int = 4250,
    val latencyMs: Int = 24,
    val fps: Int = 60,
    val packetLossPercent: Double = 0.05,
    val resolution: String = "1080x1920 @ 60fps",
    val protocol: String = "WebRTC (Opus + H.264)",
    val isPowerSavingActive: Boolean = false
)

/**
 * Manages low-latency WebRTC streaming simulation & controls for Broadcaster & Viewer
 */
class LiveKitStreamManager(
    private val scope: CoroutineScope
) {
    private val _stats = MutableStateFlow(WebRtcStats())
    val stats: StateFlow<WebRtcStats> = _stats.asStateFlow()

    private var telemetryJob: Job? = null
    private var isPowerSaving: Boolean = false

    fun startTelemetry(isBroadcasting: Boolean) {
        telemetryJob?.cancel()
        _stats.value = _stats.value.copy(
            isConnected = true,
            connectionState = "WebRTC Conectado (LiveKit SFU)"
        )
        telemetryJob = scope.launch(Dispatchers.Default) {
            while (true) {
                delay(1500)
                val baseBitrate = if (isPowerSaving) 1400 else if (isBroadcasting) 4500 else 3800
                val jitter = if (isPowerSaving) Random.nextInt(-60, 60) else Random.nextInt(-150, 180)
                val pingJitter = Random.nextInt(-3, 5)
                val currentFps = if (isPowerSaving) 24 else if (Random.nextFloat() > 0.95f) 59 else 60
                val currentRes = if (isPowerSaving) "540x960 @ 24fps (Ahorro)" else "1080x1920 @ 60fps"

                _stats.value = _stats.value.copy(
                    bitrateKbps = (baseBitrate + jitter).coerceIn(800, 6000),
                    latencyMs = (24 + pingJitter).coerceIn(16, 45),
                    fps = currentFps,
                    resolution = currentRes,
                    isPowerSavingActive = isPowerSaving,
                    packetLossPercent = (Random.nextDouble(0.01, 0.08) * 100).toInt() / 100.0
                )
            }
        }
    }

    fun stopTelemetry() {
        telemetryJob?.cancel()
    }

    /**
     * Immediately disconnects LiveKit room and releases streaming resources
     */
    fun disconnectRoom() {
        stopTelemetry()
        _stats.value = _stats.value.copy(
            isConnected = false,
            connectionState = "Desconectado (Sala cerrada)",
            bitrateKbps = 0,
            fps = 0
        )
    }

    /**
     * Enables or disables power-saving mode (e.g. when battery is below 20%)
     */
    fun setPowerSavingMode(enabled: Boolean) {
        isPowerSaving = enabled
        _stats.value = _stats.value.copy(
            isPowerSavingActive = enabled,
            resolution = if (enabled) "540x960 @ 24fps (Ahorro)" else "1080x1920 @ 60fps",
            fps = if (enabled) 24 else 60
        )
    }
}
