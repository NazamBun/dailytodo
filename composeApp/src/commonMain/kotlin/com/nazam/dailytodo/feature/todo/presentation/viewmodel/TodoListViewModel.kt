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
 * Etape 2: Ajouter
 * - On gère le texte saisi
 * - On ajoute une tâche en mémoire
 *
 * Note:
 * - L'id est généré via un compteur (simple et KMP friendly).
 * - Plus tard, la DB (SQLDelight) donnera un vrai id.
 */
class TodoListViewModel : ViewModel() {

    // Compteur d'id simple (KMP friendly)
    private var nextId: Long = 4L

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

    fun onTitleChanged(newTitle: String) {
        _uiState.value = _uiState.value.copy(
            inputTitle = newTitle,
            inputError = null
        )
    }

    fun onAddClicked() {
        val title = _uiState.value.inputTitle.trim()

        if (title.isBlank()) {
            _uiState.value = _uiState.value.copy(
                inputError = "Le titre est obligatoire"
            )
            return
        }

        val newItem = TodoItemUi(
            id = generateId(),
            title = title,
            isDone = false
        )

        _uiState.value = _uiState.value.copy(
            items = listOf(newItem) + _uiState.value.items,
            inputTitle = "",
            inputError = null
        )
    }

    private fun generateId(): String {
        val id = nextId
        nextId += 1
        return id.toString()
    }
}
