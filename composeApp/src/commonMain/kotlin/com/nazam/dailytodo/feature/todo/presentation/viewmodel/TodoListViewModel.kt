package com.nazam.dailytodo.feature.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.nazam.dailytodo.feature.todo.presentation.model.TodoItemUi
import com.nazam.dailytodo.feature.todo.presentation.model.TodoListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel (MVVM).
 *
 * Pour l'instant:
 * - On garde une liste en mémoire (fake data) juste pour afficher.
 * - Plus tard, on branchera la vraie donnée (UseCase + Repository + SQLDelight).
 */
class TodoListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        TodoListUiState(
            items = listOf(
                TodoItemUi(id = "1", title = "Acheter du lait", isDone = false),
                TodoItemUi(id = "2", title = "Faire 20 minutes de sport", isDone = true),
                TodoItemUi(id = "3", title = "Réviser Kotlin", isDone = false),
            )
        )
    )
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()
}
