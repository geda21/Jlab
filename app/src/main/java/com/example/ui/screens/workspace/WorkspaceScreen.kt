package com.example.ui.screens.workspace

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import com.example.ui.components.EmptyState
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabSearchTopBar
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme

data class WorkspaceModule(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val count: Int? = null
)

@Composable
fun WorkspaceScreen(
    tasksCount: Int = 0,
    projectsCount: Int = 0,
    notesCount: Int = 0,
    plansCount: Int = 0,
    promptsCount: Int = 0,
    learningCount: Int = 0,
    businessCount: Int = 0,
    socialCount: Int = 0,
    filesCount: Int = 0,
    habitsCount: Int = 0,
    goalsCount: Int = 0,
    infoCount: Int = 0,
    knowledgeCount: Int = 0,
    deletedNotesCount: Int = 0,
    favoriteModuleIds: Set<String> = emptySet(),
    recentModuleIds: List<String> = emptyList(),
    workspaceViewMode: String = "LIST",
    onSetViewMode: (String) -> Unit = {},
    onToggleFavorite: (String) -> Unit = {},
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onNavigate: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var coreExpanded by remember { mutableStateOf(true) }
    var knowledgeExpanded by remember { mutableStateOf(true) }
    var planningExpanded by remember { mutableStateOf(true) }
    var filesExpanded by remember { mutableStateOf(true) }
    var systemExpanded by remember { mutableStateOf(true) }

    val isGridView = workspaceViewMode == "GRID"
    val primary = AppTheme.colorScheme.primary
    val extended = AppTheme.extendedColors

    val coreModules = listOf(
        WorkspaceModule("projects", "Projects", "Milestones, tasks, attachments & progress", Icons.Default.Folder, primary, projectsCount.takeIf { it > 0 }),
        WorkspaceModule("tasks", "Tasks", "Priority scheduling, deadlines & checklists", Icons.Default.CheckCircleOutline, extended.warning, tasksCount.takeIf { it > 0 }),
        WorkspaceModule("notes", "Notes", "Rich-text formatting, search & categories", Icons.Default.Description, extended.success, notesCount.takeIf { it > 0 }),
        WorkspaceModule("plans", "Plans & Commitments", "Daily & weekly execution schedules", Icons.AutoMirrored.Filled.EventNote, extended.info, plansCount.takeIf { it > 0 })
    )

    val knowledgeModules = listOf(
        WorkspaceModule("knowledge", "Knowledge Vault", "Durable knowledge & structured takeaways", Icons.AutoMirrored.Filled.MenuBook, extended.success, knowledgeCount.takeIf { it > 0 }),
        WorkspaceModule("learning", "Learning System", "Structured curriculums, lessons & skills", Icons.Default.School, extended.info, learningCount.takeIf { it > 0 }),
        WorkspaceModule("prompts", "Prompt Library", "Reusable prompts, templates & parameters", Icons.Default.AutoAwesome, extended.violet, promptsCount.takeIf { it > 0 })
    )

    val planningModules = listOf(
        WorkspaceModule("business", "Business Workspace", "Enterprises, goals & client pipelines", Icons.Default.BusinessCenter, extended.pink, businessCount.takeIf { it > 0 }),
        WorkspaceModule("social", "Social Content", "Content pipeline from idea to publish", Icons.Default.Share, extended.info, socialCount.takeIf { it > 0 }),
        WorkspaceModule("reviews", "Daily Review", "Daily reflections, scores & milestones", Icons.Default.RateReview, primary),
        WorkspaceModule("goals", "Goals", "Life, quarterly & business objectives", Icons.Default.Flag, extended.danger, goalsCount.takeIf { it > 0 }),
        WorkspaceModule("habits", "Habits & Routines", "Consistency streaks & daily triggers", Icons.Default.Repeat, extended.success, habitsCount.takeIf { it > 0 })
    )

    val filesModules = listOf(
        WorkspaceModule("files", "File & Screenshot Vault", "Imported screenshots, documents & PDFs", Icons.Default.AttachFile, primary, filesCount.takeIf { it > 0 }),
        WorkspaceModule("information", "Information Vault", "Permanent facts, credentials & configurations", Icons.AutoMirrored.Filled.LibraryBooks, extended.info, infoCount.takeIf { it > 0 })
    )

    val systemModules = listOf(
        WorkspaceModule("insights", "Activity & Insights", "Audit history & weekly productivity trends", Icons.Default.Insights, primary),
        WorkspaceModule("calendar", "Calendar", "Integrated schedule & time commitments", Icons.Default.CalendarMonth, extended.warning),
        WorkspaceModule("trash", "Trash & Recovery", "Restore or permanently remove records", Icons.Default.DeleteOutline, extended.danger, deletedNotesCount.takeIf { it > 0 }),
        WorkspaceModule("settings", "Settings", "Theme preferences, security & data backup", Icons.Default.Settings, extended.neutral)
    )

    val allModulesMap = remember(coreModules, knowledgeModules, planningModules, filesModules, systemModules) {
        (coreModules + knowledgeModules + planningModules + filesModules + systemModules).associateBy { it.id }
    }

    val favoriteModules = remember(favoriteModuleIds, allModulesMap) {
        favoriteModuleIds.mapNotNull { allModulesMap[it] }
    }

    val recentModules = remember(recentModuleIds, allModulesMap) {
        recentModuleIds.mapNotNull { allModulesMap[it] }.take(4)
    }

    val query = searchQuery.trim().lowercase()
    val matchesSearch: (WorkspaceModule) -> Boolean = { mod ->
        if (query.isBlank()) true
        else mod.title.lowercase().contains(query) || mod.subtitle.lowercase().contains(query)
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            if (isSearchActive) {
                JlabSearchTopBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search modules...",
                    onClose = {
                        isSearchActive = false
                        searchQuery = ""
                    },
                    testTag = "workspace_search_bar"
                )
            } else {
                JlabTopBar(
                    title = "Workspace",
                    subtitle = "Personal productivity & system modules",
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    testTag = "workspace_top_bar",
                    actions = {
                        JlabIconButton(
                            icon = Icons.Default.Search,
                            contentDescription = "Search modules",
                            onClick = { isSearchActive = true },
                            testTag = "workspace_search_button"
                        )
                        JlabIconButton(
                            icon = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridView) "Switch to list view" else "Switch to grid view",
                            onClick = { onSetViewMode(if (isGridView) "LIST" else "GRID") },
                            testTag = "workspace_view_mode_toggle"
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
                .testTag("workspace_module_list"),
            contentPadding = PaddingValues(
                start = AppTheme.spacing.screenHorizontal,
                end = AppTheme.spacing.screenHorizontal,
                top = AppTheme.spacing.screenVertical,
                bottom = AppTheme.spacing.listBottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
        ) {
            if (query.isNotBlank()) {
                val searchResults = allModulesMap.values.filter(matchesSearch)
                item {
                    Text(
                        text = "SEARCH RESULTS (${searchResults.size})",
                        style = AppTheme.extendedTypography.overline,
                        color = AppTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = AppTheme.spacing.xs)
                    )
                }

                if (searchResults.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.Search,
                            title = "No matching modules",
                            message = "Nothing matched \"$searchQuery\". Try another keyword."
                        )
                    }
                } else {
                    items(searchResults, key = { "search_${it.id}" }) { mod ->
                        WorkspaceModuleRow(
                            module = mod,
                            isFavorite = mod.id in favoriteModuleIds,
                            onToggleFavorite = { onToggleFavorite(mod.id) },
                            onClick = { onNavigate(mod.id) }
                        )
                    }
                }
            } else {
                if (favoriteModules.isNotEmpty()) {
                    item {
                        WorkspaceGroupHeader(
                            title = "FAVORITES (${favoriteModules.size})",
                            icon = Icons.Default.Star,
                            isCollapsible = false
                        )
                    }
                    if (isGridView) {
                        item {
                            ModuleGridRow(
                                modules = favoriteModules,
                                favoriteModuleIds = favoriteModuleIds,
                                onToggleFavorite = onToggleFavorite,
                                onNavigate = onNavigate
                            )
                        }
                    } else {
                        items(favoriteModules, key = { "fav_${it.id}" }) { mod ->
                            WorkspaceModuleRow(
                                module = mod,
                                isFavorite = true,
                                onToggleFavorite = { onToggleFavorite(mod.id) },
                                onClick = { onNavigate(mod.id) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(AppTheme.spacing.xs)) }
                }

                if (recentModules.isNotEmpty()) {
                    item {
                        WorkspaceGroupHeader(
                            title = "RECENTLY ACCESSED",
                            icon = Icons.Default.History,
                            isCollapsible = false
                        )
                    }
                    if (isGridView) {
                        item {
                            ModuleGridRow(
                                modules = recentModules,
                                favoriteModuleIds = favoriteModuleIds,
                                onToggleFavorite = onToggleFavorite,
                                onNavigate = onNavigate
                            )
                        }
                    } else {
                        items(recentModules, key = { "recent_${it.id}" }) { mod ->
                            WorkspaceModuleRow(
                                module = mod,
                                isFavorite = mod.id in favoriteModuleIds,
                                onToggleFavorite = { onToggleFavorite(mod.id) },
                                onClick = { onNavigate(mod.id) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(AppTheme.spacing.xs)) }
                }

                item {
                    WorkspaceGroupHeader(
                        title = "CORE WORKSPACE",
                        icon = Icons.Default.Dashboard,
                        isExpanded = coreExpanded,
                        onToggleExpand = { coreExpanded = !coreExpanded }
                    )
                }
                if (coreExpanded) {
                    if (isGridView) {
                        item {
                            ModuleGridRow(
                                modules = coreModules,
                                favoriteModuleIds = favoriteModuleIds,
                                onToggleFavorite = onToggleFavorite,
                                onNavigate = onNavigate
                            )
                        }
                    } else {
                        items(coreModules, key = { it.id }) { mod ->
                            WorkspaceModuleRow(
                                module = mod,
                                isFavorite = mod.id in favoriteModuleIds,
                                onToggleFavorite = { onToggleFavorite(mod.id) },
                                onClick = { onNavigate(mod.id) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(AppTheme.spacing.xs)) }
                }

                item {
                    WorkspaceGroupHeader(
                        title = "KNOWLEDGE & LEARNING",
                        icon = Icons.Default.School,
                        isExpanded = knowledgeExpanded,
                        onToggleExpand = { knowledgeExpanded = !knowledgeExpanded }
                    )
                }
                if (knowledgeExpanded) {
                    if (isGridView) {
                        item {
                            ModuleGridRow(
                                modules = knowledgeModules,
                                favoriteModuleIds = favoriteModuleIds,
                                onToggleFavorite = onToggleFavorite,
                                onNavigate = onNavigate
                            )
                        }
                    } else {
                        items(knowledgeModules, key = { it.id }) { mod ->
                            WorkspaceModuleRow(
                                module = mod,
                                isFavorite = mod.id in favoriteModuleIds,
                                onToggleFavorite = { onToggleFavorite(mod.id) },
                                onClick = { onNavigate(mod.id) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(AppTheme.spacing.xs)) }
                }

                item {
                    WorkspaceGroupHeader(
                        title = "PLANNING & MANAGEMENT",
                        icon = Icons.AutoMirrored.Filled.EventNote,
                        isExpanded = planningExpanded,
                        onToggleExpand = { planningExpanded = !planningExpanded }
                    )
                }
                if (planningExpanded) {
                    if (isGridView) {
                        item {
                            ModuleGridRow(
                                modules = planningModules,
                                favoriteModuleIds = favoriteModuleIds,
                                onToggleFavorite = onToggleFavorite,
                                onNavigate = onNavigate
                            )
                        }
                    } else {
                        items(planningModules, key = { it.id }) { mod ->
                            WorkspaceModuleRow(
                                module = mod,
                                isFavorite = mod.id in favoriteModuleIds,
                                onToggleFavorite = { onToggleFavorite(mod.id) },
                                onClick = { onNavigate(mod.id) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(AppTheme.spacing.xs)) }
                }

                item {
                    WorkspaceGroupHeader(
                        title = "FILES & RESOURCES",
                        icon = Icons.Default.AttachFile,
                        isExpanded = filesExpanded,
                        onToggleExpand = { filesExpanded = !filesExpanded }
                    )
                }
                if (filesExpanded) {
                    if (isGridView) {
                        item {
                            ModuleGridRow(
                                modules = filesModules,
                                favoriteModuleIds = favoriteModuleIds,
                                onToggleFavorite = onToggleFavorite,
                                onNavigate = onNavigate
                            )
                        }
                    } else {
                        items(filesModules, key = { it.id }) { mod ->
                            WorkspaceModuleRow(
                                module = mod,
                                isFavorite = mod.id in favoriteModuleIds,
                                onToggleFavorite = { onToggleFavorite(mod.id) },
                                onClick = { onNavigate(mod.id) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(AppTheme.spacing.xs)) }
                }

                item {
                    WorkspaceGroupHeader(
                        title = "SYSTEM & UTILITIES",
                        icon = Icons.Default.Settings,
                        isExpanded = systemExpanded,
                        onToggleExpand = { systemExpanded = !systemExpanded }
                    )
                }
                if (systemExpanded) {
                    if (isGridView) {
                        item {
                            ModuleGridRow(
                                modules = systemModules,
                                favoriteModuleIds = favoriteModuleIds,
                                onToggleFavorite = onToggleFavorite,
                                onNavigate = onNavigate
                            )
                        }
                    } else {
                        items(systemModules, key = { it.id }) { mod ->
                            WorkspaceModuleRow(
                                module = mod,
                                isFavorite = mod.id in favoriteModuleIds,
                                onToggleFavorite = { onToggleFavorite(mod.id) },
                                onClick = { onNavigate(mod.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkspaceGroupHeader(
    title: String,
    icon: ImageVector,
    isCollapsible: Boolean = true,
    isExpanded: Boolean = true,
    onToggleExpand: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isCollapsible) {
                    Modifier
                        .minimumInteractiveComponentSize()
                        .clip(AppTheme.extendedShapes.chip)
                        .clickable(onClick = onToggleExpand)
                } else {
                    Modifier
                }
            )
            .padding(vertical = AppTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppTheme.colorScheme.primary,
                modifier = Modifier.size(AppTheme.spacing.iconSm)
            )
            Spacer(modifier = Modifier.width(AppTheme.spacing.compactGap))
            Text(
                text = title,
                style = AppTheme.extendedTypography.overline,
                color = AppTheme.colorScheme.primary
            )
        }

        if (isCollapsible) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse section" else "Expand section",
                tint = AppTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(AppTheme.spacing.iconMd)
            )
        }
    }
}

@Composable
private fun WorkspaceModuleRow(
    module: WorkspaceModule,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    JlabCard(
        shape = AppTheme.extendedShapes.compactCard,
        contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding),
        onClick = onClick,
        testTag = "workspace_module_${module.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.spacing.avatarMd)
                    .clip(CircleShape)
                    .background(module.accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = null,
                    tint = module.accentColor,
                    modifier = Modifier.size(AppTheme.spacing.iconMd)
                )
            }

            Spacer(modifier = Modifier.width(AppTheme.spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.title,
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colorScheme.onSurface
                )
                Text(
                    text = module.subtitle,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (module.count != null && module.count > 0) {
                Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                JlabTagBadge(
                    text = "${module.count}",
                    containerColor = module.accentColor.copy(alpha = 0.14f),
                    contentColor = module.accentColor
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("workspace_favorite_${module.id}")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorite) "Remove ${module.title} from favorites" else "Add ${module.title} to favorites",
                    tint = if (isFavorite) {
                        AppTheme.extendedColors.warning
                    } else {
                        AppTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                    },
                    modifier = Modifier.size(AppTheme.spacing.iconMd)
                )
            }
        }
    }
}

@Composable
private fun ModuleGridRow(
    modules: List<WorkspaceModule>,
    favoriteModuleIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        modules.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                pair.forEach { mod ->
                    ModuleGridCard(
                        module = mod,
                        isFavorite = mod.id in favoriteModuleIds,
                        onToggleFavorite = { onToggleFavorite(mod.id) },
                        onClick = { onNavigate(mod.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ModuleGridCard(
    module: WorkspaceModule,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    JlabCard(
        modifier = modifier,
        contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding),
        onClick = onClick,
        testTag = "workspace_grid_module_${module.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.spacing.avatarSm)
                    .clip(CircleShape)
                    .background(module.accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = null,
                    tint = module.accentColor,
                    modifier = Modifier.size(AppTheme.spacing.iconSm)
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("workspace_grid_favorite_${module.id}")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorite) "Remove ${module.title} from favorites" else "Add ${module.title} to favorites",
                    tint = if (isFavorite) {
                        AppTheme.extendedColors.warning
                    } else {
                        AppTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    },
                    modifier = Modifier.size(AppTheme.spacing.iconSm)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))

        Text(
            text = module.title,
            style = AppTheme.typography.titleSmall,
            color = AppTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = module.subtitle,
            style = AppTheme.extendedTypography.caption,
            color = AppTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (module.count != null && module.count > 0) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.compactGap))
            JlabTagBadge(
                text = "${module.count}",
                containerColor = module.accentColor.copy(alpha = 0.14f),
                contentColor = module.accentColor
            )
        }
    }
}
