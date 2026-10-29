package com.example.ui.screens.business

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FolderOpen
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
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.BusinessEntity
import com.example.data.local.entity.FileAttachmentEntity
import com.example.data.local.entity.GoalEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.JlabConfirmDialog
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AppTheme

@Composable
fun BusinessDetailScreen(
    business: BusinessEntity,
    goals: List<GoalEntity>,
    tasks: List<TaskEntity>,
    projects: List<ProjectEntity>,
    notes: List<NoteEntity>,
    resources: List<FileAttachmentEntity>,
    allProjects: List<ProjectEntity>,
    activities: List<ActivityEntity>,
    onUpdateBusiness: (BusinessEntity) -> Unit,
    onDeleteBusiness: () -> Unit,
    onSaveGoal: (GoalEntity) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onSaveTask: (TaskEntity) -> Unit,
    onCompleteTask: (String, String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onLinkProject: (String) -> Unit,
    onCreateProject: (ProjectEntity) -> Unit,
    onOpenProject: (ProjectEntity) -> Unit,
    onOpenNote: (NoteEntity) -> Unit,
    onCreateNote: () -> Unit,
    onOpenResource: (FileAttachmentEntity) -> Unit,
    onAttachResource: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val pendingTasksCount = remember(tasks) { tasks.count { it.status != "COMPLETED" } }
    val tabs = listOf(
        "Overview",
        "Goals (${goals.size})",
        "Tasks ($pendingTasksCount)",
        "Projects (${projects.size})",
        "Notes (${notes.size})",
        "Resources (${resources.size})",
        "Activity"
    )

    var showEditDialog by remember { mutableStateOf(false) }
    var showCreateGoalDialog by remember { mutableStateOf(false) }
    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var showLinkProjectDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = business.name,
                subtitle = "Business Operating Workspace",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                testTag = "business_detail_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit business",
                        onClick = { showEditDialog = true },
                        testTag = "business_detail_edit"
                    )
                    Box {
                        JlabIconButton(
                            icon = Icons.Default.MoreVert,
                            contentDescription = "More business actions",
                            onClick = { showMoreMenu = true },
                            testTag = "business_detail_more"
                        )
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Add Task") },
                                onClick = {
                                    showMoreMenu = false
                                    showCreateTaskDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Add Goal") },
                                onClick = {
                                    showMoreMenu = false
                                    showCreateGoalDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Link Project") },
                                onClick = {
                                    showMoreMenu = false
                                    showLinkProjectDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.FolderOpen, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Create Note") },
                                onClick = {
                                    showMoreMenu = false
                                    onCreateNote()
                                },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = null)
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Delete Business", color = AppTheme.extendedColors.danger) },
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
                        StatusBadge(status = business.status)

                        Text(
                            text = "$pendingTasksCount pending tasks",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(AppTheme.spacing.compactGap))

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
                    0 -> BusinessOverviewTab(
                        business = business,
                        goalsCount = goals.size,
                        pendingTasksCount = pendingTasksCount,
                        projectsCount = projects.size,
                        notesCount = notes.size,
                        onAddTask = { showCreateTaskDialog = true },
                        onAddGoal = { showCreateGoalDialog = true }
                    )
                    1 -> BusinessGoalsTab(
                        goals = goals,
                        onAddGoal = { showCreateGoalDialog = true },
                        onDeleteGoal = onDeleteGoal
                    )
                    2 -> BusinessTasksTab(
                        tasks = tasks,
                        onAddTask = { showCreateTaskDialog = true },
                        onCompleteTask = onCompleteTask,
                        onDeleteTask = onDeleteTask
                    )
                    3 -> BusinessProjectsTab(
                        projects = projects,
                        onLinkProject = { showLinkProjectDialog = true },
                        onOpenProject = onOpenProject
                    )
                    4 -> BusinessNotesTab(
                        notes = notes,
                        onOpenNote = onOpenNote,
                        onCreateNote = onCreateNote
                    )
                    5 -> BusinessResourcesTab(
                        resources = resources,
                        onOpenResource = onOpenResource,
                        onAttachResource = onAttachResource
                    )
                    6 -> BusinessActivityTab(
                        activities = activities.filter {
                            it.itemType == "BUSINESS" || it.summary.contains(business.name, ignoreCase = true)
                        }
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        EditBusinessDialog(
            business = business,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                onUpdateBusiness(updated)
                showEditDialog = false
            }
        )
    }

    if (showCreateTaskDialog) {
        CreateBusinessTaskDialog(
            businessId = business.id,
            onDismiss = { showCreateTaskDialog = false },
            onSave = {
                onSaveTask(it)
                showCreateTaskDialog = false
            }
        )
    }

    if (showCreateGoalDialog) {
        CreateBusinessGoalDialog(
            businessId = business.id,
            onDismiss = { showCreateGoalDialog = false },
            onSave = {
                onSaveGoal(it)
                showCreateGoalDialog = false
            }
        )
    }

    if (showLinkProjectDialog) {
        LinkProjectDialog(
            unlinkedProjects = allProjects.filter { it.businessId == null || it.businessId != business.id },
            onDismiss = { showLinkProjectDialog = false },
            onLinkExisting = { projectId ->
                onLinkProject(projectId)
                showLinkProjectDialog = false
            },
            onCreateNew = { newProj ->
                onCreateProject(newProj.copy(businessId = business.id))
                showLinkProjectDialog = false
            }
        )
    }

    if (showDeleteConfirm) {
        JlabConfirmDialog(
            title = "Delete Business?",
            message = "Are you sure you want to permanently delete \"${business.name}\"?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                showDeleteConfirm = false
                onDeleteBusiness()
            },
            onDismiss = { showDeleteConfirm = false },
            testTag = "business_detail_delete_dialog"
        )
    }
}
