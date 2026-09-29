package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.VibePrimaryNeon
import com.example.ui.theme.VibeSuccessGreen
import com.example.ui.theme.VibeSurface
import com.example.ui.theme.VibeSurfaceElevated
import com.example.ui.theme.VibeSurfaceVariant
import com.example.ui.theme.VibeTextPrimary
import com.example.ui.theme.VibeTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WithdrawDialog(
    currentEarningsUsd: Double,
    onDismiss: () -> Unit,
    onWithdrawSuccess: (Double, String) -> Unit
) {
    var withdrawAmountText by remember { mutableStateOf(if (currentEarningsUsd > 10) "50.00" else String.format("%.2f", currentEarningsUsd)) }
    var destinationEmail by remember { mutableStateOf("creador@vibestream.live") }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = VibeSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("withdraw_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Retirar Ganancias",
                            color = VibeTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Disponible (75% Creador): $${String.format("%.2f", currentEarningsUsd)} USD",
                            color = VibeSuccessGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = VibeTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = withdrawAmountText,
                    onValueChange = {
                        withdrawAmountText = it
                        errorMessage = null
                    },
                    label = { Text("Monto a retirar en USD ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibePrimaryNeon,
                        unfocusedBorderColor = VibeSurfaceElevated,
                        focusedLabelColor = VibePrimaryNeon,
                        unfocusedLabelColor = VibeTextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_amount_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = destinationEmail,
                    onValueChange = { destinationEmail = it },
                    label = { Text("Cuenta PayPal o IBAN") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibePrimaryNeon,
                        unfocusedBorderColor = VibeSurfaceElevated,
                        focusedLabelColor = VibePrimaryNeon,
                        unfocusedLabelColor = VibeTextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = error, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info banner
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VibeSurfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "⚡ Pago Instantáneo vía Supabase Edge Function",
                        color = VibePrimaryNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sin comisiones de retiro ocultas. Los fondos se transfieren en menos de 5 minutos.",
                        color = VibeTextSecondary,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val amount = withdrawAmountText.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            errorMessage = "Ingresa un monto válido"
                            return@Button
                        }
                        if (amount > currentEarningsUsd) {
                            errorMessage = "Monto excede tu saldo disponible"
                            return@Button
                        }
                        isProcessing = true
                        scope.launch {
                            delay(1200) // Simulated Edge Function payout transfer
                            isProcessing = false
                            onWithdrawSuccess(amount, destinationEmail)
                            onDismiss()
                        }
                    },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = VibeSuccessGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_withdraw_button")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Transfiriendo fondos...", color = Color.Black, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Confirmar Retiro", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
