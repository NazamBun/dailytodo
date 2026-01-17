package com.nazam.dailytodo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Thème pro (fond clair neutre + accents bleus).
 * IMPORTANT:
 * - surfaceTint = Transparent => enlève les teintes violettes “bizarres”.
 */
private val LightColors: ColorScheme = lightColorScheme(
    primary = DailyTodoColors.Blue,
    onPrimary = DailyTodoColors.White,

    secondary = DailyTodoColors.BlueSoft,
    onSecondary = DailyTodoColors.White,

    background = DailyTodoColors.Bg,
    onBackground = DailyTodoColors.TextPrimary,

    surface = DailyTodoColors.Surface,
    onSurface = DailyTodoColors.TextPrimary,

    surfaceVariant = DailyTodoColors.Surface2,
    onSurfaceVariant = DailyTodoColors.TextSecondary,

    outline = DailyTodoColors.Outline,
    outlineVariant = DailyTodoColors.Outline,

    error = DailyTodoColors.Error,
    onError = DailyTodoColors.White,

    // ✅ supprime le “violet gris”
    surfaceTint = Color.Transparent
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = DailyTodoColors.Blue,
    onPrimary = DailyTodoColors.White,

    secondary = DailyTodoColors.BlueSoft,
    onSecondary = DailyTodoColors.White,

    background = DailyTodoColors.DarkBg,
    onBackground = DailyTodoColors.White,

    surface = DailyTodoColors.DarkSurface,
    onSurface = DailyTodoColors.White,

    surfaceVariant = DailyTodoColors.DarkSurface2,
    onSurfaceVariant = DailyTodoColors.DarkTextSecondary,

    outline = DailyTodoColors.DarkOutline,
    outlineVariant = DailyTodoColors.DarkOutline,

    error = DailyTodoColors.Error,
    onError = DailyTodoColors.White,

    // ✅ supprime le “violet gris”
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
