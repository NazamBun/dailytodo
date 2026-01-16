package com.nazam.dailytodo.ui.theme

import androidx.compose.runtime.Composable

/**
 * Retourne true si le système est en mode sombre.
 * On met "expect" ici, et chaque plateforme donne sa version (actual).
 */
@Composable
expect fun isSystemInDarkMode(): Boolean
