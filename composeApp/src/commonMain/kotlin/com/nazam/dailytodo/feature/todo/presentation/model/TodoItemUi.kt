package com.nazam.dailytodo.feature.todo.presentation.model

/**
 * Modèle UI (presentation layer).
 * Simple et suffisant pour afficher une liste.
 *
 * Plus tard, le Domain aura son propre modèle (Todo),
 * et on fera un mapper Domain -> UI.
 */
data class TodoItemUi(
    val id: String,
    val title: String,
    val isDone: Boolean
)
