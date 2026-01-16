package com.nazam.dailytodo.feature.todo.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.dailytodo.feature.todo.presentation.viewmodel.TodoListViewModel

/**
 * Ecran "Liste des tâches".
 *
 * Pour l'instant:
 * - on affiche juste la liste (read-only).
 * - ensuite on fera Ajouter / Modifier / Terminer / Supprimer etc.
 */
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mes tâches",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = state.items,
                key = { it.id }
            ) { item ->
                TodoRow(
                    title = item.title,
                    isDone = item.isDone
                )
            }
        }
    }
}

@Composable
private fun TodoRow(
    title: String,
    isDone: Boolean
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Checkbox(
            checked = isDone,
            onCheckedChange = null // read-only pour l'instant
        )
    }
}
