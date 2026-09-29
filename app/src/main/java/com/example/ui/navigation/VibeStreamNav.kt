package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.LiveStreamEntity
import com.example.ui.components.CoinStoreDialog
import com.example.ui.components.WithdrawDialog
import com.example.ui.screens.ai.CreatorAiStudioScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.feed.FeedScreen
import com.example.ui.screens.inbox.InboxScreen
import com.example.ui.screens.live.BroadcasterScreen
import com.example.ui.screens.live.ViewerScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeBackground
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSurface
import com.example.ui.theme.VibeSurfaceElevated
import com.example.ui.theme.VibeTextPrimary
import com.example.ui.theme.VibeTextSecondary
import com.example.ui.screens.live.LiveSummaryScreen
import com.example.viewmodel.LiveSummaryData
import com.example.viewmodel.VibeStreamViewModel

sealed class AppDestination {
    object RegisterOnboarding : AppDestination()
    object MainTabs : AppDestination()
    object BroadcasterStudio : AppDestination()
    data class ViewerLive(val stream: LiveStreamEntity) : AppDestination()
    data class LiveSummary(val summary: LiveSummaryData) : AppDestination()
    object CreatorAiStudio : AppDestination()
    object CreatorDashboard : AppDestination()
    object Settings : AppDestination()
}

@Composable
fun VibeStreamNav(
    viewModel: VibeStreamViewModel,
    modifier: Modifier = Modifier
) {
    val consentRecord by viewModel.consentRecord.collectAsStateWithLifecycle()
    var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.MainTabs) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Feed, 1: Descubrir, 3: Inbox, 4: Perfil

    var showCoinStoreDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val videoPosts by viewModel.videoPosts.collectAsStateWithLifecycle()
    val liveStreams by viewModel.liveStreams.collectAsStateWithLifecycle()

    val coinsBalance = wallet?.coinsBalance ?: 0
    val earningsUsd = wallet?.creatorUsdBalance ?: 0.0

    // If not registered yet, display RegisterScreen directly (GDPR compliant onboarding)
    if (!consentRecord.isRegistered || currentDestination is AppDestination.RegisterOnboarding) {
        RegisterScreen(
            onRegisterSuccess = { email, username, termsVersion, timestamp ->
                viewModel.registerUser(email, username, termsVersion, timestamp) {
                    currentDestination = AppDestination.MainTabs
                }
            }
        )
        return
    }

    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Box(modifier = modifier.fillMaxSize().background(VibeBackground)) {
        androidx.compose.animation.AnimatedContent(
            targetState = currentDestination,
            transitionSpec = {
                androidx.compose.animation.slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth / 3 },
                    animationSpec = androidx.compose.animation.core.tween(280, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(280)) togetherWith
                androidx.compose.animation.slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = androidx.compose.animation.core.tween(280, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(280))
            },
            label = "destinationTransition"
        ) { dest ->
            when (dest) {
                is AppDestination.RegisterOnboarding -> {
                    // Handled above
                }

                is AppDestination.MainTabs -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        androidx.compose.animation.AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    androidx.compose.animation.slideInHorizontally(
                                        initialOffsetX = { fullWidth -> fullWidth / 3 },
                                        animationSpec = androidx.compose.animation.core.tween(240, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                                    ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(240)) togetherWith
                                    androidx.compose.animation.slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> -fullWidth / 3 },
                                        animationSpec = androidx.compose.animation.core.tween(240, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                                    ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(240))
                                } else {
                                    androidx.compose.animation.slideInHorizontally(
                                        initialOffsetX = { fullWidth -> -fullWidth / 3 },
                                        animationSpec = androidx.compose.animation.core.tween(240, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                                    ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(240)) togetherWith
                                    androidx.compose.animation.slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> fullWidth / 3 },
                                        animationSpec = androidx.compose.animation.core.tween(240, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                                    ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(240))
                                }
                            },
                            label = "tabTransition"
                        ) { tab ->
                            when (tab) {
                                0 -> FeedScreen(
                                    viewModel = viewModel,
                                    videoPosts = videoPosts,
                                    coinsBalance = coinsBalance,
                                    onOpenCoinStore = { showCoinStoreDialog = true },
                                    modifier = Modifier.fillMaxSize()
                                )

                                1 -> Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 64.dp)
                                ) {
                                    DiscoverScreen(
                                        liveStreams = liveStreams,
                                        onSelectLiveStream = { stream ->
                                            currentDestination = AppDestination.ViewerLive(stream)
                                        }
                                    )
                                }

                                3 -> Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 64.dp)
                                ) {
                                    InboxScreen(
                                        transactions = transactions
                                    )
                                }

                                4 -> Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 64.dp)
                                ) {
                                    ProfileScreen(
                                        wallet = wallet,
                                        transactions = transactions,
                                        viewModel = viewModel,
                                        onOpenCoinStore = { showCoinStoreDialog = true },
                                        onOpenWithdrawDialog = { showWithdrawDialog = true },
                                        onOpenAiStudio = { currentDestination = AppDestination.CreatorAiStudio },
                                        onOpenCreatorDashboard = { currentDestination = AppDestination.CreatorDashboard },
                                        onOpenSettings = { currentDestination = AppDestination.Settings }
                                    )
                                }
                            }
                        }

                        // Floating Bottom Navigation Bar (Overlaid at bottom of screen)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        ) {
                            VibeBottomNavigation(
                                selectedTab = selectedTab,
                                isFeed = selectedTab == 0,
                                onTabSelected = { tab ->
                                    if (tab == 2) {
                                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                        currentDestination = AppDestination.BroadcasterStudio
                                    } else {
                                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                        selectedTab = tab
                                    }
                                }
                            )
                        }
                    }
                }

                is AppDestination.BroadcasterStudio -> {
                    BroadcasterScreen(
                        viewModel = viewModel,
                        onCloseLive = { currentDestination = AppDestination.MainTabs },
                        onNavigateToSummary = { summary ->
                            currentDestination = AppDestination.LiveSummary(summary)
                        }
                    )
                }

                is AppDestination.LiveSummary -> {
                    LiveSummaryScreen(
                        summary = dest.summary,
                        onFinishAndGoHome = { currentDestination = AppDestination.MainTabs },
                        onOpenDashboard = { currentDestination = AppDestination.CreatorDashboard }
                    )
                }

                is AppDestination.ViewerLive -> {
                    ViewerScreen(
                        stream = dest.stream,
                        viewModel = viewModel,
                        coinsBalance = coinsBalance,
                        onClose = { currentDestination = AppDestination.MainTabs },
                        onOpenCoinStore = { showCoinStoreDialog = true }
                    )
                }

                is AppDestination.CreatorAiStudio -> {
                    CreatorAiStudioScreen(
                        viewModel = viewModel,
                        onBack = { currentDestination = AppDestination.MainTabs }
                    )
                }

                is AppDestination.CreatorDashboard -> {
                    com.example.ui.screens.creator.CreatorAnalyticsDashboardScreen(
                        viewModel = viewModel,
                        onBack = { currentDestination = AppDestination.MainTabs },
                        onStartLive = { currentDestination = AppDestination.BroadcasterStudio }
                    )
                }

                is AppDestination.Settings -> {
                    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
                    SettingsScreen(
                        userEmail = consentRecord.userEmail.ifEmpty { "alex@vibestream.live" },
                        termsVersion = consentRecord.termsAcceptedVersion.ifEmpty { "v1.0" },
                        currentThemeMode = themeMode,
                        onThemeModeChanged = { viewModel.setThemeMode(it) },
                        onBack = { currentDestination = AppDestination.MainTabs },
                        onDeleteAccountConfirmed = {
                            viewModel.deleteUserAccountAndData {
                                currentDestination = AppDestination.RegisterOnboarding
                            }
                        }
                    )
                }
            }
        }

        // Global Coin Store Dialog (Simulated Stripe)
        if (showCoinStoreDialog) {
            CoinStoreDialog(
                currentCoins = coinsBalance,
                onDismiss = { showCoinStoreDialog = false },
                onPurchaseSuccess = { pack ->
                    viewModel.buyCoins(pack)
                }
            )
        }

        // Global Withdraw Dialog (75% Creator Cashout)
        if (showWithdrawDialog) {
            WithdrawDialog(
                currentEarningsUsd = earningsUsd,
                onDismiss = { showWithdrawDialog = false },
                onWithdrawSuccess = { amount, dest ->
                    viewModel.withdrawEarnings(amount, dest)
                }
            )
        }
    }
}

