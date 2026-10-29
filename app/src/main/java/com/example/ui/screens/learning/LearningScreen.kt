package com.example.ui.screens.learning

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.ui.components.JlabConfirmDialog
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabFloatingActionButton
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabLinearProgress
import com.example.ui.components.JlabMetricTile
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSearchBar
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun LearningScreen(
    items: List<LearningEntity>,
    allLessons: List<LearningLessonEntity> = emptyList(),
    notes: List<NoteEntity> = emptyList(),
    files: List<FileAttachmentEntity> = emptyList(),
    getModulesForCourse: (String) -> Flow<List<LearningModuleEntity>>,
    getLessonsForCourse: (String) -> Flow<List<LearningLessonEntity>>,
    onSaveItem: (LearningEntity) -> Unit,
    onDeleteItem: (String) -> Unit,
    onSaveModule: (LearningModuleEntity) -> Unit,
    onDeleteModule: (String) -> Unit,
    onSaveLesson: (LearningLessonEntity) -> Unit,
    onDeleteLesson: (String, String) -> Unit,
    onToggleLessonCompleted: (String, String, Boolean) -> Unit,
    onOpenNote: (NoteEntity) -> Unit,
    onCreateCourseNote: (String) -> Unit,
    onOpenResource: (FileAttachmentEntity) -> Unit,
    onAttachResource: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedCourse by remember { mutableStateOf<LearningEntity?>(null) }
    var selectedLesson by remember { mutableStateOf<LearningLessonEntity?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    val filterTabs = listOf("ALL", "COURSES", "SKILLS", "BOOKS", "COMPLETED")

    var showCreateCourseDialog by remember { mutableStateOf(false) }
    var deletingCourseTarget by remember { mutableStateOf<LearningEntity?>(null) }

    val currentCourse = remember(items, selectedCourse) {
        selectedCourse?.let { sc -> items.find { it.id == sc.id } ?: sc }
    }

    BackHandler {
        when {
            selectedLesson != null -> selectedLesson = null
            selectedCourse != null -> selectedCourse = null
            else -> onNavigateBack()
        }
    }

    // 1. Lesson Workspace View
    if (currentCourse != null && selectedLesson != null) {
        val courseLessons by getLessonsForCourse(currentCourse.id).collectAsState(initial = emptyList())
        val courseModules by getModulesForCourse(currentCourse.id).collectAsState(initial = emptyList())
        val currentModule = courseModules.find { it.id == selectedLesson!!.moduleId }

        LessonWorkspaceScreen(
            courseTitle = currentCourse.title,
            moduleTitle = currentModule?.title ?: "Module",
            lesson = selectedLesson!!,
            allLessonsInCourse = courseLessons,
            onUpdateLesson = { updated ->
                onSaveLesson(updated)
                selectedLesson = updated
            },
            onToggleCompleted = { isCompleted ->
                onToggleLessonCompleted(selectedLesson!!.id, currentCourse.id, isCompleted)
                selectedLesson = selectedLesson!!.copy(
                    isCompleted = isCompleted,
                    completedAt = if (isCompleted) System.currentTimeMillis() else null
                )
            },
            onSelectLesson = { nextLesson ->
                selectedLesson = nextLesson
            },
            onBack = { selectedLesson = null }
        )
        return
    }

    // 2. Course Detail View
    if (currentCourse != null) {
        val courseModules by getModulesForCourse(currentCourse.id).collectAsState(initial = emptyList())
        val courseLessons by getLessonsForCourse(currentCourse.id).collectAsState(initial = emptyList())
        val courseNotes = remember(notes, currentCourse.id) {
            notes.filter { it.learningId == currentCourse.id }
        }
        val courseFiles = remember(files, currentCourse.id) {
            files.filter { it.learningId == currentCourse.id }
        }

        CourseDetailScreen(
            course = currentCourse,
            modules = courseModules,
            lessons = courseLessons,
            notes = courseNotes,
            resources = courseFiles,
            onUpdateCourse = { updated ->
                onSaveItem(updated)
                selectedCourse = updated
            },
            onDeleteCourse = {
                onDeleteItem(currentCourse.id)
                selectedCourse = null
            },
            onSaveModule = onSaveModule,
            onDeleteModule = onDeleteModule,
            onSaveLesson = onSaveLesson,
            onDeleteLesson = { lessonId -> onDeleteLesson(lessonId, currentCourse.id) },
            onToggleLessonCompleted = { lessonId, isDone ->
                onToggleLessonCompleted(lessonId, currentCourse.id, isDone)
            },
            onOpenLesson = { lesson -> selectedLesson = lesson },
            onOpenNote = onOpenNote,
            onCreateCourseNote = { onCreateCourseNote(currentCourse.id) },
            onOpenResource = onOpenResource,
            onAttachResource = { onAttachResource(currentCourse.id) },
            onBack = { selectedCourse = null }
        )
        return
    }

    // 3. Learning Home View
    val activeCourses = remember(items) {
        items.filter { it.status == "LEARNING" || it.status == "NOT_STARTED" }
    }
    val completedCourses = remember(items) { items.filter { it.status == "COMPLETED" } }
    val completedLessonsTotal = remember(allLessons) { allLessons.count { it.isCompleted } }

    val filteredItems = remember(items, searchQuery, selectedCategoryFilter) {
        var list = items
        if (selectedCategoryFilter != "ALL") {
            list = when (selectedCategoryFilter) {
                "COMPLETED" -> list.filter { it.status == "COMPLETED" }
                "COURSES" -> list.filter { it.type.contains("Course", ignoreCase = true) }
                "SKILLS" -> list.filter { it.type.contains("Skill", ignoreCase = true) }
                "BOOKS" -> list.filter {
                    it.type.contains("Book", ignoreCase = true) ||
                        it.type.contains("Resource", ignoreCase = true)
                }
                else -> list
            }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                    it.goal.lowercase().contains(q) ||
                    it.type.lowercase().contains(q)
            }
        }
        list
    }

    val continueCourse = remember(items) {
        items.firstOrNull { it.status == "LEARNING" && it.progressPercent < 100 }
            ?: items.firstOrNull { it.progressPercent in 1..99 }
            ?: items.firstOrNull { it.status == "NOT_STARTED" }
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Learning",
                subtitle = "Build skills systematically",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
                testTag = "learning_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "Add course",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { showCreateCourseDialog = true },
                        testTag = "learning_add_action"
                    )
                }
            )
        },
        floatingActionButton = {
            JlabFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "Create new course",
                onClick = { showCreateCourseDialog = true },
                testTag = "learning_fab"
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = AppTheme.spacing.screenHorizontal,
                end = AppTheme.spacing.screenHorizontal,
                top = AppTheme.spacing.screenVertical,
                bottom = AppTheme.spacing.listBottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.mdPlus)
        ) {
            item {
                JlabSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search courses, skills, goals...",
                    testTag = "learning_search_bar"
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    JlabMetricTile(
                        label = "Active",
                        value = "${activeCourses.size}",
                        icon = Icons.Outlined.School,
                        accentColor = AppTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    JlabMetricTile(
                        label = "Completed",
                        value = "${completedCourses.size}",
                        icon = Icons.Outlined.CheckCircle,
                        accentColor = AppTheme.extendedColors.success,
                        modifier = Modifier.weight(1f)
                    )
                    JlabMetricTile(
                        label = "Lessons Done",
                        value = "$completedLessonsTotal",
                        icon = Icons.Outlined.DoneAll,
                        accentColor = AppTheme.extendedColors.info,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (continueCourse != null && searchQuery.isBlank() && selectedCategoryFilter == "ALL") {
                item {
                    val courseLessons = allLessons.filter { it.learningId == continueCourse.id }
                    val nextLesson = courseLessons.firstOrNull { !it.isCompleted }
                    val completedInCourse = courseLessons.count { it.isCompleted }
                    val totalInCourse = courseLessons.size

                    JlabCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppTheme.extendedShapes.heroCard,
                        containerColor = AppTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        borderColor = AppTheme.colorScheme.primary.copy(alpha = 0.3f),
                        onClick = { selectedCourse = continueCourse },
                        testTag = "learning_continue_hero"
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            JlabTagBadge(
                                text = "CONTINUE LEARNING",
                                containerColor = AppTheme.colorScheme.primary,
                                contentColor = AppTheme.colorScheme.onPrimary
                            )

                            Text(
                                text = "${continueCourse.progressPercent}%",
                                style = AppTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(AppTheme.spacing.itemGap))

                        Text(
                            text = continueCourse.title,
                            style = AppTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colorScheme.onSurface
                        )

                        if (nextLesson != null) {
                            Text(
                                text = "Up next: ${nextLesson.title}",
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        } else if (totalInCourse > 0) {
                            Text(
                                text = "$completedInCourse of $totalInCourse lessons completed",
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(AppTheme.spacing.itemGap))
                        JlabLinearProgress(progress = continueCourse.progressPercent / 100f)
                        Spacer(modifier = Modifier.height(AppTheme.spacing.md))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            JlabPrimaryButton(
                                text = "Continue",
                                icon = Icons.AutoMirrored.Filled.ArrowForward,
                                onClick = { selectedCourse = continueCourse },
                                testTag = "learning_continue_button"
                            )
                        }
                    }
                }
            }

            item {
                JlabFilterChipRow(
                    options = filterTabs,
                    selectedOption = selectedCategoryFilter,
                    onSelect = { selectedCategoryFilter = it },
                    contentPadding = PaddingValues(AppTheme.spacing.none)
                )
            }

            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppTheme.spacing.xxl),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            icon = Icons.Default.School,
                            title = if (searchQuery.isNotEmpty()) {
                                "No matching courses"
                            } else {
                                "No courses found"
                            },
                            message = "Create a course or skill roadmap to organize modules, lessons, and dedicated study notes.",
                            actionButtonText = "Create Course",
                            onActionClick = { showCreateCourseDialog = true },
                            testTag = "learning_empty_state"
                        )
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { course ->
                    val courseLessons = allLessons.filter { it.learningId == course.id }
                    val totalL = courseLessons.size
                    val completedL = courseLessons.count { it.isCompleted }

                    CompactCourseCard(
                        course = course,
                        lessonsCount = totalL,
                        completedLessons = completedL,
                        onClick = { selectedCourse = course },
                        onDelete = { deletingCourseTarget = course }
                    )
                }
            }
        }
    }

    if (showCreateCourseDialog) {
        CreateCourseDialog(
            onDismiss = { showCreateCourseDialog = false },
            onSave = { newCourse ->
                onSaveItem(newCourse)
                showCreateCourseDialog = false
                selectedCourse = newCourse
            }
        )
    }

    deletingCourseTarget?.let { target ->
        JlabConfirmDialog(
            title = "Delete Course?",
            message = "Are you sure you want to delete \"${target.title}\" and its curriculum?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                onDeleteItem(target.id)
                deletingCourseTarget = null
            },
            onDismiss = { deletingCourseTarget = null },
            testTag = "delete_course_dialog"
        )
    }
}

