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
 * Etape 3: Modifier
 * - Cliquer sur une tâche => mode édition
 * - Bouton "Enregistrer" => met à jour le titre
 */
class TodoListViewModel : ViewModel() {

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

    fun onTodoClicked(id: String) {
        val item = _uiState.value.items.firstOrNull { it.id == id } ?: return
        _uiState.value = _uiState.value.copy(
            editingId = id,
            inputTitle = item.title,
            inputError = null
        )
    }

    fun onPrimaryActionClicked() {
        val title = _uiState.value.inputTitle.trim()
        if (title.isBlank()) {
            _uiState.value = _uiState.value.copy(inputError = "Le titre est obligatoire")
            return
        }

        val editingId = _uiState.value.editingId
        if (editingId == null) {
            addTodo(title)
        } else {
            updateTodo(editingId, title)
        }
    }

    fun onCancelEditClicked() {
        _uiState.value = _uiState.value.copy(
            editingId = null,
            inputTitle = "",
            inputError = null
        )
    }

    private fun addTodo(title: String) {
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

    private fun updateTodo(id: String, newTitle: String) {
        val updated = _uiState.value.items.map { item ->
            if (item.id == id) item.copy(title = newTitle) else item
        }

        _uiState.value = _uiState.value.copy(
            items = updated,
            editingId = null,
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
