package com.example.ui.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.local.entity.LearningEntity
import com.example.data.local.entity.LearningLessonEntity
import com.example.data.local.entity.LearningModuleEntity
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTextField
import com.example.ui.theme.AppTheme

@Composable
fun CourseStatusPill(status: String) {
    val extended = AppTheme.extendedColors
    val (bg, fg, label) = when (status.uppercase()) {
        "COMPLETED" -> Triple(extended.successContainer, extended.onSuccessContainer, "Completed")
        "LEARNING" -> Triple(extended.infoContainer, extended.onInfoContainer, "Learning")
        "PAUSED" -> Triple(extended.warningContainer, extended.onWarningContainer, "Paused")
        else -> Triple(extended.neutralContainer, extended.onNeutralContainer, "Not Started")
    }

    JlabTagBadge(
        text = label,
        containerColor = bg,
        contentColor = fg
    )
}

@Composable
fun EditCourseDialog(
    course: LearningEntity,
    onDismiss: () -> Unit,
    onSave: (LearningEntity) -> Unit
) {
    var title by remember { mutableStateOf(course.title) }
    var type by remember { mutableStateOf(course.type) }
    var goal by remember { mutableStateOf(course.goal) }
    var description by remember { mutableStateOf(course.description) }
    var status by remember { mutableStateOf(course.status) }

    val statusOptions = listOf("NOT_STARTED", "LEARNING", "PAUSED", "COMPLETED")

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "Edit Course Information",
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
                    value = title,
                    onValueChange = { title = it },
                    label = "Course Title *",
                    singleLine = true,
                    testTag = "edit_course_title"
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
                    contentPadding = PaddingValues(AppTheme.spacing.none),
                    labelFormatter = { it.replace("_", " ") }
                )

                JlabTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = "Type / Level",
                    singleLine = true,
                    testTag = "edit_course_type"
                )
                JlabTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = "Primary Learning Goal",
                    singleLine = false,
                    minLines = 2,
                    testTag = "edit_course_goal"
                )
                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description & Curriculum Overview",
                    singleLine = false,
                    minLines = 3,
                    testTag = "edit_course_desc"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Save",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            course.copy(
                                title = title.trim(),
                                type = type.trim().ifBlank { "Course" },
                                status = status,
                                goal = goal.trim(),
                                description = description.trim(),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                },
                testTag = "edit_course_save"
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
fun CreateModuleDialog(
    learningId: String,
    nextSortOrder: Int,
    onDismiss: () -> Unit,
    onSave: (LearningModuleEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "Add Course Module",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Module Title *",
                    placeholder = "e.g. Kotlin Coroutines & Flows",
                    singleLine = true,
                    testTag = "create_module_title"
                )
                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description / Scope",
                    singleLine = false,
                    minLines = 2,
                    testTag = "create_module_desc"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Add Module",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            LearningModuleEntity(
                                learningId = learningId,
                                title = title.trim(),
                                description = description.trim(),
                                sortOrder = nextSortOrder
                            )
                        )
                    }
                },
                testTag = "create_module_save"
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
fun EditModuleDialog(
    module: LearningModuleEntity,
    onDismiss: () -> Unit,
    onSave: (LearningModuleEntity) -> Unit
) {
    var title by remember { mutableStateOf(module.title) }
    var description by remember { mutableStateOf(module.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "Edit Module",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Module Title *",
                    singleLine = true,
                    testTag = "edit_module_title"
                )
                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    singleLine = false,
                    minLines = 2,
                    testTag = "edit_module_desc"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Save",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            module.copy(
                                title = title.trim(),
                                description = description.trim()
                            )
                        )
                    }
                },
                testTag = "edit_module_save"
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
fun CreateLessonDialog(
    learningId: String,
    moduleId: String,
    nextSortOrder: Int,
    onDismiss: () -> Unit,
    onSave: (LearningLessonEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var objective by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "Add Lesson",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Lesson Title *",
                    placeholder = "e.g. StateFlow & SharedFlow",
                    singleLine = true,
                    testTag = "create_lesson_title"
                )
                JlabTextField(
                    value = objective,
                    onValueChange = { objective = it },
                    label = "Learning Objective",
                    placeholder = "e.g. Master hot flows and UI state collection",
                    singleLine = false,
                    minLines = 2,
                    testTag = "create_lesson_objective"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Add Lesson",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            LearningLessonEntity(
                                learningId = learningId,
                                moduleId = moduleId,
                                title = title.trim(),
                                objective = objective.trim(),
                                sortOrder = nextSortOrder
                            )
                        )
                    }
                },
                testTag = "create_lesson_save"
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
