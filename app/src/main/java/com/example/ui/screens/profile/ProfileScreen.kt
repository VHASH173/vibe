package com.example.ui.screens.profile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.StreamerProfileData
import com.example.data.local.TransactionEntity
import com.example.data.local.WalletEntity
import com.example.ui.components.PublicUserProfile
import com.example.ui.components.ReportBottomSheet
import com.example.ui.components.StreamerProfileEditorDialog
import com.example.ui.components.UserProfileBottomSheet
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeYellowGold
import com.example.viewmodel.VibeStreamViewModel
import kotlinx.coroutines.launch

data class MockVideoItem(
    val id: Int,
    val views: String,
    val title: String,
    val bgGradient: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    wallet: WalletEntity?,
    transactions: List<TransactionEntity>,
    viewModel: VibeStreamViewModel? = null,
    onOpenCoinStore: () -> Unit,
    onOpenWithdrawDialog: () -> Unit,
    onOpenAiStudio: () -> Unit,
    onOpenCreatorDashboard: () -> Unit = {},
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val coins = wallet?.coinsBalance ?: 0
    val earningsUsd = wallet?.creatorUsdBalance ?: 0.0

    val streamerProfile by viewModel?.streamerProfile?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(StreamerProfileData()) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showCreatorToolsSheet by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    var selectedUserProfile by remember { mutableStateOf<PublicUserProfile?>(null) }
    var userToReport by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val creatorToolsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 12 Mock published videos
    val publishedVideos = remember {
        listOf(
            MockVideoItem(1, "124.5K", "Filtro Cyberpunk en vivo", listOf(Color(0xFF1A1A2E), Color(0xFF16213E))),
            MockVideoItem(2, "89.2K", "Directo acústico de viernes", listOf(Color(0xFF0F3460), Color(0xFF1A1A2E))),
            MockVideoItem(3, "45.1K", "Speedrun con chat interactivo", listOf(Color(0xFF2C003E), Color(0xFF1A002C))),
            MockVideoItem(4, "210.8K", "Reaccionando a regalos épicos", listOf(Color(0xFF1F1D36), Color(0xFF3F3351))),
            MockVideoItem(5, "34.0K", "Música generada con Lyria IA", listOf(Color(0xFF112D4E), Color(0xFF3F72AF))),
            MockVideoItem(6, "67.4K", "Batalla LiveKit en tiempo real", listOf(Color(0xFF2B2024), Color(0xFF4D375D))),
            MockVideoItem(7, "18.3K", "Tutorial de setup de cámara", listOf(Color(0xFF191919), Color(0xFF2D4059))),
            MockVideoItem(8, "95.6K", "Unboxing de micro profesional", listOf(Color(0xFF222831), Color(0xFF393E46))),
            MockVideoItem(9, "512.0K", "Viral: Regalo Dragón Galáctico", listOf(Color(0xFF391057), Color(0xFF6B11A1))),
            MockVideoItem(10, "41.2K", "Detrás de cámaras de stream", listOf(Color(0xFF1B262C), Color(0xFF0F4C81))),
            MockVideoItem(11, "73.9K", "Preguntas y respuestas nocturno", listOf(Color(0xFF252525), Color(0xFF4A403A))),
            MockVideoItem(12, "160.4K", "Especial 20K seguidores", listOf(Color(0xFF1E2022), Color(0xFF52616B)))
        )
    }

    // 12 Mock liked videos
    val likedVideos = remember {
        listOf(
            MockVideoItem(101, "342.1K", "Remix Cyberpunk @daniela_live", listOf(Color(0xFF2B0938), Color(0xFF590995))),
            MockVideoItem(102, "98.7K", "Diseño 3D en vivo @sara_art", listOf(Color(0xFF0C1B33), Color(0xFF03B5AA))),
            MockVideoItem(103, "15.4K", "WebRTC Ultra Latencia @hacker_01", listOf(Color(0xFF1C1936), Color(0xFF46344E))),
            MockVideoItem(104, "880.0K", "Cohete Vibe en directo @kike_fan", listOf(Color(0xFF3D0814), Color(0xFF8C1D40))),
            MockVideoItem(105, "55.2K", "Tips de iluminación gamer", listOf(Color(0xFF1B1A17), Color(0xFFF0A500))),
            MockVideoItem(106, "112.9K", "Clase de canto online", listOf(Color(0xFF2C2E43), Color(0xFF595B83))),
            MockVideoItem(107, "29.4K", "IA generativa en streams", listOf(Color(0xFF1F4068), Color(0xFF162447))),
            MockVideoItem(108, "76.8K", "Vlog de viaje a Tokio", listOf(Color(0xFF282846), Color(0xFF007580))),
            MockVideoItem(109, "640.5K", "Torneo de streamers Vibe", listOf(Color(0xFF400036), Color(0xFF1D0047))),
            MockVideoItem(110, "83.1K", "Setup minimalista blanco", listOf(Color(0xFF222831), Color(0xFF30475E))),
            MockVideoItem(111, "204.3K", "Efecto AR de partículas neón", listOf(Color(0xFF14274E), Color(0xFF394867))),
            MockVideoItem(112, "38.6K", "Podcast en vivo: Monetización", listOf(Color(0xFF2B2E4A), Color(0xFFE84545)))
        )
    }

    val sampleOtherCreators = remember {
        listOf(
            PublicUserProfile("@daniela_live", "Daniela Live", "🌸", "Streamer de Música & Vlogs • Directos todos los días"),
            PublicUserProfile("@hacker_01", "Alex Cyber", "⚡", "VTuber en Unreal / WebRTC • Efectos Neón"),
            PublicUserProfile("@sara_art", "Sara Diseñadora", "🎨", "Arte digital 2D y 3D en vivo con filtros AR"),
            PublicUserProfile("@kike_fan", "Kike Gamer", "🚀", "Speedruns & LiveKit gaming broadcasts")
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = appColors.surfaceElevated,
                    contentColor = appColors.textPrimary,
                    shape = RoundedCornerShape(14.dp)
                )
            }
        },
        containerColor = appColors.background
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 110.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(appColors.background)
                .testTag("profile_screen"),
            horizontalArrangement = Arrangement.spacedBy(1.5.dp),
            verticalArrangement = Arrangement.spacedBy(1.5.dp)
        ) {
            // Header Section spanning all 3 columns
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Top Bar with Settings Icon at top right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(appColors.surfaceElevated)
                                .testTag("profile_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ajustes y Privacidad",
                                tint = appColors.textPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Circular Profile Photo
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .border(
                                3.dp,
                                Brush.linearGradient(listOf(VibePrimaryNeon, VibeSecondaryPink, VibeAccentPurple)),
                                CircleShape
                            )
                            .background(appColors.surfaceElevated)
                            .clickable { showEditProfileDialog = true }
                            .testTag("profile_photo_main"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!streamerProfile.avatarUri.isNullOrBlank()) {
                            AsyncImage(
                                model = streamerProfile.avatarUri,
                                contentDescription = "Foto de perfil del Streamer",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(text = streamerProfile.avatarEmoji, fontSize = 46.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Display Name
                    Text(
                        text = streamerProfile.displayName,
                        color = appColors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Username / Handle & Category Tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = streamerProfile.handle,
                            color = appColors.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(VibePrimaryNeon.copy(alpha = 0.18f))
                                .border(0.8.dp, VibePrimaryNeon.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = streamerProfile.category,
                                color = VibePrimaryNeon,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Streamer Bio
                    Text(
                        text = streamerProfile.bio,
                        color = appColors.textSecondary,
                        fontSize = 12.5.sp,
                        lineHeight = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .clickable { showEditProfileDialog = true }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Row: Seguidores, Siguiendo, Me gusta
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileStat(
                            count = streamerProfile.followersCount,
                            label = "Seguidores",
                            textPrimary = appColors.textPrimary,
                            textSecondary = appColors.textSecondary
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(appColors.border)
                        )
                        ProfileStat(
                            count = streamerProfile.followingCount,
                            label = "Siguiendo",
                            textPrimary = appColors.textPrimary,
                            textSecondary = appColors.textSecondary
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(appColors.border)
                        )
                        ProfileStat(
                            count = streamerProfile.likesCount,
                            label = "Me gusta",
                            textPrimary = appColors.textPrimary,
                            textSecondary = appColors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action Buttons Row: "Editar Perfil", "Herramientas", "Métricas"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Button: Editar Perfil
                        Button(
                            onClick = { showEditProfileDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = appColors.surfaceElevated,
                                contentColor = appColors.textPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .border(1.dp, appColors.border, RoundedCornerShape(12.dp))
                                .testTag("profile_edit_button"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = appColors.textPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Editar",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }

                        // Button: Herramientas de Creador
                        Button(
                            onClick = { showCreatorToolsSheet = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = appColors.surfaceElevated,
                                contentColor = VibePrimaryNeon
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .border(
                                    1.dp,
                                    VibePrimaryNeon.copy(alpha = 0.5f),
                                    RoundedCornerShape(12.dp)
                                )
                                .testTag("creator_tools_button"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = VibePrimaryNeon,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Herramientas",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibePrimaryNeon,
                                maxLines = 1
                            )
                        }

                        // Button: Métricas / Dashboard
                        Button(
                            onClick = onOpenCreatorDashboard,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = appColors.surfaceElevated,
                                contentColor = VibeSuccessGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .border(
                                    1.dp,
                                    VibeSuccessGreen.copy(alpha = 0.5f),
                                    RoundedCornerShape(12.dp)
                                )
                                .testTag("creator_dashboard_button"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = VibeSuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Métricas",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibeSuccessGreen,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // TikTok-style TabRow: Grid (Publicados) & Corazón (Likes)
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent,
                        contentColor = VibePrimaryNeon,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = VibePrimaryNeon,
                                height = 2.dp
                            )
                        },
                        divider = {
                            HorizontalDivider(color = appColors.border, thickness = 0.5.dp)
                        }
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            modifier = Modifier.testTag("profile_tab_grid"),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = "Videos publicados",
                                    tint = if (selectedTabIndex == 0) VibePrimaryNeon else appColors.textSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            modifier = Modifier.testTag("profile_tab_likes"),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = "Videos que te gustan",
                                    tint = if (selectedTabIndex == 1) VibePrimaryNeon else appColors.textSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                }
            }

            // Grid of Videos or Elegant Empty State
            val activeVideos = if (selectedTabIndex == 0) publishedVideos else likedVideos

            if (activeVideos.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 44.dp, horizontal = 24.dp)
                            .testTag("profile_empty_state"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = appColors.textSecondary.copy(alpha = 0.35f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedTabIndex == 0) "Aún no hay videos publicados" else "Aún no tienes videos guardados",
                            color = appColors.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (selectedTabIndex == 0) "Tus transmisiones y clips destacados aparecerán aquí." else "Dale me gusta a videos en el feed para verlos aquí.",
                            color = appColors.textSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(activeVideos.size) { index ->
                    val video = activeVideos[index]
                    VideoGridItem(
                        video = video,
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Reproduciendo video: \"${video.title}\"")
                            }
                        },
                        onLongClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Vista previa de \"${video.title}\" (▷ ${video.views} vistas)")
                            }
                        },
                        modifier = Modifier.testTag("video_grid_item_$index")
                    )
                }
            }

            // Bottom space so content is never covered by the bottom navigation bar
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // ==========================================
        // CREATOR TOOLS MODAL BOTTOM SHEET
        // ==========================================
        if (showCreatorToolsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCreatorToolsSheet = false },
                sheetState = creatorToolsSheetState,
                containerColor = appColors.surface,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .size(width = 38.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(appColors.border)
                    )
                },
                modifier = Modifier.testTag("creator_tools_bottom_sheet")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                ) {
                    // Header of Bottom Sheet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(VibePrimaryNeon.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = VibePrimaryNeon,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Herramientas de Creador",
                                    color = appColors.textPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Monetización Ética & Centro de Control",
                                    color = appColors.textSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { showCreatorToolsSheet = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = appColors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. CREATOR WALLET CARD
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = appColors.surfaceElevated),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.5.dp,
                                        Brush.horizontalGradient(listOf(VibeSuccessGreen, VibePrimaryNeon)),
                                        RoundedCornerShape(20.dp)
                                    )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AccountBalanceWallet,
                                                contentDescription = null,
                                                tint = VibeSuccessGreen,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Billetera de Creador",
                                                color = appColors.textPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(VibeSuccessGreen.copy(alpha = 0.2f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "75% Reparto Justo",
                                                color = VibeSuccessGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Coins balance
                                        Column {
                                            Text(
                                                text = "Tus Monedas",
                                                color = appColors.textSecondary,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = "$coins 🪙",
                                                color = VibeYellowGold,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(appColors.surface)
                                                    .clickable {
                                                        showCreatorToolsSheet = false
                                                        onOpenCoinStore()
                                                    }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                                    .testTag("profile_buy_coins_button")
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = null,
                                                        tint = VibePrimaryNeon,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "Recargar",
                                                        color = VibePrimaryNeon,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(55.dp)
                                                .background(appColors.border)
                                        )

                                        // USD Balance
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "Saldo Creador (75%)",
                                                color = appColors.textSecondary,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = "$${String.format("%.2f", earningsUsd)} USD",
                                                color = VibeSuccessGreen,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(VibeSuccessGreen)
                                                    .clickable {
                                                        showCreatorToolsSheet = false
                                                        onOpenWithdrawDialog()
                                                    }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                                    .testTag("profile_withdraw_button")
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.MonetizationOn,
                                                        contentDescription = null,
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "Retirar",
                                                        color = Color.Black,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Black
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. WHY VIBESTREAM IS MORE PROFITABLE (Comparison)
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = appColors.surfaceElevated),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, appColors.border, RoundedCornerShape(18.dp))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.TrendingUp,
                                                contentDescription = null,
                                                tint = VibePrimaryNeon,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "¿Por qué VibeStream es más rentable?",
                                                color = appColors.textPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Platform Comparison Bars
                                    PlatformFeeBar(
                                        platform = "VibeStream (Nosotros)",
                                        creatorPercentage = 75,
                                        barColor = VibePrimaryNeon,
                                        textColor = appColors.textPrimary,
                                        highlight = true
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    PlatformFeeBar(
                                        platform = "TikTok Live",
                                        creatorPercentage = 50,
                                        barColor = Color(0xFFEF5350),
                                        textColor = appColors.textSecondary,
                                        highlight = false
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    PlatformFeeBar(
                                        platform = "Twitch",
                                        creatorPercentage = 50,
                                        barColor = Color(0xFF9575CD),
                                        textColor = appColors.textSecondary,
                                        highlight = false
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "⚡ Con 1000 monedas recibidas ganas $7.50 USD en VibeStream frente a solo $5.00 USD en otras apps.",
                                        color = appColors.textSecondary,
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        // 2.5 CREATOR ANALYTICS & DASHBOARD SHORTCUT CARD
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = appColors.surfaceElevated),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showCreatorToolsSheet = false
                                        onOpenCreatorDashboard()
                                    }
                                    .border(
                                        1.5.dp,
                                        Brush.horizontalGradient(listOf(VibePrimaryNeon, VibeSuccessGreen)),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .testTag("open_creator_dashboard_sheet_card")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(VibePrimaryNeon, VibeSuccessGreen)
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ShowChart,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Panel de Rendimiento & Métricas",
                                                color = appColors.textPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Gráficos de audiencia en vivo, regalos 75% y seguidores",
                                                color = appColors.textSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Ver ➔",
                                        color = VibeSuccessGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // 3. AI CREATIVE STUDIO SHORTCUT CARD
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = appColors.surfaceElevated),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showCreatorToolsSheet = false
                                        onOpenAiStudio()
                                    }
                                    .border(
                                        1.dp,
                                        VibePrimaryNeon.copy(alpha = 0.35f),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .testTag("open_ai_creator_studio_button")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(VibeAccentPurple, VibePrimaryNeon)
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Estudio Creativo IA",
                                                color = appColors.textPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Música Lyria, Avatares 4K, Veo 3 y Estratega",
                                                color = appColors.textSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Abrir ➔",
                                        color = VibePrimaryNeon,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // 4. COMMUNITY CREATORS (For reporting & moderation)
                        item {
                            Column {
                                Text(
                                    text = "Comunidad de Creadores",
                                    color = appColors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Toca para inspeccionar perfil o reportar conducta",
                                    color = appColors.textSecondary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(sampleOtherCreators) { creator ->
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = appColors.surface),
                                            modifier = Modifier
                                                .width(125.dp)
                                                .border(1.dp, appColors.border, RoundedCornerShape(12.dp))
                                                .clickable {
                                                    showCreatorToolsSheet = false
                                                    selectedUserProfile = creator
                                                }
                                                .testTag("creator_card_${creator.handle.removePrefix("@")}")
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(38.dp)
                                                        .clip(CircleShape)
                                                        .background(appColors.surfaceElevated),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(text = creator.avatar, fontSize = 20.sp)
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = creator.name,
                                                    color = appColors.textPrimary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = creator.handle,
                                                    color = VibePrimaryNeon,
                                                    fontSize = 9.5.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Flag,
                                                        contentDescription = "Reportar",
                                                        tint = VibeSecondaryPink,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "Perfil",
                                                        color = VibeSecondaryPink,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 5. TRANSPARENT TRANSACTION HISTORY (75% Breakdown)
                        item {
                            Text(
                                text = "Historial Transparente de Ganancias (75%)",
                                color = appColors.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (transactions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Aún no tienes movimientos registrados",
                                        color = appColors.textSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        } else {
                            items(transactions) { tx ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = appColors.surface),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, appColors.border, RoundedCornerShape(12.dp))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = tx.description,
                                                color = appColors.textPrimary,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            if (tx.creatorShareUsd > 0) {
                                                Text(
                                                    text = "75% Creador: +$${String.format("%.2f", tx.creatorShareUsd)} | 25% VibeStream: $${String.format("%.2f", tx.platformShareUsd)}",
                                                    color = VibeSuccessGreen,
                                                    fontSize = 9.5.sp
                                                )
                                            }
                                        }

                                        Text(
                                            text = if (tx.type == "WITHDRAWAL") "-$${String.format("%.2f", tx.usdAmount)}" else "+$${String.format("%.2f", if (tx.creatorShareUsd > 0) tx.creatorShareUsd else tx.usdAmount)}",
                                            color = if (tx.type == "WITHDRAWAL") Color.Red else VibeSuccessGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        }

        // ==========================================
        // STREAMER PROFILE EDITOR DIALOG (Firebase & Photo Picker Sync)
        // ==========================================
        if (showEditProfileDialog) {
            StreamerProfileEditorDialog(
                initialProfile = streamerProfile,
                onDismiss = { showEditProfileDialog = false },
                onSaveProfile = { newName, newHandle, newBio, newAvatarUri, newAvatarEmoji, newCategory ->
                    viewModel?.updateStreamerProfile(
                        displayName = newName,
                        handle = newHandle,
                        bio = newBio,
                        avatarUri = newAvatarUri,
                        avatarEmoji = newAvatarEmoji,
                        category = newCategory
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar("Perfil y foto actualizados y sincronizados con Firebase")
                    }
                }
            )
        }

        // Selected User Profile Bottom Sheet (For reporting creators)
        selectedUserProfile?.let { profile ->
            UserProfileBottomSheet(
                userProfile = profile,
                onDismiss = { selectedUserProfile = null },
                onRequestReportUser = { handle ->
                    selectedUserProfile = null
                    userToReport = handle
                }
            )
        }

        // Report Bottom Sheet
        userToReport?.let { targetUser ->
            ReportBottomSheet(
                targetUserHandle = targetUser,
                onDismiss = { userToReport = null },
                onSubmitReport = { reason, shouldBlock, details ->
                    viewModel?.reportAndBlockUser(targetUser, reason, shouldBlock, details)
                    scope.launch {
                        snackbarHostState.showSnackbar("Reporte de $targetUser enviado para moderación.")
                    }
                }
            )
        }
    }
}

/**
 * Single video thumbnail in 3-column TikTok-style grid
 * Features:
 * 1. Bottom-left view count badge with high-contrast format: "▷ 1.2M"
 * 2. Deep black gradient background at bottom ensuring text legibility over any video color
 * 3. Smooth tactile scaling and slight brightness shift on long-press
 */
@Composable
private fun VideoGridItem(
    video: MockVideoItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isLongPressed by remember { mutableStateOf(false) }

    // Subtle scaling animation on long-press (from 1f to 0.94f)
    val scale by animateFloatAsState(
        targetValue = if (isLongPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "thumbnail_scale"
    )

    // Slight brightness shift on long-press (alpha of white highlight overlay)
    val brightnessOverlayAlpha by animateFloatAsState(
        targetValue = if (isLongPressed) 0.22f else 0f,
        animationSpec = tween(180),
        label = "thumbnail_brightness"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f) // 3:4 vertical ratio standard in short-video apps
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(4.dp))
            .background(Brush.verticalGradient(video.bgGradient))
            .pointerInput(video.id) {
                detectTapGestures(
                    onPress = {
                        try {
                            awaitRelease()
                        } finally {
                            isLongPressed = false
                        }
                    },
                    onTap = { onClick() },
                    onLongPress = {
                        isLongPressed = true
                        onLongClick()
                    }
                )
            }
    ) {
        // Faint central play indicator
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.26f),
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Center)
        )

        // Subtle brightness shift overlay on long press
        if (brightnessOverlayAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = brightnessOverlayAlpha))
            )
        }

        // Bottom black gradient shade for high-contrast legibility regardless of video color
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        // View Counter in Bottom-Left Corner (e.g. "▷ 124.5K")
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "▷ ${video.views}",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
private fun ProfileStat(
    count: String,
    label: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            color = textPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            color = textSecondary,
            fontSize = 11.5.sp
        )
    }
}

@Composable
private fun PlatformFeeBar(
    platform: String,
    creatorPercentage: Int,
    barColor: Color,
    textColor: Color,
    highlight: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = platform,
                color = textColor,
                fontSize = 11.5.sp,
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = "$creatorPercentage% para ti",
                color = if (highlight) barColor else textColor,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(creatorPercentage / 100f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
    }
}
