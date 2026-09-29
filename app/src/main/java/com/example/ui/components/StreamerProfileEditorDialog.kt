package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.local.StreamerProfileData
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeYellowGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val PRESET_AVATARS = listOf(
    "👑", "🌸", "⚡", "🎨", "🚀", "🦊", "🎙️", "👾", "💎", "🐉", "🐱", "🔥"
)

val STREAMER_CATEGORIES = listOf(
    "VTuber / Gaming", "Música & Vlogs", "Arte Digital", "Tecnología", "Charla & IRL", "Educación & Tips"
)

@Composable
fun StreamerProfileEditorDialog(
    initialProfile: StreamerProfileData,
    onDismiss: () -> Unit,
    onSaveProfile: (
        displayName: String,
        handle: String,
        bio: String,
        avatarUri: String?,
        avatarEmoji: String,
        category: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val scope = rememberCoroutineScope()

    var displayName by remember { mutableStateOf(initialProfile.displayName) }
    var handle by remember { mutableStateOf(initialProfile.handle) }
    var bio by remember { mutableStateOf(initialProfile.bio) }
    var selectedAvatarUri by remember { mutableStateOf<String?>(initialProfile.avatarUri) }
    var selectedAvatarEmoji by remember { mutableStateOf(initialProfile.avatarEmoji) }
    var selectedCategory by remember { mutableStateOf(initialProfile.category) }

    var isSaving by remember { mutableStateOf(false) }
    var firebaseSyncMessage by remember { mutableStateOf<String?>(null) }

    // Android Zero-Permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedAvatarUri = uri.toString()
        }
    }

    Dialog(onDismissRequest = { if (!isSaving) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = appColors.surfaceElevated),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("streamer_profile_editor_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Editar Perfil de Creador",
                            color = appColors.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = VibeSuccessGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sincronización Firebase Cloud",
                                color = VibeSuccessGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_profile_editor_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = appColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar / Profile Picture Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(
                                width = 3.dp,
                                brush = Brush.linearGradient(listOf(VibePrimaryNeon, VibeSecondaryPink, VibeAccentPurple)),
                                shape = CircleShape
                            )
                            .background(appColors.surface)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("streamer_avatar_picker_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!selectedAvatarUri.isNullOrBlank()) {
                            AsyncImage(
                                model = selectedAvatarUri,
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(text = selectedAvatarEmoji, fontSize = 44.sp)
                        }

                        // Badge to change photo
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(VibePrimaryNeon)
                                .border(1.5.dp, Color.Black, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Cambiar foto",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.testTag("select_photo_gallery_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = VibePrimaryNeon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Elegir de Galería",
                                color = VibePrimaryNeon,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (!selectedAvatarUri.isNullOrBlank()) {
                            TextButton(
                                onClick = { selectedAvatarUri = null }
                            ) {
                                Text(
                                    text = "Usar Emoji",
                                    color = appColors.textSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Avatar Presets Carousel
                    Text(
                        text = "O elige tu avatar insignia:",
                        color = appColors.textSecondary,
                        fontSize = 11.5.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(PRESET_AVATARS) { emoji ->
                            val isSelected = selectedAvatarUri == null && selectedAvatarEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) VibePrimaryNeon.copy(alpha = 0.25f) else appColors.surface)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) VibePrimaryNeon else appColors.border,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedAvatarUri = null
                                        selectedAvatarEmoji = emoji
                                    }
                                    .testTag("avatar_preset_$emoji"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Name Field
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { if (it.length <= 40) displayName = it },
                    label = { Text("Nombre de Creador", color = appColors.textSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        focusedBorderColor = VibePrimaryNeon,
                        unfocusedBorderColor = appColors.border,
                        focusedContainerColor = appColors.surface,
                        unfocusedContainerColor = appColors.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_display_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Handle Field
                OutlinedTextField(
                    value = handle,
                    onValueChange = {
                        val formatted = if (it.startsWith("@")) it else "@$it"
                        if (formatted.length <= 30) handle = formatted
                    },
                    label = { Text("Nombre de usuario (@handle)", color = appColors.textSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        focusedBorderColor = VibePrimaryNeon,
                        unfocusedBorderColor = appColors.border,
                        focusedContainerColor = appColors.surface,
                        unfocusedContainerColor = appColors.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_handle_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Streamer Bio Field with Character Counter
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { if (it.length <= 160) bio = it },
                        label = { Text("Biografía del Creador", color = appColors.textSecondary) },
                        minLines = 3,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = appColors.textPrimary,
                            unfocusedTextColor = appColors.textPrimary,
                            focusedBorderColor = VibePrimaryNeon,
                            unfocusedBorderColor = appColors.border,
                            focusedContainerColor = appColors.surface,
                            unfocusedContainerColor = appColors.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_bio_input")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Visible en directos y perfil público",
                            color = appColors.textSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${bio.length}/160",
                            color = if (bio.length > 140) VibeYellowGold else appColors.textSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Streamer Category Selector
                Text(
                    text = "Categoría Principal de Streaming:",
                    color = appColors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(STREAMER_CATEGORIES) { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) VibePrimaryNeon else appColors.surface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) VibePrimaryNeon else appColors.border,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("category_chip_$cat"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.Black else appColors.textPrimary,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save Button & Progress
                Button(
                    onClick = {
                        isSaving = true
                        scope.launch {
                            delay(400) // Simulated Firebase update
                            onSaveProfile(
                                displayName.trim().ifBlank { "Álex Streamer" },
                                handle.trim().ifBlank { "@alex_vibe" },
                                bio.trim().ifBlank { "Creador en VibeStream" },
                                selectedAvatarUri,
                                selectedAvatarEmoji,
                                selectedCategory
                            )
                            isSaving = false
                            onDismiss()
                        }
                    },
                    enabled = !isSaving && displayName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = VibePrimaryNeon),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_profile_button")
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Guardando en Firebase...", color = Color.Black, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Guardar y Actualizar Perfil",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
