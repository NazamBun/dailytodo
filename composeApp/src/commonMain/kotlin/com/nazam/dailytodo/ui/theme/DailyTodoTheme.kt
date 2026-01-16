package com.nazam.dailytodo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Thème DailyTodo (KMP friendly)
 *
 * - Light + Dark
 * - Mode sombre détecté automatiquement (Android + iOS)
 */
private val LightColorScheme: ColorScheme = lightColorScheme(
    primary = DailyTodoColors.Primary,
    onPrimary = DailyTodoColors.OnPrimary,

    secondary = DailyTodoColors.Secondary,
    onSecondary = DailyTodoColors.OnSecondary,

    background = DailyTodoColors.Background,
    onBackground = DailyTodoColors.OnBackground,

    surface = DailyTodoColors.Surface,
    onSurface = DailyTodoColors.OnSurface,

    error = DailyTodoColors.Error,
    onError = DailyTodoColors.OnError
)

private val DarkColorScheme: ColorScheme = darkColorScheme(
    // On garde l'identité (violet/rose) mais avec des fonds sombres.
    primary = DailyTodoColors.Primary,
    onPrimary = DailyTodoColors.OnPrimary,

    secondary = DailyTodoColors.Secondary,
    onSecondary = DailyTodoColors.OnSecondary,

    background = Color(0xFF121212),
    onBackground = Color(0xFFF2F2F2),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF2F2F2),

    error = DailyTodoColors.Error,
    onError = DailyTodoColors.OnError
)

/**
 * Point d'entrée du thème.
 * Le mode sombre est choisi automatiquement.
 */
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
