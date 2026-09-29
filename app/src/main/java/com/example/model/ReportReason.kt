package com.example.model

enum class ReportReason(val displayName: String, val description: String, val iconEmoji: String) {
    INAPPROPRIATE_CONTENT("Contenido inapropiado", "Desnudez, violencia, o actividades peligrosas", "⚠️"),
    SPAM_OR_SCAM("Spam o engaño", "Publicidad no autorizada, estafas o bots", "🤖"),
    HARASSMENT("Acoso o bullying", "Insultos, amenazas o incitación al odio", "🚫"),
    UNDERAGE_RISK("Menor de edad en riesgo", "Presencia de menores en situaciones inadecuadas", "🛡️")
}

data class ContentReportRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val targetUserHandle: String,
    val reason: ReportReason,
    val additionalDetails: String = "",
    val shouldBlockUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
