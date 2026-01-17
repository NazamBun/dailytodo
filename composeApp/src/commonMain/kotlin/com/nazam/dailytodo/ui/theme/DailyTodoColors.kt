package com.nazam.dailytodo.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Palette PRO:
 * - Fond neutre (gris/blanc/noir)
 * - Accent bleu moderne (pour UI)
 * - Pas de violet
 */
object DailyTodoColors {

    // Neutres (light)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF0B0F14)
    val Gray50 = Color(0xFFF7F8FA)
    val Gray100 = Color(0xFFF0F2F5)
    val Gray200 = Color(0xFFE5E7EB)
    val Gray700 = Color(0xFF374151)

    // Neutres (dark)
    val DarkBg = Color(0xFF0B0F14)
    val DarkSurface = Color(0xFF111827)
    val DarkSurface2 = Color(0xFF161F2D)
    val DarkOutline = Color(0xFF2A3445)
    val DarkTextSecondary = Color(0xFFA7B0C0)

    // ✅ Bleu pro (dégradé search)
    val BlueStart = Color(0xFF1F5BFF)   // bleu “pro”
    val BlueEnd = Color(0xFF12B3FF)     // bleu clair/cyan

    // Status
    val Error = Color(0xFFB91C1C)
}
