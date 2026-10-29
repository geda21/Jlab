package com.example.ui.screens.goals

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.GoalEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme

@Composable
fun GoalsScreen(
    goals: List<GoalEntity>,
    onSaveGoal: (GoalEntity) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    BackHandler { onNavigateBack() }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Long-Term Goals",
                subtitle = "${goals.size} tracked objectives",
                onNavigationClick = onNavigateBack,
                testTag = "goals_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "Add Goal",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { showCreateDialog = true },
                        testTag = "goals_add_button"
                    )
                }
            )
        },
        floatingActionButton = {
            JlabFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "New Goal",
                onClick = { showCreateDialog = true },
                testTag = "goals_fab"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
        ) {
            if (goals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.Flag,
                        title = "No goals recorded",
                        message = "Set life, business, financial, and technical mastery goals.",
                        actionButtonText = "New Goal",
                        onActionClick = { showCreateDialog = true },
                        testTag = "goals_empty_state"
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
                    items(goals, key = { it.id }) { goal ->
                        JlabCard(testTag = "goal_card_${goal.id}") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                JlabTagBadge(
                                    text = goal.goalType,
                                    containerColor = AppTheme.colorScheme.primaryContainer,
                                    contentColor = AppTheme.colorScheme.onPrimaryContainer
                                )

                                JlabIconButton(
                                    icon = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Goal",
                                    tint = AppTheme.colorScheme.error,
                                    onClick = { onDeleteGoal(goal.id) },
                                    testTag = "goal_delete_${goal.id}"
                                )
                            }

                            Spacer(modifier = Modifier.height(AppTheme.spacing.xs))

                            Text(
                                text = goal.title,
                                style = AppTheme.typography.titleLarge,
                                color = AppTheme.colorScheme.onSurface
                            )

                            if (goal.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                                Text(
                                    text = goal.description,
                                    style = AppTheme.typography.bodyMedium,
                                    color = AppTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(AppTheme.spacing.smPlus))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progress",
                                    style = AppTheme.typography.labelMedium,
                                    color = AppTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${goal.progressPercent}%",
                                    style = AppTheme.typography.labelMedium,
                                    color = AppTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(AppTheme.spacing.xs))

                            JlabLinearProgress(
                                progress = goal.progressPercent / 100f
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateGoalDialog(
            onDismiss = { showCreateDialog = false },
            onSave = {
                onSaveGoal(it)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CreateGoalDialog(
    onDismiss: () -> Unit,
    onSave: (GoalEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var goalType by remember { mutableStateOf("Personal") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.large,
        containerColor = AppTheme.colorScheme.surfaceContainerHigh,
        title = { Text("New Goal", style = AppTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Goal Title *",
                    testTag = "goal_title_input"
                )

                Text(
                    text = "GOAL TYPE",
                    style = AppTheme.extendedTypography.overline,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )

                JlabFilterChipRow(
                    options = listOf("Personal", "Business", "Financial", "Learning"),
                    selectedOption = goalType,
                    onSelect = { goalType = it },
                    contentPadding = PaddingValues(0.dp)
                )

                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description & Milestones",
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    testTag = "goal_description_input"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Save Goal",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            GoalEntity(
                                title = title.trim(),
                                description = description.trim(),
                                goalType = goalType
                            )
                        )
                    }
                },
                testTag = "goal_save_button"
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
