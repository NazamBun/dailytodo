package com.nazam.dailytodo.feature.todo.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.dailytodo.core.time.nowMillis
import com.nazam.dailytodo.feature.todo.presentation.model.TodoCategory
import com.nazam.dailytodo.feature.todo.presentation.model.TodoFilter
import com.nazam.dailytodo.feature.todo.presentation.model.TodoItemUi
import com.nazam.dailytodo.feature.todo.presentation.model.TodoSort
import com.nazam.dailytodo.feature.todo.presentation.viewmodel.TodoListViewModel
import kotlin.math.abs
import kotlin.math.ceil

/**
 * Refonte UI:
 * - FAB "+" pour ajouter
 * - BottomSheet (popup en bas) pour ajouter / modifier
 * - Liste en cards (plus joli)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel
) {
    val state by viewModel.uiState.collectAsState()

    // "now" une seule fois (perf)
    val now = nowMillis()

    // BottomSheet open/close
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = { TopAppBar(title = { Text("DailyTodo") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onAddRequested()
                    isSheetOpen = true
                }
            ) { Text("+") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchBar(query = state.query, onQueryChanged = viewModel::onQueryChanged)

            ChoiceRow(
                items = listOf(TodoFilter.ALL, TodoFilter.IN_PROGRESS, TodoFilter.DONE),
                selected = state.filter,
                label = { f ->
                    when (f) {
                        TodoFilter.ALL -> "Toutes"
                        TodoFilter.IN_PROGRESS -> "En cours"
                        TodoFilter.DONE -> "Terminées"
                    }
                },
                onSelected = viewModel::onFilterSelected
            )

            ChoiceRow(
                items = listOf(TodoSort.DATE, TodoSort.TITLE),
                selected = state.sort,
                label = { s ->
                    when (s) {
                        TodoSort.DATE -> "Date"
                        TodoSort.TITLE -> "Titre"
                    }
                },
                onSelected = viewModel::onSortSelected
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = state.visibleItems, key = { it.id }) { item ->
                    TodoCard(
                        item = item,
                        nowMillis = now,
                        onClick = {
                            viewModel.onEditRequested(item.id)
                            isSheetOpen = true
                        },
                        onDoneChanged = { checked -> viewModel.onDoneToggled(item.id, checked) },
                        onDeleteClick = { viewModel.onDeleteClicked(item.id) }
                    )
                }
            }
        }
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                isSheetOpen = false
                viewModel.onCancelClicked()
            },
            sheetState = sheetState
        ) {
            TodoFormSheet(
                title = state.inputTitle,
                error = state.inputError,
                selectedCategory = state.selectedCategory,
                inputDueDateMillis = state.inputDueDateMillis,
                isEditing = state.editingId != null,
                nowMillis = now,
                onTitleChanged = viewModel::onTitleChanged,
                onCategorySelected = viewModel::onCategorySelected,
                onAddDays = viewModel::onDueDateAddDays,
                onClearDue = viewModel::onDueDateCleared,
                onCancel = {
                    isSheetOpen = false
                    viewModel.onCancelClicked()
                },
                onSave = {
                    val ok = viewModel.onSaveClicked()
                    if (ok) isSheetOpen = false
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
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
        label = { Text("Rechercher") }
    )
}

@Composable
private fun <T> ChoiceRow(
    items: List<T>,
    selected: T,
    label: (T) -> String,
    onSelected: (T) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            val isSelected = item == selected
            Button(onClick = { onSelected(item) }) {
                Text(if (isSelected) "✓ ${label(item)}" else label(item))
            }
        }
    }
}

@Composable
private fun TodoCard(
    item: TodoItemUi,
    nowMillis: Long,
    onClick: () -> Unit,
    onDoneChanged: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    val due = item.dueDateMillis
    val isOverdue = due != null && !item.isDone && due < nowMillis

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(checked = item.isDone, onCheckedChange = onDoneChanged)

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                Text(text = item.category.label, style = MaterialTheme.typography.bodySmall)

                if (due != null) {
                    val label = buildDueLabel(nowMillis, due)
                    Text(
                        text = if (isOverdue) "EN RETARD • $label" else "Date limite • $label",
                        color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            TextButton(onClick = onDeleteClick) { Text("Suppr.") }
        }
    }
}

@Composable
private fun TodoFormSheet(
    title: String,
    error: String?,
    selectedCategory: TodoCategory,
    inputDueDateMillis: Long?,
    isEditing: Boolean,
    nowMillis: Long,
    onTitleChanged: (String) -> Unit,
    onCategorySelected: (TodoCategory) -> Unit,
    onAddDays: (Int) -> Unit,
    onClearDue: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isEditing) "Modifier la tâche" else "Nouvelle tâche",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Titre") }
        )

        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Text("Catégorie", style = MaterialTheme.typography.titleSmall)
        ChoiceRow(
            items = listOf(TodoCategory.PERSONAL, TodoCategory.WORK, TodoCategory.SPORT),
            selected = selectedCategory,
            label = { it.label },
            onSelected = onCategorySelected
        )

        Text("Date limite", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onAddDays(1) }) { Text("+1j") }
            Button(onClick = { onAddDays(3) }) { Text("+3j") }
            Button(onClick = { onAddDays(7) }) { Text("+7j") }
            Button(onClick = onClearDue) { Text("Aucune") }
        }

        if (inputDueDateMillis != null) {
            Text(
                text = "Choisie: ${buildDueLabel(nowMillis, inputDueDateMillis)}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(modifier = Modifier.weight(1f), onClick = onCancel) { Text("Annuler") }
            Button(modifier = Modifier.weight(1f), onClick = onSave) { Text("Enregistrer") }
        }
    }
}

private fun buildDueLabel(now: Long, due: Long): String {
    val diff = due - now
    val dayMs = 24.0 * 60.0 * 60.0 * 1000.0
    val days = ceil(abs(diff).toDouble() / dayMs).toInt().coerceAtLeast(0)
    return if (diff < 0) "il y a $days j" else "dans $days j"
}
