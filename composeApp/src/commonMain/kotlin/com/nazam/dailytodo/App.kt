package com.nazam.dailytodo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nazam.dailytodo.ui.theme.DailyTodoTheme

/**
 * Point d'entrée UI commun (Android + iOS).
 * Ici on applique notre thème (Palette 3).
 */
@Composable
@Preview
fun App() {
    DailyTodoTheme {
        Surface {
            HomeScreen()
        }
    }
}

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
