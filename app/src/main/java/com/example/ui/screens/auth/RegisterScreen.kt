package com.example.ui.screens.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LegalContent
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.VibeAccentPurple
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSecondaryPink
import com.example.ui.theme.VibeSuccessGreen

@Composable
fun RegisterScreen(
    onRegisterSuccess: (email: String, username: String, termsVersion: String, timestamp: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Checkboxes desmarcados por defecto conforme a GDPR/CCPA
    var termsAccepted by remember { mutableStateOf(false) }
    var privacyAccepted by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var legalDialogContent by remember { mutableStateOf<Pair<String, String>?>(null) } // Title to Content

    val isFormValid = username.isNotBlank() && email.contains("@") && password.length >= 6 && termsAccepted && privacyAccepted

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
            .testTag("register_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Brand Logo & Header
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(VibePrimaryNeon, VibeSecondaryPink, VibeAccentPurple))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚡", fontSize = 38.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Únete a VibeStream",
                color = appColors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Streaming en vivo con 75% de ingresos para ti",
                color = VibePrimaryNeon,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Inputs
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario (@streamer)") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = appColors.textSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibePrimaryNeon,
                    unfocusedBorderColor = appColors.border,
                    focusedTextColor = appColors.textPrimary,
                    unfocusedTextColor = appColors.textPrimary,
                    focusedLabelColor = VibePrimaryNeon,
                    unfocusedLabelColor = appColors.textSecondary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_username_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = appColors.textSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibePrimaryNeon,
                    unfocusedBorderColor = appColors.border,
                    focusedTextColor = appColors.textPrimary,
                    unfocusedTextColor = appColors.textPrimary,
                    focusedLabelColor = VibePrimaryNeon,
                    unfocusedLabelColor = appColors.textSecondary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_email_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (mínimo 6 caracteres)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = appColors.textSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibePrimaryNeon,
                    unfocusedBorderColor = appColors.border,
                    focusedTextColor = appColors.textPrimary,
                    unfocusedTextColor = appColors.textPrimary,
                    focusedLabelColor = VibePrimaryNeon,
                    unfocusedLabelColor = appColors.textSecondary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_password_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Legal & Compliance Card (Checkboxes desmarcados por defecto)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = appColors.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, appColors.border, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = VibePrimaryNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Consentimiento y Privacidad (GDPR / CCPA)",
                            color = VibePrimaryNeon,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Checkbox Términos y Condiciones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = VibePrimaryNeon,
                                checkmarkColor = Color.Black,
                                uncheckedColor = appColors.textSecondary
                            ),
                            modifier = Modifier.testTag("checkbox_terms")
                        )

                        val termsAnnotatedString = buildAnnotatedString {
                            append("He leído y acepto los ")
                            pushStringAnnotation(tag = "TERMS", annotation = "terms")
                            withStyle(
                                style = SpanStyle(
                                    color = VibePrimaryNeon,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("Términos y Condiciones")
                            }
                            pop()
                            append(" (${LegalContent.TERMS_VERSION}).")
                        }

                        ClickableText(
                            text = termsAnnotatedString,
                            style = androidx.compose.ui.text.TextStyle(
                                color = appColors.textPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            onClick = { offset ->
                                termsAnnotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                                    .firstOrNull()?.let {
                                        legalDialogContent = Pair("Términos y Condiciones", LegalContent.TERMS_AND_CONDITIONS)
                                    }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Checkbox Política de Privacidad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = privacyAccepted,
                            onCheckedChange = { privacyAccepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = VibePrimaryNeon,
                                checkmarkColor = Color.Black,
                                uncheckedColor = appColors.textSecondary
                            ),
                            modifier = Modifier.testTag("checkbox_privacy")
                        )

                        val privacyAnnotatedString = buildAnnotatedString {
                            append("Acepto la ")
                            pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                            withStyle(
                                style = SpanStyle(
                                    color = VibeSecondaryPink,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("Política de Privacidad")
                            }
                            pop()
                            append(" y el tratamiento de mis datos de perfil.")
                        }

                        ClickableText(
                            text = privacyAnnotatedString,
                            style = androidx.compose.ui.text.TextStyle(
                                color = appColors.textPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            onClick = { offset ->
                                privacyAnnotatedString.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                                    .firstOrNull()?.let {
                                        legalDialogContent = Pair("Política de Privacidad", LegalContent.PRIVACY_POLICY)
                                    }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón "Registrarse" - Deshabilitado (enabled = false) hasta que ambos checkboxes estén marcados
            Button(
                onClick = {
                    if (isFormValid) {
                        isSubmitting = true
                        val timestamp = System.currentTimeMillis()
                        onRegisterSuccess(
                            email.trim(),
                            username.trim().let { if (it.startsWith("@")) it else "@$it" },
                            LegalContent.TERMS_VERSION,
                            timestamp
                        )
                    }
                },
                enabled = isFormValid && !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VibePrimaryNeon,
                    disabledContainerColor = appColors.surfaceElevated
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("register_submit_button")
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (isFormValid) "Registrarse en VibeStream" else "Acepta los Términos para continuar",
                        color = if (isFormValid) Color.Black else appColors.textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
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
                        Text(text = "Entendido", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
