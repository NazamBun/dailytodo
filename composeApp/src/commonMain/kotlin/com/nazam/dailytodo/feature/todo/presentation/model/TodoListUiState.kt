package com.nazam.dailytodo.feature.todo.presentation.model

/**
 * Etat de l'écran "Liste des tâches".
 *
 * On ajoute:
 * - editingId : id de la tâche qu'on est en train de modifier (null = pas d'édition)
 */
data class TodoListUiState(
    val items: List<TodoItemUi> = emptyList(),
    val inputTitle: String = "",
    val inputError: String? = null,
    val editingId: String? = null
)
