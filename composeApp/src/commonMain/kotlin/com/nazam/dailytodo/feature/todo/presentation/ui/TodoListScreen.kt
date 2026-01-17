package com.nazam.dailytodo.feature.todo.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nazam.dailytodo.core.time.nowMillis
import com.nazam.dailytodo.feature.todo.presentation.model.TodoCategory
import com.nazam.dailytodo.feature.todo.presentation.model.TodoFilter
import com.nazam.dailytodo.feature.todo.presentation.model.TodoItemUi
import com.nazam.dailytodo.feature.todo.presentation.model.TodoSort
import com.nazam.dailytodo.feature.todo.presentation.viewmodel.TodoListViewModel
import com.nazam.dailytodo.ui.strings.DailyTodoStrings
import kotlin.math.abs
import kotlin.math.ceil

/**
 * UI Premium (KMP friendly) :
 * - Header premium (titre + sous-titre)
 * - Tabs style "Instagram" (barre sous l'onglet actif) => sans tabIndicatorOffset (stable)
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
                                text = DailyTodoStrings.AppName,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = DailyTodoStrings.Subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f)
                            )
                        }
                    }
                )

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
            ) { Text(DailyTodoStrings.FabPlus) }
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

            TodoList(
                items = state.visibleItems,
                nowMillis = now,
                onItemClick = { id ->
                    viewModel.onEditRequested(id)
                    isSheetOpen = true
                },
                onDoneChanged = { id, checked -> viewModel.onDoneToggled(id, checked) },
                onDeleteClick = { id -> viewModel.onDeleteClicked(id) }
            )
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
        TodoFilter.ALL to DailyTodoStrings.TabAll,
        TodoFilter.IN_PROGRESS to DailyTodoStrings.TabInProgress,
        TodoFilter.DONE to DailyTodoStrings.TabDone
    )

    val selectedIndex = tabs.indexOfFirst { it.first == selected }.coerceAtLeast(0)

    SecondaryTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        tabs.forEachIndexed { index, (filter, title) ->
            val isSelected = index == selectedIndex

            Tab(
                selected = isSelected,
                onClick = { onSelected(filter) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = if (isSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
                    )

                    // ✅ barre "Instagram" (sans tabIndicatorOffset)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                    )
                }
            }
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(DailyTodoStrings.SearchLabel) }
            )

            Text(DailyTodoStrings.SortTitle, style = MaterialTheme.typography.labelLarge)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SortChip(
                    text = DailyTodoStrings.SortDate,
                    selected = sort == TodoSort.DATE,
                    onClick = { onSortSelected(TodoSort.DATE) }
                )
                SortChip(
                    text = DailyTodoStrings.SortTitleAlpha,
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
private fun TodoList(
    items: List<TodoItemUi>,
    nowMillis: Long,
    onItemClick: (String) -> Unit,
    onDoneChanged: (String, Boolean) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        androidx.compose.foundation.lazy.items(items, key = { it.id }) { item ->
            TodoItemCard(
                item = item,
                nowMillis = nowMillis,
                onClick = { onItemClick(item.id) },
                onDoneChanged = { checked -> onDoneChanged(item.id, checked) },
                onDeleteClick = { onDeleteClick(item.id) }
            )
        }
    }
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
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            androidx.compose.material3.Checkbox(
                checked = item.isDone,
                onCheckedChange = onDoneChanged
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                Text(text = item.category.label, style = MaterialTheme.typography.bodySmall)

                if (due != null) {
                    val label = buildDueLabel(nowMillis, due)
                    Text(
                        text = if (isOverdue) "${DailyTodoStrings.Overdue} • $label" else "${DailyTodoStrings.DueDate} • $label",
                        color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.80f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            TextButton(onClick = onDeleteClick) { Text(DailyTodoStrings.Delete) }
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
            text = if (isEditing) DailyTodoStrings.SheetEdit else DailyTodoStrings.SheetNew,
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(DailyTodoStrings.TitleLabel) }
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(DailyTodoStrings.CategoryTitle, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryChip(TodoCategory.PERSONAL, selectedCategory, onCategorySelected)
            CategoryChip(TodoCategory.WORK, selectedCategory, onCategorySelected)
            CategoryChip(TodoCategory.SPORT, selectedCategory, onCategorySelected)
        }

        Text(DailyTodoStrings.DueDate, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = false, onClick = { onAddDays(1) }, label = { Text(DailyTodoStrings.Add1d) })
            FilterChip(selected = false, onClick = { onAddDays(3) }, label = { Text(DailyTodoStrings.Add3d) })
            FilterChip(selected = false, onClick = { onAddDays(7) }, label = { Text(DailyTodoStrings.Add7d) })
            FilterChip(selected = inputDueDateMillis == null, onClick = onClearDue, label = { Text(DailyTodoStrings.NoDue) })
        }

        if (inputDueDateMillis != null) {
            Text(
                text = "${DailyTodoStrings.DueChosen}: ${buildDueLabel(nowMillis, inputDueDateMillis)}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(modifier = Modifier.weight(1f), onClick = onCancel) { Text(DailyTodoStrings.Cancel) }
            TextButton(modifier = Modifier.weight(1f), onClick = onSave) { Text(DailyTodoStrings.Save) }
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
