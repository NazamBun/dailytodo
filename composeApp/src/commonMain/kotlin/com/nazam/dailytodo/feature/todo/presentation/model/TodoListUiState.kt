package com.nazam.dailytodo.feature.todo.presentation.model

/**
 * Etat de l'écran "Liste des tâches".
 *
 * On ajoute:
 * - inputTitle : texte tapé par l'utilisateur
 * - inputError : message d'erreur simple si le titre est vide
 */
data class TodoListUiState(
    val items: List<TodoItemUi> = emptyList(),
    val inputTitle: String = "",
    val inputError: String? = null
)
