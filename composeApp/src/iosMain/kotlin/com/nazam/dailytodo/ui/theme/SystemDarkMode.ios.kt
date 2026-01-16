package com.nazam.dailytodo.ui.theme

import androidx.compose.runtime.Composable
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIScreen

/**
 * iOS : on lit le mode sombre via la configuration de l'écran.
 */
@Composable
actual fun isSystemInDarkMode(): Boolean {
    val style = UIScreen.mainScreen.traitCollection.userInterfaceStyle
    return style == UIUserInterfaceStyle.UIUserInterfaceStyleDark
}
