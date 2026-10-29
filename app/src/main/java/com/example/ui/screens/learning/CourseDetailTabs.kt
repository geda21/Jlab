package com.example.ui.screens.learning

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import com.example.data.local.entity.FileAttachmentEntity
import com.example.data.local.entity.LearningEntity
import com.example.data.local.entity.LearningLessonEntity
import com.example.data.local.entity.LearningModuleEntity
import com.example.data.local.entity.NoteEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabMetricTile
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSectionCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun CourseOverviewTab(
    course: LearningEntity,
    modulesCount: Int,
    lessonsCount: Int,
    completedCount: Int,
    progress: Int,
    onAddModule: () -> Unit,
    onContinue: () -> Unit
) {
    var isDescriptionExpanded by remember { mutableStateOf(false) }

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
        if (lessonsCount > 0 && progress < 100) {
            item {
                JlabCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = AppTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    borderColor = AppTheme.colorScheme.primary.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NEXT STEP",
                                style = AppTheme.extendedTypography.overline,
                                color = AppTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                            Text(
                                text = "Continue Curriculum",
                                style = AppTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${lessonsCount - completedCount} lessons remaining",
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        JlabPrimaryButton(
                            text = "Continue",
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            onClick = onContinue,
                            testTag = "course_overview_continue"
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                JlabMetricTile(
                    label = "Modules",
                    value = "$modulesCount",
                    icon = Icons.Outlined.Layers,
                    accentColor = AppTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                JlabMetricTile(
                    label = "Completed",
                    value = "$completedCount / $lessonsCount",
                    icon = Icons.Outlined.CheckCircle,
                    accentColor = AppTheme.extendedColors.success,
                    modifier = Modifier.weight(1f)
                )
                JlabMetricTile(
                    label = "Progress",
                    value = "$progress%",
                    icon = Icons.Outlined.PieChart,
                    accentColor = AppTheme.extendedColors.info,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            JlabSectionCard(overline = "LEARNING OBJECTIVE") {
                Text(
                    text = course.goal.ifBlank { "Master this subject through disciplined daily lessons." },
                    style = AppTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AppTheme.colorScheme.onSurface
                )

                if (course.startDate != null || course.targetDate != null) {
                    Spacer(modifier = Modifier.height(AppTheme.spacing.itemGap))
                    HorizontalDivider(color = AppTheme.extendedColors.cardBorder)
                    Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (course.startDate != null) {
                            val sDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(course.startDate))
                            Text(
                                text = "Started: $sDate",
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (course.targetDate != null) {
                            val tDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(course.targetDate))
                            Text(
                                text = "Target: $tDate",
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (course.description.isNotBlank()) {
            item {
                JlabSectionCard(
                    overline = "COURSE DESCRIPTION",
                    headerAction = if (course.description.length > 180) {
                        {
                            TextButton(
                                onClick = { isDescriptionExpanded = !isDescriptionExpanded },
                                contentPadding = PaddingValues(AppTheme.spacing.none),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text(
                                    text = if (isDescriptionExpanded) "Show less" else "Show more",
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
                        text = course.description,
                        style = AppTheme.typography.bodyMedium,
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 4,
                        overflow = TextOverflow.Ellipsis,
                        color = AppTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (modulesCount == 0) {
            item {
                JlabCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(AppTheme.spacing.xl)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountTree,
                            contentDescription = null,
                            tint = AppTheme.colorScheme.primary,
                            modifier = Modifier.size(AppTheme.spacing.avatarSm)
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
                        Text(
                            text = "Ready to structure your learning?",
                            style = AppTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Divide this course into sequential modules and bite-sized lessons.",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                        JlabPrimaryButton(
                            text = "Add First Module",
                            icon = Icons.Default.Add,
                            onClick = onAddModule,
                            testTag = "course_add_first_module"
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CourseCurriculumTab(
    modules: List<LearningModuleEntity>,
    lessons: List<LearningLessonEntity>,
    onAddModule: () -> Unit,
    onEditModule: (LearningModuleEntity) -> Unit,
    onDeleteModule: (String) -> Unit,
    onAddLesson: (String) -> Unit,
    onEditLesson: (LearningLessonEntity) -> Unit,
    onDeleteLesson: (String) -> Unit,
    onToggleLessonCompleted: (String, Boolean) -> Unit,
    onOpenLesson: (LearningLessonEntity) -> Unit
) {
    if (modules.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Default.School,
                title = "No curriculum modules yet",
                message = "Build this course step by step. Create modules and add lessons underneath.",
                actionButtonText = "Add Module",
                onActionClick = onAddModule
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
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
    ) {
        item {
            SectionHeader(
                title = "Modules",
                count = modules.size,
                actionText = "+ Add Module",
                onActionClick = onAddModule,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.none)
            )
        }

        items(modules.sortedBy { it.sortOrder }, key = { it.id }) { module ->
            val moduleLessons = lessons.filter { it.moduleId == module.id }.sortedBy { it.sortOrder }
            ModuleAccordionCard(
                module = module,
                lessons = moduleLessons,
                onEditModule = { onEditModule(module) },
                onDeleteModule = { onDeleteModule(module.id) },
                onAddLesson = { onAddLesson(module.id) },
                onEditLesson = onEditLesson,
                onDeleteLesson = onDeleteLesson,
                onToggleLessonCompleted = onToggleLessonCompleted,
                onOpenLesson = onOpenLesson
            )
        }
    }
}

@Composable
private fun ModuleAccordionCard(
    module: LearningModuleEntity,
    lessons: List<LearningLessonEntity>,
    onEditModule: () -> Unit,
    onDeleteModule: () -> Unit,
    onAddLesson: () -> Unit,
    onEditLesson: (LearningLessonEntity) -> Unit,
    onDeleteLesson: (String) -> Unit,
    onToggleLessonCompleted: (String, Boolean) -> Unit,
    onOpenLesson: (LearningLessonEntity) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val completedCount = lessons.count { it.isCompleted }

    JlabCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(AppTheme.spacing.none),
        testTag = "module_card_${module.id}"
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(
                    start = AppTheme.spacing.mdPlus,
                    end = AppTheme.spacing.xs,
                    top = AppTheme.spacing.compactCardPadding,
                    bottom = AppTheme.spacing.compactCardPadding
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse module" else "Expand module",
                    tint = AppTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = module.title,
                        style = AppTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$completedCount of ${lessons.size} completed",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                JlabIconButton(
                    icon = Icons.Default.Add,
                    contentDescription = "Add lesson",
                    tint = AppTheme.colorScheme.primary,
                    onClick = onAddLesson
                )
                var showModuleMenu by remember { mutableStateOf(false) }
                Box {
                    JlabIconButton(
                        icon = Icons.Default.MoreVert,
                        contentDescription = "Module options",
                        onClick = { showModuleMenu = true }
                    )
                    DropdownMenu(
                        expanded = showModuleMenu,
                        onDismissRequest = { showModuleMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Module") },
                            onClick = {
                                showModuleMenu = false
                                onEditModule()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Module", color = AppTheme.extendedColors.danger) },
                            onClick = {
                                showModuleMenu = false
                                onDeleteModule()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = AppTheme.extendedColors.danger
                                )
                            }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.compactCardPadding,
                    vertical = AppTheme.spacing.xs
                )
            ) {
                if (module.description.isNotBlank()) {
                    Text(
                        text = module.description,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = AppTheme.spacing.compactGap,
                            vertical = AppTheme.spacing.xxs
                        )
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                }

                if (lessons.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppTheme.spacing.sm),
                        shape = AppTheme.extendedShapes.chip,
                        color = AppTheme.colorScheme.surfaceContainerLow
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppTheme.spacing.compactCardPadding),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "No lessons in this module",
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = onAddLesson,
                                contentPadding = PaddingValues(AppTheme.spacing.none),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text(
                                    text = "+ Add Lesson",
                                    style = AppTheme.typography.labelLarge,
                                    color = AppTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                } else {
                    lessons.forEachIndexed { idx, lesson ->
                        LessonItemRow(
                            index = idx + 1,
                            lesson = lesson,
                            onToggleCompleted = { onToggleLessonCompleted(lesson.id, !lesson.isCompleted) },
                            onOpenLesson = { onOpenLesson(lesson) },
                            onEdit = { onEditLesson(lesson) },
                            onDelete = { onDeleteLesson(lesson.id) }
                        )
                        if (idx < lessons.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = AppTheme.spacing.sm),
                                color = AppTheme.extendedColors.cardBorder
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(AppTheme.spacing.compactGap))
            }
        }
    }
}

@Composable
private fun LessonItemRow(
    index: Int,
    lesson: LearningLessonEntity,
    onToggleCompleted: () -> Unit,
    onOpenLesson: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenLesson)
            .padding(
                horizontal = AppTheme.spacing.xs,
                vertical = AppTheme.spacing.compactGap
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
        ) {
            JlabIconButton(
                icon = if (lesson.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = if (lesson.isCompleted) "Completed" else "Mark complete",
                tint = if (lesson.isCompleted) {
                    AppTheme.extendedColors.success
                } else {
                    AppTheme.colorScheme.onSurfaceVariant
                },
                onClick = onToggleCompleted
            )

            Column {
                Text(
                    text = "$index. ${lesson.title}",
                    style = AppTheme.typography.bodyMedium,
                    fontWeight = if (lesson.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                    color = if (lesson.isCompleted) {
                        AppTheme.colorScheme.onSurfaceVariant
                    } else {
                        AppTheme.colorScheme.onSurface
                    }
                )
                if (lesson.objective.isNotBlank()) {
                    Text(
                        text = lesson.objective,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AppTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(AppTheme.spacing.iconMd)
            )

            Box {
                JlabIconButton(
                    icon = Icons.Default.MoreVert,
                    contentDescription = "Lesson options",
                    onClick = { showMenu = true }
                )
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Open Workspace") },
                        onClick = {
                            showMenu = false
                            onOpenLesson()
                        },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit Details") },
                        onClick = {
                            showMenu = false
                            onEdit()
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Delete Lesson", color = AppTheme.extendedColors.danger) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = AppTheme.extendedColors.danger
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun CourseNotesTab(
    notes: List<NoteEntity>,
    onOpenNote: (NoteEntity) -> Unit,
    onCreateNote: () -> Unit
) {
    if (notes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Default.NoteAlt,
                title = "No course notes yet",
                message = "Keep dedicated study notes, cheat sheets, and summaries tied directly to this course.",
                actionButtonText = "Create Course Note",
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
                title = "Course Notes",
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
                onClick = { onOpenNote(note) }
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
internal fun CourseResourcesTab(
    resources: List<FileAttachmentEntity>,
    onOpenResource: (FileAttachmentEntity) -> Unit,
    onAttachResource: () -> Unit
) {
    if (resources.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Default.AttachFile,
                title = "No course resources attached",
                message = "Attach cheat sheet screenshots, syllabus documents, diagrams, and reference PDFs to this course.",
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
                title = "Attachments",
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
                onClick = { onOpenResource(file) }
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
