package com.example.ui.screens.learning

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.data.local.entity.LearningLessonEntity
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabOutlinedButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSectionCard
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonWorkspaceScreen(
    courseTitle: String,
    moduleTitle: String,
    lesson: LearningLessonEntity,
    allLessonsInCourse: List<LearningLessonEntity>,
    onUpdateLesson: (LearningLessonEntity) -> Unit,
    onToggleCompleted: (Boolean) -> Unit,
    onSelectLesson: (LearningLessonEntity) -> Unit,
    onBack: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Theory", "Examples", "Practice", "Notes")

    val sortedLessons = remember(allLessonsInCourse) {
        allLessonsInCourse.sortedWith(compareBy({ it.sortOrder }, { it.createdAt }))
    }
    val currentIndex = sortedLessons.indexOfFirst { it.id == lesson.id }
    val prevLesson = if (currentIndex > 0) sortedLessons[currentIndex - 1] else null
    val nextLesson = if (currentIndex in 0 until sortedLessons.size - 1) {
        sortedLessons[currentIndex + 1]
    } else {
        null
    }

    val extended = AppTheme.extendedColors

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = moduleTitle,
                subtitle = courseTitle,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                testTag = "lesson_workspace_top_bar",
                actions = {
                    FilledTonalButton(
                        onClick = { onToggleCompleted(!lesson.isCompleted) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (lesson.isCompleted) {
                                extended.successContainer
                            } else {
                                AppTheme.colorScheme.surfaceContainerHigh
                            },
                            contentColor = if (lesson.isCompleted) {
                                extended.onSuccessContainer
                            } else {
                                AppTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        shape = AppTheme.extendedShapes.button,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("lesson_toggle_complete")
                    ) {
                        Icon(
                            imageVector = if (lesson.isCompleted) {
                                Icons.Default.CheckCircle
                            } else {
                                Icons.Outlined.CheckCircle
                            },
                            contentDescription = null,
                            modifier = Modifier.size(AppTheme.spacing.iconSm)
                        )
                        Spacer(modifier = Modifier.width(AppTheme.spacing.compactGap))
                        Text(
                            text = if (lesson.isCompleted) "Completed" else "Mark Complete",
                            style = AppTheme.typography.labelLarge
                        )
                    }

                    JlabIconButton(
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit lesson content",
                        onClick = { showEditDialog = true },
                        testTag = "lesson_edit_action"
                    )
                }
            )
        },
        bottomBar = {
            Surface(
                color = AppTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = AppTheme.spacing.xs,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.spacing.screenHorizontal,
                            vertical = AppTheme.spacing.itemGap
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { prevLesson?.let { onSelectLesson(it) } },
                        enabled = prevLesson != null,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("lesson_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                        Text("Previous", style = AppTheme.typography.labelLarge)
                    }

                    Text(
                        text = "${currentIndex + 1} of ${sortedLessons.size}",
                        style = AppTheme.typography.labelMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )

                    JlabPrimaryButton(
                        text = "Next",
                        icon = Icons.AutoMirrored.Filled.NavigateNext,
                        enabled = nextLesson != null,
                        onClick = { nextLesson?.let { onSelectLesson(it) } },
                        testTag = "lesson_next_button"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AppTheme.colorScheme.surface,
                tonalElevation = AppTheme.spacing.xxs
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = AppTheme.spacing.screenHorizontal,
                        vertical = AppTheme.spacing.screenVertical
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                    ) {
                        Text(
                            text = "Lesson ${currentIndex + 1}",
                            style = AppTheme.typography.labelMedium,
                            color = AppTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        if (lesson.isCompleted && lesson.completedAt != null) {
                            val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                                .format(Date(lesson.completedAt))
                            Text(
                                text = "• Completed on $dateStr",
                                style = AppTheme.typography.labelSmall,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppTheme.spacing.xs))

                    Text(
                        text = lesson.title,
                        style = AppTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colorScheme.onSurface
                    )

                    if (lesson.objective.isNotBlank()) {
                        Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
                        JlabTagBadge(
                            text = "Objective: ${lesson.objective}",
                            icon = Icons.Default.Flag,
                            containerColor = AppTheme.colorScheme.surfaceContainerHigh,
                            contentColor = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

                    PrimaryTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        divider = {}
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        style = AppTheme.typography.labelLarge,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(AppTheme.spacing.screenPadding)
            ) {
                when (selectedTab) {
                    0 -> LessonTheoryTab(theory = lesson.theory, onEditClick = { showEditDialog = true })
                    1 -> LessonExamplesTab(examples = lesson.examples, onEditClick = { showEditDialog = true })
                    2 -> LessonPracticeTab(practice = lesson.practice, onEditClick = { showEditDialog = true })
                    3 -> LessonNotesTab(notes = lesson.notes, onEditClick = { showEditDialog = true })
                }
            }
        }
    }

    if (showEditDialog) {
        EditLessonDialog(
            lesson = lesson,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                onUpdateLesson(updated)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun LessonTheoryTab(theory: String, onEditClick: () -> Unit) {
    if (theory.isBlank()) {
        EmptySectionCard(
            title = "No theory recorded yet",
            subtitle = "Document key formulas, core concepts, mental models, and principles.",
            actionText = "Write Theory",
            onAction = onEditClick
        )
    } else {
        JlabSectionCard(
            overline = "CORE CONCEPTS & THEORY",
            headerAction = {
                JlabIconButton(
                    icon = Icons.Default.Edit,
                    contentDescription = "Edit theory",
                    onClick = onEditClick
                )
            }
        ) {
            Text(
                text = theory,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LessonExamplesTab(examples: String, onEditClick: () -> Unit) {
    if (examples.isBlank()) {
        EmptySectionCard(
            title = "No code or examples yet",
            subtitle = "Add code snippets, walkthroughs, patterns, and practical diagrams.",
            actionText = "Add Example",
            onAction = onEditClick
        )
    } else {
        JlabSectionCard(
            overline = "CODE & PRACTICAL EXAMPLES",
            headerAction = {
                JlabIconButton(
                    icon = Icons.Default.Edit,
                    contentDescription = "Edit examples",
                    onClick = onEditClick
                )
            }
        ) {
            Surface(
                shape = AppTheme.extendedShapes.compactCard,
                color = AppTheme.extendedColors.codeSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = examples,
                    style = AppTheme.extendedTypography.codeMedium,
                    color = AppTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(AppTheme.spacing.compactCardPadding)
                )
            }
        }
    }
}

@Composable
private fun LessonPracticeTab(practice: String, onEditClick: () -> Unit) {
    if (practice.isBlank()) {
        EmptySectionCard(
            title = "No practice exercises yet",
            subtitle = "Set up self-test prompts, challenges, and mini-exercises to test recall.",
            actionText = "Add Practice",
            onAction = onEditClick
        )
    } else {
        JlabSectionCard(
            overline = "EXPERIMENT & PRACTICE",
            headerAction = {
                JlabIconButton(
                    icon = Icons.Default.Edit,
                    contentDescription = "Edit practice",
                    onClick = onEditClick
                )
            }
        ) {
            Text(
                text = practice,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LessonNotesTab(notes: String, onEditClick: () -> Unit) {
    if (notes.isBlank()) {
        EmptySectionCard(
            title = "No personal lesson notes yet",
            subtitle = "Record key takeaways, stumbling blocks, insights, and review notes.",
            actionText = "Write Note",
            onAction = onEditClick
        )
    } else {
        JlabSectionCard(
            overline = "PERSONAL LESSON NOTES",
            headerAction = {
                JlabIconButton(
                    icon = Icons.Default.Edit,
                    contentDescription = "Edit notes",
                    onClick = onEditClick
                )
            }
        ) {
            Text(
                text = notes,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EmptySectionCard(
    title: String,
    subtitle: String,
    actionText: String,
    onAction: () -> Unit
) {
    JlabCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(AppTheme.spacing.xxl)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
            Text(
                text = subtitle,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.mdPlus))
            JlabOutlinedButton(
                text = actionText,
                icon = Icons.Default.Add,
                onClick = onAction
            )
        }
    }
}

@Composable
fun EditLessonDialog(
    lesson: LearningLessonEntity,
    onDismiss: () -> Unit,
    onSave: (LearningLessonEntity) -> Unit
) {
    var title by remember { mutableStateOf(lesson.title) }
    var objective by remember { mutableStateOf(lesson.objective) }
    var theory by remember { mutableStateOf(lesson.theory) }
    var examples by remember { mutableStateOf(lesson.examples) }
    var practice by remember { mutableStateOf(lesson.practice) }
    var notes by remember { mutableStateOf(lesson.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "Edit Lesson Material",
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
                    label = "Lesson Title *",
                    singleLine = true,
                    testTag = "edit_lesson_title"
                )
                JlabTextField(
                    value = objective,
                    onValueChange = { objective = it },
                    label = "Objective / Key Goal",
                    singleLine = false,
                    minLines = 2,
                    testTag = "edit_lesson_objective"
                )
                JlabTextField(
                    value = theory,
                    onValueChange = { theory = it },
                    label = "Theory & Concepts",
                    singleLine = false,
                    minLines = 3,
                    testTag = "edit_lesson_theory"
                )
                JlabTextField(
                    value = examples,
                    onValueChange = { examples = it },
                    label = "Code & Walkthrough Examples",
                    singleLine = false,
                    minLines = 3,
                    testTag = "edit_lesson_examples"
                )
                JlabTextField(
                    value = practice,
                    onValueChange = { practice = it },
                    label = "Practice / Challenge",
                    singleLine = false,
                    minLines = 2,
                    testTag = "edit_lesson_practice"
                )
                JlabTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Personal Notes",
                    singleLine = false,
                    minLines = 2,
                    testTag = "edit_lesson_notes"
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
                            lesson.copy(
                                title = title.trim(),
                                objective = objective.trim(),
                                theory = theory.trim(),
                                examples = examples.trim(),
                                practice = practice.trim(),
                                notes = notes.trim(),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                },
                testTag = "edit_lesson_save"
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
