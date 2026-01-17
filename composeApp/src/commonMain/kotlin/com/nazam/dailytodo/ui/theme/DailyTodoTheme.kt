package com.nazam.dailytodo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Thème DailyTodo (fond neutre + accent bleu)
 * IMPORTANT: surfaceTint = Transparent => enlève la teinte “violet/gris”
 */

private val LightColors: ColorScheme = lightColorScheme(
    // Accent
    primary = DailyTodoColors.BlueStart,
    onPrimary = DailyTodoColors.White,
    secondary = DailyTodoColors.BlueEnd,
    onSecondary = DailyTodoColors.White,

    // Neutres
    background = DailyTodoColors.Gray50,
    onBackground = DailyTodoColors.Black,
    surface = DailyTodoColors.White,
    onSurface = DailyTodoColors.Black,
    surfaceVariant = DailyTodoColors.Gray100,
    onSurfaceVariant = DailyTodoColors.Gray700,
    outline = DailyTodoColors.Gray200,
    outlineVariant = DailyTodoColors.Gray200,

    error = DailyTodoColors.Error,
    onError = DailyTodoColors.White,

    // ✅ enlève l'effet violet
    surfaceTint = Color.Transparent
)

private val DarkColors: ColorScheme = darkColorScheme(
    // Accent
    primary = DailyTodoColors.BlueStart,
    onPrimary = DailyTodoColors.White,
    secondary = DailyTodoColors.BlueEnd,
    onSecondary = DailyTodoColors.White,

    // Neutres
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

    // ✅ enlève l'effet violet
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
