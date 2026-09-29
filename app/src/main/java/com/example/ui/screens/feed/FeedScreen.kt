package com.example.ui.screens.feed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ui.components.PublicUserProfile
import com.example.ui.components.ReportBottomSheet
import com.example.ui.components.UserProfileBottomSheet
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.VideoPostEntity
import com.example.model.Gift
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.FloatingHeartsOverlay
import com.example.ui.components.GiftAnimationOverlay
import com.example.ui.components.GiftSendBottomSheet
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeBackground
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeSurface
import com.example.ui.theme.VibeTextPrimary
import com.example.ui.theme.VibeTextSecondary
import com.example.ui.theme.VibeYellowGold
import com.example.viewmodel.VibeStreamViewModel
import kotlin.math.sin

@Composable
fun FeedScreen(
    viewModel: VibeStreamViewModel,
    videoPosts: List<VideoPostEntity>,
    coinsBalance: Int,
    onOpenCoinStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (videoPosts.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(VibeBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Cargando VibeStream Feed...", color = VibeTextSecondary)
        }
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Para ti, 1 = Sugerencias
    var isMuted by remember { mutableStateOf(false) }
    var showGiftSheetForPost by remember { mutableStateOf<VideoPostEntity?>(null) }
    var showCommentsForPost by remember { mutableStateOf<VideoPostEntity?>(null) }
    var showProfileForPost by remember { mutableStateOf<VideoPostEntity?>(null) }
    var userToReport by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    val displayedPosts = remember(selectedTab, videoPosts) {
        if (selectedTab == 1) {
            // "Sugerencias": Recommended videos with high engagement
            videoPosts.sortedByDescending { it.likesCount + it.commentsCount }
        } else {
            // "Para ti": Main personalized feed
            videoPosts
        }
    }
    val pagerState = rememberPagerState(pageCount = { displayedPosts.size })

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1E2132),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
                )
            }
        },
        containerColor = Color.Black
    ) { _ ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("feed_screen")
        ) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val post = displayedPosts[page]
                VideoPageItem(
                    post = post,
                    isMuted = isMuted,
                    onToggleMute = { isMuted = !isMuted },
                    onLikeClicked = {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        viewModel.toggleLikeVideo(post)
                    },
                    onCommentsClicked = { showCommentsForPost = post },
                    onGiftClicked = { showGiftSheetForPost = post },
                    onCreatorClicked = { showProfileForPost = post },
                    onDoubleTapHeart = {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        viewModel.triggerFloatingHeart()
                    }
                )
            }

            // Top Header: LIVE badge | "Para ti" & "Sugerencias" | Sound Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: LIVE indicator pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(VibeSecondaryPink)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Center Tabs: "Para ti" & "Sugerencias"
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 0: "Para ti"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedTab = 0 }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Para ti",
                            color = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = if (selectedTab == 0) 17.sp else 15.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(2.5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (selectedTab == 0) Color.White else Color.Transparent)
                        )
                    }

                    // Tab 1: "Sugerencias"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedTab = 1 }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Sugerencias",
                            color = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = if (selectedTab == 1) 17.sp else 15.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(2.5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (selectedTab == 1) Color.White else Color.Transparent)
                        )
                    }
                }

                // Right: Audio / Mute toggle aligned in top header
                IconButton(
                    onClick = { isMuted = !isMuted },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = "Audio",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Floating Hearts
            val floatingHearts by viewModel.floatingHearts.collectAsStateWithLifecycle()
            FloatingHeartsOverlay(
                hearts = floatingHearts,
                onHeartCompleted = { viewModel.removeHeart(it) }
            )

            // Active Gift Takeover Animation
            val activeGift by viewModel.activeViewerGift.collectAsStateWithLifecycle()
            GiftAnimationOverlay(
                activeGift = activeGift,
                onAnimationFinished = { viewModel.clearActiveGiftAnimation() }
            )

            // Gift Bottom Sheet
            showGiftSheetForPost?.let { post ->
                GiftSendBottomSheet(
                    coinsBalance = coinsBalance,
                    streamerName = post.creatorHandle,
                    onDismiss = { showGiftSheetForPost = null },
                    onSendGift = { gift ->
                        viewModel.sendGift(gift, post.creatorHandle)
                    },
                    onOpenCoinStore = {
                        showGiftSheetForPost = null
                        onOpenCoinStore()
                    },
                    onSendTreasureBox = { coins, winners ->
                        viewModel.sendTreasureBox(coins, winners)
                    }
                )
            }

            // Comments Bottom Sheet
            showCommentsForPost?.let { post ->
                CommentsBottomSheet(
                    commentsCount = post.commentsCount,
                    onDismiss = { showCommentsForPost = null }
                )
            }

            // Other User Profile Bottom Sheet (From Video Feed)
            showProfileForPost?.let { post ->
                UserProfileBottomSheet(
                    userProfile = PublicUserProfile(
                        handle = post.creatorHandle,
                        name = post.creatorName,
                        avatar = post.creatorAvatar,
                        bio = post.caption,
                        followers = "42.8K",
                        likes = "${post.likesCount * 4}"
                    ),
                    onDismiss = { showProfileForPost = null },
                    onRequestReportUser = { handle ->
                        showProfileForPost = null
                        userToReport = handle
                    }
                )
            }

            // Report User / Content Bottom Sheet
            userToReport?.let { targetUser ->
                ReportBottomSheet(
                    targetUserHandle = targetUser,
                    onDismiss = { userToReport = null },
                    onSubmitReport = { reason, shouldBlock, details ->
                        viewModel.reportAndBlockUser(targetUser, reason, shouldBlock, details)
                        scope.launch {
                            val msg = if (shouldBlock) {
                                "Reporte enviado. Usuario $targetUser bloqueado."
                            } else {
                                "Reporte de $targetUser enviado para moderación."
                            }
                            snackbarHostState.showSnackbar(msg)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun VideoPageItem(
    post: VideoPostEntity,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onLikeClicked: () -> Unit,
    onCommentsClicked: () -> Unit,
    onGiftClicked: () -> Unit,
    onCreatorClicked: () -> Unit,
    onDoubleTapHeart: () -> Unit
) {
    // Dynamic looping visual background representing live video content
    val infiniteTransition = rememberInfiniteTransition(label = "video_bg")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "animOffset"
    )
    val vinylRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3500, easing = LinearEasing), RepeatMode.Restart),
        label = "vinyl"
    )

    val gradientColors = when (post.bgTheme) {
        "cyber" -> listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF311042), Color(0xFF090A10))
        "synthwave" -> listOf(Color(0xFF2E1065), Color(0xFF4C0519), Color(0xFF172554), Color(0xFF090A10))
        "anime" -> listOf(Color(0xFF1E293B), Color(0xFF701A75), Color(0xFF1E1B4B), Color(0xFF090A10))
        else -> listOf(Color(0xFF064E3B), Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF090A10))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTapHeart() },
                    onTap = { onToggleMute() }
                )
            }
    ) {
        // Animated Canvas Video Stream Simulator filling 100% of the screen
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = size.height
                )
            )

            // Animated light orbs mimicking video lighting
            val orb1X = size.width * (0.3f + sin(animOffset * 3.14f) * 0.2f)
            val orb1Y = size.height * 0.35f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(VibePrimaryNeon.copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(orb1X, orb1Y),
                    radius = 260f
                ),
                radius = 260f,
                center = Offset(orb1X, orb1Y)
            )

            val orb2X = size.width * 0.7f
            val orb2Y = size.height * (0.6f + sin(animOffset * 6.28f) * 0.1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(VibeSecondaryPink.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(orb2X, orb2Y),
                    radius = 300f
                ),
                radius = 300f,
                center = Offset(orb2X, orb2Y)
            )
        }

        // Overlay vignette for readable text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Right Floating Action Bar (TikTok Style)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 12.dp, bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Creator Avatar with (+) Follow (Click to view Profile & Report)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clickable { onCreatorClicked() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, VibePrimaryNeon, CircleShape)
                        .background(VibeSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = post.creatorAvatar, fontSize = 24.sp)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 6.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(VibeSecondaryPink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Seguir", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }

            // Like Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onLikeClicked() }
            ) {
                val scale = remember { Animatable(1f) }
                LaunchedEffect(post.isLiked) {
                    if (post.isLiked) {
                        scale.animateTo(1.3f, tween(150))
                        scale.animateTo(1f, tween(150))
                    }
                }
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Me gusta",
                    tint = if (post.isLiked) VibeSecondaryPink else Color.White,
                    modifier = Modifier
                        .size(34.dp)
                        .scale(scale.value)
                        .testTag("like_button_${post.id}")
                )
                Text(
                    text = "${post.likesCount}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Comment Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onCommentsClicked() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Comment,
                    contentDescription = "Comentar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("comments_button_${post.id}")
                )
                Text(
                    text = "${post.commentsCount}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Share Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartir",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "${post.sharesCount}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 🎁 GIFTS BUTTON (Prominent Floating Button with 75% Creator Model Highlight)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onGiftClicked() }
                    .testTag("gift_button_${post.id}")
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(VibeSecondaryPink, VibeAccentPurple, VibePrimaryNeon))
                        )
                        .shadow(12.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Enviar Regalo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Regalar",
                    color = VibeYellowGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "75% Creador",
                    color = VibeSuccessGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Spinning Vinyl Music Disc
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(2.dp, Color.DarkGray, CircleShape)
                    .rotate(vinylRotation),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💿", fontSize = 20.sp)
            }
        }

        // Bottom Left Creator Info & Caption
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 80.dp, bottom = 64.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = post.creatorHandle,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onCreatorClicked() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Verified Creator Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VibeSuccessGreen.copy(alpha = 0.2f))
                        .border(1.dp, VibeSuccessGreen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "75% Creador", color = VibeSuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = post.caption,
                color = VibeTextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Music track pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = VibePrimaryNeon,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = post.musicTitle,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
