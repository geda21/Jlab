package com.example.ui.screens.business

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.BusinessEntity
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabTextField
import com.example.ui.theme.AppTheme

@Composable
fun EditBusinessDialog(
    business: BusinessEntity,
    onDismiss: () -> Unit,
    onSave: (BusinessEntity) -> Unit
) {
    var name by remember { mutableStateOf(business.name) }
    var description by remember { mutableStateOf(business.description) }
    var status by remember { mutableStateOf(business.status) }
    var goals by remember { mutableStateOf(business.goals) }
    var revenueNotes by remember { mutableStateOf(business.revenueNotes) }
    var marketingNotes by remember { mutableStateOf(business.marketingNotes) }
    var customerNotes by remember { mutableStateOf(business.customerNotes) }

    val statusOptions = listOf("ACTIVE", "PAUSED", "PLANNING", "COMPLETED")

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "Edit Business Workspace",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
            ) {
                JlabTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Business Name *",
                    singleLine = true,
                    testTag = "edit_business_name"
                )

                Text(
                    text = "STATUS",
                    style = AppTheme.extendedTypography.overline,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )

                JlabFilterChipRow(
                    options = statusOptions,
                    selectedOption = status.uppercase(),
                    onSelect = { status = it },
                    contentPadding = PaddingValues(AppTheme.spacing.none)
                )

                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Value Proposition",
                    singleLine = false,
                    minLines = 2,
                    testTag = "edit_business_desc"
                )
                JlabTextField(
                    value = goals,
                    onValueChange = { goals = it },
                    label = "Growth Goals",
                    singleLine = false,
                    minLines = 2,
                    testTag = "edit_business_goals"
                )
                JlabTextField(
                    value = revenueNotes,
                    onValueChange = { revenueNotes = it },
                    label = "Revenue / Pricing Model",
                    singleLine = false,
                    minLines = 2,
                    testTag = "edit_business_revenue"
                )
                JlabTextField(
                    value = marketingNotes,
                    onValueChange = { marketingNotes = it },
                    label = "Marketing Channels",
                    singleLine = false,
                    minLines = 2,
                    testTag = "edit_business_marketing"
                )
                JlabTextField(
                    value = customerNotes,
                    onValueChange = { customerNotes = it },
                    label = "Target Customers",
                    singleLine = false,
                    minLines = 2,
                    testTag = "edit_business_customers"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Save",
                enabled = name.isNotBlank(),
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            business.copy(
                                name = name.trim(),
                                description = description.trim(),
                                status = status,
                                goals = goals.trim(),
                                revenueNotes = revenueNotes.trim(),
                                marketingNotes = marketingNotes.trim(),
                                customerNotes = customerNotes.trim(),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                },
                testTag = "edit_business_save"
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Cancel", style = AppTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
fun CreateBusinessTaskDialog(
    businessId: String,
    onDismiss: () -> Unit,
    onSave: (TaskEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIUM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "New Business Task",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Task Title *",
                    singleLine = true,
                    testTag = "biz_task_title_input"
                )
                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    singleLine = false,
                    minLines = 2,
                    testTag = "biz_task_desc_input"
                )
                Text(
                    text = "PRIORITY",
                    style = AppTheme.extendedTypography.overline,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                JlabFilterChipRow(
                    options = listOf("LOW", "MEDIUM", "HIGH", "CRITICAL"),
                    selectedOption = priority,
                    onSelect = { priority = it },
                    contentPadding = PaddingValues(AppTheme.spacing.none)
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Add Task",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            TaskEntity(
                                title = title.trim(),
                                description = description.trim(),
                                priority = priority,
                                businessId = businessId,
                                category = "Business"
                            )
                        )
                    }
                },
                testTag = "biz_task_save"
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Cancel", style = AppTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
fun CreateBusinessGoalDialog(
    businessId: String,
    onDismiss: () -> Unit,
    onSave: (GoalEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "New Business Goal",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Goal Title *",
                    singleLine = true,
                    testTag = "biz_goal_title_input"
                )
                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Target & Success Metric",
                    singleLine = false,
                    minLines = 2,
                    testTag = "biz_goal_desc_input"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Create Goal",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            GoalEntity(
                                title = title.trim(),
                                description = description.trim(),
                                businessId = businessId,
                                goalType = "Business",
                                progressPercent = 0
                            )
                        )
                    }
                },
                testTag = "biz_goal_save"
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Cancel", style = AppTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
fun LinkProjectDialog(
    unlinkedProjects: List<ProjectEntity>,
    onDismiss: () -> Unit,
    onLinkExisting: (String) -> Unit,
    onCreateNew: (ProjectEntity) -> Unit
) {
    var showCreateNew by remember { mutableStateOf(unlinkedProjects.isEmpty()) }
    var newProjectName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = if (showCreateNew) "Create Project for Business" else "Link Project to Business",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)) {
                if (!showCreateNew && unlinkedProjects.isNotEmpty()) {
                    Text(
                        text = "Select an existing project:",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)
                    ) {
                        items(unlinkedProjects, key = { it.id }) { proj ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(AppTheme.extendedShapes.chip)
                                    .clickable { onLinkExisting(proj.id) }
                                    .minimumInteractiveComponentSize(),
                                shape = AppTheme.extendedShapes.chip,
                                color = AppTheme.colorScheme.surfaceContainerHigh
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AppTheme.spacing.compactCardPadding),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = proj.name,
                                        style = AppTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Link",
                                        style = AppTheme.typography.labelLarge,
                                        color = AppTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    TextButton(
                        onClick = { showCreateNew = true },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text(
                            text = "+ Or create a new project instead",
                            style = AppTheme.typography.labelLarge,
                            color = AppTheme.colorScheme.primary
                        )
                    }
                } else {
                    JlabTextField(
                        value = newProjectName,
                        onValueChange = { newProjectName = it },
                        label = "Project Name *",
                        singleLine = true,
                        testTag = "link_project_new_name"
                    )
                }
            }
        },
        confirmButton = {
            if (showCreateNew) {
                JlabPrimaryButton(
                    text = "Create Project",
                    enabled = newProjectName.isNotBlank(),
                    onClick = {
                        if (newProjectName.isNotBlank()) {
                            onCreateNew(ProjectEntity(name = newProjectName.trim(), category = "Business"))
                        }
                    },
                    testTag = "link_project_create_confirm"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Cancel", style = AppTheme.typography.labelLarge)
            }
        }
    )
}
