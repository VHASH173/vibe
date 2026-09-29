package com.example.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.LegalContent
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeYellowGold

@Composable
fun SettingsScreen(
    userEmail: String,
    termsVersion: String,
    currentThemeMode: AppThemeMode,
    onThemeModeChanged: (AppThemeMode) -> Unit,
    onBack: () -> Unit,
    onDeleteAccountConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    var expandedFaqId by remember { mutableStateOf<String?>(null) }
    var legalDialogContent by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    BackHandler { onBack() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
            .testTag("settings_screen")
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
                    .background(appColors.surfaceElevated)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = appColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Ajustes, Privacidad y FAQ",
                    color = appColors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cumplimiento Legal GDPR / CCPA",
                    color = VibePrimaryNeon,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Theme Selection Card (Claro, Oscuro, Predeterminado del Sistema)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = appColors.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, appColors.border, RoundedCornerShape(18.dp))
                        .testTag("theme_selection_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (appColors.isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = VibePrimaryNeon
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tema de la Aplicación",
                                color = appColors.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Ajusta la apariencia visual para sesiones de streaming diurnas o nocturnas.",
                            color = appColors.textSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Theme Mode Options: Claro, Oscuro, Predeterminado del Sistema
                        listOf(
                            Triple(AppThemeMode.DARK, "Oscuro", Icons.Default.DarkMode),
                            Triple(AppThemeMode.LIGHT, "Claro", Icons.Default.LightMode),
                            Triple(AppThemeMode.SYSTEM, "Predeterminado del Sistema", Icons.Default.BrightnessMedium)
                        ).forEach { (mode, label, icon) ->
                            val isSelected = currentThemeMode == mode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) appColors.surfaceElevated else Color.Transparent)
                                    .clickable { onThemeModeChanged(mode) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("theme_option_${mode.name}"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onThemeModeChanged(mode) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = VibePrimaryNeon,
                                        unselectedColor = appColors.textSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) VibePrimaryNeon else appColors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = label,
                                    color = if (isSelected) VibePrimaryNeon else appColors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // User Consent Status Card (Auditoría Local DataStore)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = appColors.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, appColors.border, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = VibeSuccessGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Estado de Consentimiento y Auditoría",
                                color = appColors.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Cuenta: $userEmail",
                            color = appColors.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Versión de Términos Aceptada: $termsVersion",
                            color = VibePrimaryNeon,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Auditoría legal verificada y guardada en Jetpack DataStore.",
                            color = VibeSuccessGreen,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Legal Documents Direct Links
            item {
                Text(
                    text = "Documentación Legal",
                    color = appColors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = appColors.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, appColors.border, RoundedCornerShape(18.dp))
                ) {
                    Column {
                        // T&C link
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    legalDialogContent = Pair("Términos y Condiciones", LegalContent.TERMS_AND_CONDITIONS)
                                }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = VibePrimaryNeon)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Términos y Condiciones (T&C)",
                                    color = appColors.textPrimary,
                                    fontSize = 13.sp
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = appColors.textSecondary)
                        }

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(appColors.border))

                        // Privacy policy link
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    legalDialogContent = Pair("Política de Privacidad", LegalContent.PRIVACY_POLICY)
                                }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = VibeSecondaryPink)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Política de Privacidad (GDPR/CCPA)",
                                    color = appColors.textPrimary,
                                    fontSize = 13.sp
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = appColors.textSecondary)
                        }
                    }
                }
            }

            // FAQ Section
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Help, contentDescription = null, tint = VibeYellowGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Preguntas Frecuentes (FAQ)",
                        color = appColors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(LegalContent.FAQ_ITEMS) { faq ->
                val isExpanded = expandedFaqId == faq.id

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = appColors.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, appColors.border, RoundedCornerShape(16.dp))
                        .clickable {
                            expandedFaqId = if (isExpanded) null else faq.id
                        }
                        .testTag("faq_item_${faq.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = faq.iconEmoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = faq.question,
                                        color = appColors.textPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = faq.category,
                                        color = VibePrimaryNeon,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = appColors.textSecondary
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(appColors.border))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = faq.answer,
                                    color = appColors.textSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            // DERECHO AL OLVIDO / ELIMINACIÓN TOTAL DE CUENTA (GDPR)
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.08f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Red.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zona de Peligro (GDPR / CCPA)",
                                color = Color.Red,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Bajo los reglamentos GDPR y CCPA, tienes derecho al borrado total e irreversible de todos tus datos, saldo y transacciones.",
                            color = appColors.textSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showDeleteConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("delete_account_button")
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Eliminar cuenta y datos personales",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Modal emergente con texto legal completo (AlertDialog)
        legalDialogContent?.let { (title, body) ->
            AlertDialog(
                onDismissRequest = { legalDialogContent = null },
                containerColor = appColors.surface,
                title = {
                    Text(text = title, color = appColors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                text = {
                    Column(
                        modifier = Modifier
                            .height(380.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = body,
                            color = appColors.textSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { legalDialogContent = null },
                        colors = ButtonDefaults.buttonColors(containerColor = VibePrimaryNeon),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Cerrar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Diálogo estricto de confirmación para Eliminación Total de Cuenta (GDPR)
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { if (!isDeleting) showDeleteConfirmDialog = false },
                containerColor = appColors.surface,
                icon = {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(36.dp))
                },
                title = {
                    Text(
                        text = "¿Eliminar cuenta y todos los datos?",
                        color = appColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                text = {
                    Text(
                        text = "Esta acción es irreversible conforme a los estándares GDPR y CCPA. Se eliminará tu perfil, saldo de monedas, ganancias de creador, historial de transacciones, caché y registros locales.",
                        color = appColors.textSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isDeleting = true
                            showDeleteConfirmDialog = false
                            onDeleteAccountConfirmed()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("confirm_delete_account_button")
                    ) {
                        Text(text = "Sí, eliminar definitivamente", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text(text = "Cancelar", color = appColors.textSecondary)
                    }
                }
            )
        }
    }
}
