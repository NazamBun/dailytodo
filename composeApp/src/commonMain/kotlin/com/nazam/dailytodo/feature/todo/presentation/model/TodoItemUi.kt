package com.nazam.dailytodo.feature.todo.presentation.model

/**
 * Modèle UI (presentation layer).
 * Simple et suffisant pour afficher une liste.
 */
data class TodoItemUi(
    val id: String,
    val title: String,
    val isDone: Boolean,
    val category: TodoCategory
)
