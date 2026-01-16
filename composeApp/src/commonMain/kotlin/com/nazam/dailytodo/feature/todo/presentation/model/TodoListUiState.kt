package com.nazam.dailytodo.feature.todo.presentation.model

/**
 * Etat de l'écran "Liste des tâches".
 */
data class TodoListUiState(
    val items: List<TodoItemUi> = emptyList(),

    // Liste affichée après filtre + recherche + tri
    val visibleItems: List<TodoItemUi> = emptyList(),

    // Ajout / édition
    val inputTitle: String = "",
    val inputError: String? = null,
    val editingId: String? = null,

    // Filtre + tri
    val filter: TodoFilter = TodoFilter.ALL,
    val sort: TodoSort = TodoSort.DATE,

    // Recherche
    val query: String = ""
)
