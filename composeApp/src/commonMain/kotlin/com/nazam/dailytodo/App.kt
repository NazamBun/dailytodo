package com.nazam.dailytodo

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nazam.dailytodo.feature.todo.presentation.ui.TodoListScreen
import com.nazam.dailytodo.feature.todo.presentation.viewmodel.TodoListViewModel
import com.nazam.dailytodo.ui.theme.DailyTodoTheme

/**
 * Point d'entrée UI commun (Android + iOS).
 */
@Composable
@Preview
fun App() {
    DailyTodoTheme {
        Surface {
            // Pour l'instant on crée le ViewModel "simplement".
            // Ensuite on fera une vraie injection (DI) propre.
            TodoListScreen(viewModel = TodoListViewModel())
        }
    }
}
