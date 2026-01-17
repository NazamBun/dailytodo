package com.nazam.dailytodo.feature.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.nazam.dailytodo.core.time.nowMillis
import com.nazam.dailytodo.feature.todo.presentation.model.TodoCategory
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
 * UI refonte:
 * - Le formulaire (titre/catégorie/date) est utilisé dans un BottomSheet (popup).
 * - Le FAB "+" ouvre le BottomSheet en mode "Ajout".
 * - Le clic sur une tâche ouvre le BottomSheet en mode "Edition".
 */
class TodoListViewModel : ViewModel() {

    private var nextId: Long = 4L

    private val _uiState = MutableStateFlow(
        TodoListUiState(
            items = listOf(
                TodoItemUi(id = "1", title = "Acheter du lait", isDone = false, category = TodoCategory.PERSONAL, dueDateMillis = null),
                TodoItemUi(id = "2", title = "Faire 20 minutes de sport", isDone = true, category = TodoCategory.SPORT, dueDateMillis = null),
                TodoItemUi(id = "3", title = "Réviser Kotlin", isDone = false, category = TodoCategory.WORK, dueDateMillis = null),
            ),
            filter = TodoFilter.ALL,
            sort = TodoSort.DATE
        )
    )
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    init {
        updateState(_uiState.value)
    }

    // ----------------------------
    // Actions UI (liste)
    // ----------------------------

    fun onQueryChanged(newQuery: String) {
        updateState(_uiState.value.copy(query = newQuery))
    }

    fun onFilterSelected(filter: TodoFilter) {
        updateState(_uiState.value.copy(filter = filter))
    }

    fun onSortSelected(sort: TodoSort) {
        updateState(_uiState.value.copy(sort = sort))
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
                inputError = if (shouldCancelEdit) null else _uiState.value.inputError,
                selectedCategory = if (shouldCancelEdit) TodoCategory.PERSONAL else _uiState.value.selectedCategory,
                inputDueDateMillis = if (shouldCancelEdit) null else _uiState.value.inputDueDateMillis
            )
        )
    }

    // ----------------------------
    // BottomSheet (formulaire)
    // ----------------------------

    /**
     * Mode AJOUT: reset du formulaire.
     * (Le screen ouvrira le BottomSheet après.)
     */
    fun onAddRequested() {
        updateState(
            _uiState.value.copy(
                editingId = null,
                inputTitle = "",
                inputError = null,
                selectedCategory = TodoCategory.PERSONAL,
                inputDueDateMillis = null
            )
        )
    }

    /**
     * Mode EDITION: on charge la tâche dans le formulaire.
     * (Le screen ouvrira le BottomSheet après.)
     */
    fun onEditRequested(id: String) {
        val item = _uiState.value.items.firstOrNull { it.id == id } ?: return
        updateState(
            _uiState.value.copy(
                editingId = id,
                inputTitle = item.title,
                inputError = null,
                selectedCategory = item.category,
                inputDueDateMillis = item.dueDateMillis
            )
        )
    }

    fun onTitleChanged(newTitle: String) {
        updateState(_uiState.value.copy(inputTitle = newTitle, inputError = null))
    }

    fun onCategorySelected(category: TodoCategory) {
        updateState(_uiState.value.copy(selectedCategory = category))
    }

    fun onDueDateAddDays(days: Int) {
        val due = nowMillis() + days.toMillisDays()
        updateState(_uiState.value.copy(inputDueDateMillis = due))
    }

    fun onDueDateCleared() {
        updateState(_uiState.value.copy(inputDueDateMillis = null))
    }

    /**
     * Save depuis le BottomSheet.
     * @return true si OK (on peut fermer le BottomSheet), false si erreur.
     */
    fun onSaveClicked(): Boolean {
        val title = _uiState.value.inputTitle.trim()
        if (title.isBlank()) {
            updateState(_uiState.value.copy(inputError = "Le titre est obligatoire"))
            return false
        }

        val editingId = _uiState.value.editingId
        return if (editingId == null) {
            addTodo(title)
            true
        } else {
            updateTodo(editingId, title)
            true
        }
    }

    fun onCancelClicked() {
        updateState(
            _uiState.value.copy(
                editingId = null,
                inputTitle = "",
                inputError = null,
                selectedCategory = TodoCategory.PERSONAL,
                inputDueDateMillis = null
            )
        )
    }

    // ----------------------------
    // Private
    // ----------------------------

    private fun addTodo(title: String) {
        val newItem = TodoItemUi(
            id = generateId(),
            title = title,
            isDone = false,
            category = _uiState.value.selectedCategory,
            dueDateMillis = _uiState.value.inputDueDateMillis
        )

        updateState(
            _uiState.value.copy(
                items = listOf(newItem) + _uiState.value.items,
                inputTitle = "",
                inputError = null,
                selectedCategory = TodoCategory.PERSONAL,
                inputDueDateMillis = null,
                editingId = null
            )
        )
    }

    private fun updateTodo(id: String, newTitle: String) {
        val newCategory = _uiState.value.selectedCategory
        val newDue = _uiState.value.inputDueDateMillis

        val updated = _uiState.value.items.map { item ->
            if (item.id == id) item.copy(title = newTitle, category = newCategory, dueDateMillis = newDue) else item
        }

        updateState(
            _uiState.value.copy(
                items = updated,
                editingId = null,
                inputTitle = "",
                inputError = null,
                selectedCategory = TodoCategory.PERSONAL,
                inputDueDateMillis = null
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
        val searched = if (query.isBlank()) filtered else filtered.filter { it.title.lowercase().contains(query) }

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

    private fun Int.toMillisDays(): Long = this.toLong() * 24L * 60L * 60L * 1000L
}
