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
import com.nazam.dailytodo.feature.todo.presentation.model.TodoCategory
import com.nazam.dailytodo.feature.todo.presentation.model.TodoFilter
import com.nazam.dailytodo.feature.todo.presentation.model.TodoItemUi
import com.nazam.dailytodo.feature.todo.presentation.model.TodoSort
import com.nazam.dailytodo.feature.todo.presentation.viewmodel.TodoListViewModel

/**
 * Etape 9: Catégories
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

        FilterRow(
            selected = state.filter,
            onSelected = viewModel::onFilterSelected
        )

        SortRow(
            selected = state.sort,
            onSelected = viewModel::onSortSelected
        )

        SearchBar(
            query = state.query,
            onQueryChanged = viewModel::onQueryChanged
        )

        CategoryRow(
            selected = state.selectedCategory,
            onSelected = viewModel::onCategorySelected
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
                items = state.visibleItems,
                key = { it.id }
            ) { item ->
                TodoRow(
                    item = item,
                    onRowClick = { viewModel.onTodoClicked(item.id) },
                    onDoneChanged = { checked -> viewModel.onDoneToggled(item.id, checked) },
                    onDeleteClick = { viewModel.onDeleteClicked(item.id) }
                )
            }
        }
    }
}

@Composable
private fun FilterRow(
    selected: TodoFilter,
    onSelected: (TodoFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SimpleButton("Toutes", selected == TodoFilter.ALL) { onSelected(TodoFilter.ALL) }
        SimpleButton("En cours", selected == TodoFilter.IN_PROGRESS) { onSelected(TodoFilter.IN_PROGRESS) }
        SimpleButton("Terminées", selected == TodoFilter.DONE) { onSelected(TodoFilter.DONE) }
    }
}

@Composable
private fun SortRow(
    selected: TodoSort,
    onSelected: (TodoSort) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SimpleButton("Date", selected == TodoSort.DATE) { onSelected(TodoSort.DATE) }
        SimpleButton("Titre", selected == TodoSort.TITLE) { onSelected(TodoSort.TITLE) }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Rechercher…") }
    )
}

@Composable
private fun CategoryRow(
    selected: TodoCategory,
    onSelected: (TodoCategory) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryButton(TodoCategory.PERSONAL, selected, onSelected)
        CategoryButton(TodoCategory.WORK, selected, onSelected)
        CategoryButton(TodoCategory.SPORT, selected, onSelected)
    }
}

@Composable
private fun CategoryButton(
    category: TodoCategory,
    selected: TodoCategory,
    onSelected: (TodoCategory) -> Unit
) {
    val isSelected = selected == category
    Button(onClick = { onSelected(category) }) {
        Text(text = if (isSelected) "✓ ${category.label}" else category.label)
    }
}

@Composable
private fun SimpleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text(text = if (isSelected) "✓ $text" else text)
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
    onRowClick: () -> Unit,
    onDoneChanged: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRowClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = item.isDone,
            onCheckedChange = onDoneChanged
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = item.category.label,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(onClick = onDeleteClick) {
            Text("Suppr.")
        }
    }
}
