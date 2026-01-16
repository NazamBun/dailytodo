package com.nazam.dailytodo.feature.todo.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.dailytodo.feature.todo.presentation.viewmodel.TodoListViewModel

/**
 * Etape 2: Ajouter
 *
 * - Champ texte + bouton
 * - Affichage erreur si le titre est vide
 * - Liste en dessous
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

        AddTodoSection(
            title = state.inputTitle,
            error = state.inputError,
            onTitleChanged = viewModel::onTitleChanged,
            onAddClicked = viewModel::onAddClicked
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
private fun AddTodoSection(
    title: String,
    error: String?,
    onTitleChanged: (String) -> Unit,
    onAddClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChanged,
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Nouvelle tâche") }
            )
            Button(
                onClick = onAddClicked
            ) {
                Text("Ajouter")
            }
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TodoRow(
    title: String,
    isDone: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = isDone,
            onCheckedChange = null // Etape 4 plus tard
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
