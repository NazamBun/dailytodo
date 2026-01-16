package com.nazam.dailytodo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Thème DailyTodo (KMP friendly)
 *
 * - On définit Light et Dark pour faire "pro".
 * - Plus tard, on pourra détecter automatiquement le mode sombre.
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

    background = androidx.compose.ui.graphics.Color(0xFF121212),
    onBackground = androidx.compose.ui.graphics.Color(0xFFF2F2F2),

    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    onSurface = androidx.compose.ui.graphics.Color(0xFFF2F2F2),

    error = DailyTodoColors.Error,
    onError = DailyTodoColors.OnError
)

/**
 * Point d'entrée du thème.
 * Pour l'instant on met isDark = false.
 * Ensuite on fera une détection automatique (Android/iOS) proprement.
 */
@Composable
fun DailyTodoTheme(
    isDark: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDark) DarkColorScheme else LightColorScheme,
        content = content
    )
}