@Composable
fun VibeBottomNavigation(
    selectedTab: Int,
    isFeed: Boolean = false,
    onTabSelected: (Int) -> Unit
) {
    val appColors = com.example.ui.theme.LocalAppColors.current
    val backgroundModifier = if (isFeed) {
        Modifier.background(
            Brush.verticalGradient(
                listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.45f),
                    Color.Black.copy(alpha = 0.85f)
                )
            )
        )
    } else {
        Modifier
            .background(appColors.surface.copy(alpha = 0.96f))
            .border(
                width = 0.5.dp,
                color = appColors.border,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(backgroundModifier)
            .navigationBarsPadding()
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .testTag("bottom_nav_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Inicio
            BottomNavItem(
                label = "Inicio",
                iconSelected = Icons.Filled.Home,
                iconUnselected = Icons.Outlined.Home,
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                testTag = "nav_home"
            )

            // Tab 1: Descubrir
            BottomNavItem(
                label = "Descubrir",
                iconSelected = Icons.Filled.Explore,
                iconUnselected = Icons.Outlined.Explore,
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                testTag = "nav_discover"
            )

            // Tab 2: Botón Central (Crear / Transmitir en Vivo)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(VibePrimaryNeon, VibeSecondaryPink, VibeAccentPurple)
                        )
                    )
                    .clickable { onTabSelected(2) }
                    .shadow(14.dp, CircleShape)
                    .testTag("nav_central_live_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Transmitir en Vivo",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Tab 3: Bandeja de Entrada
            BottomNavItem(
                label = "Bandeja",
                iconSelected = Icons.Filled.Mail,
                iconUnselected = Icons.Outlined.Mail,
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) },
                testTag = "nav_inbox"
            )

            // Tab 4: Perfil
            BottomNavItem(
                label = "Perfil",
                iconSelected = Icons.Filled.Person,
                iconUnselected = Icons.Outlined.Person,
                isSelected = selectedTab == 4,
                onClick = { onTabSelected(4) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = if (isSelected) iconSelected else iconUnselected,
            contentDescription = label,
            tint = if (isSelected) VibePrimaryNeon else VibeTextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) VibePrimaryNeon else com.example.ui.theme.LocalAppColors.current.textSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
