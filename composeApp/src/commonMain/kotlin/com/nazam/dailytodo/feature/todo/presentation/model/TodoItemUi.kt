package com.nazam.dailytodo.feature.todo.presentation.model

/**
 * Modèle UI (presentation layer).
 *
 * dueDateMillis:
 * - null = pas de date limite
 * - sinon = timestamp en millisecondes
 */
data class TodoItemUi(
    val id: String,
    val title: String,
    val isDone: Boolean,
    val category: TodoCategory,
    val dueDateMillis: Long? = null
)
