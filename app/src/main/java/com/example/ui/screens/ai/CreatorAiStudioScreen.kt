package com.example.ui.screens.ai

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibeBackground
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.VibeStreamViewModel

@Composable
fun CreatorAiStudioScreen(
    viewModel: VibeStreamViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Música Lyria, 1: Avatar 4K, 2: Video Veo 3, 3: Estratega High Thinking

    var musicPrompt by remember { mutableStateOf("Synthwave cyberpunk enérgico a 128 BPM con arpegios brillantes para live gaming") }
    var imagePrompt by remember { mutableStateOf("Avatar anime VTuber con orejitas holográficas cian y visor AR, estilo cyberpunk") }
    var videoPrompt by remember { mutableStateOf("Conteo regresivo holográfico 3D de 5 segundos con explosión de neón y logotipo de VibeStream") }
    var thinkingPrompt by remember { mutableStateOf("¿Cómo puedo estructurar mi transmisión de 2 horas para maximizar los regalos con el reparto del 75%?") }

    BackHandler { onBack() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VibeBackground)
            .testTag("ai_studio_screen")
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VibeSurfaceElevated)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "VibeStream AI Creator Studio",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Herramientas de IA para Streamers",
                    color = VibePrimaryNeon,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = VibeSurface,
            contentColor = VibePrimaryNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = VibePrimaryNeon
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Música", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Avatar 4K", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Veo Video", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Estratega", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedTab) {
                // TAB 0: Lyria Music Generation
                0 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = VibeSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "🎵 Generador de Música de Fondo (Lyria 3)",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Genera bandas sonoras libres de copyright para tus streams con lyria-3-clip-preview (hasta 30s) o lyria-3-pro-preview (completo).",
                                    color = VibeTextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Clip Mode Switcher
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (aiState.isMusicClipShort) VibePrimaryNeon else VibeSurfaceElevated)
                                            .clickable { viewModel.setMusicClipType(true) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "Clip Corto (30s)",
                                            color = if (aiState.isMusicClipShort) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (!aiState.isMusicClipShort) VibePrimaryNeon else VibeSurfaceElevated)
                                            .clickable { viewModel.setMusicClipType(false) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "Track Completo (Pro)",
                                            color = if (!aiState.isMusicClipShort) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = musicPrompt,
                                    onValueChange = { musicPrompt = it },
                                    label = { Text("Descripción del ambiente musical") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = VibePrimaryNeon,
                                        unfocusedBorderColor = VibeSurfaceElevated,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { viewModel.generateAiMusic(musicPrompt) },
                                    enabled = !aiState.isLoading,
                                    colors = ButtonDefaults.buttonColors(containerColor = VibePrimaryNeon),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (aiState.isLoading) {
                                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Generar Música con Lyria 3", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    val musicResult = aiState.generatedMusicResult
                    if (musicResult != null) {
                        item {
                            ResultCard(title = "Resultado Musical", text = musicResult, color = VibePrimaryNeon)
                        }
                    }
                }

                // TAB 1: Gemini 3 Pro High Quality Image (1K, 2K, 4K)
                1 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = VibeSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "✨ Generador de Avatares y Overlays (Gemini 3 Pro)",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Renderiza avatares VTuber y portadas de stream de ultra alta definición con model gemini-3-pro-image-preview.",
                                    color = VibeTextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(text = "Resolución de Salida:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("1K", "2K", "4K").forEach { res ->
                                        val isSel = aiState.selectedImageResolution == res
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSel) VibeSecondaryPink else VibeSurfaceElevated)
                                                .clickable { viewModel.setAiImageResolution(res) }
                                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = res,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = imagePrompt,
                                    onValueChange = { imagePrompt = it },
                                    label = { Text("Prompt del Avatar o Carátula") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = VibeSecondaryPink,
                                        unfocusedBorderColor = VibeSurfaceElevated,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { viewModel.generateAiAvatar(imagePrompt) },
                                    enabled = !aiState.isLoading,
                                    colors = ButtonDefaults.buttonColors(containerColor = VibeSecondaryPink),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (aiState.isLoading) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Generar Imagen en ${aiState.selectedImageResolution}", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    val imageResult = aiState.generatedImageResult
                    if (imageResult != null) {
                        item {
                            ResultCard(title = "Imagen Generada", text = imageResult, color = VibeSecondaryPink)
                        }
                    }
                }

                // TAB 2: Veo 3 Video Generation (9:16 or 16:9)
                2 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = VibeSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "🎬 Generador de Video (Veo 3 Fast)",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Genera intros cinemáticas para tus streams en vivo usando model veo-3.1-fast-generate-preview.",
                                    color = VibeTextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(text = "Relación de Aspecto:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("9:16", "16:9").forEach { aspect ->
                                        val isSel = aiState.selectedVideoAspect == aspect
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSel) VibeAccentPurple else VibeSurfaceElevated)
                                                .clickable { viewModel.setAiVideoAspect(aspect) }
                                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = if (aspect == "9:16") "9:16 (Vertical Feed)" else "16:9 (Horizontal)",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = videoPrompt,
                                    onValueChange = { videoPrompt = it },
                                    label = { Text("Prompt del video de intro") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = VibeAccentPurple,
                                        unfocusedBorderColor = VibeSurfaceElevated,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { viewModel.generateAiVideo(videoPrompt) },
                                    enabled = !aiState.isLoading,
                                    colors = ButtonDefaults.buttonColors(containerColor = VibeAccentPurple),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (aiState.isLoading) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Renderizar Video Veo 3 (${aiState.selectedVideoAspect})", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    val videoResult = aiState.generatedVideoResult
                    if (videoResult != null) {
                        item {
                            ResultCard(title = "Video Generado con Veo 3", text = videoResult, color = VibeAccentPurple)
                        }
                    }
                }

                // TAB 3: High Thinking Stream Strategist
                3 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = VibeSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = VibeYellowGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Estratega IA (High Thinking Mode)",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Utiliza el modelo gemini-3.1-pro-preview con thinkingLevel = HIGH para razonar en profundidad estrategias virales y monetización del 75%.",
                                    color = VibeTextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = thinkingPrompt,
                                    onValueChange = { thinkingPrompt = it },
                                    label = { Text("Consulta o desafío de streaming") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = VibeYellowGold,
                                        unfocusedBorderColor = VibeSurfaceElevated,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { viewModel.requestHighThinkingStrategy(thinkingPrompt) },
                                    enabled = !aiState.isLoading,
                                    colors = ButtonDefaults.buttonColors(containerColor = VibeYellowGold),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (aiState.isLoading) {
                                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Pensar en Profundidad (Thinking High)", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    val thinkingResult = aiState.thinkingStrategyResult
                    if (thinkingResult != null) {
                        item {
                            ResultCard(title = "Estrategia Profunda (Gemini 3.1 Pro)", text = thinkingResult, color = VibeYellowGold)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun ResultCard(title: String, text: String, color: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VibeSurfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = text, color = Color.White, fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}
