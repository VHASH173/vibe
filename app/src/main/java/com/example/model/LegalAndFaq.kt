package com.example.model

data class FaqItem(
    val id: String,
    val question: String,
    val answer: String,
    val category: String, // "Monetización", "Privacidad & Datos", "Comunidad"
    val iconEmoji: String
)

object LegalContent {
    const val TERMS_VERSION = "v1.0"
    const val LAST_UPDATED = "Septiembre 2026"

    val TERMS_AND_CONDITIONS = """
TÉRMINOS Y CONDICIONES DE VIBESTREAM (Versión $TERMS_VERSION - $LAST_UPDATED)

1. Aceptación del Servicio
Al registrarse o utilizar la plataforma VibeStream, usted acepta cumplir estos Términos y Condiciones y todas las leyes aplicables. Si no está de acuerdo, absténgase de usar el servicio.

2. Reparto de Ingresos y Comisión Competitiva (75% / 25%)
- Los creadores de contenido reciben el setenta y cinco por ciento (75%) del valor neto de cada regalo o moneda virtual recibido durante sus transmisiones en vivo o en su perfil.
- VibeStream retiene una comisión del veinticinco por ciento (25%) para cubrir costos de infraestructura WebRTC de baja latencia (LiveKit SFU), servidores y pasarelas de pago.
- Los retiros de fondos en USD están sujetos a verificación de identidad y cumplimiento de leyes fiscales.

3. Economía Virtual y Regalos
- Las monedas virtuales ("VibeCoins") no constituyen moneda de curso legal y no son transferibles fuera de la plataforma, salvo las ganancias acumuladas por creadores que califican para retiro a PayPal o cuenta bancaria.
- Todas las compras de monedas son finales e irreversibles una vez acreditadas.

4. Normas de Conducta y Streaming
- Queda terminantemente prohibido transmitir contenido ilegal, violento, que infrinja derechos de autor, o que promueva el acoso y la discriminación.
- VibeStream se reserva el derecho de suspender o revocar el acceso a cualquier cuenta que vulnere estas directrices.
    """.trimIndent()

    val PRIVACY_POLICY = """
POLÍTICA DE PRIVACIDAD Y CUMPLIMIENTO GDPR / CCPA (Versión $TERMS_VERSION - $LAST_UPDATED)

1. Responsable del Tratamiento
VibeStream Inc. es el responsable del tratamiento de los datos personales recabados a través de esta aplicación.

2. Datos que Recopilamos
- Datos de cuenta: Correo electrónico, nombre de usuario y avatar.
- Datos de auditoría de consentimiento: Versión de términos aceptada ("$TERMS_VERSION") y marca de tiempo exacta (timestamp epoch).
- Datos de telemetría y stream: Latencia de red WebRTC, bitrate e interacciones de chat en vivo.
- Datos financieros: Registro de monedas compradas, regalos recibidos y solicitudes de retiro. No almacenamos datos sensibles de tarjetas de crédito (gestionados por Stripe).

3. Base Jurídica y Finalidad
Tratamos sus datos para prestar el servicio de streaming en vivo, procesar los pagos y el reparto de ingresos (75/25), y garantizar la seguridad de la comunidad.

4. Derechos del Usuario (GDPR / CCPA)
- Derecho de Acceso y Rectificación: Puede consultar sus datos y saldo en cualquier momento en su Perfil.
- Derecho al Olvido (Eliminación Total): Puede solicitar la supresión completa e inmediata de su cuenta y datos personales desde Ajustes -> "Eliminar cuenta y datos personales". Al confirmar, se eliminarán en cascada todos los registros en base de datos, caché y DataStore local.
    """.trimIndent()

    val FAQ_ITEMS = listOf(
        FaqItem(
            id = "faq_monetization_1",
            question = "¿Cómo funciona el modelo de monetización 75% para creadores?",
            answer = "En VibeStream creemos en un reparto justo. Por cada regalo que recibes en vivo, el 75% del valor en USD se deposita de inmediato en tu billetera virtual, mientras la plataforma retiene solo el 25% para costear la infraestructura de video en tiempo real. Esto te da 50% más de ingresos comparado con plataformas tradicionales que se quedan con el 50%.",
            category = "Monetización",
            iconEmoji = "💰"
        ),
        FaqItem(
            id = "faq_monetization_2",
            question = "¿Cómo y cuándo puedo retirar mis ganancias acumuladas?",
            answer = "Puedes retirar tus ganancias en USD en cualquier momento desde tu Perfil pulsando 'Retirar Fondos'. El saldo mínimo es de $10 USD y los fondos se transfieren directamente a tu cuenta de PayPal o cuenta bancaria mediante nuestra función segura de pago en menos de 5 minutos.",
            category = "Monetización",
            iconEmoji = "🏦"
        ),
        FaqItem(
            id = "faq_privacy_1",
            question = "¿Cómo almacena y protege VibeStream mis datos y foto de perfil?",
            answer = "Tus credenciales y datos se almacenan cifrados. Cumplimos rigurosamente con los estándares internacionales GDPR y CCPA. No vendemos tus datos a terceros y solo recopilamos la información estrictamente necesaria para el streaming y la auditoría de consentimiento legal.",
            category = "Privacidad & Datos",
            iconEmoji = "🛡️"
        ),
        FaqItem(
            id = "faq_privacy_2",
            question = "¿Cómo ejerzo mi derecho al olvido (eliminación total de datos)?",
            answer = "Puedes eliminar definitivamente tu cuenta y todos tus datos personales asociados en cualquier momento desde Ajustes -> 'Eliminar cuenta y datos personales'. La acción borra tu perfil, historial de transacciones, memoria caché local y registros de base de datos de forma irreversible.",
            category = "Privacidad & Datos",
            iconEmoji = "🗑️"
        ),
        FaqItem(
            id = "faq_community_1",
            question = "¿Cuáles son las normas de seguridad de la comunidad?",
            answer = "VibeStream promueve un ambiente seguro y creativo. Está estrictamente prohibido el contenido de odio, acoso a otros streamers, exhibición de material explícito o peligroso y la retransmisión no autorizada de contenido con copyright. Las infracciones resultan en baneo inmediato.",
            category = "Comunidad",
            iconEmoji = "🤝"
        )
    )
}
