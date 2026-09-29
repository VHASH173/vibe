package com.example.ui.screens.creator

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeCommissionBlue
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeYellowGold
import com.example.viewmodel.VibeStreamViewModel
import kotlinx.coroutines.launch

data class TimeSeriesDataPoint(
    val label: String,
    val peakViewers: Int,
    val giftEarningsUsd: Double,
    val coins: Int,
    val newFollowers: Int
)

data class StickerShare(
    val name: String,
    val emoji: String,
    val percentage: Int,
    val totalEarningsUsd: Double,
    val color: Color
)

data class TimeFilterOption(
    val label: String,
    val compactLabel: String,
    val icon: ImageVector
)

@Composable
fun CreatorAnalyticsDashboardScreen(
    viewModel: VibeStreamViewModel,
    onBack: () -> Unit,
    onStartLive: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val streamerProfile by viewModel.streamerProfile.collectAsStateWithLifecycle()
    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedTimeFrameIndex by remember { mutableIntStateOf(1) } // 0: 24h, 1: 7 días, 2: 30 días, 3: Todo
    val timeFilterOptions = remember {
        listOf(
            TimeFilterOption("24 Horas", "24h", Icons.Default.Schedule),
            TimeFilterOption("7 Días", "7d", Icons.Default.DateRange),
            TimeFilterOption("30 Días", "30d", Icons.Default.CalendarMonth),
            TimeFilterOption("Todo", "Todo", Icons.Default.AllInclusive)
        )
    }

    BackHandler { onBack() }

    // Mock analytical data sets for charts
    val dataPoints7d = remember {
        listOf(
            TimeSeriesDataPoint("Lun", 840, 28.50, 1140, 14),
            TimeSeriesDataPoint("Mar", 1250, 42.00, 1680, 22),
            TimeSeriesDataPoint("Mié", 1100, 36.75, 1470, 18),
            TimeSeriesDataPoint("Jue", 1920, 68.25, 2730, 35),
            TimeSeriesDataPoint("Vie", 2480, 94.50, 3780, 48),
            TimeSeriesDataPoint("Sáb", 2850, 112.00, 4480, 56),
            TimeSeriesDataPoint("Dom", 2150, 81.50, 3260, 39)
        )
    }

    val topStickers = remember {
        listOf(
            StickerShare("Dragón Galáctico", "🐉", 34, 158.00, VibeAccentPurple),
            StickerShare("Cohete Vibe", "🚀", 26, 120.00, VibePrimaryNeon),
            StickerShare("Corona de Oro", "👑", 18, 84.50, VibeYellowGold),
            StickerShare("Rosa Neón", "🌹", 12, 56.00, VibeSecondaryPink),
            StickerShare("Otros Stickers", "🎁", 10, 45.00, VibeCommissionBlue)
        )
    }

    var selectedPointIndex by remember { mutableIntStateOf(4) } // Default to "Vie"

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = appColors.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(appColors.background)
                .testTag("creator_analytics_dashboard_screen")
        ) {
            // Top App Bar with balanced touch targets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(appColors.surfaceElevated)
                            .testTag("dashboard_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = appColors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Panel de Creador",
                            color = appColors.textPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Métricas en vivo de ${streamerProfile.displayName}",
                            color = appColors.textSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Action Row in Top Bar with Arrangement.spacedBy
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Enlace del panel copiado al portapapeles")
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(appColors.surfaceElevated)
                            .testTag("share_analytics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir",
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Reporte de rendimiento exportado a PDF y CSV")
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(appColors.surfaceElevated)
                            .testTag("export_analytics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Exportar informe",
                            tint = VibePrimaryNeon,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ==========================================
                // 1. ADAPTIVE ROW: TIME FILTER BUTTONS
                // (Material3 Icons + Compact Labels with Arrangement.spacedBy)
                // ==========================================
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        timeFilterOptions.forEachIndexed { index, option ->
                            val isSelected = selectedTimeFrameIndex == index
                            Button(
                                onClick = { selectedTimeFrameIndex = index },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) VibePrimaryNeon else appColors.surfaceElevated,
                                    contentColor = if (isSelected) Color.Black else appColors.textPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) VibePrimaryNeon else appColors.border,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .testTag("time_filter_button_$index")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = option.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.Black else appColors.textSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = option.compactLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 2. ADAPTIVE ROW: CREATOR QUICK ACTIONS BAR (#DashboardButtons)
                // (FlowRow with Arrangement.spacedBy(8.dp), Modifier.weight(1f) and fixed aspect container)
                // ==========================================
                item {
                    @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp)
                            .testTag("DashboardButtons"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        // Quick Action: Transmitir
                        Button(
                            onClick = onStartLive,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VibePrimaryNeon,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("dashboard_quick_live_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "En Vivo",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }

                        // Quick Action: Retirar Ganancias
                        Button(
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Billetera 75%: Retiro disponible desde tu perfil")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = appColors.surfaceElevated,
                                contentColor = VibeSuccessGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .border(1.dp, VibeSuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .testTag("dashboard_quick_withdraw_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = VibeSuccessGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Retirar",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibeSuccessGreen,
                                    maxLines = 1
                                )
                            }
                        }

                        // Quick Action: Descargar Datos
                        Button(
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Datos analíticos descargados en formato CSV")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = appColors.surfaceElevated,
                                contentColor = appColors.textPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .border(1.dp, appColors.border, RoundedCornerShape(12.dp))
                                .testTag("dashboard_quick_export_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    tint = appColors.textPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Exportar",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // 4 Summary Metric Cards (Compact 2x2 Grid)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DashboardKpiCard(
                                icon = Icons.Default.Group,
                                iconColor = VibePrimaryNeon,
                                title = "Pico Máximo",
                                value = "2,850",
                                subtext = "+34% vs semana pasada",
                                modifier = Modifier.weight(1f)
                            )
                            DashboardKpiCard(
                                icon = Icons.Default.MonetizationOn,
                                iconColor = VibeSuccessGreen,
                                title = "Ganancias (75%)",
                                value = "$463.50",
                                subtext = "+$112 hoy • 18.5K 🪙",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DashboardKpiCard(
                                icon = Icons.Default.PersonAdd,
                                iconColor = VibeSecondaryPink,
                                title = "Nuevos Seguidores",
                                value = "+232",
                                subtext = "Tasa conversión 9.4%",
                                modifier = Modifier.weight(1f)
                            )
                            DashboardKpiCard(
                                icon = Icons.Default.Timer,
                                iconColor = VibeYellowGold,
                                title = "Tiempo al Aire",
                                value = "16.4h",
                                subtext = "7 transmisiones en vivo",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 1. PEAK VIEWERS TIMELINE CHART (Interactive Curve & Gradient Area)
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = appColors.surfaceElevated),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, appColors.border, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = VibePrimaryNeon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Curva de Audiencia en Vivo",
                                        color = appColors.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                val currentPoint = dataPoints7d[selectedPointIndex]
                                Text(
                                    text = "${currentPoint.label}: ${currentPoint.peakViewers} espectadores",
                                    color = VibePrimaryNeon,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Canvas Area Chart for Peak Viewers
                            ViewersTimelineCanvasChart(
                                dataPoints = dataPoints7d,
                                selectedIndex = selectedPointIndex,
                                onSelectPoint = { selectedPointIndex = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                        }
                    }
                }

                // 2. GIFT EARNINGS & 75/25 REVENUE SPLIT BAR CHART
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = appColors.surfaceElevated),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, appColors.border, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = null,
                                        tint = VibeSuccessGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Ganancias de Regalos (75% Creador)",
                                        color = appColors.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                val currentPoint = dataPoints7d[selectedPointIndex]
                                Text(
                                    text = "$${String.format("%.2f", currentPoint.giftEarningsUsd)} USD",
                                    color = VibeSuccessGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Canvas Column Bar Chart
                            GiftEarningsBarChart(
                                dataPoints = dataPoints7d,
                                selectedIndex = selectedPointIndex,
                                onSelectPoint = { selectedPointIndex = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )
                        }
                    }
                }

                // 3. TOP BRANDED STICKERS DISTRIBUTION
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = appColors.surfaceElevated),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, appColors.border, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Stickers Animados Más Recibidos",
                                color = appColors.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Distribución porcentual por ingresos generados",
                                color = appColors.textSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Stacked Progress Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                            ) {
                                topStickers.forEach { sticker ->
                                    Box(
                                        modifier = Modifier
                                            .weight(sticker.percentage.toFloat())
                                            .fillMaxSize()
                                            .background(sticker.color)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Stickers List
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                topStickers.forEach { sticker ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(appColors.surface)
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = sticker.emoji, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = sticker.name,
                                                    color = appColors.textPrimary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${sticker.percentage}% del total recibido",
                                                    color = appColors.textSecondary,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }

                                        Text(
                                            text = "+$${String.format("%.2f", sticker.totalEarningsUsd)} USD",
                                            color = VibeSuccessGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Spacing
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun DashboardKpiCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surfaceElevated),
        modifier = modifier.border(1.dp, appColors.border, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = appColors.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = appColors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                color = if (subtext.startsWith("+")) VibeSuccessGreen else appColors.textSecondary,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Custom Canvas Chart for Viewers Over Time
 */
@Composable
private fun ViewersTimelineCanvasChart(
    dataPoints: List<TimeSeriesDataPoint>,
    selectedIndex: Int,
    onSelectPoint: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
    }

    Canvas(
        modifier = modifier
            .pointerInput(dataPoints) {
                detectTapGestures { offset ->
                    val segmentWidth = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                    val clickedIndex = (offset.x / segmentWidth).toInt().coerceIn(0, dataPoints.size - 1)
                    onSelectPoint(clickedIndex)
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val maxViewers = (dataPoints.maxOfOrNull { it.peakViewers } ?: 3000) * 1.15f
        val stepX = w / (dataPoints.size - 1).coerceAtLeast(1)

        // Draw horizontal grid lines
        for (i in 1..3) {
            val y = h * (i / 4f)
            drawLine(
                color = Color.White.copy(alpha = 0.08f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        }

        // Build curve path and fill path
        val path = Path()
        val fillPath = Path()

        dataPoints.forEachIndexed { index, pt ->
            val x = index * stepX
            val normalizedY = 1f - (pt.peakViewers / maxViewers)
            val y = (h * 0.1f + normalizedY * (h * 0.8f)) * animProgress.value

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, h)
                fillPath.lineTo(x, y)
            } else {
                val prevX = (index - 1) * stepX
                val prevPt = dataPoints[index - 1]
                val prevNormalizedY = 1f - (prevPt.peakViewers / maxViewers)
                val prevY = (h * 0.1f + prevNormalizedY * (h * 0.8f)) * animProgress.value

                val cx1 = prevX + (x - prevX) / 2f
                val cy1 = prevY
                val cx2 = prevX + (x - prevX) / 2f
                val cy2 = y

                path.cubicTo(cx1, cy1, cx2, cy2, x, y)
                fillPath.cubicTo(cx1, cy1, cx2, cy2, x, y)
            }

            if (index == dataPoints.size - 1) {
                fillPath.lineTo(x, h)
                fillPath.close()
            }
        }

        // Draw gradient area fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    VibePrimaryNeon.copy(alpha = 0.35f),
                    VibeAccentPurple.copy(alpha = 0.15f),
                    Color.Transparent
                )
            )
        )

        // Draw main line
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(listOf(VibePrimaryNeon, VibeSecondaryPink)),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw points
        dataPoints.forEachIndexed { index, pt ->
            val x = index * stepX
            val normalizedY = 1f - (pt.peakViewers / maxViewers)
            val y = (h * 0.1f + normalizedY * (h * 0.8f)) * animProgress.value
            val isSelected = index == selectedIndex

            drawCircle(
                color = if (isSelected) Color.White else VibePrimaryNeon,
                radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                center = Offset(x, y)
            )

            if (isSelected) {
                drawCircle(
                    color = VibePrimaryNeon,
                    radius = 9.dp.toPx(),
                    center = Offset(x, y),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

/**
 * Custom Canvas Bar Chart for Gift Earnings
 */
@Composable
private fun GiftEarningsBarChart(
    dataPoints: List<TimeSeriesDataPoint>,
    selectedIndex: Int,
    onSelectPoint: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(650, easing = FastOutSlowInEasing))
    }

    Canvas(
        modifier = modifier
            .pointerInput(dataPoints) {
                detectTapGestures { offset ->
                    val barSlotWidth = size.width / dataPoints.size
                    val clickedIndex = (offset.x / barSlotWidth).toInt().coerceIn(0, dataPoints.size - 1)
                    onSelectPoint(clickedIndex)
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val maxEarnings = (dataPoints.maxOfOrNull { it.giftEarningsUsd } ?: 150.0) * 1.2
        val barSlotWidth = w / dataPoints.size
        val barWidth = barSlotWidth * 0.52f

        dataPoints.forEachIndexed { index, pt ->
            val x = index * barSlotWidth + (barSlotWidth - barWidth) / 2f
            val barHeight = ((pt.giftEarningsUsd / maxEarnings) * (h * 0.85f)).toFloat() * animProgress.value
            val y = h - barHeight
            val isSelected = index == selectedIndex

            // Background subtle slot
            drawRoundRect(
                color = Color.White.copy(alpha = 0.05f),
                topLeft = Offset(x, 0f),
                size = Size(barWidth, h),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Active Bar (Creator 75% Share)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = if (isSelected) {
                        listOf(VibePrimaryNeon, VibeSuccessGreen)
                    } else {
                        listOf(VibeSuccessGreen.copy(alpha = 0.85f), VibeSuccessGreen.copy(alpha = 0.45f))
                    }
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        }
    }
}
