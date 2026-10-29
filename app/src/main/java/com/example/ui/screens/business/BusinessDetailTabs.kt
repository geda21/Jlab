package com.example.ui.screens.business

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.BusinessEntity
import com.example.data.local.entity.FileAttachmentEntity
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabLinearProgress
import com.example.ui.components.JlabMetricTile
import com.example.ui.components.JlabOutlinedButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSectionCard
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun BusinessOverviewTab(
    business: BusinessEntity,
    goalsCount: Int,
    pendingTasksCount: Int,
    projectsCount: Int,
    notesCount: Int,
    onAddTask: () -> Unit,
    onAddGoal: () -> Unit
) {
    var isDescExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.md,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.mdPlus)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                JlabMetricTile(
                    label = "Goals",
                    value = "$goalsCount",
                    icon = Icons.Outlined.Flag,
                    accentColor = AppTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                JlabMetricTile(
                    label = "Tasks",
                    value = "$pendingTasksCount",
                    icon = Icons.Outlined.CheckCircle,
                    accentColor = AppTheme.extendedColors.info,
                    modifier = Modifier.weight(1f)
                )
                JlabMetricTile(
                    label = "Projects",
                    value = "$projectsCount",
                    icon = Icons.Outlined.Folder,
                    accentColor = AppTheme.extendedColors.violet,
                    modifier = Modifier.weight(1f)
                )
                JlabMetricTile(
                    label = "Notes",
                    value = "$notesCount",
                    icon = Icons.Outlined.NoteAlt,
                    accentColor = AppTheme.extendedColors.success,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (business.description.isNotBlank()) {
            item {
                JlabSectionCard(
                    overline = "VALUE PROPOSITION",
                    headerAction = if (business.description.length > 150) {
                        {
                            TextButton(
                                onClick = { isDescExpanded = !isDescExpanded },
                                contentPadding = PaddingValues(AppTheme.spacing.none),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text(
                                    text = if (isDescExpanded) "Show less" else "Show more",
                                    style = AppTheme.typography.labelSmall,
                                    color = AppTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        null
                    }
                ) {
                    Text(
                        text = business.description,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface,
                        maxLines = if (isDescExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (business.goals.isNotBlank()) {
            item {
                JlabSectionCard(overline = "PRIMARY GROWTH GOALS") {
                    Text(
                        text = business.goals,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (business.revenueNotes.isNotBlank()) {
            item {
                JlabSectionCard(overline = "REVENUE & PRICING MODEL") {
                    Text(
                        text = business.revenueNotes,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (business.marketingNotes.isNotBlank() || business.customerNotes.isNotBlank()) {
            item {
                JlabSectionCard(overline = "MARKETING & CUSTOMER CHANNELS") {
                    if (business.marketingNotes.isNotBlank()) {
                        Text(
                            text = business.marketingNotes,
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colorScheme.onSurface
                        )
                    }
                    if (business.customerNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                        Text(
                            text = "Target Clients: ${business.customerNotes}",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (pendingTasksCount == 0 && goalsCount == 0) {
            item {
                JlabCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(AppTheme.spacing.xl)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No active tasks or goals for this venture",
                            style = AppTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                        Text(
                            text = "Set actionable goals and daily execution tasks for this business.",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                            JlabPrimaryButton(
                                text = "Add Task",
                                icon = Icons.Default.Add,
                                onClick = onAddTask,
                                testTag = "biz_overview_add_task"
                            )
                            JlabOutlinedButton(
                                text = "Add Goal",
                                onClick = onAddGoal,
                                testTag = "biz_overview_add_goal"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun BusinessGoalsTab(
    goals: List<GoalEntity>,
    onAddGoal: () -> Unit,
    onDeleteGoal: (String) -> Unit
) {
    if (goals.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Default.Flag,
                title = "No business goals yet",
                message = "Set growth milestones, revenue targets, and strategic objectives for this venture.",
                actionButtonText = "Create Goal",
                onActionClick = onAddGoal
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.sm,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
    ) {
        item {
            SectionHeader(
                title = "Business Goals",
                count = goals.size,
                actionText = "+ Add Goal",
                onActionClick = onAddGoal,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.none)
            )
        }

        items(goals, key = { it.id }) { goal ->
            JlabCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(AppTheme.spacing.mdPlus),
                testTag = "biz_goal_card_${goal.id}"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = goal.title,
                        style = AppTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    JlabIconButton(
                        icon = Icons.Default.DeleteOutline,
                        contentDescription = "Delete goal",
                        tint = AppTheme.extendedColors.danger,
                        onClick = { onDeleteGoal(goal.id) }
                    )
                }

                if (goal.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                    Text(
                        text = goal.description,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.itemGap))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress",
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${goal.progressPercent}%",
                        style = AppTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                JlabLinearProgress(progress = goal.progressPercent / 100f)
            }
        }
    }
}

@Composable
internal fun BusinessTasksTab(
    tasks: List<TaskEntity>,
    onAddTask: () -> Unit,
    onCompleteTask: (String, String) -> Unit,
    onDeleteTask: (String) -> Unit
) {
    if (tasks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Default.CheckCircle,
                title = "No active tasks",
                message = "Add deliverables, client follow-ups, and operational tasks for this business.",
                actionButtonText = "Add Task",
                onActionClick = onAddTask
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.sm,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
    ) {
        item {
            SectionHeader(
                title = "Pending Tasks",
                count = tasks.count { it.status != "COMPLETED" },
                actionText = "+ Add Task",
                onActionClick = onAddTask,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.none)
            )
        }

        items(tasks, key = { it.id }) { task ->
            val isCompleted = task.status == "COMPLETED"
            JlabCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppTheme.extendedShapes.compactCard,
                containerColor = if (isCompleted) {
                    AppTheme.colorScheme.surfaceContainerLow
                } else {
                    AppTheme.colorScheme.surface
                },
                contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding),
                testTag = "biz_task_card_${task.id}"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    JlabIconButton(
                        icon = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = if (isCompleted) "Completed" else "Mark done",
                        tint = if (isCompleted) {
                            AppTheme.extendedColors.success
                        } else {
                            AppTheme.colorScheme.onSurfaceVariant
                        },
                        onClick = { onCompleteTask(task.id, task.title) }
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = AppTheme.typography.bodyMedium,
                            fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                            color = if (isCompleted) {
                                AppTheme.colorScheme.onSurfaceVariant
                            } else {
                                AppTheme.colorScheme.onSurface
                            }
                        )
                        if (task.description.isNotBlank()) {
                            Text(
                                text = task.description,
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    PriorityBadge(priority = task.priority)

                    JlabIconButton(
                        icon = Icons.Default.DeleteOutline,
                        contentDescription = "Delete task",
                        tint = AppTheme.extendedColors.danger,
                        onClick = { onDeleteTask(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun BusinessProjectsTab(
    projects: List<ProjectEntity>,
    onLinkProject: () -> Unit,
    onOpenProject: (ProjectEntity) -> Unit
) {
    if (projects.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Default.Folder,
                title = "No projects linked to this business",
                message = "Connect active development or client projects to this business to track collective progress.",
                actionButtonText = "Link / Add Project",
                onActionClick = onLinkProject
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.sm,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
    ) {
        item {
            SectionHeader(
                title = "Linked Projects",
                count = projects.size,
                actionText = "+ Link Project",
                onActionClick = onLinkProject,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.none)
            )
        }

        items(projects, key = { it.id }) { proj ->
            JlabCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(AppTheme.spacing.mdPlus),
                onClick = { onOpenProject(proj) },
                testTag = "biz_project_card_${proj.id}"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = proj.name,
                        style = AppTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(status = proj.status)
                }

                if (proj.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                    Text(
                        text = proj.description,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.itemGap))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress",
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${proj.progressPercent}%",
                        style = AppTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                JlabLinearProgress(progress = proj.progressPercent / 100f)
            }
        }
    }
}

@Composable
internal fun BusinessNotesTab(
    notes: List<NoteEntity>,
    onOpenNote: (NoteEntity) -> Unit,
    onCreateNote: () -> Unit
) {
    if (notes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Default.NoteAlt,
                title = "No business notes yet",
                message = "Store client proposals, meeting briefs, service outlines, and revenue ideas.",
                actionButtonText = "Create Business Note",
                onActionClick = onCreateNote
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.sm,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
    ) {
        item {
            SectionHeader(
                title = "Business Notes",
                count = notes.size,
                actionText = "+ Add Note",
                onActionClick = onCreateNote,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.none)
            )
        }

        items(notes, key = { it.id }) { note ->
            JlabCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(AppTheme.spacing.mdPlus),
                onClick = { onOpenNote(note) },
                testTag = "biz_note_card_${note.id}"
            ) {
                Text(
                    text = note.title.ifBlank { "Untitled Note" },
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colorScheme.onSurface
                )
                if (note.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                    Text(
                        text = note.content.take(120),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(AppTheme.spacing.compactGap))
                val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(note.updatedAt))
                Text(
                    text = "Updated $dateStr • ${note.wordCount} words",
                    style = AppTheme.extendedTypography.caption,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
internal fun BusinessResourcesTab(
    resources: List<FileAttachmentEntity>,
    onOpenResource: (FileAttachmentEntity) -> Unit,
    onAttachResource: () -> Unit
) {
    if (resources.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Default.AttachFile,
                title = "No business resources",
                message = "Attach invoices, contract screenshots, logo files, and marketing collateral.",
                actionButtonText = "Attach File / Screenshot",
                onActionClick = onAttachResource
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.sm,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
    ) {
        item {
            SectionHeader(
                title = "Attached Files",
                count = resources.size,
                actionText = "+ Attach File",
                onActionClick = onAttachResource,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.none)
            )
        }

        items(resources, key = { it.id }) { file ->
            JlabCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppTheme.extendedShapes.compactCard,
                contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding),
                onClick = { onOpenResource(file) },
                testTag = "biz_resource_card_${file.id}"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                ) {
                    Surface(
                        shape = AppTheme.extendedShapes.chip,
                        color = AppTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(AppTheme.spacing.avatarMd)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (file.category == "Screenshot") {
                                    Icons.Default.Image
                                } else {
                                    Icons.Default.InsertDriveFile
                                },
                                contentDescription = null,
                                tint = AppTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(AppTheme.spacing.iconLg)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = file.name,
                            style = AppTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${file.category} • ${file.sizeBytes / 1024} KB",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AppTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
internal fun BusinessActivityTab(activities: List<ActivityEntity>) {
    if (activities.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Default.History,
                title = "No logged business activities yet",
                message = "Actions taken for this business will appear here in chronological order."
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.md,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
    ) {
        items(activities, key = { it.id }) { act ->
            JlabCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppTheme.extendedShapes.compactCard,
                contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
                ) {
                    Icon(
                        imageVector = when (act.actionType) {
                            "CREATED" -> Icons.Default.Add
                            "DELETED" -> Icons.Default.Delete
                            "COMPLETED" -> Icons.Default.Check
                            else -> Icons.Default.Edit
                        },
                        contentDescription = null,
                        tint = AppTheme.colorScheme.primary,
                        modifier = Modifier.size(AppTheme.spacing.iconMd)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = act.title,
                            style = AppTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colorScheme.onSurface
                        )
                        if (act.summary.isNotBlank()) {
                            Text(
                                text = act.summary,
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    val timeStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(act.timestamp))
                    Text(
                        text = timeStr,
                        style = AppTheme.extendedTypography.caption,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
