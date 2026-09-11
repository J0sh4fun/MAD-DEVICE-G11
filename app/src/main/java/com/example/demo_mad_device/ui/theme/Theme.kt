package com.example.demo_mad_device.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HealthGuardColorScheme = darkColorScheme(
    primary = MedicalCyanPrimary,
    onPrimary = Color(0xFF0284C7),
    primaryContainer = Color(0xFF082F49),
    onPrimaryContainer = Color(0xFFE0F2FE),

    secondary = MedicalEmeraldSecondary,
    onSecondary = Color(0xFF047857),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFD1FAE5),

    tertiary = MedicalBlueTertiary,
    onTertiary = Color(0xFF1D4ED8),
    tertiaryContainer = Color(0xFF1E3A8A),
    onTertiaryContainer = Color(0xFFDBEAFE),

    background = MedicalDarkBackground,
    onBackground = MedicalTextPrimary,

    surface = MedicalDarkSurface,
    onSurface = MedicalTextPrimary,
    surfaceVariant = MedicalDarkSurfaceVariant,
    onSurfaceVariant = MedicalTextSecondary,

    outline = MedicalCardBorder,
    outlineVariant = Color(0xFF1E293B),

    error = MedicalAlertRed,
    onError = Color(0xFF991B1B),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

@Composable
fun HealthGuardTheme(
    darkTheme: Boolean = true, // Force Dark Medical Theme by default for healthcare monitor look
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HealthGuardColorScheme,
        typography = Typography,
        content = content
    )
}
