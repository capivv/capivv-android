package com.capivv.sdk.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Default Capivv color palette.
 */
object CapivvColors {
    val Primary = Color(0xFF6366F1)
    val PrimaryVariant = Color(0xFF4F46E5)
    val Secondary = Color(0xFF10B981)
    val SecondaryVariant = Color(0xFF059669)
    val Background = Color(0xFFFAFAFA)
    val Surface = Color(0xFFFFFFFF)
    val Error = Color(0xFFEF4444)
    val OnPrimary = Color.White
    val OnSecondary = Color.White
    val OnBackground = Color(0xFF1F2937)
    val OnSurface = Color(0xFF1F2937)
    val OnError = Color.White

    // Dark theme
    val DarkBackground = Color(0xFF111827)
    val DarkSurface = Color(0xFF1F2937)
    val DarkOnBackground = Color(0xFFF9FAFB)
    val DarkOnSurface = Color(0xFFF9FAFB)
}

private val LightColorScheme = lightColorScheme(
    primary = CapivvColors.Primary,
    secondary = CapivvColors.Secondary,
    background = CapivvColors.Background,
    surface = CapivvColors.Surface,
    error = CapivvColors.Error,
    onPrimary = CapivvColors.OnPrimary,
    onSecondary = CapivvColors.OnSecondary,
    onBackground = CapivvColors.OnBackground,
    onSurface = CapivvColors.OnSurface,
    onError = CapivvColors.OnError
)

private val DarkColorScheme = darkColorScheme(
    primary = CapivvColors.Primary,
    secondary = CapivvColors.Secondary,
    background = CapivvColors.DarkBackground,
    surface = CapivvColors.DarkSurface,
    error = CapivvColors.Error,
    onPrimary = CapivvColors.OnPrimary,
    onSecondary = CapivvColors.OnSecondary,
    onBackground = CapivvColors.DarkOnBackground,
    onSurface = CapivvColors.DarkOnSurface,
    onError = CapivvColors.OnError
)

/**
 * Capivv theme configuration.
 */
data class CapivvThemeConfig(
    val primaryColor: Color = CapivvColors.Primary,
    val secondaryColor: Color = CapivvColors.Secondary,
    val useDarkTheme: Boolean? = null // null = follow system
)

/**
 * Capivv themed composable wrapper.
 */
@Composable
fun CapivvTheme(
    config: CapivvThemeConfig = CapivvThemeConfig(),
    darkTheme: Boolean = config.useDarkTheme ?: isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme.copy(
            primary = config.primaryColor,
            secondary = config.secondaryColor
        )
        else -> LightColorScheme.copy(
            primary = config.primaryColor,
            secondary = config.secondaryColor
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private val Typography = Typography()
