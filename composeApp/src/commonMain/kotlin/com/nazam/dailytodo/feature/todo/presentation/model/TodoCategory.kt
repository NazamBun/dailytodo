package com.nazam.dailytodo.feature.todo.presentation.model

/**
 * Catégories simples pour commencer.
 * (Plus tard on pourra les rendre dynamiques via DB.)
 */
enum class TodoCategory(val label: String) {
    PERSONAL("Perso"),
    WORK("Travail"),
    SPORT("Sport")
}
