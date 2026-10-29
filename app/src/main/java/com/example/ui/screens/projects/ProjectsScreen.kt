package com.example.ui.screens.projects

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.local.entity.ProjectEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabConfirmDialog
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabLinearProgress
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSearchBar
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTopBar
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AppTheme
import com.example.util.FileStorageHelper

@Composable
fun ProjectsScreen(
    projects: List<ProjectEntity>,
    onSaveProject: (ProjectEntity) -> Unit,
    onDeleteProject: (String) -> Unit,
    onSelectProject: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<ProjectEntity?>(null) }

    val filteredProjects = remember(projects, searchQuery, selectedFilter) {
        projects.filter { proj ->
            val matchesSearch = searchQuery.isBlank() ||
                proj.name.contains(searchQuery, ignoreCase = true) ||
                proj.objective.contains(searchQuery, ignoreCase = true) ||
                proj.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "ACTIVE" -> proj.status == "ACTIVE"
                "PLANNING" -> proj.status == "PLANNING"
                "COMPLETED" -> proj.status == "COMPLETED"
                "PAUSED" -> proj.status == "PAUSED"
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Projects",
                subtitle = "${projects.size} total projects",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
                testTag = "projects_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "New project",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { showCreateDialog = true },
                        testTag = "projects_add_button"
                    )
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
            JlabSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search projects by name, goal, category…",
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.compactGap
                ),
                testTag = "projects_search_bar"
            )

            JlabFilterChipRow(
                options = listOf("ALL", "ACTIVE", "PLANNING", "COMPLETED", "PAUSED"),
                selectedOption = selectedFilter,
                onSelect = { selectedFilter = it },
                modifier = Modifier.padding(vertical = AppTheme.spacing.xs)
            )

            if (filteredProjects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.FolderOpen,
                        title = "No projects found",
                        message = "Create high-impact projects with custom covers, milestones, and objectives.",
                        actionButtonText = "New Project",
                        onActionClick = { showCreateDialog = true }
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = AppTheme.spacing.screenHorizontal,
                        end = AppTheme.spacing.screenHorizontal,
                        top = AppTheme.spacing.smPlus,
                        bottom = AppTheme.spacing.listBottomPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.mdPlus),
                    modifier = Modifier.testTag("projects_list")
                ) {
                    items(filteredProjects, key = { it.id }) { project ->
                        ProjectCard(
                            project = project,
                            onClick = { onSelectProject(project.id) },
                            onDelete = { projectToDelete = project }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        ProjectCreateDialog(
            onDismiss = { showCreateDialog = false },
            onSave = {
                onSaveProject(it)
                showCreateDialog = false
            }
        )
    }

    projectToDelete?.let { target ->
        JlabConfirmDialog(
            title = "Delete Project?",
            message = "Are you sure you want to permanently delete \"${target.name}\"?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                onDeleteProject(target.id)
                projectToDelete = null
            },
            onDismiss = { projectToDelete = null },
            testTag = "delete_project_dialog"
        )
    }
}

@Composable
fun ProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val prog = project.progressPercent.coerceIn(0, 100)

    JlabCard(
        contentPadding = PaddingValues(AppTheme.spacing.none),
        onClick = onClick,
        testTag = "project_card_${project.id}"
    ) {
        if (project.coverUri.isNotBlank()) {
            AsyncImage(
                model = project.coverUri,
                contentDescription = "${project.name} cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            )
        }

        Column(modifier = Modifier.padding(AppTheme.spacing.cardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    StatusBadge(status = project.status)
                    JlabTagBadge(
                        text = project.category,
                        containerColor = AppTheme.colorScheme.primaryContainer,
                        contentColor = AppTheme.colorScheme.onPrimaryContainer
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriorityBadge(priority = project.priority)
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete ${project.name}",
                            tint = AppTheme.extendedColors.danger,
                            modifier = Modifier.size(AppTheme.spacing.iconMd)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

            Text(
                text = project.name,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )

            if (project.objective.isNotBlank()) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                Text(
                    text = "Goal: ${project.objective}",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$prog%",
                    style = AppTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (prog >= 100) {
                        AppTheme.extendedColors.success
                    } else {
                        AppTheme.colorScheme.primary
                    }
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.compactGap))

            JlabLinearProgress(progress = prog / 100f)
        }
    }
}

@Composable
fun ProjectCreateDialog(
    onDismiss: () -> Unit,
    onSave: (ProjectEntity) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var objective by remember { mutableStateOf("") }
    val description by remember { mutableStateOf("") }
    val priority by remember { mutableStateOf("HIGH") }
    var category by remember { mutableStateOf("Core") }
    var coverUri by remember { mutableStateOf("") }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val saved = FileStorageHelper.copyUriToInternalStorage(context, uri, "project_covers")
            coverUri = saved?.absolutePath ?: uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "New Project",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)
            ) {
                Surface(
                    shape = AppTheme.extendedShapes.compactCard,
                    color = AppTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp)
                        .clip(AppTheme.extendedShapes.compactCard)
                        .clickable {
                            coverPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ) {
                    if (coverUri.isNotBlank()) {
                        AsyncImage(
                            model = coverUri,
                            contentDescription = "Cover preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                            Text(
                                text = "Add Custom Cover Image",
                                style = AppTheme.typography.labelMedium,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                JlabTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Project Name *",
                    singleLine = true,
                    testTag = "project_create_name_input"
                )

                JlabTextField(
                    value = objective,
                    onValueChange = { objective = it },
                    label = "Core Objective / Goal",
                    placeholder = "What defines success for this project?",
                    singleLine = false,
                    minLines = 2
                )

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
                text = "Create Project",
                enabled = name.isNotBlank(),
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            ProjectEntity(
                                name = name.trim(),
                                objective = objective.trim(),
                                description = description.trim(),
                                priority = priority,
                                category = category.trim(),
                                coverUri = coverUri,
                                status = "ACTIVE"
                            )
                        )
                    }
                },
                testTag = "project_create_submit_button"
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
