package com.nazam.dailytodo.feature.todo.presentation.ui

import androidx.compose.material3.MaterialTheme

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nazam.dailytodo.core.time.nowMillis
import com.nazam.dailytodo.feature.todo.presentation.model.TodoCategory
import com.nazam.dailytodo.feature.todo.presentation.model.TodoFilter
import com.nazam.dailytodo.feature.todo.presentation.model.TodoItemUi
import com.nazam.dailytodo.feature.todo.presentation.model.TodoSort
import com.nazam.dailytodo.feature.todo.presentation.viewmodel.TodoListViewModel
import com.nazam.dailytodo.ui.dimens.DailyTodoDimens
import com.nazam.dailytodo.ui.strings.DailyTodoStrings
import com.nazam.dailytodo.ui.theme.DailyTodoColors
import kotlin.math.abs
import kotlin.math.ceil

/**
 * UI PRO (comme ton image):
 * - Fond dégradé bleu
 * - Cartes blanches avec ombres douces
 * - Header + Tabs transparents
 * - FAB blanc + texte bleu
 *
 * ✅ KMP friendly
 * ✅ Factorisé (strings + dimens)
 * ✅ On ne change PAS la logique du ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel
) {
    val state by viewModel.uiState.collectAsState()

    val now = nowMillis()

    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ✅ Fond dégradé (comme ton image)
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Scaffold(
            // ✅ IMPORTANT: transparent pour voir le dégradé
            containerColor = Color.Transparent,
            topBar = {
                Column {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        title = {
                            Column(verticalArrangement = Arrangement.spacedBy(DailyTodoDimens.SmallSpacing)) {
                                Text(
                                    text = DailyTodoStrings.AppName,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White
                                )
                                Text(
                                    text = DailyTodoStrings.Subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.80f)
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
                    },
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 10.dp)
                ) {
                    Text(
                        text = DailyTodoStrings.FabPlus,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(
                        horizontal = DailyTodoDimens.ScreenPadding,
                        vertical = DailyTodoDimens.ScreenPaddingVertical
                    ),
                verticalArrangement = Arrangement.spacedBy(DailyTodoDimens.MediumSpacing)
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
                sheetState = sheetState,
                containerColor = Color.White
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

                Spacer(modifier = Modifier.height(DailyTodoDimens.ScreenPaddingVertical))
            }
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
        containerColor = Color.Transparent,
        indicator = {}
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
                        .padding(vertical = DailyTodoDimens.TabPaddingVertical),
                    verticalArrangement = Arrangement.spacedBy(DailyTodoDimens.MediumSpacing)
                ) {
                    Text(
                        text = title,
                        style = if (isSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // ✅ 1 seule barre (instagram)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(DailyTodoDimens.TabIndicatorHeight)
                            .clip(RoundedCornerShape(DailyTodoDimens.RadiusPill))
                            .background(
                                if (isSelected) Color.White else Color.Transparent
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
        shape = RoundedCornerShape(DailyTodoDimens.RadiusCard),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(DailyTodoDimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(DailyTodoDimens.MediumSpacing)
        ) {
            val searchBrush = Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(DailyTodoDimens.RadiusPill))
                    .background(searchBrush)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(DailyTodoDimens.RadiusPill),
                    label = { Text(DailyTodoStrings.SearchLabel) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = Color.White.copy(alpha = 0.90f),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.85f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Text(
                text = DailyTodoStrings.SortTitle,
                style = MaterialTheme.typography.labelLarge
            )

            Row(horizontalArrangement = Arrangement.spacedBy(DailyTodoDimens.ChipRowSpacing)) {
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(DailyTodoDimens.ItemSpacing)
    ) {
        items(
            items = items,
            key = { it.id }
        ) { todo ->
            TodoItemCard(
                item = todo,
                nowMillis = nowMillis,
                onClick = { onItemClick(todo.id) },
                onDoneChanged = { checked -> onDoneChanged(todo.id, checked) },
                onDeleteClick = { onDeleteClick(todo.id) }
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(DailyTodoDimens.RadiusCard),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DailyTodoDimens.CardPadding),
            horizontalArrangement = Arrangement.spacedBy(DailyTodoDimens.MediumSpacing)
        ) {
            Checkbox(
                checked = item.isDone,
                onCheckedChange = onDoneChanged
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(DailyTodoDimens.SmallSpacing)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = item.category.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (due != null) {
                    val label = buildDueLabel(nowMillis, due)
                    Text(
                        text = if (isOverdue) "${DailyTodoStrings.Overdue} • $label"
                        else "${DailyTodoStrings.DueDate} • $label",
                        color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            TextButton(onClick = onDeleteClick) { Text(DailyTodoStrings.Delete) }
        }
    }
}

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
            .padding(horizontal = DailyTodoDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(DailyTodoDimens.MediumSpacing)
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
        Row(horizontalArrangement = Arrangement.spacedBy(DailyTodoDimens.ChipRowSpacing)) {
            CategoryChip(TodoCategory.PERSONAL, selectedCategory, onCategorySelected)
            CategoryChip(TodoCategory.WORK, selectedCategory, onCategorySelected)
            CategoryChip(TodoCategory.SPORT, selectedCategory, onCategorySelected)
        }

        Text(DailyTodoStrings.DueDate, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(DailyTodoDimens.ChipRowSpacing)) {
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
            horizontalArrangement = Arrangement.spacedBy(DailyTodoDimens.ChipRowSpacing)
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
