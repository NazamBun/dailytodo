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
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
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
 * UI Premium (KMP friendly) :
 * - Header premium (titre + sous-titre)
 * - Tabs style "Instagram" (Toutes / En cours / Terminées)
 * - Search + Tri en Card
 * - Liste en ElevatedCard
 * - FAB "+"
 * - BottomSheet propre pour Ajouter / Modifier
 *
 * ✅ On ne touche pas la logique du ViewModel.
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "DailyTodo",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Organise ta journée",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f)
                            )
                        }
                    }
                )

                // ✅ Tabs "Instagram"
                FilterTabs(
                    selected = state.filter,
                    onSelected = viewModel::onFilterSelected
                )
            }
        },
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchAndSortCard(
                query = state.query,
                onQueryChanged = viewModel::onQueryChanged,
                sort = state.sort,
                onSortSelected = viewModel::onSortSelected
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = state.visibleItems, key = { it.id }) { item ->
                    TodoItemCard(
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
                isEditing = state.editingId != null,
                title = state.inputTitle,
                error = state.inputError,
                selectedCategory = state.selectedCategory,
                inputDueDateMillis = state.inputDueDateMillis,
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
private fun FilterTabs(
    selected: TodoFilter,
    onSelected: (TodoFilter) -> Unit
) {
    val tabs = listOf(
        TodoFilter.ALL to "Toutes",
        TodoFilter.IN_PROGRESS to "En cours",
        TodoFilter.DONE to "Terminées"
    )

    val selectedIndex = tabs.indexOfFirst { it.first == selected }.coerceAtLeast(0)

    SecondaryTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        tabs.forEachIndexed { index, (filter, title) ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onSelected(filter) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
private fun SearchAndSortCard(
    query: String,
    onQueryChanged: (String) -> Unit,
    sort: TodoSort,
    onSortSelected: (TodoSort) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Rechercher") }
            )

            Text("Tri", style = MaterialTheme.typography.labelLarge)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SortChip(
                    text = "Date",
                    selected = sort == TodoSort.DATE,
                    onClick = { onSortSelected(TodoSort.DATE) }
                )
                SortChip(
                    text = "Titre",
                    selected = sort == TodoSort.TITLE,
                    onClick = { onSortSelected(TodoSort.TITLE) }
                )
            }
        }
    }
}

@Composable
private fun SortChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(if (selected) "✓ $text" else text) }
    )
}

@Composable
private fun TodoItemCard(
    item: TodoItemUi,
    nowMillis: Long,
    onClick: () -> Unit,
    onDoneChanged: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    val due = item.dueDateMillis
    val isOverdue = due != null && !item.isDone && due < nowMillis

    ElevatedCard(
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
            Checkbox(
                checked = item.isDone,
                onCheckedChange = onDoneChanged
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
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

/**
 * BottomSheet (formulaire)
 * - Simple, lisible, et compatible KMP
 */
@Composable
private fun TodoFormSheet(
    isEditing: Boolean,
    title: String,
    error: String?,
    selectedCategory: TodoCategory,
    inputDueDateMillis: Long?,
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
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text("Catégorie", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryChip(TodoCategory.PERSONAL, selectedCategory, onCategorySelected)
            CategoryChip(TodoCategory.WORK, selectedCategory, onCategorySelected)
            CategoryChip(TodoCategory.SPORT, selectedCategory, onCategorySelected)
        }

        Text("Date limite", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = false, onClick = { onAddDays(1) }, label = { Text("+1j") })
            FilterChip(selected = false, onClick = { onAddDays(3) }, label = { Text("+3j") })
            FilterChip(selected = false, onClick = { onAddDays(7) }, label = { Text("+7j") })
            FilterChip(selected = inputDueDateMillis == null, onClick = onClearDue, label = { Text("Aucune") })
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
            TextButton(modifier = Modifier.weight(1f), onClick = onCancel) { Text("Annuler") }
            TextButton(modifier = Modifier.weight(1f), onClick = onSave) { Text("Enregistrer") }
        }
    }
}

@Composable
private fun CategoryChip(
    category: TodoCategory,
    selected: TodoCategory,
    onSelected: (TodoCategory) -> Unit
) {
    val isSelected = category == selected
    FilterChip(
        selected = isSelected,
        onClick = { onSelected(category) },
        label = { Text(if (isSelected) "✓ ${category.label}" else category.label) }
    )
}

private fun buildDueLabel(now: Long, due: Long): String {
    val diff = due - now
    val dayMs = 24.0 * 60.0 * 60.0 * 1000.0
    val days = ceil(abs(diff).toDouble() / dayMs).toInt().coerceAtLeast(0)
    return if (diff < 0) "il y a $days j" else "dans $days j"
}
