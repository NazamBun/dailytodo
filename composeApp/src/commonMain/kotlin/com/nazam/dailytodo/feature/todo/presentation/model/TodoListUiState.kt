package com.nazam.dailytodo.feature.todo.presentation.model

/**
 * Etat de l'écran "Liste des tâches".
 * Ici on ne gère que l'affichage (pas encore d'erreurs, pas de loading).
 */
data class TodoListUiState(
    val items: List<TodoItemUi> = emptyList()
)
