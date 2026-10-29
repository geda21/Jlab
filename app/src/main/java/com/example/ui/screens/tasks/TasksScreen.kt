package com.example.ui.screens.tasks

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabConfirmDialog
import com.example.ui.components.JlabFilterChip
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSearchTopBar
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTopBar
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TaskFilterTab(val label: String) {
    TODAY("Today"),
    UPCOMING("Upcoming"),
    OVERDUE("Overdue"),
    ALL("All Active"),
    COMPLETED("Completed"),
    INBOX("Inbox")
}

enum class TaskSortOrder(val label: String) {
    DUE_DATE("Due Date"),
    PRIORITY("Priority"),
    TITLE("Title (A-Z)"),
    NEWEST("Newest First")
}

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    projects: List<ProjectEntity> = emptyList(),
    onSaveTask: (TaskEntity) -> Unit,
    onCompleteTask: (String, String) -> Unit,
    onReopenTask: (String, String) -> Unit = { _, _ -> },
    onSnoozeTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(TaskFilterTab.TODAY) }
    var sortOrder by remember { mutableStateOf(TaskSortOrder.DUE_DATE) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var quickTodoText by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    val endOfToday = cal.timeInMillis

    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    val startOfToday = cal.timeInMillis

    val tabFiltered = remember(tasks, selectedTab, endOfToday, startOfToday) {
        when (selectedTab) {
            TaskFilterTab.TODAY -> tasks.filter {
                it.status != "COMPLETED" && ((it.dueDate != null && it.dueDate <= endOfToday) || it.dueDate == null)
            }
            TaskFilterTab.UPCOMING -> tasks.filter {
                it.status != "COMPLETED" && it.dueDate != null && it.dueDate > endOfToday
            }
            TaskFilterTab.OVERDUE -> tasks.filter {
                it.status != "COMPLETED" && it.dueDate != null && it.dueDate < startOfToday
            }
            TaskFilterTab.ALL -> tasks.filter { it.status != "COMPLETED" }
            TaskFilterTab.COMPLETED -> tasks.filter { it.status == "COMPLETED" }
            TaskFilterTab.INBOX -> tasks.filter { it.isQuickTodo || it.status == "INBOX" }
        }
    }

    val searchFiltered = remember(tabFiltered, searchQuery) {
        if (searchQuery.isBlank()) {
            tabFiltered
        } else {
            val q = searchQuery.trim().lowercase()
            tabFiltered.filter {
                it.title.lowercase().contains(q) ||
                    it.description.lowercase().contains(q) ||
                    it.category.lowercase().contains(q)
            }
        }
    }

    val sortedTasks = remember(searchFiltered, sortOrder) {
        when (sortOrder) {
            TaskSortOrder.DUE_DATE -> searchFiltered.sortedWith(
                compareBy<TaskEntity> { it.dueDate ?: Long.MAX_VALUE }
                    .thenBy { it.priorityRank() }
            )
            TaskSortOrder.PRIORITY -> searchFiltered.sortedBy { it.priorityRank() }
            TaskSortOrder.TITLE -> searchFiltered.sortedBy { it.title.lowercase() }
            TaskSortOrder.NEWEST -> searchFiltered.sortedByDescending { it.createdAt }
        }
    }

    val pendingCount = remember(tasks) { tasks.count { it.status != "COMPLETED" } }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            if (isSearchActive) {
                JlabSearchTopBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search tasks...",
                    onClose = {
                        isSearchActive = false
                        searchQuery = ""
                    },
                    testTag = "tasks_search_bar"
                )
            } else {
                JlabTopBar(
                    title = "Task Management",
                    subtitle = "$pendingCount pending · ${tasks.size} total tasks",
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    onNavigationClick = onNavigateBack,
                    testTag = "tasks_top_bar",
                    actions = {
                        JlabIconButton(
                            icon = Icons.Default.Search,
                            contentDescription = "Search tasks",
                            onClick = { isSearchActive = true },
                            testTag = "tasks_search_button"
                        )

                        Box {
                            JlabIconButton(
                                icon = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort tasks",
                                onClick = { showSortMenu = true },
                                testTag = "tasks_sort_button"
                            )
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                TaskSortOrder.entries.forEach { order ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (sortOrder == order) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(AppTheme.spacing.iconSm),
                                                        tint = AppTheme.colorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.width(AppTheme.spacing.compactGap))
                                                }
                                                Text(
                                                    text = order.label,
                                                    style = AppTheme.typography.bodyMedium
                                                )
                                            }
                                        },
                                        onClick = {
                                            sortOrder = order
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        JlabIconButton(
                            icon = Icons.Default.Add,
                            contentDescription = "Add task",
                            tint = AppTheme.colorScheme.primary,
                            onClick = { showAddDialog = true },
                            testTag = "tasks_add_button"
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
        ) {
            // Quick Todo Creation Bar
            JlabCard(
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.compactGap
                ),
                shape = AppTheme.extendedShapes.input,
                contentPadding = PaddingValues(
                    horizontal = AppTheme.spacing.compactCardPadding,
                    vertical = AppTheme.spacing.xxs
                ),
                testTag = "quick_todo_bar"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = AppTheme.colorScheme.primary,
                        modifier = Modifier.size(AppTheme.spacing.iconMd)
                    )
                    Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                    TextField(
                        value = quickTodoText,
                        onValueChange = { quickTodoText = it },
                        placeholder = {
                            Text(
                                text = "Quick todo (press send to create)",
                                style = AppTheme.typography.bodyMedium,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_todo_input"),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    JlabIconButton(
                        icon = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Add quick todo",
                        tint = if (quickTodoText.isNotBlank()) {
                            AppTheme.colorScheme.primary
                        } else {
                            AppTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        },
                        enabled = quickTodoText.isNotBlank(),
                        onClick = {
                            if (quickTodoText.isNotBlank()) {
                                onSaveTask(
                                    TaskEntity(
                                        title = quickTodoText.trim(),
                                        isQuickTodo = true,
                                        status = "INBOX",
                                        dueDate = System.currentTimeMillis()
                                    )
                                )
                                quickTodoText = ""
                                Toast.makeText(context, "Quick todo added!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        testTag = "quick_todo_submit"
                    )
                }
            }

            // Status Filter Tabs
            LazyRow(
                contentPadding = PaddingValues(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.xs
                ),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                items(TaskFilterTab.entries) { tab ->
                    val count = when (tab) {
                        TaskFilterTab.TODAY -> tasks.count {
                            it.status != "COMPLETED" && ((it.dueDate != null && it.dueDate <= endOfToday) || it.dueDate == null)
                        }
                        TaskFilterTab.UPCOMING -> tasks.count {
                            it.status != "COMPLETED" && it.dueDate != null && it.dueDate > endOfToday
                        }
                        TaskFilterTab.OVERDUE -> tasks.count {
                            it.status != "COMPLETED" && it.dueDate != null && it.dueDate < startOfToday
                        }
                        TaskFilterTab.ALL -> pendingCount
                        TaskFilterTab.COMPLETED -> tasks.count { it.status == "COMPLETED" }
                        TaskFilterTab.INBOX -> tasks.count { it.isQuickTodo || it.status == "INBOX" }
                    }

                    JlabFilterChip(
                        label = "${tab.label} ($count)",
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        testTag = "tasks_tab_${tab.name.lowercase()}"
                    )
                }
            }

            if (sortedTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = if (selectedTab == TaskFilterTab.COMPLETED) {
                            Icons.Default.CheckCircleOutline
                        } else {
                            Icons.Outlined.AssignmentTurnedIn
                        },
                        title = if (searchQuery.isNotBlank()) {
                            "No matching tasks found"
                        } else {
                            "No ${selectedTab.label.lowercase()} tasks"
                        },
                        message = if (searchQuery.isNotBlank()) {
                            "Try searching for a different keyword or filter."
                        } else {
                            "All tasks caught up for this view."
                        },
                        actionButtonText = if (selectedTab != TaskFilterTab.COMPLETED) "Create Task" else null,
                        onActionClick = { showAddDialog = true }
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = AppTheme.spacing.screenHorizontal,
                        end = AppTheme.spacing.screenHorizontal,
                        top = AppTheme.spacing.sm,
                        bottom = AppTheme.spacing.listBottomPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("tasks_list")
                ) {
                    items(sortedTasks, key = { it.id }) { task ->
                        val project = remember(task.projectId, projects) {
                            projects.find { it.id == task.projectId }
                        }

                        TaskItemRow(
                            task = task,
                            projectName = project?.name,
                            onComplete = {
                                onCompleteTask(task.id, task.title)
                                Toast.makeText(context, "Completed!", Toast.LENGTH_SHORT).show()
                            },
                            onReopen = {
                                onReopenTask(task.id, task.title)
                                Toast.makeText(context, "Reopened task", Toast.LENGTH_SHORT).show()
                            },
                            onSnooze = { onSnoozeTask(task.id) },
                            onEdit = { editingTask = task },
                            onDelete = { taskToDelete = task }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog || editingTask != null) {
        TaskEditDialog(
            existingTask = editingTask,
            projects = projects,
            onDismiss = {
                showAddDialog = false
                editingTask = null
            },
            onSave = { task ->
                onSaveTask(task)
                showAddDialog = false
                editingTask = null
                Toast.makeText(context, "Task saved successfully", Toast.LENGTH_SHORT).show()
            }
        )
    }

    taskToDelete?.let { target ->
        JlabConfirmDialog(
            title = "Delete Task?",
            message = "Are you sure you want to delete task \"${target.title}\"? This action cannot be undone.",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                onDeleteTask(target.id)
                taskToDelete = null
                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { taskToDelete = null },
            testTag = "delete_task_dialog"
        )
    }
}

@Composable
fun TaskItemRow(
    task: TaskEntity,
    projectName: String? = null,
    onComplete: () -> Unit,
    onReopen: () -> Unit = onComplete,
    onSnooze: () -> Unit = {},
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = task.status == "COMPLETED"
    val dateStr = task.dueDate?.let {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
    }
    val isOverdue = !isCompleted && task.dueDate != null && task.dueDate < System.currentTimeMillis() - 86_400_000L

    JlabCard(
        shape = AppTheme.extendedShapes.compactCard,
        contentPadding = PaddingValues(
            horizontal = AppTheme.spacing.compactCardPadding,
            vertical = AppTheme.spacing.sm
        ),
        onClick = onEdit,
        testTag = "task_item_${task.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (isCompleted) onReopen() else onComplete() },
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = if (isCompleted) "Reopen ${task.title}" else "Complete ${task.title}",
                    tint = if (isCompleted) {
                        AppTheme.extendedColors.success
                    } else {
                        AppTheme.colorScheme.primary
                    }
                )
            }

            Spacer(modifier = Modifier.width(AppTheme.spacing.xs))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = AppTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
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

                Row(
                    modifier = Modifier.padding(top = AppTheme.spacing.xxs),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (dateStr != null) {
                        Text(
                            text = if (isOverdue) "Overdue · $dateStr" else "Due: $dateStr",
                            style = AppTheme.extendedTypography.caption,
                            color = if (isOverdue) {
                                AppTheme.extendedColors.danger
                            } else {
                                AppTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Medium
                        )
                    }

                    if (!projectName.isNullOrBlank()) {
                        JlabTagBadge(
                            text = projectName,
                            containerColor = AppTheme.colorScheme.primaryContainer,
                            contentColor = AppTheme.colorScheme.onPrimaryContainer
                        )
                    } else if (task.category.isNotBlank() && task.category != "General") {
                        Text(
                            text = "• ${task.category}",
                            style = AppTheme.extendedTypography.caption,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(AppTheme.spacing.xs))

            PriorityBadge(priority = task.priority)

            IconButton(
                onClick = onDelete,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete ${task.title}",
                    modifier = Modifier.size(AppTheme.spacing.iconMd),
                    tint = AppTheme.extendedColors.danger
                )
            }
        }
    }
}

@Composable
fun TaskEditDialog(
    existingTask: TaskEntity?,
    projects: List<ProjectEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (TaskEntity) -> Unit
) {
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var priority by remember { mutableStateOf(existingTask?.priority ?: "MEDIUM") }
    var category by remember { mutableStateOf(existingTask?.category ?: "General") }
    var selectedProjectId by remember { mutableStateOf(existingTask?.projectId) }
    var dueDateSelection by remember {
        mutableStateOf(if (existingTask?.dueDate == null) "NONE" else "SET")
    }
    var isSaving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = if (existingTask != null) "Edit Task Details" else "New Actionable Task",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)
            ) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Task Title *",
                    singleLine = true,
                    testTag = "task_dialog_title_input"
                )

                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description & Notes",
                    singleLine = false,
                    minLines = 2,
                    testTag = "task_dialog_description_input"
                )

                if (projects.isNotEmpty()) {
                    Text(
                        text = "Link to Project",
                        style = AppTheme.typography.labelMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)) {
                        item {
                            JlabFilterChip(
                                label = "Standalone",
                                selected = selectedProjectId == null,
                                onClick = { selectedProjectId = null }
                            )
                        }
                        items(projects) { p ->
                            JlabFilterChip(
                                label = p.name,
                                selected = selectedProjectId == p.id,
                                onClick = { selectedProjectId = p.id }
                            )
                        }
                    }
                }

                Text(
                    text = "Priority Level",
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)) {
                    items(listOf("LOW", "MEDIUM", "HIGH", "CRITICAL")) { p ->
                        JlabFilterChip(
                            label = p,
                            selected = priority == p,
                            onClick = { priority = p }
                        )
                    }
                }

                Text(
                    text = "Schedule / Due Date",
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)) {
                    items(
                        listOf(
                            "TODAY" to "Today",
                            "TOMORROW" to "Tomorrow",
                            "NONE" to "No Date"
                        )
                    ) { (key, label) ->
                        JlabFilterChip(
                            label = label,
                            selected = dueDateSelection == key,
                            onClick = { dueDateSelection = key }
                        )
                    }
                }

                JlabTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = "Category",
                    singleLine = true
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Save Task",
                enabled = title.isNotBlank() && !isSaving,
                onClick = {
                    if (title.isNotBlank() && !isSaving) {
                        isSaving = true
                        val computedDueDate: Long? = when (dueDateSelection) {
                            "TODAY" -> System.currentTimeMillis()
                            "TOMORROW" -> System.currentTimeMillis() + 86_400_000L
                            "NONE" -> null
                            else -> existingTask?.dueDate ?: System.currentTimeMillis()
                        }

                        val task = existingTask?.copy(
                            title = title.trim(),
                            description = description.trim(),
                            priority = priority,
                            category = category.trim(),
                            projectId = selectedProjectId,
                            dueDate = computedDueDate,
                            updatedAt = System.currentTimeMillis()
                        ) ?: TaskEntity(
                            title = title.trim(),
                            description = description.trim(),
                            priority = priority,
                            category = category.trim(),
                            projectId = selectedProjectId,
                            dueDate = computedDueDate
                        )
                        onSave(task)
                    }
                },
                testTag = "task_dialog_save_button"
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text(
                    text = "Cancel",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

private fun TaskEntity.priorityRank(): Int {
    return when (this.priority) {
        "CRITICAL" -> 1
        "HIGH" -> 2
        "MEDIUM" -> 3
        else -> 4
    }
}
