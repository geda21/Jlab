package com.example.ui.screens.projects

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.data.local.entity.FileAttachmentEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.ProjectDecisionEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabConfirmDialog
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabLinearProgress
import com.example.ui.components.JlabMetricTile
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSectionCard
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTopBar
import com.example.ui.components.PriorityBadge
import com.example.ui.components.ProjectCoverImage
import com.example.ui.components.StatusBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ProjectMilestone(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val targetDate: Long? = null
)

@Composable
fun ProjectDetailScreen(
    project: ProjectEntity,
    tasks: List<TaskEntity>,
    notes: List<NoteEntity>,
    files: List<FileAttachmentEntity> = emptyList(),
    decisions: List<ProjectDecisionEntity> = emptyList(),
    onUpdateProject: (ProjectEntity) -> Unit,
    onDuplicateProject: (String) -> Unit = {},
    onArchiveProject: (String) -> Unit = {},
    onDeleteProject: (String) -> Unit = {},
    onAddTask: (TaskEntity) -> Unit,
    onUpdateTask: (TaskEntity) -> Unit = {},
    onCompleteTask: (String, String) -> Unit,
    onReopenTask: (String, String) -> Unit = { _, _ -> },
    onDeleteTask: (String) -> Unit,
    onCreateNote: (NoteEntity) -> Unit = {},
    onDeleteNote: (String) -> Unit = {},
    onOpenNote: (String) -> Unit = {},
    onSaveFile: (FileAttachmentEntity) -> Unit = {},
    onDeleteFile: (FileAttachmentEntity) -> Unit = {},
    onSaveDecision: (ProjectDecisionEntity) -> Unit = {},
    onDeleteDecision: (String) -> Unit = {},
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val extended = AppTheme.extendedColors

    var selectedTab by remember { mutableIntStateOf(0) }
    var showEditProjectDialog by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var selectedTaskFilter by remember { mutableStateOf("ALL") }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }
    var showAddMilestoneDialog by remember { mutableStateOf(false) }

    var showAddDecisionDialog by remember { mutableStateOf(false) }
    var editingDecision by remember { mutableStateOf<ProjectDecisionEntity?>(null) }
    var decisionToDelete by remember { mutableStateOf<ProjectDecisionEntity?>(null) }

    var fileToDelete by remember { mutableStateOf<FileAttachmentEntity?>(null) }
    var previewImageFile by remember { mutableStateOf<FileAttachmentEntity?>(null) }
    var previewTextFile by remember { mutableStateOf<FileAttachmentEntity?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                for (uri in uris) {
                    importProjectAttachment(context, uri, project.id, "Image", onSaveFile)
                }
            }
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                for (uri in uris) {
                    val mime = context.contentResolver.getType(uri) ?: ""
                    val cat = if (mime.contains("pdf", ignoreCase = true)) "PDF" else "Document"
                    importProjectAttachment(context, uri, project.id, cat, onSaveFile)
                }
            }
        }
    }

    val completedTasksCount = tasks.count { it.status == "COMPLETED" }
    val totalTasksCount = tasks.size
    val autoCalculatedProgress = if (totalTasksCount > 0) {
        ((completedTasksCount.toFloat() / totalTasksCount.toFloat()) * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }

    val isAutoMode = project.progressMode == "AUTOMATIC"
    val displayProgress = if (isAutoMode) autoCalculatedProgress else project.manualProgressPercent.coerceIn(0, 100)

    val tabs = listOf(
        "Overview",
        "Tasks ($totalTasksCount)",
        "Notes (${notes.size})",
        "Files (${files.size})",
        "Decisions (${decisions.size})"
    )

    BackHandler(onBack = onNavigateBack)

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = project.name,
                subtitle = "Status: ${project.status} · $displayProgress% Complete (${if (isAutoMode) "Auto" else "Manual"})",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
                testTag = "project_detail_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Tune,
                        contentDescription = "Adjust progress",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { showProgressDialog = true },
                        testTag = "project_detail_progress_button"
                    )

                    Box {
                        JlabIconButton(
                            icon = Icons.Default.MoreVert,
                            contentDescription = "Project actions",
                            onClick = { showMoreMenu = true },
                            testTag = "project_detail_more_button"
                        )
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Project") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                onClick = {
                                    showMoreMenu = false
                                    showEditProjectDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Adjust Progress") },
                                leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null) },
                                onClick = {
                                    showMoreMenu = false
                                    showProgressDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Duplicate Project") },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                                onClick = {
                                    showMoreMenu = false
                                    onDuplicateProject(project.id)
                                    Toast.makeText(context, "Project duplicated", Toast.LENGTH_SHORT).show()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (project.isArchived) "Unarchive" else "Archive") },
                                leadingIcon = { Icon(Icons.Default.Archive, contentDescription = null) },
                                onClick = {
                                    showMoreMenu = false
                                    onArchiveProject(project.id)
                                    Toast.makeText(context, if (project.isArchived) "Unarchived" else "Archived", Toast.LENGTH_SHORT).show()
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Delete Project", color = extended.danger) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = extended.danger) },
                                onClick = {
                                    showMoreMenu = false
                                    showDeleteConfirmDialog = true
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
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = AppTheme.spacing.screenHorizontal,
                containerColor = AppTheme.colorScheme.surface,
                contentColor = AppTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = AppTheme.typography.labelLarge
                            )
                        },
                        modifier = Modifier.testTag("project_tab_$index")
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = AppTheme.spacing.screenHorizontal,
                            end = AppTheme.spacing.screenHorizontal,
                            top = AppTheme.spacing.screenPadding,
                            bottom = AppTheme.spacing.listBottomPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.mdPlus)
                    ) {
                        // Visual Identity Header Card (Cover + Avatar + Quick Edit)
                        item {
                            JlabCard(
                                contentPadding = PaddingValues(AppTheme.spacing.none),
                                testTag = "project_hero_card"
                            ) {
                                ProjectCoverImage(
                                    coverUri = project.coverUri,
                                    category = project.category,
                                    height = 128.dp
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AppTheme.spacing.cardPadding),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        UserAvatar(
                                            avatarUri = project.avatarUri,
                                            name = project.name,
                                            size = AppTheme.spacing.avatarMd,
                                            onClick = { showEditProjectDialog = true }
                                        )
                                        Spacer(modifier = Modifier.width(AppTheme.spacing.md))
                                        Column {
                                            Text(
                                                text = project.name,
                                                style = AppTheme.typography.titleMedium,
                                                color = AppTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = project.category,
                                                style = AppTheme.typography.bodySmall,
                                                color = AppTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    StatusBadge(status = project.status)
                                }
                            }
                        }

                        if (displayProgress >= 100 && project.status != "COMPLETED") {
                            item {
                                Surface(
                                    shape = AppTheme.extendedShapes.card,
                                    color = extended.successContainer,
                                    border = BorderStroke(AppTheme.spacing.borderThin, extended.success.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(AppTheme.spacing.mdPlus),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Project Reached 100%",
                                                style = AppTheme.typography.titleSmall,
                                                color = extended.onSuccessContainer
                                            )
                                            Text(
                                                text = "All milestones achieved. Mark this project Completed?",
                                                style = AppTheme.typography.bodySmall,
                                                color = AppTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
                                        JlabPrimaryButton(
                                            text = "Mark Done",
                                            onClick = {
                                                onUpdateProject(
                                                    project.copy(
                                                        status = "COMPLETED",
                                                        progressPercent = 100,
                                                        updatedAt = System.currentTimeMillis()
                                                    )
                                                )
                                                Toast.makeText(context, "Marked project as Completed!", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Progress Command Card
                        item {
                            JlabSectionCard(
                                overline = "Project Progress",
                                headerAction = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        JlabTagBadge(
                                            text = if (isAutoMode) "AUTO (Tasks)" else "MANUAL",
                                            containerColor = if (isAutoMode) extended.infoContainer else extended.warningContainer,
                                            contentColor = if (isAutoMode) extended.onInfoContainer else extended.onWarningContainer
                                        )
                                        TextButton(
                                            onClick = { showProgressDialog = true },
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                                modifier = Modifier.size(AppTheme.spacing.iconXs)
                                            )
                                            Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                                            Text("Adjust", style = AppTheme.typography.labelMedium)
                                        }
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isAutoMode) "$completedTasksCount of $totalTasksCount tasks complete" else "Manual progress set",
                                        style = AppTheme.typography.bodySmall,
                                        color = AppTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$displayProgress%",
                                        style = AppTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
                                JlabLinearProgress(
                                    progress = displayProgress / 100f,
                                    height = AppTheme.spacing.sm,
                                    modifier = Modifier.clickable { showProgressDialog = true }
                                )
                            }
                        }

                        // Objective Card
                        item {
                            JlabSectionCard(
                                overline = "Project Objective / Target",
                                headerAction = {
                                    JlabIconButton(
                                        icon = Icons.Default.Edit,
                                        contentDescription = "Edit objective",
                                        onClick = { showEditProjectDialog = true }
                                    )
                                }
                            ) {
                                Text(
                                    text = project.objective.ifBlank { "No specific objective specified yet. Tap edit to define targets." },
                                    style = AppTheme.typography.bodyMedium,
                                    color = AppTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Description Card
                        item {
                            var isExpanded by remember { mutableStateOf(false) }
                            val desc = project.description.trim()

                            JlabSectionCard(
                                overline = "Description & Context",
                                headerAction = {
                                    JlabIconButton(
                                        icon = Icons.Default.Edit,
                                        contentDescription = "Edit description",
                                        onClick = { showEditProjectDialog = true }
                                    )
                                }
                            ) {
                                if (desc.isBlank()) {
                                    Text(
                                        text = "No description provided for this project.",
                                        style = AppTheme.typography.bodyMedium,
                                        color = AppTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Text(
                                        text = desc,
                                        style = AppTheme.typography.bodyMedium,
                                        color = AppTheme.colorScheme.onSurface,
                                        maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (desc.length > 180) {
                                        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                                        TextButton(
                                            onClick = { isExpanded = !isExpanded },
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Text(
                                                text = if (isExpanded) "Show Less" else "Read More",
                                                style = AppTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Operational Stats Grid
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                            ) {
                                JlabMetricTile(
                                    label = "Open",
                                    value = "${totalTasksCount - completedTasksCount}",
                                    accentColor = extended.warning,
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedTab = 1 }
                                )
                                JlabMetricTile(
                                    label = "Done",
                                    value = "$completedTasksCount",
                                    accentColor = extended.success,
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedTab = 1 }
                                )
                                JlabMetricTile(
                                    label = "Notes",
                                    value = "${notes.size}",
                                    accentColor = extended.info,
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedTab = 2 }
                                )
                                JlabMetricTile(
                                    label = "Files",
                                    value = "${files.size}",
                                    accentColor = AppTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedTab = 3 }
                                )
                            }
                        }

                        // Metadata Parameters Card
                        item {
                            JlabSectionCard(
                                overline = "Project Parameters",
                                headerAction = {
                                    JlabIconButton(
                                        icon = Icons.Default.Edit,
                                        contentDescription = "Edit parameters",
                                        onClick = { showEditProjectDialog = true }
                                    )
                                }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Category", style = AppTheme.extendedTypography.caption, color = AppTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                                        Text(project.category, style = AppTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column {
                                        Text("Priority", style = AppTheme.extendedTypography.caption, color = AppTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                                        PriorityBadge(priority = project.priority)
                                    }
                                    Column {
                                        Text("Status", style = AppTheme.extendedTypography.caption, color = AppTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                                        StatusBadge(status = project.status)
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = AppTheme.spacing.smPlus))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val dateFmt = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
                                    Column {
                                        Text("Start Date", style = AppTheme.extendedTypography.caption, color = AppTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = dateFmt.format(Date(project.startDate ?: project.createdAt)),
                                            style = AppTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Column {
                                        Text("Target Deadline", style = AppTheme.extendedTypography.caption, color = AppTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = project.targetDate?.let { dateFmt.format(Date(it)) } ?: "No deadline set",
                                            style = AppTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Project Milestones Card
                        item {
                            val milestones = remember(project.milestonesJson) {
                                parseMilestones(project.milestonesJson)
                            }
                            JlabSectionCard(
                                overline = "Project Milestones (${milestones.count { it.isCompleted }}/${milestones.size})",
                                headerAction = {
                                    TextButton(
                                        onClick = { showAddMilestoneDialog = true },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(AppTheme.spacing.iconSm))
                                        Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                                        Text("Add", style = AppTheme.typography.labelMedium)
                                    }
                                }
                            ) {
                                if (milestones.isEmpty()) {
                                    Text(
                                        text = "No milestones defined yet. Break this project down into major phases.",
                                        style = AppTheme.typography.bodySmall,
                                        color = AppTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                                        milestones.forEach { m ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(AppTheme.extendedShapes.chip)
                                                    .clickable {
                                                        val updated = milestones.map {
                                                            if (it.id == m.id) it.copy(isCompleted = !it.isCompleted) else it
                                                        }
                                                        onUpdateProject(
                                                            project.copy(
                                                                milestonesJson = serializeMilestones(updated),
                                                                updatedAt = System.currentTimeMillis()
                                                            )
                                                        )
                                                    }
                                                    .padding(vertical = AppTheme.spacing.xs),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (m.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                                    contentDescription = null,
                                                    tint = if (m.isCompleted) extended.success else AppTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(AppTheme.spacing.iconMd)
                                                )
                                                Spacer(modifier = Modifier.width(AppTheme.spacing.smPlus))
                                                Text(
                                                    text = m.title,
                                                    style = AppTheme.typography.bodyMedium,
                                                    textDecoration = if (m.isCompleted) TextDecoration.LineThrough else null,
                                                    color = if (m.isCompleted) AppTheme.colorScheme.onSurfaceVariant else AppTheme.colorScheme.onSurface,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                IconButton(
                                                    onClick = {
                                                        val updated = milestones.filter { it.id != m.id }
                                                        onUpdateProject(
                                                            project.copy(
                                                                milestonesJson = serializeMilestones(updated),
                                                                updatedAt = System.currentTimeMillis()
                                                            )
                                                        )
                                                    },
                                                    modifier = Modifier.minimumInteractiveComponentSize()
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Delete milestone",
                                                        modifier = Modifier.size(AppTheme.spacing.iconSm),
                                                        tint = AppTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> ProjectTasksTab(
                    projectName = project.name,
                    tasks = tasks,
                    selectedTaskFilter = selectedTaskFilter,
                    onSelectFilter = { selectedTaskFilter = it },
                    onAddTaskClick = { showAddTaskDialog = true },
                    onEditTask = { editingTask = it },
                    onCompleteTask = onCompleteTask,
                    onReopenTask = onReopenTask,
                    onRequestDeleteTask = { taskToDelete = it },
                    context = context
                )

                2 -> ProjectNotesTab(
                    projectName = project.name,
                    notes = notes,
                    onAddNoteClick = { showAddNoteDialog = true },
                    onOpenNote = onOpenNote,
                    onRequestDeleteNote = { noteToDelete = it }
                )

                3 -> ProjectFilesTab(
                    files = files,
                    onPickPhotos = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onPickDocuments = { documentPickerLauncher.launch(arrayOf("*/*")) },
                    onOpenFile = { file ->
                        openProjectFile(
                            file = file,
                            context = context,
                            onPreviewImage = { previewImageFile = it },
                            onPreviewText = { previewTextFile = it }
                        )
                    },
                    onRequestDeleteFile = { fileToDelete = it }
                )

                4 -> ProjectDecisionsTab(
                    decisions = decisions,
                    onAddDecisionClick = {
                        editingDecision = null
                        showAddDecisionDialog = true
                    },
                    onEditDecision = {
                        editingDecision = it
                        showAddDecisionDialog = true
                    },
                    onRequestDeleteDecision = { decisionToDelete = it }
                )
            }
        }
    }

    if (showProgressDialog) {
        ProjectProgressDialog(
            currentMode = project.progressMode,
            currentManualProgress = project.manualProgressPercent,
            autoCalculatedProgress = autoCalculatedProgress,
            onSave = { newMode, newManualProg ->
                val newProg = if (newMode == "AUTOMATIC") autoCalculatedProgress else newManualProg
                onUpdateProject(
                    project.copy(
                        progressMode = newMode,
                        manualProgressPercent = newManualProg,
                        progressPercent = newProg,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                showProgressDialog = false
                Toast.makeText(context, "Progress updated to $newProg% ($newMode)", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showProgressDialog = false }
        )
    }

    if (showEditProjectDialog) {
        ProjectEditFullDialog(
            project = project,
            onSave = { updated ->
                onUpdateProject(updated)
                showEditProjectDialog = false
                Toast.makeText(context, "Project details updated", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showEditProjectDialog = false }
        )
    }

    if (showDeleteConfirmDialog) {
        JlabConfirmDialog(
            title = "Delete Project?",
            message = "Are you sure you want to permanently delete \"${project.name}\"? All project details will be removed.",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                onDeleteProject(project.id)
                showDeleteConfirmDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }

    if (showAddTaskDialog || editingTask != null) {
        ProjectTaskDialog(
            initialTask = editingTask,
            projectId = project.id,
            onSave = { task ->
                if (editingTask != null) onUpdateTask(task) else onAddTask(task)
                showAddTaskDialog = false
                editingTask = null
            },
            onDismiss = {
                showAddTaskDialog = false
                editingTask = null
            }
        )
    }

    taskToDelete?.let { target ->
        JlabConfirmDialog(
            title = "Delete Task?",
            message = "Are you sure you want to delete \"${target.title}\"?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                onDeleteTask(target.id)
                taskToDelete = null
            },
            onDismiss = { taskToDelete = null }
        )
    }

    if (showAddNoteDialog) {
        ProjectNoteCreateDialog(
            projectId = project.id,
            onSave = { note ->
                onCreateNote(note)
                showAddNoteDialog = false
                Toast.makeText(context, "Project note created!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showAddNoteDialog = false }
        )
    }

    noteToDelete?.let { target ->
        JlabConfirmDialog(
            title = "Delete Note?",
            message = "Are you sure you want to delete note \"${target.title}\"?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                onDeleteNote(target.id)
                noteToDelete = null
            },
            onDismiss = { noteToDelete = null }
        )
    }

    fileToDelete?.let { target ->
        JlabConfirmDialog(
            title = "Remove Attachment?",
            message = "Are you sure you want to delete \"${target.name}\" from this project?",
            confirmText = "Remove",
            isDestructive = true,
            onConfirm = {
                onDeleteFile(target)
                fileToDelete = null
            },
            onDismiss = { fileToDelete = null }
        )
    }

    if (showAddMilestoneDialog) {
        var milestoneTitle by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddMilestoneDialog = false },
            shape = AppTheme.shapes.extraLarge,
            containerColor = AppTheme.colorScheme.surface,
            title = { Text("Add Project Milestone", style = AppTheme.typography.titleLarge) },
            text = {
                JlabTextField(
                    value = milestoneTitle,
                    onValueChange = { milestoneTitle = it },
                    label = "Milestone Phase / Goal *",
                    placeholder = "e.g. Beta release launched",
                    singleLine = true
                )
            },
            confirmButton = {
                JlabPrimaryButton(
                    text = "Add Milestone",
                    enabled = milestoneTitle.isNotBlank(),
                    onClick = {
                        if (milestoneTitle.isNotBlank()) {
                            val currentList = parseMilestones(project.milestonesJson).toMutableList()
                            currentList.add(ProjectMilestone(title = milestoneTitle.trim()))
                            onUpdateProject(
                                project.copy(
                                    milestonesJson = serializeMilestones(currentList),
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                            showAddMilestoneDialog = false
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddMilestoneDialog = false },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Cancel", style = AppTheme.typography.labelLarge)
                }
            }
        )
    }

    if (showAddDecisionDialog) {
        DecisionDialog(
            initialDecision = editingDecision,
            projectId = project.id,
            onSave = { saved ->
                onSaveDecision(saved)
                showAddDecisionDialog = false
                editingDecision = null
            },
            onDismiss = {
                showAddDecisionDialog = false
                editingDecision = null
            }
        )
    }

    decisionToDelete?.let { target ->
        JlabConfirmDialog(
            title = "Delete Decision Record?",
            message = "Delete decision record \"${target.title}\"?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                onDeleteDecision(target.id)
                decisionToDelete = null
            },
            onDismiss = { decisionToDelete = null }
        )
    }

    previewImageFile?.let { file ->
        ProjectImagePreviewDialog(file = file, onDismiss = { previewImageFile = null })
    }

    previewTextFile?.let { file ->
        ProjectTextPreviewDialog(file = file, onDismiss = { previewTextFile = null })
    }
}

private fun openProjectFile(
    file: FileAttachmentEntity,
    context: Context,
    onPreviewImage: (FileAttachmentEntity) -> Unit,
    onPreviewText: (FileAttachmentEntity) -> Unit
) {
    val name = file.name.lowercase()
    val mime = file.mimeType.lowercase()

    when {
        mime.startsWith("image") || name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".webp") || name.endsWith(".jpeg") -> {
            onPreviewImage(file)
        }
        mime.startsWith("text") || name.endsWith(".txt") || name.endsWith(".md") || name.endsWith(".json") || name.endsWith(".csv") -> {
            onPreviewText(file)
        }
        else -> {
            val localFile = File(file.localFilePath)
            if (localFile.exists()) {
                try {
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", localFile)
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, mime.ifBlank { "*/*" })
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    context.startActivity(Intent.createChooser(intent, "Open file with"))
                } catch (_: Exception) {
                    Toast.makeText(context, "No app available to open this file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "File does not exist on disk", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private suspend fun importProjectAttachment(
    context: Context,
    uri: Uri,
    projectId: String,
    category: String,
    onSaveFile: (FileAttachmentEntity) -> Unit
) = withContext(Dispatchers.IO) {
    try {
        val vaultDir = File(context.filesDir, "vault")
        vaultDir.mkdirs()

        val time = System.currentTimeMillis()
        val originalName = uri.lastPathSegment?.substringAfterLast('/') ?: "file"
        val fileName = if (originalName.contains(".")) originalName else "file_$time"
        val destFile = File(vaultDir, "proj_${projectId}_${time}_$fileName")

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }

        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val friendlyName = fileName.removePrefix("proj_${projectId}_${time}_")

        val entity = FileAttachmentEntity(
            name = friendlyName,
            localFilePath = destFile.absolutePath,
            mimeType = mimeType,
            sizeBytes = destFile.length(),
            category = category,
            projectId = projectId
        )
        onSaveFile(entity)

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Attached $friendlyName to project!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Failed to attach: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

internal fun parseMilestones(json: String): List<ProjectMilestone> {
    if (json.isBlank() || json == "[]") return emptyList()
    return try {
        val array = JSONArray(json)
        val list = mutableListOf<ProjectMilestone>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                ProjectMilestone(
                    id = obj.optString("id", UUID.randomUUID().toString()),
                    title = obj.optString("title", ""),
                    isCompleted = obj.optBoolean("isCompleted", false),
                    targetDate = if (obj.has("targetDate")) obj.optLong("targetDate") else null
                )
            )
        }
        list
    } catch (_: Exception) {
        emptyList()
    }
}

internal fun serializeMilestones(milestones: List<ProjectMilestone>): String {
    val array = JSONArray()
    for (m in milestones) {
        val obj = JSONObject()
        obj.put("id", m.id)
        obj.put("title", m.title)
        obj.put("isCompleted", m.isCompleted)
        m.targetDate?.let { obj.put("targetDate", it) }
        array.put(obj)
    }
    return array.toString()
}
