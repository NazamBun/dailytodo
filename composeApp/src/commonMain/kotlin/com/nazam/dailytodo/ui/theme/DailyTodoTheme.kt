package com.nazam.dailytodo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.ui.graphics.Color

/**
 * Thème DailyTodo (Monochrome strict)
 * => Noir / Gris / Blanc uniquement
 * => IMPORTANT: surfaceTint = Transparent (supprime l'effet violet/lavande)
 */
private val LightColorScheme: ColorScheme = lightColorScheme(
    primary = DailyTodoColors.Black,
    onPrimary = DailyTodoColors.White,

    secondary = DailyTodoColors.Black,
    onSecondary = DailyTodoColors.White,

    tertiary = DailyTodoColors.Black,
    onTertiary = DailyTodoColors.White,

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

    // ✅ C’EST ÇA QUI ENLÈVE LE “VIOLET GRIS”
    surfaceTint = Color.Transparent
)

private val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = DailyTodoColors.White,
    onPrimary = DailyTodoColors.Black,

    secondary = DailyTodoColors.White,
    onSecondary = DailyTodoColors.Black,

    tertiary = DailyTodoColors.White,
    onTertiary = DailyTodoColors.Black,

    background = DailyTodoColors.DarkBg,
    onBackground = DailyTodoColors.White,

    surface = DailyTodoColors.DarkSurface,
    onSurface = DailyTodoColors.White,

    surfaceVariant = DailyTodoColors.DarkSurface2,
    onSurfaceVariant = DailyTodoColors.Gray300,

    outline = DailyTodoColors.DarkOutline,
    outlineVariant = DailyTodoColors.DarkOutline,

    error = DailyTodoColors.Error,
    onError = DailyTodoColors.White,

    // ✅ enlève la teinte “magenta/violet”
    surfaceTint = Color.Transparent
)

@Composable
fun DailyTodoTheme(
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkMode()

    MaterialTheme(
        colorScheme = if (isDark) DarkColorScheme else LightColorScheme,
        content = content
    )
}