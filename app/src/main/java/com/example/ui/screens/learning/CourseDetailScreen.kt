package com.example.ui.screens.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.data.local.entity.FileAttachmentEntity
import com.example.data.local.entity.LearningEntity
import com.example.data.local.entity.LearningLessonEntity
import com.example.data.local.entity.LearningModuleEntity
import com.example.data.local.entity.NoteEntity
import com.example.ui.components.JlabConfirmDialog
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabLinearProgress
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme

@Composable
fun CourseDetailScreen(
    course: LearningEntity,
    modules: List<LearningModuleEntity>,
    lessons: List<LearningLessonEntity>,
    notes: List<NoteEntity>,
    resources: List<FileAttachmentEntity>,
    onUpdateCourse: (LearningEntity) -> Unit,
    onDeleteCourse: () -> Unit,
    onSaveModule: (LearningModuleEntity) -> Unit,
    onDeleteModule: (String) -> Unit,
    onSaveLesson: (LearningLessonEntity) -> Unit,
    onDeleteLesson: (String) -> Unit,
    onToggleLessonCompleted: (String, Boolean) -> Unit,
    onOpenLesson: (LearningLessonEntity) -> Unit,
    onOpenNote: (NoteEntity) -> Unit,
    onCreateCourseNote: () -> Unit,
    onOpenResource: (FileAttachmentEntity) -> Unit,
    onAttachResource: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Overview",
        "Curriculum",
        "Notes (${notes.size})",
        "Resources (${resources.size})"
    )

    var showEditCourseDialog by remember { mutableStateOf(false) }
    var showAddModuleDialog by remember { mutableStateOf(false) }
    var editingModule by remember { mutableStateOf<LearningModuleEntity?>(null) }
    var addingLessonToModuleId by remember { mutableStateOf<String?>(null) }
    var editingLesson by remember { mutableStateOf<LearningLessonEntity?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    val completedLessons = remember(lessons) { lessons.count { it.isCompleted } }
    val totalLessons = remember(lessons) { lessons.size }
    val calculatedProgress = remember(completedLessons, totalLessons, course.progressPercent) {
        if (totalLessons > 0) (completedLessons * 100) / totalLessons else course.progressPercent
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = course.title,
                subtitle = "${course.type} • $calculatedProgress% complete",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                testTag = "course_detail_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit course",
                        onClick = { showEditCourseDialog = true },
                        testTag = "course_detail_edit"
                    )
                    Box {
                        JlabIconButton(
                            icon = Icons.Default.MoreVert,
                            contentDescription = "More course actions",
                            onClick = { showMoreMenu = true },
                            testTag = "course_detail_more"
                        )
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Add Module") },
                                onClick = {
                                    showMoreMenu = false
                                    showAddModuleDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Add Course Note") },
                                onClick = {
                                    showMoreMenu = false
                                    onCreateCourseNote()
                                },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = null)
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Delete Course", color = AppTheme.extendedColors.danger) },
                                onClick = {
                                    showMoreMenu = false
                                    showDeleteConfirm = true
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
            )
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
                        vertical = AppTheme.spacing.sm
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                        ) {
                            CourseStatusPill(status = course.status)
                            JlabTagBadge(
                                text = "$completedLessons / $totalLessons lessons",
                                containerColor = AppTheme.colorScheme.surfaceContainerHigh,
                                contentColor = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "$calculatedProgress%",
                            style = AppTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(AppTheme.spacing.compactGap))
                    JlabLinearProgress(progress = calculatedProgress / 100f)
                    Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        edgePadding = AppTheme.spacing.none,
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
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

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> CourseOverviewTab(
                        course = course,
                        modulesCount = modules.size,
                        lessonsCount = totalLessons,
                        completedCount = completedLessons,
                        progress = calculatedProgress,
                        onAddModule = { showAddModuleDialog = true },
                        onContinue = {
                            val next = lessons.firstOrNull { !it.isCompleted } ?: lessons.firstOrNull()
                            if (next != null) onOpenLesson(next)
                        }
                    )
                    1 -> CourseCurriculumTab(
                        modules = modules,
                        lessons = lessons,
                        onAddModule = { showAddModuleDialog = true },
                        onEditModule = { editingModule = it },
                        onDeleteModule = onDeleteModule,
                        onAddLesson = { moduleId -> addingLessonToModuleId = moduleId },
                        onEditLesson = { editingLesson = it },
                        onDeleteLesson = onDeleteLesson,
                        onToggleLessonCompleted = onToggleLessonCompleted,
                        onOpenLesson = onOpenLesson
                    )
                    2 -> CourseNotesTab(
                        notes = notes,
                        onOpenNote = onOpenNote,
                        onCreateNote = onCreateCourseNote
                    )
                    3 -> CourseResourcesTab(
                        resources = resources,
                        onOpenResource = onOpenResource,
                        onAttachResource = onAttachResource
                    )
                }
            }
        }
    }

    if (showEditCourseDialog) {
        EditCourseDialog(
            course = course,
            onDismiss = { showEditCourseDialog = false },
            onSave = { updated ->
                onUpdateCourse(updated)
                showEditCourseDialog = false
            }
        )
    }

    if (showAddModuleDialog) {
        CreateModuleDialog(
            learningId = course.id,
            nextSortOrder = modules.size + 1,
            onDismiss = { showAddModuleDialog = false },
            onSave = {
                onSaveModule(it)
                showAddModuleDialog = false
            }
        )
    }

    editingModule?.let { mod ->
        EditModuleDialog(
            module = mod,
            onDismiss = { editingModule = null },
            onSave = {
                onSaveModule(it)
                editingModule = null
            }
        )
    }

    addingLessonToModuleId?.let { moduleId ->
        val moduleLessons = lessons.filter { it.moduleId == moduleId }
        CreateLessonDialog(
            learningId = course.id,
            moduleId = moduleId,
            nextSortOrder = moduleLessons.size + 1,
            onDismiss = { addingLessonToModuleId = null },
            onSave = {
                onSaveLesson(it)
                addingLessonToModuleId = null
            }
        )
    }

    editingLesson?.let { lesson ->
        EditLessonDialog(
            lesson = lesson,
            onDismiss = { editingLesson = null },
            onSave = {
                onSaveLesson(it)
                editingLesson = null
            }
        )
    }

    if (showDeleteConfirm) {
        JlabConfirmDialog(
            title = "Delete Course?",
            message = "Are you sure you want to permanently delete \"${course.title}\"?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                showDeleteConfirm = false
                onDeleteCourse()
            },
            onDismiss = { showDeleteConfirm = false },
            testTag = "course_detail_delete_dialog"
        )
    }
}
