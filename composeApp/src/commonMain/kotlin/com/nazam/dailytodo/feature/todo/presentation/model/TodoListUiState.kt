package com.nazam.dailytodo.feature.todo.presentation.model

/**
 * Etat de l'écran "Liste des tâches".
 */
data class TodoListUiState(
    val items: List<TodoItemUi> = emptyList(),

    // Liste affichée après filtre
    val visibleItems: List<TodoItemUi> = emptyList(),

    val inputTitle: String = "",
    val inputError: String? = null,
    val editingId: String? = null,

    val filter: TodoFilter = TodoFilter.ALL
)
