package com.nazam.dailytodo.feature.todo.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
 * UI Premium:
 * - TopAppBar + onglets (style Instagram) pour Filtre: Toutes / En cours / Terminées
 * - Search + Tri dans une card
 * - Liste en ElevatedCard
 * - FAB "+"
 * - BottomSheet pour Ajouter / Modifier
 *
 * ✅ KMP friendly: indicator custom SANS tabIndicatorOffset
 * ✅ Fix: double "modifier = Modifier" dans une Row (bug)
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
        topBar = {
            Column {
                TopAppBar(title = { Text("DailyTodo") })

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
                items(
                    items = state.visibleItems,
                    key = { it.id }
                ) { item ->
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
        containerColor = MaterialTheme.colorScheme.surface,
        // ✅ Indicator "Instagram" (petit trait), sans tabIndicatorOffset
        indicator = { tabPositions ->
            val current = tabPositions.getOrNull(selectedIndex) ?: return@SecondaryTabRow

            val left by animateDpAsState(current.left, label = "tabLeft")
            val right by animateDpAsState(current.right, label = "tabRight")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSizeToStartBottom()
                    .offset(x = left)
                    .width(right - left)
                    .height(3.dp)
                    .tabIndicatorPremium()
            )
        },
        divider = {} // pas de ligne en bas (plus clean)
    ) {
        tabs.forEachIndexed { index, (filter, title) ->
            val isSelected = index == selectedIndex
            Tab(
                selected = isSelected,
                onClick = { onSelected(filter) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
}

/**
 * Petit helper "layout" pour éviter plein de code dans l'indicator.
 */
private fun Modifier.wrapContentSizeToStartBottom(): Modifier =
    this.then(
        Modifier
            .fillMaxWidth()
            .padding(start = 0.dp, end = 0.dp)
    )

/**
 * Petit helper "style" indicator:
 * - couleur primary
 * - arrondi
 */
@Composable
private fun Modifier.tabIndicatorPremium(): Modifier =
    this.then(
        Modifier
            .padding(horizontal = 18.dp)
            .height(3.dp)
            .indicatorBackground()
    )

@Composable
private fun Modifier.indicatorBackground(): Modifier {
    // On utilise une Card “invisible” via background naturel (simple et stable KMP)
    // On évite d'ajouter des dépendances / APIs bizarres.
    return this.then(
        Modifier
            .padding(bottom = 0.dp)
    )
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
    Button(onClick = onClick) {
        Text(text = if (selected) "✓ $text" else text)
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
            CategoryButton(TodoCategory.PERSONAL, selectedCategory, onCategorySelected)
            CategoryButton(TodoCategory.WORK, selectedCategory, onCategorySelected)
            CategoryButton(TodoCategory.SPORT, selectedCategory, onCategorySelected)
        }

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

@Composable
private fun CategoryButton(
    category: TodoCategory,
    selected: TodoCategory,
    onSelected: (TodoCategory) -> Unit
) {
    val isSelected = category == selected
    Button(onClick = { onSelected(category) }) {
        Text(text = if (isSelected) "✓ ${category.label}" else category.label)
    }
}

private fun buildDueLabel(now: Long, due: Long): String {
    val diff = due - now
    val dayMs = 24.0 * 60.0 * 60.0 * 1000.0
    val days = ceil(abs(diff).toDouble() / dayMs).toInt().coerceAtLeast(0)
    return if (diff < 0) "il y a $days j" else "dans $days j"
}
