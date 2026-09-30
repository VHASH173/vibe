package com.example.ui.screens.live

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.FilterType
import com.example.ui.components.ArCanvasOverlay
import com.example.ui.components.FloatingGiftComboBanner
import com.example.ui.components.GiftAnimationOverlay
import com.example.ui.components.LiveKitQualityPill
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeCommissionBlue
import com.example.ui.theme.VibeOrangeHot
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeSurface
import com.example.ui.theme.VibeSurfaceElevated
import com.example.ui.theme.VibeSurfaceVariant
import com.example.ui.theme.VibeTextPrimary
import com.example.ui.theme.VibeTextSecondary
import com.example.ui.theme.VibeYellowGold
import com.example.viewmodel.LiveSummaryData
import com.example.viewmodel.VibeStreamViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BroadcasterScreen(
    viewModel: VibeStreamViewModel,
    onCloseLive: () -> Unit,
    onNavigateToSummary: (LiveSummaryData) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val broadcasterState by viewModel.broadcasterState.collectAsStateWithLifecycle()
    val webRtcStats by viewModel.webRtcStats.collectAsStateWithLifecycle()
    val viewerChat by viewModel.viewerChat.collectAsStateWithLifecycle()
    val activeGift by viewModel.activeViewerGift.collectAsStateWithLifecycle()
    val isLocalRecording by viewModel.isLocalRecording.collectAsStateWithLifecycle()
    val recordingSeconds by viewModel.recordingDurationSeconds.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showFilterDrawer by remember { mutableStateOf(false) }
    var streamSeconds by remember { mutableIntStateOf(0) }
    var showEndStreamConfirmation by remember { mutableStateOf(false) }

    // Runtime Permissions check (No request on cold startup)
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasAudioPermission
    }

    // Intercept back button to trigger confirmation dialog safely
    BackHandler {
        showEndStreamConfirmation = true
    }

    LaunchedEffect(hasCameraPermission, hasAudioPermission) {
        if (hasCameraPermission && hasAudioPermission) {
            viewModel.startBroadcasting(
                title = "🔴 Transmisión VibeStream | LiveKit WebRTC + Filtros AR",
                category = "VTuber / Gaming"
            )
            while (true) {
                delay(1000)
                streamSeconds++
            }
        }
    }

    // If permissions not yet granted, render Permission Explanation Gate
    if (!hasCameraPermission || !hasAudioPermission) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VibeSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VibePrimaryNeon, RoundedCornerShape(24.dp))
                    .testTag("broadcaster_permission_gate")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(VibePrimaryNeon.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎥", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Permisos de Cámara y Audio",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "VibeStream requiere acceso a tu cámara y micrófono únicamente para transmitir tu señal en vivo y aplicar los filtros AR de partículas a tu rostro.",
                        color = VibeTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.RECORD_AUDIO
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VibePrimaryNeon),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("request_live_permissions_button")
                    ) {
                        Text(text = "Permitir y Comenzar Live", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onCloseLive,
                        colors = ButtonDefaults.buttonColors(containerColor = VibeSurfaceElevated),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Cancelar", color = VibeTextSecondary)
                    }
                }
            }
        }
        return
    }

    Scaffold(
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
                .testTag("broadcaster_screen")
        ) {
        // 1. CameraX Preview Layer with Full-Screen Crop (No black bars)
        CameraPreviewLayer(
            isFrontCamera = broadcasterState.isFrontCamera,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Real-time AR / WebGL Canvas Overlay
        ArCanvasOverlay(
            filterType = broadcasterState.activeFilter,
            modifier = Modifier.fillMaxSize()
        )

        // 2.1 Full-screen Cinematic Gift Animation Overlay
        GiftAnimationOverlay(
            activeGift = activeGift,
            onAnimationFinished = { viewModel.clearActiveGiftAnimation() }
        )

        // 2.2 Floating Gift Combo Pill on Left Side
        FloatingGiftComboBanner(
            activeGift = activeGift,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(bottom = 70.dp)
        )

        // 3. Top HUD: Live Badge, Timer, Local REC Button, Viewers, LiveKit WebRTC Stats, Earnings, and Safe End Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 12.dp, end = 12.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live & Time counter
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VibeSecondaryPink)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "LIVE", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    val mins = streamSeconds / 60
                    val secs = streamSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        color = Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Local Screen Recording Toggle Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isLocalRecording) Color.Red.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.65f))
                        .border(
                            1.dp,
                            if (isLocalRecording) Color.White else Color.Red.copy(alpha = 0.6f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            val started = viewModel.toggleLocalRecording()
                            scope.launch {
                                if (started) {
                                    snackbarHostState.showSnackbar("🔴 Grabación local de pantalla iniciada en almacenamiento")
                                } else {
                                    val path = viewModel.lastSavedRecordingPath.value ?: "Almacenamiento/Movies/VibeStream"
                                    snackbarHostState.showSnackbar("💾 Grabación guardada en: $path")
                                }
                            }
                        }
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                        .testTag("toggle_local_recording_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isLocalRecording) Icons.Default.FiberManualRecord else Icons.Default.RadioButtonChecked,
                        contentDescription = "Grabar en dispositivo",
                        tint = if (isLocalRecording) Color.White else Color.Red,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (isLocalRecording) {
                        val recMins = recordingSeconds / 60
                        val recSecs = recordingSeconds % 60
                        Text(
                            text = String.format("REC %02d:%02d", recMins, recSecs),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    } else {
                        Text(
                            text = "Grabar",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Viewers Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = VibePrimaryNeon, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${broadcasterState.liveViewers}", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                // LiveKit WebRTC Quality Pill
                LiveKitQualityPill(stats = webRtcStats)

                // Cierre Seguro de Transmisión (End Live Button)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xD9DC2626))
                        .clickable { showEndStreamConfirmation = true }
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                        .testTag("end_stream_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Finalizar",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Salir",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Real-Time Earnings Pill (75% Commission Advantage Highlight)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .border(1.dp, VibeSuccessGreen, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💰 Ganancia Live (75% Creador): ", color = Color.White, fontSize = 11.sp)
                Text(
                    text = "+$${String.format("%.2f", broadcasterState.liveEarningsUsd)} USD",
                    color = VibeSuccessGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
                Text(
                    text = " (${broadcasterState.liveCoinsReceived} 🪙)",
                    color = VibeYellowGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // WebRTC / LiveKit Telemetry Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(VibeSuccessGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LiveKit SFU • ${webRtcStats.resolution} • ${webRtcStats.latencyMs}ms",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 4. Live Chat Overlay (Bottom Left)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, bottom = 90.dp)
                .fillMaxWidth(0.72f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
                reverseLayout = true
            ) {
                items(viewerChat) { chat ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${chat.senderName}: ",
                            color = VibePrimaryNeon,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = chat.message,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 5. Broadcaster Right Controls Bar (Local REC, AR Filters, Camera Flip, Mute)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 85.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Local Screen Recording Quick Toggle
            IconButton(
                onClick = {
                    val started = viewModel.toggleLocalRecording()
                    scope.launch {
                        if (started) {
                            snackbarHostState.showSnackbar("🔴 Grabación iniciada en almacenamiento interno")
                        } else {
                            val path = viewModel.lastSavedRecordingPath.value ?: "Almacenamiento/Movies/VibeStream"
                            snackbarHostState.showSnackbar("💾 Guardado en: $path")
                        }
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isLocalRecording) Color.Red else Color.Black.copy(alpha = 0.65f))
                    .border(
                        width = 1.5.dp,
                        color = if (isLocalRecording) Color.White else Color.Red.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
                    .testTag("broadcaster_record_button")
            ) {
                Icon(
                    imageVector = if (isLocalRecording) Icons.Default.FiberManualRecord else Icons.Default.RadioButtonChecked,
                    contentDescription = "Grabar pantalla local",
                    tint = if (isLocalRecording) Color.White else Color.Red,
                    modifier = Modifier.size(22.dp)
                )
            }

            // AR Filters Toggle
            IconButton(
                onClick = { showFilterDrawer = !showFilterDrawer },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (showFilterDrawer) VibePrimaryNeon else Color.Black.copy(alpha = 0.65f))
                    .border(1.5.dp, VibePrimaryNeon, CircleShape)
                    .testTag("broadcaster_filters_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Filtros AR",
                    tint = if (showFilterDrawer) Color.Black else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Camera Flip
            IconButton(
                onClick = { viewModel.switchCamera() },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .testTag("switch_camera_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Cambiar Cámara",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Mute / Unmute
            IconButton(
                onClick = { viewModel.toggleMute() },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (broadcasterState.isMuted) Color.Red.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .testTag("toggle_mute_button")
            ) {
                Icon(
                    imageVector = if (broadcasterState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Micrófono",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 6. Sliding AR Filter Selector Drawer
        AnimatedVisibility(
            visible = showFilterDrawer,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(VibeSurface.copy(alpha = 0.95f))
                    .border(1.dp, VibeSurfaceElevated, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎭 Filtros AR en Tiempo Real (LiveKit WebRTC)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showFilterDrawer = false }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = VibeTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(FilterType.entries) { filter ->
                            val isSelected = broadcasterState.activeFilter == filter
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) VibeSurfaceElevated else VibeSurfaceVariant)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) filter.accentColor else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.setFilter(filter) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .testTag("filter_option_${filter.name}")
                            ) {
                                Text(text = filter.iconEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = filter.title,
                                    color = if (isSelected) filter.accentColor else VibeTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Active Gift Pop-Up Banner
        val activeGift by viewModel.activeViewerGift.collectAsStateWithLifecycle()
        GiftAnimationOverlay(
            activeGift = activeGift,
            onAnimationFinished = { viewModel.clearActiveGiftAnimation() }
        )

        // 8. Mandatory Safe Live End AlertDialog
        if (showEndStreamConfirmation) {
            AlertDialog(
                onDismissRequest = { showEndStreamConfirmation = false },
                title = {
                    Text(
                        text = "¿Terminar transmisión en vivo?",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de que deseas finalizar tu directo? Se desconectará la sala de LiveKit, se liberarán los recursos de cámara y micrófono, y se calcularán tus ganancias.",
                        color = VibeTextSecondary,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showEndStreamConfirmation = false
                            // Disconnect LiveKit room, release camera/audio resources and navigate to LiveSummaryScreen
                            val summary = viewModel.finishBroadcasting(streamSeconds)
                            onNavigateToSummary(summary)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("confirm_end_stream_button")
                    ) {
                        Text("Confirmar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEndStreamConfirmation = false },
                        modifier = Modifier.testTag("cancel_end_stream_button")
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                },
                containerColor = VibeSurfaceElevated,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
}

/**
 * CameraPreviewLayer embeds Android CameraX PreviewView with full-screen edge-to-edge crop
 * without letterboxing or black borders.
 */
@Composable
private fun CameraPreviewLayer(
    isFrontCamera: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraHardware by remember { mutableStateOf(true) }

    if (hasCameraHardware) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    // Full-screen crop without black bars
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = if (isFrontCamera) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                    } catch (e: Exception) {
                        hasCameraHardware = false
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            update = { previewView ->
                previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
            },
            modifier = modifier.fillMaxSize()
        )
    } else {
        // Fallback simulation video canvas when camera hardware is absent in container
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF090A10))
                )
            )
        }
    }
}
