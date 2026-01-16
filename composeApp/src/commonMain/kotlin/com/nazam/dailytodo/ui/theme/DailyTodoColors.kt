package com.nazam.dailytodo.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Palette 3 (énergie douce) - DailyTodo
 *
 * Idée:
 * - Violet doux = couleur principale
 * - Rose clair = accent
 * - Fond très clair = confortable pour les yeux
 *
 * On garde les couleurs dans un seul endroit pour rester "clean" et factorisé.
 */
object DailyTodoColors {

    // Brand
    val Primary = Color(0xFF6C63FF)        // Violet doux
    val Secondary = Color(0xFFFF8FAB)      // Rose clair

    // Background / Surface
    val Background = Color(0xFFF5F5F7)     // Gris très clair
    val Surface = Color(0xFFFFFFFF)        // Blanc

    // Text
    val OnPrimary = Color(0xFFFFFFFF)      // Texte sur le violet
    val OnSecondary = Color(0xFF1F1F1F)    // Texte sur le rose
    val OnBackground = Color(0xFF1F1F1F)   // Texte principal
    val OnSurface = Color(0xFF1F1F1F)

    // Status
    val Error = Color(0xFFE94B3C)          // Rouge doux
    val OnError = Color(0xFFFFFFFF)
}
