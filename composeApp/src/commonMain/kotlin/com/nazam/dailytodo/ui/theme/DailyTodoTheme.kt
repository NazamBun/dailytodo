package com.nazam.dailytodo.ui.theme

import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Thème DailyTodo (PRO bleu)
 * IMPORTANT: surfaceTint = Transparent => évite les teintes bizarres
 */
private val LightColors: ColorScheme = lightColorScheme(
    primary = DailyTodoColors.Primary,
    onPrimary = Color.White,

    secondary = DailyTodoColors.PrimarySoft,
    onSecondary = Color.White,

    background = DailyTodoColors.SurfaceSoft,
    onBackground = DailyTodoColors.TextPrimary,

    surface = DailyTodoColors.Surface,
    onSurface = DailyTodoColors.TextPrimary,

    surfaceVariant = DailyTodoColors.SurfaceSoft,
    onSurfaceVariant = DailyTodoColors.TextSecondary,

    outline = Color(0xFFE6ECF5),
    error = DailyTodoColors.Error,
    onError = Color.White,

    // ✅ empêche les teintes violet/gris
    surfaceTint = Color.Transparent
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = DailyTodoColors.PrimarySoft,
    onPrimary = Color.Black,

    secondary = DailyTodoColors.Primary,
    onSecondary = Color.White,

    background = Color(0xFF070B12),
    onBackground = Color(0xFFEAF0FF),

    surface = Color(0xFF0C1220),
    onSurface = Color(0xFFEAF0FF),

    surfaceVariant = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFFB6C3D6),

    outline = Color(0xFF22304A),
    error = DailyTodoColors.Error,
    onError = Color.White,

    // ✅ empêche les teintes violet/gris
    surfaceTint = Color.Transparent
)

@Composable
fun DailyTodoTheme(
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkMode()

    MaterialTheme(
        colorScheme = if (isDark) DarkColors else LightColors,
        content = content
    )
}
