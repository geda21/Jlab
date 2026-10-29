package com.example.ui.screens.projects

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.entity.FileAttachmentEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.ProjectDecisionEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabFilterChip
import com.example.ui.components.JlabOutlinedButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun ProjectTasksTab(
    projectName: String,
    tasks: List<TaskEntity>,
    selectedTaskFilter: String,
    onSelectFilter: (String) -> Unit,
    onAddTaskClick: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onCompleteTask: (String, String) -> Unit,
    onReopenTask: (String, String) -> Unit,
    onRequestDeleteTask: (TaskEntity) -> Unit,
    context: Context
) {
    val completedTasksCount = tasks.count { it.status == "COMPLETED" }
    val totalTasksCount = tasks.size
    val filteredProjectTasks = remember(tasks, selectedTaskFilter) {
        when (selectedTaskFilter) {
            "PENDING" -> tasks.filter { it.status != "COMPLETED" }
            "COMPLETED" -> tasks.filter { it.status == "COMPLETED" }
            else -> tasks
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.screenPadding,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                JlabPrimaryButton(
                    text = "Add Project Task",
                    icon = Icons.Default.Add,
                    onClick = onAddTaskClick,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "project_add_task_button"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        JlabFilterChip(
                            label = "All ($totalTasksCount)",
                            selected = selectedTaskFilter == "ALL",
                            onClick = { onSelectFilter("ALL") }
                        )
                    }
                    item {
                        JlabFilterChip(
                            label = "Open (${totalTasksCount - completedTasksCount})",
                            selected = selectedTaskFilter == "PENDING",
                            onClick = { onSelectFilter("PENDING") }
                        )
                    }
                    item {
                        JlabFilterChip(
                            label = "Done ($completedTasksCount)",
                            selected = selectedTaskFilter == "COMPLETED",
                            onClick = { onSelectFilter("COMPLETED") }
                        )
                    }
                }
            }
        }

        if (filteredProjectTasks.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.CheckCircle,
                    title = if (selectedTaskFilter == "COMPLETED") "No completed tasks yet" else "No project tasks found",
                    message = "Tasks created here link directly to $projectName and count toward automatic progress.",
                    actionButtonText = if (selectedTaskFilter != "COMPLETED") "Create Task" else null,
                    onActionClick = onAddTaskClick
                )
            }
        } else {
            items(filteredProjectTasks, key = { it.id }) { task ->
                val isDone = task.status == "COMPLETED"
                JlabCard(
                    shape = AppTheme.extendedShapes.compactCard,
                    contentPadding = PaddingValues(
                        horizontal = AppTheme.spacing.compactCardPadding,
                        vertical = AppTheme.spacing.sm
                    ),
                    onClick = { onEditTask(task) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (isDone) {
                                    onReopenTask(task.id, task.title)
                                    Toast.makeText(context, "Task reopened", Toast.LENGTH_SHORT).show()
                                } else {
                                    onCompleteTask(task.id, task.title)
                                    Toast.makeText(context, "Task completed!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                contentDescription = if (isDone) "Reopen ${task.title}" else "Complete ${task.title}",
                                tint = if (isDone) AppTheme.extendedColors.success else AppTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(AppTheme.spacing.xs))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = AppTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (isDone) TextDecoration.LineThrough else null,
                                color = if (isDone) AppTheme.colorScheme.onSurfaceVariant else AppTheme.colorScheme.onSurface
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
                            if (task.dueDate != null) {
                                val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(task.dueDate))
                                Text(
                                    text = "Due: $dateStr",
                                    style = AppTheme.extendedTypography.caption,
                                    color = AppTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                        PriorityBadge(priority = task.priority)

                        IconButton(
                            onClick = { onRequestDeleteTask(task) },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete ${task.title}",
                                tint = AppTheme.extendedColors.danger,
                                modifier = Modifier.size(AppTheme.spacing.iconMd)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProjectNotesTab(
    projectName: String,
    notes: List<NoteEntity>,
    onAddNoteClick: () -> Unit,
    onOpenNote: (String) -> Unit,
    onRequestDeleteNote: (NoteEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.screenPadding,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
    ) {
        item {
            JlabPrimaryButton(
                text = "New Note for $projectName",
                icon = Icons.Default.Add,
                onClick = onAddNoteClick,
                modifier = Modifier.fillMaxWidth(),
                testTag = "project_add_note_button"
            )
        }

        if (notes.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.NoteAlt,
                    title = "No project notes yet",
                    message = "Capture meeting notes, architectural thoughts, or specifications directly linked to this project.",
                    actionButtonText = "Create Note",
                    onActionClick = onAddNoteClick
                )
            }
        } else {
            items(notes, key = { it.id }) { note ->
                JlabCard(
                    shape = AppTheme.extendedShapes.compactCard,
                    onClick = { onOpenNote(note.id) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = note.title.ifBlank { "Untitled Note" },
                            style = AppTheme.typography.titleMedium,
                            color = AppTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onRequestDeleteNote(note) },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete note",
                                tint = AppTheme.extendedColors.danger,
                                modifier = Modifier.size(AppTheme.spacing.iconMd)
                            )
                        }
                    }

                    if (note.content.isNotBlank()) {
                        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                        Text(
                            text = note.content.take(150),
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
                    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(note.updatedAt))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        JlabTagBadge(
                            text = note.category,
                            containerColor = AppTheme.colorScheme.primaryContainer,
                            contentColor = AppTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = dateStr,
                            style = AppTheme.extendedTypography.caption,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProjectFilesTab(
    files: List<FileAttachmentEntity>,
    onPickPhotos: () -> Unit,
    onPickDocuments: () -> Unit,
    onOpenFile: (FileAttachmentEntity) -> Unit,
    onRequestDeleteFile: (FileAttachmentEntity) -> Unit
) {
    val extended = AppTheme.extendedColors
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.screenPadding,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)
            ) {
                JlabPrimaryButton(
                    text = "Add Image",
                    icon = Icons.Default.AddPhotoAlternate,
                    onClick = onPickPhotos,
                    modifier = Modifier.weight(1f),
                    testTag = "project_add_image_button"
                )
                JlabOutlinedButton(
                    text = "Add Document",
                    icon = Icons.Default.AttachFile,
                    onClick = onPickDocuments,
                    modifier = Modifier.weight(1f),
                    testTag = "project_add_doc_button"
                )
            }
        }

        if (files.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.FolderZip,
                    title = "No attachments in project",
                    message = "Attach screenshots, wireframes, PDF specifications, and documents stored securely in private app storage.",
                    actionButtonText = "Attach File",
                    onActionClick = onPickDocuments
                )
            }
        } else {
            items(files, key = { it.id }) { file ->
                val isImg = file.mimeType.startsWith("image") ||
                    file.name.endsWith(".jpg", true) ||
                    file.name.endsWith(".png", true) ||
                    file.name.endsWith(".webp", true)
                val isPdf = file.category.equals("PDF", true) || file.name.endsWith(".pdf", true)

                val (iconBg, iconTint, iconVec) = when {
                    isImg -> Triple(AppTheme.colorScheme.primaryContainer, AppTheme.colorScheme.onPrimaryContainer, Icons.Default.Image)
                    isPdf -> Triple(extended.dangerContainer, extended.onDangerContainer, Icons.Default.PictureAsPdf)
                    else -> Triple(extended.infoContainer, extended.onInfoContainer, Icons.AutoMirrored.Filled.InsertDriveFile)
                }

                JlabCard(
                    shape = AppTheme.extendedShapes.compactCard,
                    contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding),
                    onClick = { onOpenFile(file) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(AppTheme.spacing.avatarMd)
                                .clip(AppTheme.extendedShapes.chip)
                                .background(iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVec,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(AppTheme.spacing.iconMd)
                            )
                        }

                        Spacer(modifier = Modifier.width(AppTheme.spacing.md))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.name,
                                style = AppTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val sizeKb = (file.sizeBytes / 1024).coerceAtLeast(1)
                            val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(file.createdAt))
                            Text(
                                text = "$sizeKb KB · ${file.category} · $dateStr",
                                style = AppTheme.extendedTypography.caption,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { onRequestDeleteFile(file) },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete ${file.name}",
                                tint = extended.danger,
                                modifier = Modifier.size(AppTheme.spacing.iconMd)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProjectDecisionsTab(
    decisions: List<ProjectDecisionEntity>,
    onAddDecisionClick: () -> Unit,
    onEditDecision: (ProjectDecisionEntity) -> Unit,
    onRequestDeleteDecision: (ProjectDecisionEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.screenPadding,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
    ) {
        item {
            JlabPrimaryButton(
                text = "Add Decision Record",
                icon = Icons.Default.Add,
                onClick = onAddDecisionClick,
                modifier = Modifier.fillMaxWidth(),
                testTag = "project_add_decision_button"
            )
        }

        if (decisions.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.Gavel,
                    title = "No decisions logged yet",
                    message = "Record key architecture decisions, pivots, reasons, and intended outcomes as structured milestones.",
                    actionButtonText = "Log First Decision",
                    onActionClick = onAddDecisionClick
                )
            }
        } else {
            items(decisions, key = { it.id }) { decision ->
                DecisionRecordCard(
                    decision = decision,
                    onEdit = { onEditDecision(decision) },
                    onDelete = { onRequestDeleteDecision(decision) }
                )
            }
        }
    }
}

@Composable
private fun DecisionRecordCard(
    decision: ProjectDecisionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(decision.createdAt) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(decision.createdAt))
    }

    JlabCard(shape = AppTheme.extendedShapes.compactCard) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                JlabTagBadge(
                    text = dateStr,
                    containerColor = AppTheme.colorScheme.primaryContainer,
                    contentColor = AppTheme.colorScheme.onPrimaryContainer
                )

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit decision",
                            modifier = Modifier.size(AppTheme.spacing.iconSm),
                            tint = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete decision",
                            modifier = Modifier.size(AppTheme.spacing.iconSm),
                            tint = AppTheme.extendedColors.danger
                        )
                    }
                }
            }

            Text(
                text = decision.title,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colorScheme.onSurface
            )

            if (decision.description.isNotBlank()) {
                Text(
                    text = decision.description,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }

            if (decision.reason.isNotBlank()) {
                Surface(
                    shape = AppTheme.extendedShapes.chip,
                    color = AppTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(AppTheme.spacing.smPlus)) {
                        Text(
                            text = "Reason / Context",
                            style = AppTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                        Text(
                            text = decision.reason,
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (decision.outcome.isNotBlank()) {
                Surface(
                    shape = AppTheme.extendedShapes.chip,
                    color = AppTheme.extendedColors.successContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(AppTheme.spacing.smPlus)) {
                        Text(
                            text = "Expected Outcome / Resolution",
                            style = AppTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.extendedColors.onSuccessContainer
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                        Text(
                            text = decision.outcome,
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
