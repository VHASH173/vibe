package com.example.ui.screens.live

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.LiveStreamEntity
import com.example.model.FilterType
import com.example.ui.components.ArCanvasOverlay
import com.example.ui.components.FloatingGiftComboBanner
import com.example.ui.components.FloatingHeartsOverlay
import com.example.ui.components.FloatingLikeButton
import com.example.ui.components.GiftAnimationOverlay
import com.example.ui.components.GiftSendBottomSheet
import com.example.ui.components.LiveChatOverlay
import com.example.ui.components.LiveGiftDockBar
import com.example.ui.components.LiveKitVideoRenderer
import com.example.ui.components.PublicUserProfile
import com.example.ui.components.ReportBottomSheet
import com.example.ui.components.StreamerHeaderOverlay
import com.example.ui.components.TreasureBoxOverlay
import com.example.ui.components.UserProfileBottomSheet
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.viewmodel.VibeStreamViewModel
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun ViewerScreen(
    stream: LiveStreamEntity,
    viewModel: VibeStreamViewModel,
    coinsBalance: Int,
    onClose: () -> Unit,
    onOpenCoinStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewerChat by viewModel.viewerChat.collectAsStateWithLifecycle()
    val floatingHearts by viewModel.floatingHearts.collectAsStateWithLifecycle()
    val activeGift by viewModel.activeViewerGift.collectAsStateWithLifecycle()
    val webRtcStats by viewModel.webRtcStats.collectAsStateWithLifecycle()
    val availableGifts by viewModel.availableGifts.collectAsStateWithLifecycle()

    var showGiftSheet by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(false) }

    // Moderation & Reporting States
    var userToReport by remember { mutableStateOf<String?>(null) }
    var userProfileToView by remember { mutableStateOf<PublicUserProfile?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val initialFilter = remember(stream.activeFilterName) {
        try {
            FilterType.valueOf(stream.activeFilterName)
        } catch (_: Exception) {
            FilterType.CYBER_NEON
        }
    }
    var currentFilter by remember { mutableStateOf(initialFilter) }

    BackHandler { onClose() }

    LaunchedEffect(Unit) {
        viewModel.startSimulatedLiveAudience()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopSimulatedLiveAudience()
        }
    }

    // Dynamic animated stream background
    val infiniteTransition = rememberInfiniteTransition(label = "viewer_stream_anim")
    val streamAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse),
        label = "streamAnim"
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1E2132),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.padding(bottom = 70.dp, start = 16.dp, end = 16.dp)
                )
            }
        },
        containerColor = Color.Black
    ) { _ ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("viewer_screen")
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { viewModel.triggerFloatingHeart() }
                    )
                }
        ) {
            // 1. LiveKit WebRTC VideoRenderer (Edge-to-Edge Full Screen with SCALE_ASPECT_FILL)
            LiveKitVideoRenderer(
                scalingType = livekit.org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FILL,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Animated aura and particle lighting on top of video stream
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF2E1065),
                            Color(0xFF090A10)
                        )
                    )
                )

                // Streamer light aura
                val auraX = size.width / 2f
                val auraY = size.height * (0.42f + sin(streamAnim * 3.14f) * 0.05f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(VibePrimaryNeon.copy(alpha = 0.35f), Color.Transparent),
                        center = Offset(auraX, auraY),
                        radius = 340f
                    ),
                    radius = 340f,
                    center = Offset(auraX, auraY)
                )
            }

            // Streamer Avatar Persona in center
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stream.streamerAvatar, fontSize = 90.sp)
            }

            // 2. Real-time AR / WebGL Canvas Particle Overlay
            ArCanvasOverlay(
                filterType = currentFilter,
                modifier = Modifier.fillMaxSize()
            )

            // 3. Top Corner Streamer Header Overlay (Streamer Name, Viewer Count, Follow & Controls)
            StreamerHeaderOverlay(
                streamerName = stream.streamerName,
                streamerAvatar = stream.streamerAvatar,
                category = stream.category,
                viewerCount = stream.viewerCount,
                isFollowing = isFollowing,
                webRtcStats = webRtcStats,
                onFollowToggle = { isFollowing = !isFollowing },
                onStreamerProfileClicked = {
                    userProfileToView = PublicUserProfile(
                        handle = stream.streamerName,
                        name = stream.streamerName.removePrefix("@").replaceFirstChar { it.uppercase() },
                        avatar = stream.streamerAvatar,
                        bio = "Transmisor oficial en VibeStream • ${stream.category}",
                        followers = "${stream.viewerCount * 12}",
                        likes = "128K"
                    )
                },
                onReportClicked = { userToReport = stream.streamerName },
                onCloseClicked = onClose,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Floating Hearts Layer (drifting hearts river)
            FloatingHeartsOverlay(
                hearts = floatingHearts,
                onHeartCompleted = { viewModel.removeHeart(it) }
            )

            // 5. Floating Treasure Box Overlay (Top Left below streamer header)
            val activeTreasureBox by viewModel.activeTreasureBox.collectAsStateWithLifecycle()
            TreasureBoxOverlay(
                activeBox = activeTreasureBox,
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 92.dp, start = 12.dp)
            )

            // 6. Active Screen-Takeover Gift Animation (TikTok Live Style)
            GiftAnimationOverlay(
                activeGift = activeGift,
                onAnimationFinished = { viewModel.clearActiveGiftAnimation() }
            )

            // 6.1 Floating Gift Combo Pill on Left Side (TikTok Live Style: Avatar + Name + Gift + x1)
            FloatingGiftComboBanner(
                activeGift = activeGift,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(bottom = 70.dp)
            )

            // 7. Bottom Section: Official TikTok Live Chat Overlay & Action Bar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 6.dp)
            ) {
                // Official TikTok Live Chat & Bottom Action Row (Escribe algo... + 👥 + 🌹 + 🎁 + ↗)
                LiveChatOverlay(
                    messages = viewerChat,
                    onSendMessage = { msg -> viewModel.sendChatMessage(msg) },
                    onUserClicked = { handle ->
                        userProfileToView = PublicUserProfile(
                            handle = handle,
                            name = handle.removePrefix("@").replaceFirstChar { it.uppercase() },
                            avatar = "👤",
                            bio = "Espectador activo en VibeStream Live",
                            followers = "480",
                            likes = "1.2K"
                        )
                    },
                    onOpenGiftSheet = { showGiftSheet = true },
                    onSendQuickRose = {
                        val rose = availableGifts.find { it.name.contains("Rosa", ignoreCase = true) } ?: availableGifts.firstOrNull()
                        if (rose != null) {
                            viewModel.sendDynamicGift(rose, stream.streamerName)
                        }
                    },
                    onShareClicked = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Enlace del LIVE copiado al portapapeles")
                        }
                    },
                    onMultiGuestClicked = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Solicitud de conexión enviada al anfitrión")
                        }
                    }
                )
            }

            // Gift Bottom Sheet Modal
            if (showGiftSheet) {
                GiftSendBottomSheet(
                    coinsBalance = coinsBalance,
                    streamerName = stream.streamerName,
                    availableGifts = availableGifts,
                    onDismiss = { showGiftSheet = false },
                    onSendGift = { gift ->
                        viewModel.sendDynamicGift(gift, stream.streamerName)
                    },
                    onOpenCoinStore = {
                        showGiftSheet = false
                        onOpenCoinStore()
                    },
                    onSendTreasureBox = { coins, winners ->
                        viewModel.sendTreasureBox(coins, winners)
                    }
                )
            }

            // Report Bottom Sheet
            userToReport?.let { targetUser ->
                ReportBottomSheet(
                    targetUserHandle = targetUser,
                    onDismiss = { userToReport = null },
                    onSubmitReport = { reason, shouldBlock, details ->
                        viewModel.reportAndBlockUser(targetUser, reason, shouldBlock, details)
                        scope.launch {
                            val msg = if (shouldBlock) {
                                "Reporte enviado. Usuario $targetUser bloqueado y mensajes ocultados."
                            } else {
                                "Reporte enviado para moderación. Gracias por mantener la comunidad segura."
                            }
                            snackbarHostState.showSnackbar(msg)
                        }
                    }
                )
            }

            // Other User Profile Bottom Sheet
            userProfileToView?.let { profile ->
                UserProfileBottomSheet(
                    userProfile = profile,
                    onDismiss = { userProfileToView = null },
                    onRequestReportUser = { handle ->
                        userProfileToView = null
                        userToReport = handle
                    }
                )
            }
        }
    }
}
