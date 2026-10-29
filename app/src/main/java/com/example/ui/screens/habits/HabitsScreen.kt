package com.example.ui.screens.habits

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.HabitLogEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme

@Composable
fun HabitsScreen(
    habits: List<HabitEntity>,
    todayLogs: List<HabitLogEntity>,
    onSaveHabit: (HabitEntity) -> Unit,
    onToggleHabit: (String, Boolean) -> Unit,
    onDeleteHabit: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    val completedHabitIds = remember(todayLogs) {
        todayLogs.filter { it.isCompleted }.map { it.habitId }.toSet()
    }

    BackHandler { onNavigateBack() }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Habits & Routines",
                subtitle = "${completedHabitIds.size}/${habits.size} completed today",
                onNavigationClick = onNavigateBack,
                testTag = "habits_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "Add Habit",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { showCreateDialog = true },
                        testTag = "habits_add_button"
                    )
                }
            )
        },
        floatingActionButton = {
            JlabFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "New Habit",
                onClick = { showCreateDialog = true },
                testTag = "habits_fab"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
        ) {
            if (habits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.Repeat,
                        title = "No habits created",
                        message = "Build consistent routines like daily deep work, reading, or exercise.",
                        actionButtonText = "New Habit",
                        onActionClick = { showCreateDialog = true },
                        testTag = "habits_empty_state"
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = AppTheme.spacing.screenHorizontal,
                        end = AppTheme.spacing.screenHorizontal,
                        top = AppTheme.spacing.md,
                        bottom = AppTheme.spacing.listBottomPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
                ) {
                    items(habits, key = { it.id }) { habit ->
                        val isDone = completedHabitIds.contains(habit.id)
                        JlabCard(
                            isSelected = isDone,
                            borderColor = if (isDone) AppTheme.extendedColors.success.copy(alpha = 0.5f) else null,
                            onClick = { onToggleHabit(habit.id, !isDone) },
                            testTag = "habit_card_${habit.id}"
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                JlabIconButton(
                                    icon = if (isDone) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                    contentDescription = if (isDone) "Mark incomplete" else "Mark completed",
                                    tint = if (isDone) AppTheme.extendedColors.success else AppTheme.colorScheme.primary,
                                    onClick = { onToggleHabit(habit.id, !isDone) },
                                    testTag = "habit_toggle_${habit.id}"
                                )

                                Spacer(modifier = Modifier.width(AppTheme.spacing.xs))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = habit.title,
                                        style = AppTheme.typography.titleMedium,
                                        color = AppTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${habit.category} • ${habit.frequency}",
                                        style = AppTheme.extendedTypography.caption,
                                        color = AppTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                JlabIconButton(
                                    icon = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Habit",
                                    tint = AppTheme.colorScheme.error,
                                    onClick = { onDeleteHabit(habit.id) },
                                    testTag = "habit_delete_${habit.id}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateHabitDialog(
            onDismiss = { showCreateDialog = false },
            onSave = {
                onSaveHabit(it)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CreateHabitDialog(
    onDismiss: () -> Unit,
    onSave: (HabitEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Productivity") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.large,
        containerColor = AppTheme.colorScheme.surfaceContainerHigh,
        title = { Text("New Daily Routine", style = AppTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Habit Title *",
                    placeholder = "e.g., Read 20 pages",
                    testTag = "habit_title_input"
                )
                JlabTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = "Category",
                    testTag = "habit_category_input"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Create Habit",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            HabitEntity(
                                title = title.trim(),
                                category = category.trim().ifBlank { "Productivity" }
                            )
                        )
                    }
                },
                testTag = "habit_save_button"
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Cancel")
            }
        }
    )
}
