package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val displayName: String) {
    SYSTEM("Predeterminado del Sistema"),
    DARK("Oscuro"),
    LIGHT("Claro")
}

data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val border: Color,
    val isDark: Boolean
)

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        background = VibeBackground,
        surface = VibeSurface,
        surfaceVariant = VibeSurfaceVariant,
        surfaceElevated = VibeSurfaceElevated,
        textPrimary = VibeTextPrimary,
        textSecondary = VibeTextSecondary,
        textMuted = VibeTextMuted,
        border = Color(0xFF282C42),
        isDark = true
    )
}

val VibeDarkColorScheme = darkColorScheme(
    primary = VibePrimaryNeon,
    onPrimary = Color.Black,
    primaryContainer = VibeSurfaceVariant,
    onPrimaryContainer = VibePrimaryNeon,
    secondary = VibeSecondaryPink,
    onSecondary = Color.White,
    secondaryContainer = VibeAccentPurple,
    onSecondaryContainer = Color.White,
    tertiary = VibeYellowGold,
    onTertiary = Color.Black,
    background = VibeBackground,
    onBackground = VibeTextPrimary,
    surface = VibeSurface,
    onSurface = VibeTextPrimary,
    surfaceVariant = VibeSurfaceVariant,
    onSurfaceVariant = VibeTextSecondary,
    outline = VibeSurfaceElevated
)

val VibeLightColorScheme = lightColorScheme(
    primary = Color(0xFF009688),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2F1),
    onPrimaryContainer = Color(0xFF004D40),
    secondary = Color(0xFFE91E63),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE4EC),
    onSecondaryContainer = Color(0xFF880E4F),
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val colorScheme = if (isDark) VibeDarkColorScheme else VibeLightColorScheme
    val appColors = if (isDark) {
        AppColors(
            background = VibeBackground,
            surface = VibeSurface,
            surfaceVariant = VibeSurfaceVariant,
            surfaceElevated = VibeSurfaceElevated,
            textPrimary = VibeTextPrimary,
            textSecondary = VibeTextSecondary,
            textMuted = VibeTextMuted,
            border = Color(0xFF282C42),
            isDark = true
        )
    } else {
        AppColors(
            background = Color(0xFFF1F5F9),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE2E8F0),
            surfaceElevated = Color(0xFFF8FAFC),
            textPrimary = Color(0xFF0F172A),
            textSecondary = Color(0xFF475569),
            textMuted = Color(0xFF94A3B8),
            border = Color(0xFFCBD5E1),
            isDark = false
        )
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
