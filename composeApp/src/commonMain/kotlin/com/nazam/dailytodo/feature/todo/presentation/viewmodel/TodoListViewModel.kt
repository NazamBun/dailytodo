package com.nazam.dailytodo.feature.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.nazam.dailytodo.feature.todo.presentation.model.TodoFilter
import com.nazam.dailytodo.feature.todo.presentation.model.TodoItemUi
import com.nazam.dailytodo.feature.todo.presentation.model.TodoListUiState
import com.nazam.dailytodo.feature.todo.presentation.model.TodoSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel (MVVM).
 *
 * Etape 8: Recherche
 * - Champ de recherche qui filtre par titre
 * - Ordre: filtre -> recherche -> tri
 */
class TodoListViewModel : ViewModel() {

    private var nextId: Long = 4L

    private val _uiState = MutableStateFlow(
        TodoListUiState(
            items = listOf(
                TodoItemUi(id = "1", title = "Acheter du lait", isDone = false),
                TodoItemUi(id = "2", title = "Faire 20 minutes de sport", isDone = true),
                TodoItemUi(id = "3", title = "Réviser Kotlin", isDone = false),
            ),
            filter = TodoFilter.ALL,
            sort = TodoSort.DATE,
            query = ""
        )
    )
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    init {
        updateState(_uiState.value)
    }

    fun onTitleChanged(newTitle: String) {
        updateState(
            _uiState.value.copy(
                inputTitle = newTitle,
                inputError = null
            )
        )
    }

    fun onQueryChanged(newQuery: String) {
        updateState(_uiState.value.copy(query = newQuery))
    }

    fun onTodoClicked(id: String) {
        val item = _uiState.value.items.firstOrNull { it.id == id } ?: return
        updateState(
            _uiState.value.copy(
                editingId = id,
                inputTitle = item.title,
                inputError = null
            )
        )
    }

    fun onPrimaryActionClicked() {
        val title = _uiState.value.inputTitle.trim()
        if (title.isBlank()) {
            updateState(_uiState.value.copy(inputError = "Le titre est obligatoire"))
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
        updateState(
            _uiState.value.copy(
                editingId = null,
                inputTitle = "",
                inputError = null
            )
        )
    }

    fun onDoneToggled(id: String, isDone: Boolean) {
        val updated = _uiState.value.items.map { item ->
            if (item.id == id) item.copy(isDone = isDone) else item
        }
        updateState(_uiState.value.copy(items = updated))
    }

    fun onDeleteClicked(id: String) {
        val newItems = _uiState.value.items.filterNot { it.id == id }
        val shouldCancelEdit = _uiState.value.editingId == id

        updateState(
            _uiState.value.copy(
                items = newItems,
                editingId = if (shouldCancelEdit) null else _uiState.value.editingId,
                inputTitle = if (shouldCancelEdit) "" else _uiState.value.inputTitle,
                inputError = if (shouldCancelEdit) null else _uiState.value.inputError
            )
        )
    }

    fun onFilterSelected(filter: TodoFilter) {
        updateState(_uiState.value.copy(filter = filter))
    }

    fun onSortSelected(sort: TodoSort) {
        updateState(_uiState.value.copy(sort = sort))
    }

    private fun addTodo(title: String) {
        val newItem = TodoItemUi(
            id = generateId(),
            title = title,
            isDone = false
        )

        updateState(
            _uiState.value.copy(
                items = listOf(newItem) + _uiState.value.items,
                inputTitle = "",
                inputError = null
            )
        )
    }

    private fun updateTodo(id: String, newTitle: String) {
        val updated = _uiState.value.items.map { item ->
            if (item.id == id) item.copy(title = newTitle) else item
        }

        updateState(
            _uiState.value.copy(
                items = updated,
                editingId = null,
                inputTitle = "",
                inputError = null
            )
        )
    }

    private fun updateState(state: TodoListUiState) {
        val filtered = when (state.filter) {
            TodoFilter.ALL -> state.items
            TodoFilter.IN_PROGRESS -> state.items.filter { !it.isDone }
            TodoFilter.DONE -> state.items.filter { it.isDone }
        }

        val query = state.query.trim().lowercase()
        val searched = if (query.isBlank()) {
            filtered
        } else {
            filtered.filter { it.title.lowercase().contains(query) }
        }

        val sorted = when (state.sort) {
            TodoSort.DATE -> searched.sortedByDescending { it.id.toLongOrNull() ?: 0L }
            TodoSort.TITLE -> searched.sortedBy { it.title.lowercase() }
        }

        _uiState.value = state.copy(visibleItems = sorted)
    }

    private fun generateId(): String {
        val id = nextId
        nextId += 1
        return id.toString()
    }
}
