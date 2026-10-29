package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.PlanEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.PromptEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.AppTheme

enum class CaptureType(val label: String, val icon: ImageVector) {
    TASK("Task", Icons.Default.CheckCircle),
    NOTE("Note", Icons.Default.Description),
    IDEA("Idea", Icons.Default.Lightbulb),
    PROMPT("Prompt", Icons.Default.AutoAwesome),
    PLAN("Plan", Icons.AutoMirrored.Filled.EventNote),
    PROJECT("Project", Icons.Default.Folder),
    REMINDER("Reminder", Icons.Default.Alarm),
    FILE("File", Icons.Default.AttachFile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCaptureSheet(
    onDismiss: () -> Unit,
    onSaveTask: (TaskEntity) -> Unit,
    onSaveNote: (NoteEntity) -> Unit,
    onSaveProject: (ProjectEntity) -> Unit,
    onSavePlan: (PlanEntity) -> Unit,
    onSavePrompt: (PromptEntity) -> Unit
) {
    var selectedType by remember { mutableStateOf(CaptureType.TASK) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("MEDIUM") }
    val category by remember { mutableStateOf("General") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = AppTheme.extendedShapes.sheetTop,
        containerColor = AppTheme.colorScheme.surface,
        modifier = Modifier.testTag("quick_capture_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.spacing.sheetHorizontal)
                .padding(bottom = AppTheme.spacing.sheetBottom)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Capture",
                    style = AppTheme.typography.titleLarge,
                    color = AppTheme.colorScheme.onSurface
                )
                JlabIconButton(
                    icon = Icons.Default.Close,
                    contentDescription = "Close quick capture",
                    onClick = onDismiss,
                    testTag = "quick_capture_close"
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.md))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CaptureType.entries) { type ->
                    JlabFilterChip(
                        label = type.label,
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        leadingIcon = type.icon,
                        testTag = "capture_type_${type.name.lowercase()}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.lg))

            JlabTextField(
                value = title,
                onValueChange = { title = it },
                label = if (selectedType == CaptureType.NOTE) "Note Title" else "Title / Thought",
                placeholder = when (selectedType) {
                    CaptureType.TASK -> "e.g., Review contract draft"
                    CaptureType.IDEA -> "e.g., Explore Kotlin Multiplatform idea"
                    CaptureType.PROJECT -> "e.g., Q4 App Redesign"
                    CaptureType.PLAN -> "e.g., Week 40 Sprint Plan"
                    CaptureType.PROMPT -> "e.g., High-Converting Headline Generator"
                    CaptureType.REMINDER -> "e.g., Call accountant at 3 PM"
                    CaptureType.FILE -> "e.g., Quarterly Report File Note"
                    else -> "Enter title..."
                },
                singleLine = true,
                testTag = "quick_capture_title_input"
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.smPlus))

            JlabTextField(
                value = description,
                onValueChange = { description = it },
                label = if (selectedType == CaptureType.PROMPT) {
                    "Full Prompt Text"
                } else {
                    "Details / Description / Script"
                },
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                testTag = "quick_capture_description_input"
            )

            if (selectedType == CaptureType.TASK || selectedType == CaptureType.REMINDER) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                Text(
                    text = "Priority",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf("LOW", "MEDIUM", "HIGH", "CRITICAL")) { priority ->
                        JlabFilterChip(
                            label = priority,
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            testTag = "quick_capture_priority_${priority.lowercase()}"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.xl))

            JlabPrimaryButton(
                text = "Capture to JLab",
                icon = Icons.Default.Bolt,
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isBlank()) return@JlabPrimaryButton
                    when (selectedType) {
                        CaptureType.TASK -> {
                            onSaveTask(
                                TaskEntity(
                                    title = title.trim(),
                                    description = description.trim(),
                                    priority = selectedPriority,
                                    status = "PLANNED",
                                    category = category,
                                    dueDate = System.currentTimeMillis()
                                )
                            )
                        }
                        CaptureType.IDEA -> {
                            onSaveTask(
                                TaskEntity(
                                    title = title.trim(),
                                    description = description.trim(),
                                    status = "INBOX",
                                    category = "Idea",
                                    isQuickTodo = true
                                )
                            )
                        }
                        CaptureType.FILE -> {
                            onSaveNote(
                                NoteEntity(
                                    title = title.trim(),
                                    content = description.trim(),
                                    category = "Files"
                                )
                            )
                        }
                        CaptureType.REMINDER -> {
                            val inOneHour = System.currentTimeMillis() + (60 * 60 * 1000L)
                            onSaveTask(
                                TaskEntity(
                                    title = title.trim(),
                                    description = description.trim(),
                                    priority = selectedPriority,
                                    status = "PLANNED",
                                    dueDate = inOneHour,
                                    reminderTimestamp = inOneHour
                                )
                            )
                        }
                        CaptureType.NOTE -> {
                            onSaveNote(
                                NoteEntity(
                                    title = title.trim(),
                                    content = description.trim(),
                                    category = "Personal"
                                )
                            )
                        }
                        CaptureType.PROJECT -> {
                            onSaveProject(
                                ProjectEntity(
                                    name = title.trim(),
                                    objective = description.trim(),
                                    status = "ACTIVE"
                                )
                            )
                        }
                        CaptureType.PLAN -> {
                            onSavePlan(
                                PlanEntity(
                                    title = title.trim(),
                                    planType = "WEEKLY",
                                    goals = description.trim()
                                )
                            )
                        }
                        CaptureType.PROMPT -> {
                            onSavePrompt(
                                PromptEntity(
                                    title = title.trim(),
                                    fullPrompt = description.ifBlank { title.trim() },
                                    category = "Coding"
                                )
                            )
                        }
                    }
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "quick_capture_submit_button"
            )
        }
    }
}
