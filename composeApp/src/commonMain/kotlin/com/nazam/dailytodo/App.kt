package com.nazam.dailytodo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * Point d'entrée UI commun (Android + iOS).
 * Pour l'instant on garde un écran très simple, sans code de démo.
 * Ensuite on va construire l'app en MVVM + Clean Architecture.
 */
@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface {
            HomeScreen()
        }
    }
}

/**
 * Écran de base (placeholder).
 * On le remplacera par le vrai écran Todo plus tard.
 */
@Composable
private fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "DailyTodo")
    }
}
