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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.theme.VibeTextPrimary
import com.example.ui.theme.VibeTextSecondary
import com.example.ui.theme.VibeYellowGold
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeVerificationScreen(
    onVerificationCompleted: (birthdate: String, age: Int, canGoLive: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado inicial: fecha de referencia sugerida (~20 años atrás)
    val defaultMillis = remember {
        Calendar.getInstance().apply {
            add(Calendar.YEAR, -20)
        }.timeInMillis
    }

    val datePickerState: DatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = defaultMillis
    )

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(defaultMillis) }

    val formattedDate = remember(selectedDateMillis) {
        selectedDateMillis?.let { millis ->
            val sdf = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
            sdf.format(Date(millis))
        } ?: "Selecciona tu fecha"
    }

    val isoDate = remember(selectedDateMillis) {
        selectedDateMillis?.let { millis ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.format(Date(millis))
        } ?: ""
    }

    val calculatedAge = remember(selectedDateMillis) {
        selectedDateMillis?.let { millis ->
            val birthCal = Calendar.getInstance().apply { timeInMillis = millis }
            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age.coerceAtLeast(0)
        } ?: 0
    }

    val canGoLive = calculatedAge >= 18

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VibeBackground)
            .testTag("age_verification_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Icono de Seguridad y Verificación
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(VibePrimaryNeon.copy(alpha = 0.25f), VibeSecondaryPink.copy(alpha = 0.25f))
                        )
                    )
                    .border(2.dp, VibePrimaryNeon, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Verificación de Edad",
                    tint = VibePrimaryNeon,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Verificación de Edad",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Para brindarte la mejor experiencia y mantener una comunidad segura, necesitamos confirmar tu fecha de nacimiento antes de entrar.",
                color = VibeTextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Selector interactivo de fecha
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VibeSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, VibeSurfaceElevated, RoundedCornerShape(20.dp))
                    .clickable { showDatePickerDialog = true }
                    .testTag("birthdate_picker_trigger")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Fecha de Nacimiento",
                            color = VibeTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formattedDate,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(VibeSurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Cambiar fecha",
                            tint = VibePrimaryNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto legal requerido: "Tu fecha de nacimiento no se mostrará públicamente"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = VibeYellowGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tu fecha de nacimiento no se mostrará públicamente",
                    color = VibeYellowGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de estado de elegibilidad para Streaming (>= 18 vs < 18)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (canGoLive) VibeSurfaceElevated else Color(0xFF24151E)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = if (canGoLive) VibeSuccessGreen.copy(alpha = 0.7f) else VibeOrangeHot.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (canGoLive) VibeSuccessGreen.copy(alpha = 0.2f) else VibeOrangeHot.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (canGoLive) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = null,
                                tint = if (canGoLive) VibeSuccessGreen else VibeOrangeHot,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Edad calculada: $calculatedAge años",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (canGoLive) "Apto para Transmitir en Vivo (Go Live)" else "Transmisión en Vivo Restringida (< 18)",
                                color = if (canGoLive) VibeSuccessGreen else VibeOrangeHot,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (canGoLive) {
                            "Cumples con la política de mayoría de edad (>=18) para emitir en directo, recibir regalos monetizables y usar filtros AR de cámara."
                        } else {
                            "Podrás disfrutar de todas las demás funciones de VibeStream: navegar por el Feed, dar me gusta, enviar regalos a tus streamers favoritos y personalizar tu perfil."
                        },
                        color = VibeTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de confirmación para avanzar al FeedScreen
            Button(
                onClick = {
                    onVerificationCompleted(isoDate, calculatedAge, canGoLive)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VibePrimaryNeon
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .testTag("confirm_age_verification_button")
            ) {
                Text(
                    text = "Continuar al Feed",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Modal DatePickerDialog nativo de Material 3
    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDateMillis = millis
                        }
                        showDatePickerDialog = false
                    },
                    modifier = Modifier.testTag("date_picker_confirm_button")
                ) {
                    Text("Aceptar", color = VibePrimaryNeon, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancelar", color = VibeTextSecondary)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}
