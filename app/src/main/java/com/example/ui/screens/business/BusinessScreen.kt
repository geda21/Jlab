package com.example.ui.screens.business

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.NoteAlt
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
import com.example.ui.components.JlabConfirmDialog
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabFloatingActionButton
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSearchBar
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AppTheme

@Composable
fun BusinessScreen(
    businesses: List<BusinessEntity>,
    allTasks: List<TaskEntity> = emptyList(),
    allProjects: List<ProjectEntity> = emptyList(),
    allGoals: List<GoalEntity> = emptyList(),
    allNotes: List<NoteEntity> = emptyList(),
    allFiles: List<FileAttachmentEntity> = emptyList(),
    activities: List<ActivityEntity> = emptyList(),
    onSaveBusiness: (BusinessEntity) -> Unit,
    onDeleteBusiness: (String) -> Unit,
    onSaveGoal: (GoalEntity) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onSaveTask: (TaskEntity) -> Unit,
    onCompleteTask: (String, String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onSaveProject: (ProjectEntity) -> Unit,
    onOpenProject: (ProjectEntity) -> Unit,
    onOpenNote: (NoteEntity) -> Unit,
    onCreateBusinessNote: (String) -> Unit,
    onOpenResource: (FileAttachmentEntity) -> Unit,
    onAttachResource: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedBusiness by remember { mutableStateOf<BusinessEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }
    val statusFilters = listOf("ALL", "ACTIVE", "PAUSED", "PLANNING")

    var showCreateDialog by remember { mutableStateOf(false) }
    var deletingBusinessTarget by remember { mutableStateOf<BusinessEntity?>(null) }

    val currentBusiness = remember(businesses, selectedBusiness) {
        selectedBusiness?.let { sb -> businesses.find { it.id == sb.id } ?: sb }
    }

    BackHandler {
        if (selectedBusiness != null) {
            selectedBusiness = null
        } else {
            onNavigateBack()
        }
    }

    if (currentBusiness != null) {
        val bizGoals = remember(allGoals, currentBusiness.id) {
            allGoals.filter { it.businessId == currentBusiness.id }
        }
        val bizTasks = remember(allTasks, currentBusiness.id) {
            allTasks.filter { it.businessId == currentBusiness.id }
        }
        val bizProjects = remember(allProjects, currentBusiness.id) {
            allProjects.filter { it.businessId == currentBusiness.id }
        }
        val bizNotes = remember(allNotes, currentBusiness.id) {
            allNotes.filter { it.businessId == currentBusiness.id }
        }
        val bizFiles = remember(allFiles, currentBusiness.id) {
            allFiles.filter { it.businessId == currentBusiness.id }
        }

        BusinessDetailScreen(
            business = currentBusiness,
            goals = bizGoals,
            tasks = bizTasks,
            projects = bizProjects,
            notes = bizNotes,
            resources = bizFiles,
            allProjects = allProjects,
            activities = activities,
            onUpdateBusiness = { updated ->
                onSaveBusiness(updated)
                selectedBusiness = updated
            },
            onDeleteBusiness = {
                onDeleteBusiness(currentBusiness.id)
                selectedBusiness = null
            },
            onSaveGoal = onSaveGoal,
            onDeleteGoal = onDeleteGoal,
            onSaveTask = onSaveTask,
            onCompleteTask = onCompleteTask,
            onDeleteTask = onDeleteTask,
            onLinkProject = { projId ->
                val p = allProjects.find { it.id == projId }
                if (p != null) {
                    onSaveProject(p.copy(businessId = currentBusiness.id))
                }
            },
            onCreateProject = onSaveProject,
            onOpenProject = onOpenProject,
            onOpenNote = onOpenNote,
            onCreateNote = { onCreateBusinessNote(currentBusiness.id) },
            onOpenResource = onOpenResource,
            onAttachResource = { onAttachResource(currentBusiness.id) },
            onBack = { selectedBusiness = null }
        )
        return
    }

    val filteredBusinesses = remember(businesses, searchQuery, selectedStatusFilter) {
        var list = businesses
        if (selectedStatusFilter != "ALL") {
            list = list.filter { it.status.equals(selectedStatusFilter, ignoreCase = true) }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                    it.description.lowercase().contains(q) ||
                    it.goals.lowercase().contains(q)
            }
        }
        list
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Business Workspace",
                subtitle = "${businesses.size} entrepreneurial ventures",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
                testTag = "business_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "New business venture",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { showCreateDialog = true },
                        testTag = "business_add_action"
                    )
                }
            )
        },
        floatingActionButton = {
            JlabFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "Create business venture",
                onClick = { showCreateDialog = true },
                testTag = "business_fab"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
        ) {
            JlabSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search businesses, goals, offerings...",
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.compactGap
                ),
                testTag = "business_search_bar"
            )

            JlabFilterChipRow(
                options = statusFilters,
                selectedOption = selectedStatusFilter,
                onSelect = { selectedStatusFilter = it },
                modifier = Modifier.padding(vertical = AppTheme.spacing.xs)
            )

            if (filteredBusinesses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.BusinessCenter,
                        title = if (searchQuery.isNotEmpty()) {
                            "No matching businesses"
                        } else {
                            "No business records"
                        },
                        message = "Organize ventures, revenue goals, marketing angles, and product offerings.",
                        actionButtonText = "New Business",
                        onActionClick = { showCreateDialog = true },
                        testTag = "business_empty_state"
                    )
                }
            } else {
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
                    items(filteredBusinesses, key = { it.id }) { biz ->
                        val bizGoals = allGoals.count { it.businessId == biz.id }
                        val bizTasks = allTasks.count {
                            it.businessId == biz.id && it.status != "COMPLETED"
                        }
                        val bizProjects = allProjects.count { it.businessId == biz.id }
                        val bizNotes = allNotes.count { it.businessId == biz.id }

                        CompactBusinessCard(
                            business = biz,
                            goalsCount = bizGoals,
                            tasksCount = bizTasks,
                            projectsCount = bizProjects,
                            notesCount = bizNotes,
                            onClick = { selectedBusiness = biz },
                            onDelete = { deletingBusinessTarget = biz }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateBusinessDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { newBiz ->
                onSaveBusiness(newBiz)
                showCreateDialog = false
                selectedBusiness = newBiz
            }
        )
    }

    deletingBusinessTarget?.let { target ->
        JlabConfirmDialog(
            title = "Delete Business?",
            message = "Are you sure you want to delete \"${target.name}\"?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                onDeleteBusiness(target.id)
                deletingBusinessTarget = null
            },
            onDismiss = { deletingBusinessTarget = null },
            testTag = "delete_business_dialog"
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompactBusinessCard(
    business: BusinessEntity,
    goalsCount: Int,
    tasksCount: Int,
    projectsCount: Int,
    notesCount: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    JlabCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(AppTheme.spacing.mdPlus),
        onClick = onClick,
        testTag = "business_card_${business.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusBadge(status = business.status)

            Box {
                JlabIconButton(
                    icon = Icons.Default.MoreVert,
                    contentDescription = "Business options",
                    onClick = { showMenu = true },
                    testTag = "business_card_menu_${business.id}"
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
                        text = { Text("Delete Business", color = AppTheme.extendedColors.danger) },
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
            text = business.name,
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (business.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
            Text(
                text = business.description,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.itemGap))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)
        ) {
            JlabTagBadge(
                text = "$goalsCount Goals",
                icon = Icons.Outlined.Flag,
                containerColor = AppTheme.colorScheme.surfaceContainerHigh,
                contentColor = AppTheme.colorScheme.onSurfaceVariant
            )
            JlabTagBadge(
                text = "$tasksCount Tasks",
                icon = Icons.Outlined.CheckCircle,
                containerColor = AppTheme.colorScheme.surfaceContainerHigh,
                contentColor = AppTheme.colorScheme.onSurfaceVariant
            )
            JlabTagBadge(
                text = "$projectsCount Projects",
                icon = Icons.Outlined.Folder,
                containerColor = AppTheme.colorScheme.surfaceContainerHigh,
                contentColor = AppTheme.colorScheme.onSurfaceVariant
            )
            JlabTagBadge(
                text = "$notesCount Notes",
                icon = Icons.Outlined.NoteAlt,
                containerColor = AppTheme.colorScheme.surfaceContainerHigh,
                contentColor = AppTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.itemGap))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Open Workspace",
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
fun CreateBusinessDialog(
    onDismiss: () -> Unit,
    onSave: (BusinessEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var goals by remember { mutableStateOf("") }
    var revenueNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "New Business Venture",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)) {
                JlabTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Business Name *",
                    placeholder = "e.g. Fiverr Services, SaaS, Shop",
                    singleLine = true,
                    testTag = "business_name_input"
                )
                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Value Proposition",
                    placeholder = "What product or service is delivered?",
                    singleLine = false,
                    minLines = 2,
                    testTag = "business_desc_input"
                )
                JlabTextField(
                    value = goals,
                    onValueChange = { goals = it },
                    label = "Primary Growth Goal",
                    placeholder = "e.g. Get 5 consistent client orders/month",
                    singleLine = false,
                    minLines = 2,
                    testTag = "business_goals_input"
                )
                JlabTextField(
                    value = revenueNotes,
                    onValueChange = { revenueNotes = it },
                    label = "Revenue / Pricing Model",
                    placeholder = "e.g. Hourly rate, fixed package, recurring",
                    singleLine = false,
                    minLines = 2,
                    testTag = "business_revenue_input"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Create Business",
                enabled = name.isNotBlank(),
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            BusinessEntity(
                                name = name.trim(),
                                description = description.trim(),
                                goals = goals.trim(),
                                revenueNotes = revenueNotes.trim(),
                                status = "ACTIVE"
                            )
                        )
                    }
                },
                testTag = "create_business_confirm"
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
