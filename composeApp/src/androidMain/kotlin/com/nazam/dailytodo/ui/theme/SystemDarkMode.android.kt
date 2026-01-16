package com.nazam.dailytodo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

/**
 * Android : Compose sait déjà lire le mode sombre du téléphone.
 */
@Composable
actual fun isSystemInDarkMode(): Boolean = isSystemInDarkTheme()
