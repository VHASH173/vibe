package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.eventbus.LiveEventBus
import com.example.data.eventbus.LiveStreamEvent
import com.example.data.local.ConsentRepository
import com.example.data.local.LegalConsentRecord
import com.example.data.local.LiveStreamEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.VibeDatabase
import com.example.data.local.VideoPostEntity
import com.example.data.local.WalletEntity
import com.example.data.supabase.SupabaseEdgeFunctions
import com.example.data.webrtc.LiveKitStreamManager
import com.example.data.webrtc.WebRtcStats
import com.example.model.ChatMessage
import com.example.model.CoinPackage
import com.example.model.ContentReportRecord
import com.example.model.FilterType
import com.example.model.Gift
import com.example.model.ReportReason
import com.example.service.GeminiService
import com.example.ui.components.ActiveGiftAnimation
import com.example.ui.components.FloatingHeart
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class BroadcasterState(
    val isBroadcasting: Boolean = false,
    val streamTitle: String = "🔴 Mi Primer Live en VibeStream | Comisión 75% Creador",
    val category: String = "VTuber / Gaming",
    val isFrontCamera: Boolean = true,
    val isMuted: Boolean = false,
    val isTorchOn: Boolean = false,
    val activeFilter: FilterType = FilterType.CYBER_NEON,
    val liveViewers: Int = 1840,
    val liveEarningsUsd: Double = 0.0,
    val liveCoinsReceived: Int = 0,
    val activeGiftBanner: ActiveGiftAnimation? = null
)

data class ActiveTreasureBox(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderName: String = "@mi_usuario",
    val totalCoins: Int = 100,
    val maxWinners: Int = 10,
    val remainingSeconds: Int = 60,
    val isReadyToOpen: Boolean = false,
    val isClaimed: Boolean = false
)

data class LiveSummaryData(
    val title: String,
    val durationSeconds: Int,
    val peakViewers: Int,
    val totalCoinsReceived: Int,
    val creatorEarningsUsd: Double,
    val platformEarningsUsd: Double,
    val giftsCount: Int,
    val newFollowersCount: Int
)

data class AiStudioState(
    val isLoading: Boolean = false,
    val generatedMusicResult: String? = null,
    val generatedImageResult: String? = null,
    val generatedVideoResult: String? = null,
    val thinkingStrategyResult: String? = null,
    val selectedImageResolution: String = "2K",
    val selectedVideoAspect: String = "9:16",
    val isMusicClipShort: Boolean = true
)

class VibeStreamViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VibeDatabase.getDatabase(application)
    private val dao = database.vibeDao()
    val streamManager = LiveKitStreamManager(viewModelScope)
    val consentRepository = ConsentRepository(application)

    // Dynamic Theme Mode State
    val themeMode: StateFlow<AppThemeMode> = consentRepository.themeModeFlow.map { modeStr ->
        try {
            AppThemeMode.valueOf(modeStr)
        } catch (_: Exception) {
            AppThemeMode.DARK
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppThemeMode.DARK
    )

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            consentRepository.setThemeMode(mode.name)
        }
    }

    // Moderation: Blocked users & Content reports
    private val _blockedUsers = MutableStateFlow<Set<String>>(emptySet())
    val blockedUsers: StateFlow<Set<String>> = _blockedUsers.asStateFlow()

    private val _reports = MutableStateFlow<List<ContentReportRecord>>(emptyList())
    val reports: StateFlow<List<ContentReportRecord>> = _reports.asStateFlow()

    fun reportAndBlockUser(
        targetHandle: String,
        reason: ReportReason,
        shouldBlock: Boolean,
        details: String
    ) {
        val record = ContentReportRecord(
            targetUserHandle = targetHandle,
            reason = reason,
            additionalDetails = details,
            shouldBlockUser = shouldBlock
        )
        _reports.value = _reports.value + record

        if (shouldBlock) {
            _blockedUsers.value = _blockedUsers.value + targetHandle
            // Immediately filter out all local chat messages from that user
            _viewerChat.value = _viewerChat.value.filterNot { it.senderName == targetHandle }
        }
    }

    fun unblockUser(targetHandle: String) {
        _blockedUsers.value = _blockedUsers.value - targetHandle
    }

    // Power-Saving Mode (< 20% Battery or Explicit Toggle)
    private val _isPowerSavingEnabled = MutableStateFlow(false)
    val isPowerSavingEnabled: StateFlow<Boolean> = _isPowerSavingEnabled.asStateFlow()

    fun setPowerSavingMode(enabled: Boolean) {
        _isPowerSavingEnabled.value = enabled
        streamManager.setPowerSavingMode(enabled)
    }

    fun getBatteryLevel(): Int {
        return try {
            val batteryManager = getApplication<Application>().getSystemService(android.content.Context.BATTERY_SERVICE) as? android.os.BatteryManager
            val level = batteryManager?.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 85
            if (level in 0..100) level else 85
        } catch (_: Exception) {
            85
        }
    }

    fun checkAndApplyLowBatteryMode() {
        val level = getBatteryLevel()
        if (level in 1..19) {
            setPowerSavingMode(true)
        }
    }

    val consentRecord: StateFlow<LegalConsentRecord> = consentRepository.consentFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LegalConsentRecord(
            isRegistered = false,
            termsAcceptedVersion = "",
            termsAcceptedTimestamp = 0L,
            privacyAccepted = false,
            userEmail = "",
            username = ""
        )
    )

    // Streamer Profile StateFlow with Firebase Sync
    val streamerProfile: StateFlow<com.example.data.local.StreamerProfileData> = consentRepository.streamerProfileFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = com.example.data.local.StreamerProfileData()
    )

    fun updateStreamerProfile(
        displayName: String,
        handle: String,
        bio: String,
        avatarUri: String?,
        avatarEmoji: String,
        category: String
    ) {
        viewModelScope.launch {
            consentRepository.updateStreamerProfile(
                displayName = displayName,
                handle = handle,
                bio = bio,
                avatarUri = avatarUri,
                avatarEmoji = avatarEmoji,
                category = category
            )
            // Sync to Firebase infrastructure
            com.example.service.FirebaseProfileService.syncProfileToFirebase(
                com.example.data.local.StreamerProfileData(
                    displayName = displayName,
                    handle = handle,
                    bio = bio,
                    avatarUri = avatarUri,
                    avatarEmoji = avatarEmoji,
                    category = category
                )
            )
        }
    }

    // Room Database StateFlows
    val wallet: StateFlow<WalletEntity?> = dao.getWalletFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WalletEntity()
    )

    val transactions: StateFlow<List<TransactionEntity>> = dao.getTransactionsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val videoPosts: StateFlow<List<VideoPostEntity>> = dao.getVideoPostsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val liveStreams: StateFlow<List<LiveStreamEntity>> = dao.getActiveLiveStreamsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val webRtcStats: StateFlow<WebRtcStats> = streamManager.stats

    // Live Broadcaster State
    private val _broadcasterState = MutableStateFlow(BroadcasterState())
    val broadcasterState: StateFlow<BroadcasterState> = _broadcasterState.asStateFlow()

    // Local Screen Recording State
    private val _isLocalRecording = MutableStateFlow(false)
    val isLocalRecording: StateFlow<Boolean> = _isLocalRecording.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0)
    val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()

    private val _lastSavedRecordingPath = MutableStateFlow<String?>(null)
    val lastSavedRecordingPath: StateFlow<String?> = _lastSavedRecordingPath.asStateFlow()

    private var recordingJob: Job? = null

    fun startLocalRecording() {
        if (_isLocalRecording.value) return
        _isLocalRecording.value = true
        _recordingDurationSeconds.value = 0
        _lastSavedRecordingPath.value = null

        recordingJob?.cancel()
        recordingJob = viewModelScope.launch {
            while (_isLocalRecording.value) {
                delay(1000)
                _recordingDurationSeconds.value += 1
            }
        }
    }

    fun stopLocalRecording(): String {
        _isLocalRecording.value = false
        recordingJob?.cancel()
        
        val timestamp = System.currentTimeMillis()
        val filename = "VibeStream_Live_REC_${timestamp}.mp4"
        val storagePath = "/storage/emulated/0/Movies/VibeStream/$filename"
        _lastSavedRecordingPath.value = storagePath
        return storagePath
    }

    fun toggleLocalRecording(): Boolean {
        return if (_isLocalRecording.value) {
            stopLocalRecording()
            false
        } else {
            startLocalRecording()
            true
        }
    }

    // Live Viewer State
    private val _viewerChat = MutableStateFlow<List<ChatMessage>>(emptyList())
    val viewerChat: StateFlow<List<ChatMessage>> = _viewerChat.asStateFlow()

    private val _floatingHearts = MutableStateFlow<List<FloatingHeart>>(emptyList())
    val floatingHearts: StateFlow<List<FloatingHeart>> = _floatingHearts.asStateFlow()

    private val _activeViewerGift = MutableStateFlow<ActiveGiftAnimation?>(null)
    val activeViewerGift: StateFlow<ActiveGiftAnimation?> = _activeViewerGift.asStateFlow()

    // AI Studio State
    private val _aiState = MutableStateFlow(AiStudioState())
    val aiState: StateFlow<AiStudioState> = _aiState.asStateFlow()

    private var simulatedChatJob: Job? = null

    init {
        streamManager.startTelemetry(isBroadcasting = false)
        listenToLiveEvents()
        initInitialChat()
    }

    private fun listenToLiveEvents() {
        viewModelScope.launch {
            LiveEventBus.events.collect { event ->
                when (event) {
                    is LiveStreamEvent.GiftReceived -> {
                        val activeAnim = ActiveGiftAnimation(
                            gift = event.gift,
                            senderName = event.senderName,
                            creatorShareUsd = event.creatorEarningsUsd
                        )
                        _activeViewerGift.value = activeAnim

                        if (_broadcasterState.value.isBroadcasting) {
                            _broadcasterState.value = _broadcasterState.value.copy(
                                liveEarningsUsd = _broadcasterState.value.liveEarningsUsd + event.creatorEarningsUsd,
                                liveCoinsReceived = _broadcasterState.value.liveCoinsReceived + event.creatorCoins,
                                activeGiftBanner = activeAnim
                            )
                        }

                        // Add gift announcement into chat
                        val giftMsg = ChatMessage(
                            senderName = event.senderName,
                            senderAvatar = "🎁",
                            message = "¡Envió un ${event.gift.name}! (+${event.gift.coinCost} monedas / +$${String.format("%.2f", event.creatorEarningsUsd)} para el creador)",
                            isDonation = true,
                            giftName = event.gift.name,
                            giftCoins = event.gift.coinCost,
                            creatorShareUsd = event.creatorEarningsUsd
                        )
                        _viewerChat.value = listOf(giftMsg) + _viewerChat.value.take(40)
                    }

                    is LiveStreamEvent.NewChatMessage -> {
                        if (!_blockedUsers.value.contains(event.message.senderName)) {
                            _viewerChat.value = listOf(event.message) + _viewerChat.value.take(40)
                        }
                    }

                    is LiveStreamEvent.HeartReaction -> {
                        _floatingHearts.value = _floatingHearts.value + FloatingHeart()
                    }

                    is LiveStreamEvent.ViewerCountChanged -> {
                        _broadcasterState.value = _broadcasterState.value.copy(liveViewers = event.count)
                    }

                    is LiveStreamEvent.FilterChanged -> {
                        // Filter synced
                    }
                }
            }
        }
    }

    private fun initInitialChat() {
        _viewerChat.value = listOf(
            ChatMessage(senderName = "@daniela_live", senderAvatar = "🌸", message = "¡Qué nitidez tiene el stream WebRTC!"),
            ChatMessage(senderName = "@hacker_01", senderAvatar = "⚡", message = "Activando el filtro Cyberpunk se ve increíble."),
            ChatMessage(senderName = "@kike_fan", senderAvatar = "🚀", message = "Apoyando al creador con la comisión justa del 75%!"),
            ChatMessage(senderName = "@sara_art", senderAvatar = "🎨", message = "Saludos desde Bogotá, excelente calidad.")
        )
    }

    fun startSimulatedLiveAudience() {
        simulatedChatJob?.cancel()
        simulatedChatJob = viewModelScope.launch {
            val sampleMessages = listOf(
                Pair("@marcos_vibe", "Vamooos con ese live! 🔥"),
                Pair("@cyber_chick", "Ese filtro AR le queda perfecto! ✨"),
                Pair("@elena_music", "¿Qué música tienes de fondo?"),
                Pair("@stream_king", "75% de comisión para creadores es el futuro 🚀"),
                Pair("@david_code", "VibeStream le gana a TikTok en monetización"),
                Pair("@camila_vt", "Amo las orejitas holográficas 🐱"),
                Pair("@pablo_tech", "Cero delay con LiveKit WebRTC!")
            )
            while (true) {
                delay(Random.nextLong(3000, 7000))
                val (user, text) = sampleMessages.random()
                if (!_blockedUsers.value.contains(user)) {
                    _viewerChat.value = listOf(
                        ChatMessage(
                            senderName = user,
                            senderAvatar = listOf("🎮", "🎧", "✨", "🔥", "🚀", "🦊").random(),
                            message = text
                        )
                    ) + _viewerChat.value.take(40)
                }

                // Occasional floating hearts
                if (Random.nextBoolean()) {
                    _floatingHearts.value = _floatingHearts.value + FloatingHeart()
                }
            }
        }
    }

    fun stopSimulatedLiveAudience() {
        simulatedChatJob?.cancel()
    }

    fun sendChatMessage(text: String, senderName: String = "@yo") {
        if (text.isBlank()) return
        val msg = ChatMessage(
            senderName = senderName,
            senderAvatar = "🌟",
            message = text.trim()
        )
        viewModelScope.launch {
            LiveEventBus.emitChat(msg)
        }
    }

    fun triggerFloatingHeart() {
        viewModelScope.launch {
            _floatingHearts.value = _floatingHearts.value + FloatingHeart()
            LiveEventBus.emitHeart(0xFF00F5D4, Random.nextFloat())
        }
    }

    fun removeHeart(id: Long) {
        _floatingHearts.value = _floatingHearts.value.filterNot { it.id == id }
    }

    fun clearActiveGiftAnimation() {
        _activeViewerGift.value = null
        _broadcasterState.value = _broadcasterState.value.copy(activeGiftBanner = null)
    }

    // ==========================================
    // TREASURE BOX (COFRES DEL TESORO) SYSTEM
    // ==========================================
    private val _activeTreasureBox = MutableStateFlow<ActiveTreasureBox?>(null)
    val activeTreasureBox: StateFlow<ActiveTreasureBox?> = _activeTreasureBox.asStateFlow()

    private var treasureBoxCountdownJob: Job? = null

    fun sendTreasureBox(totalCoins: Int, maxWinners: Int, initialSeconds: Int = 60) {
        viewModelScope.launch {
            val currentWallet = dao.getWalletOnce() ?: WalletEntity()
            if (currentWallet.coinsBalance < totalCoins) return@launch

            // 1. Deduct coins from local Room Database
            val updatedWallet = currentWallet.copy(
                coinsBalance = currentWallet.coinsBalance - totalCoins,
                lifetimeSentCoins = currentWallet.lifetimeSentCoins + totalCoins
            )
            dao.updateWallet(updatedWallet)

            // 2. Insert transaction record
            dao.insertTransaction(
                TransactionEntity(
                    type = "TREASURE_BOX_SENT",
                    description = "Lanzaste Cofre del Tesoro ($totalCoins 🪙 para $maxWinners ganadores)",
                    coinsAmount = totalCoins,
                    usdAmount = totalCoins * 0.01,
                    creatorShareUsd = 0.0,
                    platformShareUsd = 0.0,
                    counterpartName = "Espectadores en Vivo"
                )
            )

            // 3. Emit announcement message in live chat
            val announcement = ChatMessage(
                senderName = "🎁 Sistema",
                senderAvatar = "🧰",
                message = "¡@mi_usuario lanzó un Cofre del Tesoro con $totalCoins monedas para $maxWinners espectadores!"
            )
            LiveEventBus.emitChat(announcement)

            // 4. Start countdown timer coroutine
            val newBox = ActiveTreasureBox(
                totalCoins = totalCoins,
                maxWinners = maxWinners,
                remainingSeconds = initialSeconds,
                isReadyToOpen = false,
                isClaimed = false
            )
            _activeTreasureBox.value = newBox

            treasureBoxCountdownJob?.cancel()
            treasureBoxCountdownJob = viewModelScope.launch {
                var seconds = initialSeconds
                while (seconds > 0) {
                    delay(1000)
                    seconds--
                    _activeTreasureBox.value = _activeTreasureBox.value?.copy(
                        remainingSeconds = seconds,
                        isReadyToOpen = seconds == 0
                    )
                }
            }
        }
    }

    suspend fun claimTreasureBoxReward(): Pair<Boolean, Int> {
        val currentBox = _activeTreasureBox.value ?: return Pair(false, 0)
        if (currentBox.isClaimed) return Pair(false, 0)

        // Mark as claimed locally
        _activeTreasureBox.value = currentBox.copy(isClaimed = true)

        // 70% win chance simulation
        val isWinner = Random.nextDouble() < 0.75
        if (isWinner) {
            val baseShare = (currentBox.totalCoins / currentBox.maxWinners.coerceAtLeast(1)).coerceAtLeast(5)
            val wonCoins = Random.nextInt(baseShare.coerceAtLeast(5), (baseShare * 2).coerceAtLeast(8))

            val currentWallet = dao.getWalletOnce() ?: WalletEntity()
            dao.updateWallet(
                currentWallet.copy(coinsBalance = currentWallet.coinsBalance + wonCoins)
            )

            dao.insertTransaction(
                TransactionEntity(
                    type = "TREASURE_BOX_WON",
                    description = "¡Ganaste $wonCoins monedas del Cofre del Tesoro!",
                    coinsAmount = wonCoins,
                    usdAmount = wonCoins * 0.01,
                    creatorShareUsd = 0.0,
                    platformShareUsd = 0.0,
                    counterpartName = currentBox.senderName
                )
            )

            return Pair(true, wonCoins)
        } else {
            return Pair(false, 0)
        }
    }

    fun dismissTreasureBox() {
        treasureBoxCountdownJob?.cancel()
        _activeTreasureBox.value = null
    }

    /**
     * Send Gift using the Fair 75/25 Split Model
     */
    fun sendGift(gift: Gift, streamerName: String) {
        viewModelScope.launch {
            val currentWallet = dao.getWalletOnce() ?: WalletEntity()
            if (currentWallet.coinsBalance < gift.coinCost) return@launch

            // 1. Calculate Split
            val split = SupabaseEdgeFunctions.calculateSplit(gift)

            // 2. Deduct user coins & record transaction
            val updatedWallet = currentWallet.copy(
                coinsBalance = currentWallet.coinsBalance - gift.coinCost,
                lifetimeSentCoins = currentWallet.lifetimeSentCoins + gift.coinCost
            )
            dao.updateWallet(updatedWallet)

            dao.insertTransaction(
                TransactionEntity(
                    type = "GIFT_SENT",
                    description = "Enviaste ${gift.name} a $streamerName",
                    coinsAmount = gift.coinCost,
                    usdAmount = gift.usdValue,
                    creatorShareUsd = split.creatorUsd,
                    platformShareUsd = split.platformUsd,
                    counterpartName = streamerName
                )
            )

            // 3. Emit real-time WebSocket event
            LiveEventBus.emitGift(gift, senderName = "@mi_usuario")
        }
    }

    /**
     * Buy Coins with simulated Stripe Checkout
     */
    fun buyCoins(pack: CoinPackage) {
        viewModelScope.launch {
            val currentWallet = dao.getWalletOnce() ?: WalletEntity()
            val updatedWallet = currentWallet.copy(
                coinsBalance = currentWallet.coinsBalance + pack.totalCoins
            )
            dao.updateWallet(updatedWallet)

            dao.insertTransaction(
                TransactionEntity(
                    type = "COIN_PURCHASE",
                    description = "Compra de paquete ${pack.coins} (+${pack.bonusCoins}) Monedas",
                    coinsAmount = pack.totalCoins,
                    usdAmount = pack.usdPrice,
                    creatorShareUsd = 0.0,
                    platformShareUsd = 0.0,
                    counterpartName = "Stripe / Google Play"
                )
            )
        }
    }

    /**
     * Withdraw Creator USD Earnings
     */
    fun withdrawEarnings(amountUsd: Double, destination: String) {
        viewModelScope.launch {
            val currentWallet = dao.getWalletOnce() ?: WalletEntity()
            if (currentWallet.creatorUsdBalance < amountUsd) return@launch

            val updatedWallet = currentWallet.copy(
                creatorUsdBalance = currentWallet.creatorUsdBalance - amountUsd
            )
            dao.updateWallet(updatedWallet)

            dao.insertTransaction(
                TransactionEntity(
                    type = "WITHDRAWAL",
                    description = "Retiro a $destination (Transferencia directa)",
                    coinsAmount = 0,
                    usdAmount = amountUsd,
                    creatorShareUsd = amountUsd,
                    platformShareUsd = 0.0,
                    counterpartName = destination
                )
            )
        }
    }

    /**
     * Toggle Like on Video Post
     */
    fun toggleLikeVideo(post: VideoPostEntity) {
        viewModelScope.launch {
            val updated = post.copy(
                isLiked = !post.isLiked,
                likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
            )
            dao.updateVideoPost(updated)
            if (updated.isLiked) {
                triggerFloatingHeart()
            }
        }
    }

    // Broadcaster Controls
    fun startBroadcasting(title: String, category: String) {
        _broadcasterState.value = _broadcasterState.value.copy(
            isBroadcasting = true,
            streamTitle = title,
            category = category,
            liveEarningsUsd = 0.0,
            liveCoinsReceived = 0
        )
        streamManager.startTelemetry(isBroadcasting = true)
        startSimulatedLiveAudience()
    }

    private val _lastLiveSummary = MutableStateFlow<LiveSummaryData?>(null)
    val lastLiveSummary: StateFlow<LiveSummaryData?> = _lastLiveSummary.asStateFlow()

    fun stopBroadcasting() {
        _broadcasterState.value = _broadcasterState.value.copy(isBroadcasting = false)
        streamManager.disconnectRoom()
        stopSimulatedLiveAudience()
    }

    /**
     * Completes broadcast, disconnects LiveKit room, persists 75% earnings to Room wallet,
     * and compiles the LiveSummaryData for the LiveSummaryScreen.
     */
    fun finishBroadcasting(durationSeconds: Int): LiveSummaryData {
        val current = _broadcasterState.value
        val creatorUsd = current.liveEarningsUsd
        val platformUsd = if (creatorUsd > 0) (creatorUsd / 0.75) * 0.25 else 0.0

        val summary = LiveSummaryData(
            title = current.streamTitle,
            durationSeconds = durationSeconds,
            peakViewers = current.liveViewers,
            totalCoinsReceived = current.liveCoinsReceived,
            creatorEarningsUsd = creatorUsd,
            platformEarningsUsd = platformUsd,
            giftsCount = if (current.liveCoinsReceived > 0) (current.liveCoinsReceived / 25).coerceAtLeast(1) else 0,
            newFollowersCount = Random.nextInt(18, 54)
        )
        _lastLiveSummary.value = summary

        // Persist earnings into creator wallet in database (75% share)
        if (creatorUsd > 0 || current.liveCoinsReceived > 0) {
            viewModelScope.launch {
                val currentWallet = dao.getWalletOnce() ?: WalletEntity()
                dao.updateWallet(
                    currentWallet.copy(
                        creatorUsdBalance = currentWallet.creatorUsdBalance + creatorUsd,
                        lifetimeReceivedUsd = currentWallet.lifetimeReceivedUsd + creatorUsd
                    )
                )
                dao.insertTransaction(
                    TransactionEntity(
                        type = "LIVE_BROADCAST_PAYOUT",
                        description = "Ganancias de Transmisión en Vivo: ${current.streamTitle}",
                        coinsAmount = current.liveCoinsReceived,
                        usdAmount = creatorUsd + platformUsd,
                        creatorShareUsd = creatorUsd,
                        platformShareUsd = platformUsd,
                        counterpartName = "VibeStream Live Payout (75%)"
                    )
                )
            }
        }

        stopBroadcasting()
        return summary
    }

    fun switchCamera() {
        _broadcasterState.value = _broadcasterState.value.copy(
            isFrontCamera = !_broadcasterState.value.isFrontCamera
        )
    }

    fun toggleMute() {
        _broadcasterState.value = _broadcasterState.value.copy(
            isMuted = !_broadcasterState.value.isMuted
        )
    }

    fun toggleTorch() {
        _broadcasterState.value = _broadcasterState.value.copy(
            isTorchOn = !_broadcasterState.value.isTorchOn
        )
    }

    fun setFilter(filter: FilterType) {
        _broadcasterState.value = _broadcasterState.value.copy(activeFilter = filter)
    }

    // Creative AI Studio
    fun setAiImageResolution(res: String) {
        _aiState.value = _aiState.value.copy(selectedImageResolution = res)
    }

    fun setAiVideoAspect(aspect: String) {
        _aiState.value = _aiState.value.copy(selectedVideoAspect = aspect)
    }

    fun setMusicClipType(isShort: Boolean) {
        _aiState.value = _aiState.value.copy(isMusicClipShort = isShort)
    }

    fun generateAiMusic(prompt: String) {
        viewModelScope.launch {
            _aiState.value = _aiState.value.copy(isLoading = true, generatedMusicResult = null)
            val result = GeminiService.generateStreamBgmMusic(prompt, _aiState.value.isMusicClipShort)
            _aiState.value = _aiState.value.copy(isLoading = false, generatedMusicResult = result)
        }
    }

    fun generateAiAvatar(prompt: String) {
        viewModelScope.launch {
            _aiState.value = _aiState.value.copy(isLoading = true, generatedImageResult = null)
            val result = GeminiService.generateStreamAvatarImage(prompt, _aiState.value.selectedImageResolution)
            _aiState.value = _aiState.value.copy(isLoading = false, generatedImageResult = result)
        }
    }

    fun generateAiVideo(prompt: String) {
        viewModelScope.launch {
            _aiState.value = _aiState.value.copy(isLoading = true, generatedVideoResult = null)
            val result = GeminiService.generateStreamIntroVideo(prompt, _aiState.value.selectedVideoAspect)
            _aiState.value = _aiState.value.copy(isLoading = false, generatedVideoResult = result)
        }
    }

    fun requestHighThinkingStrategy(prompt: String) {
        viewModelScope.launch {
            _aiState.value = _aiState.value.copy(isLoading = true, thinkingStrategyResult = null)
            val result = GeminiService.getStreamerStrategyWithHighThinking(prompt)
            _aiState.value = _aiState.value.copy(isLoading = false, thinkingStrategyResult = result)
        }
    }

    /**
     * Registra al usuario y guarda auditoría legal en DataStore y Supabase
     */
    fun registerUser(
        email: String,
        username: String,
        termsVersion: String,
        timestamp: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            consentRepository.saveRegistrationConsent(email, username, termsVersion, timestamp)
            SupabaseEdgeFunctions.recordRegistrationAudit(email, username, termsVersion, timestamp)

            // Ensure initial demo wallet is populated
            val existing = dao.getWalletOnce()
            if (existing == null) {
                dao.insertWallet(
                    WalletEntity(
                        id = "primary_user_wallet",
                        coinsBalance = 1500,
                        creatorUsdBalance = 245.50,
                        lifetimeReceivedUsd = 480.00,
                        lifetimeSentCoins = 3200
                    )
                )
            }
            onSuccess()
        }
    }

    /**
     * Derecho al Olvido (GDPR/CCPA):
     * 1. Llama al endpoint DELETE /api/users/me del backend
     * 2. Limpia DataStore local
     * 3. Limpia todas las tablas de Room
     * 4. Borra la memoria caché de la app (context.cacheDir.deleteRecursively())
     * 5. Redirige a pantalla de bienvenida/registro
     */
    fun deleteUserAccountAndData(onCompleted: () -> Unit) {
        viewModelScope.launch {
            val current = consentRepository.getConsentOnce()
            SupabaseEdgeFunctions.deleteUserAccountBackend(current.userEmail)

            consentRepository.clearConsentData()
            dao.clearWallet()
            dao.clearTransactions()
            dao.clearVideoPosts()
            dao.clearLiveStreams()

            try {
                getApplication<Application>().cacheDir.deleteRecursively()
            } catch (_: Exception) {}

            onCompleted()
        }
    }
}