@Composable
private fun CompactCourseCard(
    course: LearningEntity,
    lessonsCount: Int,
    completedLessons: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    JlabCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(AppTheme.spacing.mdPlus),
        onClick = onClick,
        testTag = "course_card_${course.id}"
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
                Text(
                    text = course.type,
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }

            Box {
                JlabIconButton(
                    icon = Icons.Default.MoreVert,
                    contentDescription = "Course options",
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
                            onClick()
                        },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Delete Course", color = AppTheme.extendedColors.danger) },
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

        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))

        Text(
            text = course.title,
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (course.goal.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
            Text(
                text = course.goal,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
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
                text = if (lessonsCount > 0) {
                    "$completedLessons / $lessonsCount lessons"
                } else {
                    "Curriculum ready"
                },
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${course.progressPercent}%",
                style = AppTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
        JlabLinearProgress(
            progress = course.progressPercent / 100f,
            height = AppTheme.spacing.progressHeightThin
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Continue",
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = AppTheme.colorScheme.primary,
                modifier = Modifier.size(AppTheme.spacing.iconXs)
            )
        }
    }
}

@Composable
fun CreateCourseDialog(
    onDismiss: () -> Unit,
    onSave: (LearningEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Course") }
    var goal by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "Create New Course",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Course Title *",
                    placeholder = "e.g. Modern Android Architecture",
                    singleLine = true,
                    testTag = "course_title_input"
                )
                JlabTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = "Type / Level",
                    placeholder = "e.g. Course, Skill, Book",
                    singleLine = true,
                    testTag = "course_type_input"
                )
                JlabTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = "Primary Objective / Target",
                    placeholder = "e.g. Build reactive offline-first apps",
                    singleLine = false,
                    minLines = 2,
                    testTag = "course_goal_input"
                )
                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    placeholder = "Curriculum overview & roadmap notes",
                    singleLine = false,
                    minLines = 2,
                    testTag = "course_desc_input"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Create Course",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            LearningEntity(
                                title = title.trim(),
                                type = type.trim().ifBlank { "Course" },
                                goal = goal.trim(),
                                description = description.trim(),
                                status = "NOT_STARTED",
                                progressPercent = 0,
                                startDate = System.currentTimeMillis()
                            )
                        )
                    }
                },
                testTag = "create_course_confirm"
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
