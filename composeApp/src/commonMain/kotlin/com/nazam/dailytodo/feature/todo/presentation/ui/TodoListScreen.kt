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
import com.nazam.dailytodo.core.time.nowMillis
import com.nazam.dailytodo.feature.todo.presentation.model.TodoCategory
import com.nazam.dailytodo.feature.todo.presentation.model.TodoFilter
import com.nazam.dailytodo.feature.todo.presentation.model.TodoItemUi
import com.nazam.dailytodo.feature.todo.presentation.model.TodoSort
import com.nazam.dailytodo.feature.todo.presentation.viewmodel.TodoListViewModel
import kotlin.math.ceil

/**
 * Etape 10: Date limite + alerte visuelle ("en retard")
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
        Text(text = "Mes tâches", style = MaterialTheme.typography.headlineSmall)

        FilterRow(selected = state.filter, onSelected = viewModel::onFilterSelected)
        SortRow(selected = state.sort, onSelected = viewModel::onSortSelected)
        SearchBar(query = state.query, onQueryChanged = viewModel::onQueryChanged)
        CategoryRow(selected = state.selectedCategory, onSelected = viewModel::onCategorySelected)

        DueDateRow(
            isEditing = isEditing,
            inputDueDateMillis = state.inputDueDateMillis,
            onAddDays = viewModel::onDueDateAddDays,
            onClear = viewModel::onDueDateCleared
        )

        TodoInputSection(
            title = state.inputTitle,
            error = state.inputError,
            isEditing = isEditing,
            onTitleChanged = viewModel::onTitleChanged,
            onPrimaryActionClicked = viewModel::onPrimaryActionClicked,
            onCancelEditClicked = viewModel::onCancelEditClicked
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = state.visibleItems, key = { it.id }) { item ->
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
private fun FilterRow(selected: TodoFilter, onSelected: (TodoFilter) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SimpleButton("Toutes", selected == TodoFilter.ALL) { onSelected(TodoFilter.ALL) }
        SimpleButton("En cours", selected == TodoFilter.IN_PROGRESS) { onSelected(TodoFilter.IN_PROGRESS) }
        SimpleButton("Terminées", selected == TodoFilter.DONE) { onSelected(TodoFilter.DONE) }
    }
}

@Composable
private fun SortRow(selected: TodoSort, onSelected: (TodoSort) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SimpleButton("Date", selected == TodoSort.DATE) { onSelected(TodoSort.DATE) }
        SimpleButton("Titre", selected == TodoSort.TITLE) { onSelected(TodoSort.TITLE) }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Rechercher…") }
    )
}

@Composable
private fun CategoryRow(selected: TodoCategory, onSelected: (TodoCategory) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CategoryButton(TodoCategory.PERSONAL, selected, onSelected)
        CategoryButton(TodoCategory.WORK, selected, onSelected)
        CategoryButton(TodoCategory.SPORT, selected, onSelected)
    }
}

@Composable
private fun DueDateRow(
    isEditing: Boolean,
    inputDueDateMillis: Long?,
    onAddDays: (Int) -> Unit,
    onClear: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = if (isEditing) "Date limite (édition)" else "Date limite (nouvelle)", style = MaterialTheme.typography.bodySmall)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onAddDays(1) }) { Text("+1j") }
            Button(onClick = { onAddDays(3) }) { Text("+3j") }
            Button(onClick = { onAddDays(7) }) { Text("+7j") }
            Button(onClick = onClear) { Text("Aucune") }
        }

        if (inputDueDateMillis != null) {
            val now = nowMillis()
            val label = buildDueLabel(now, inputDueDateMillis)
            Text(text = "Choisie: $label", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun CategoryButton(category: TodoCategory, selected: TodoCategory, onSelected: (TodoCategory) -> Unit) {
    val isSelected = selected == category
    Button(onClick = { onSelected(category) }) {
        Text(text = if (isSelected) "✓ ${category.label}" else category.label)
    }
}

@Composable
private fun SimpleButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick) { Text(text = if (isSelected) "✓ $text" else text) }
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChanged,
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text(if (isEditing) "Modifier la tâche" else "Nouvelle tâche") }
            )

            Button(onClick = onPrimaryActionClicked) { Text(if (isEditing) "Enregistrer" else "Ajouter") }

            if (isEditing) {
                Button(onClick = onCancelEditClicked) { Text("Annuler") }
            }
        }

        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
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
    val now = nowMillis()
    val due = item.dueDateMillis
    val isOverdue = due != null && !item.isDone && due < now

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRowClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(checked = item.isDone, onCheckedChange = onDoneChanged)

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
            Text(text = item.category.label, style = MaterialTheme.typography.bodySmall)

            if (due != null) {
                val label = buildDueLabel(now, due)
                Text(
                    text = if (isOverdue) "EN RETARD • $label" else "Date limite • $label",
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Button(onClick = onDeleteClick) { Text("Suppr.") }
    }
}

private fun buildDueLabel(now: Long, due: Long): String {
    val diff = due - now
    val dayMs = 24.0 * 60.0 * 60.0 * 1000.0
    val days = ceil(kotlin.math.abs(diff) / dayMs).toInt().coerceAtLeast(0)
    return if (diff < 0) "il y a $days j" else "dans $days j"
}
