package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.pref.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color(0xFF031024),
    primaryContainer = Color(0xFF0A2540),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = NeonViolet,
    onSecondary = Color(0xFF24033B),
    secondaryContainer = Color(0xFF381454),
    onSecondaryContainer = Color(0xFFF3E8FF),
    tertiary = AmberThinking,
    background = CosmicDarkBg,
    onBackground = TextPrimaryDark,
    surface = CosmicDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = CosmicDarkCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = CosmicDarkCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF134E4A),
    tertiary = AmberThinking,
    background = PureLightBg,
    onBackground = TextPrimaryLight,
    surface = PureLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = PureLightCard,
    onSurfaceVariant = TextSecondaryLight,
    outline = PureLightCardBorder
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
