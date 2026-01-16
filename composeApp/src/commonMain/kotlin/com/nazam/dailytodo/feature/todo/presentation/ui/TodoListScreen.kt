package com.nazam.dailytodo.feature.todo.presentation.ui

import androidx.compose.foundation.clickable
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
import com.nazam.dailytodo.feature.todo.presentation.model.TodoItemUi
import com.nazam.dailytodo.feature.todo.presentation.viewmodel.TodoListViewModel

/**
 * Etape 3: Modifier
 * - Taper sur une tâche => édition
 * - Bouton devient "Enregistrer"
 * - Bouton "Annuler" pendant l'édition
 */
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val isEditing = state.editingId != null

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

        TodoInputSection(
            title = state.inputTitle,
            error = state.inputError,
            isEditing = isEditing,
            onTitleChanged = viewModel::onTitleChanged,
            onPrimaryActionClicked = viewModel::onPrimaryActionClicked,
            onCancelEditClicked = viewModel::onCancelEditClicked
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = state.items,
                key = { it.id }
            ) { item ->
                TodoRow(
                    item = item,
                    onClick = { viewModel.onTodoClicked(item.id) }
                )
            }
        }
    }
}

@Composable
private fun TodoInputSection(
    title: String,
    error: String?,
    isEditing: Boolean,
    onTitleChanged: (String) -> Unit,
    onPrimaryActionClicked: () -> Unit,
    onCancelEditClicked: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChanged,
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text(if (isEditing) "Modifier la tâche" else "Nouvelle tâche") }
            )

            Button(onClick = onPrimaryActionClicked) {
                Text(if (isEditing) "Enregistrer" else "Ajouter")
            }

            if (isEditing) {
                Button(onClick = onCancelEditClicked) {
                    Text("Annuler")
                }
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
    item: TodoItemUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = item.isDone,
            onCheckedChange = null // Etape 4 plus tard
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
