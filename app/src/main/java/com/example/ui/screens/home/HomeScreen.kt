package com.example.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabLinearProgress
import com.example.ui.components.JlabSectionCard
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTonalButton
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.UserAvatar
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    userName: String = "Geda",
    userAvatarUri: String = "",
    homeBannerUri: String = "",
    homeBannerPosition: String = "COVER",
    todayTasks: List<TaskEntity>,
    completedTasksCount: Int,
    pendingTasksCount: Int = 0,
    activeProjects: List<ProjectEntity>,
    recentNotes: List<NoteEntity>,
    allPlansCount: Int = 0,
    allNotesCount: Int = 0,
    recentActivities: List<ActivityEntity> = emptyList(),
    allTasks: List<TaskEntity> = emptyList(),
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onQuickAdd: () -> Unit = {},
    onCompleteTask: (String, String) -> Unit,
    onReopenTask: (String, String) -> Unit = { _, _ -> },
    onSnoozeTask: (String) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToPlans: () -> Unit = {},
    onNavigateToFiles: () -> Unit = {},
    onNavigateToSearch: () -> Unit,
    onNavigateToProjectDetail: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val timeGreeting = when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..22 -> "Good Evening"
        else -> "Good Night"
    }
    val greetingText = "$timeGreeting, $userName"

    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }
    val currentDateStr = remember { dateFormat.format(calendar.time) }
    val pendingTodayTasks = remember(todayTasks) { todayTasks.filter { it.status != "COMPLETED" } }
    val extended = AppTheme.extendedColors

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.background)
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.screenHorizontal,
            end = AppTheme.spacing.screenHorizontal,
            top = AppTheme.spacing.screenVertical,
            bottom = AppTheme.spacing.listBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap)
    ) {
        // 1. Personal Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppTheme.spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = greetingText,
                        style = AppTheme.typography.headlineSmall,
                        color = AppTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                    Text(
                        text = currentDateStr,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
                ) {
                    IconButton(
                        onClick = onNavigateToSearch,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("home_search_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(AppTheme.spacing.avatarSm)
                                .clip(CircleShape)
                                .background(AppTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Global search",
                                tint = AppTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(AppTheme.spacing.iconSm)
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("home_theme_toggle")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(AppTheme.spacing.avatarSm)
                                .clip(CircleShape)
                                .background(AppTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isDarkTheme) "Switch to light mode" else "Switch to dark mode",
                                tint = if (isDarkTheme) extended.warning else AppTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(AppTheme.spacing.iconSm)
                            )
                        }
                    }

                    UserAvatar(
                        avatarUri = userAvatarUri,
                        name = userName,
                        size = AppTheme.spacing.avatarMd,
                        testTag = "home_profile_avatar",
                        onClick = onOpenProfile
                    )
                }
            }
        }

        // 1B. Custom Homepage Graphic / Banner
        if (homeBannerUri.isNotBlank()) {
            item {
                Card(
                    shape = AppTheme.extendedShapes.heroCard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp)
                        .clip(AppTheme.extendedShapes.heroCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.spacing.none)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = homeBannerUri,
                            contentDescription = "Personalized workspace banner",
                            contentScale = when (homeBannerPosition.uppercase()) {
                                "CONTAIN" -> ContentScale.Fit
                                else -> ContentScale.Crop
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, extended.mediaScrim)
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(AppTheme.spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = extended.onMediaOverlay.copy(alpha = 0.9f),
                                modifier = Modifier.size(AppTheme.spacing.iconSm)
                            )
                            Spacer(modifier = Modifier.width(AppTheme.spacing.compactGap))
                            Text(
                                text = "Personalized Command Workspace",
                                style = AppTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = extended.onMediaOverlay
                            )
                        }
                    }
                }
            }
        }

        // 2. Workspace Snapshot
        item {
            JlabSectionCard(
                overline = "Workspace Snapshot",
                testTag = "home_snapshot_card"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    MetricPill(
                        label = "Tasks Due",
                        value = "${pendingTodayTasks.size}",
                        color = extended.warning,
                        icon = Icons.Outlined.CheckCircle,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToTasks
                    )
                    MetricPill(
                        label = "Projects",
                        value = "${activeProjects.size}",
                        color = AppTheme.colorScheme.primary,
                        icon = Icons.Outlined.Folder,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToProjects
                    )
                    MetricPill(
                        label = "Plans",
                        value = "$allPlansCount",
                        color = extended.info,
                        icon = Icons.AutoMirrored.Outlined.EventNote,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToPlans
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    MetricPill(
                        label = "Completed",
                        value = "$completedTasksCount",
                        color = extended.success,
                        icon = Icons.Default.TaskAlt,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToTasks
                    )
                    MetricPill(
                        label = "Notes",
                        value = "${if (allNotesCount > 0) allNotesCount else recentNotes.size}",
                        color = extended.violet,
                        icon = Icons.Outlined.Description,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToNotes
                    )
                }
            }
        }

        // 3. Smart Quick Actions
        item {
            JlabSectionCard(
                overline = "Quick Capture",
                testTag = "home_quick_actions_card"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionItem(
                        label = "New Task",
                        icon = Icons.Default.AddTask,
                        color = extended.warning,
                        onClick = onNavigateToTasks
                    )
                    QuickActionItem(
                        label = "Project",
                        icon = Icons.Default.CreateNewFolder,
                        color = AppTheme.colorScheme.primary,
                        onClick = onNavigateToProjects
                    )
                    QuickActionItem(
                        label = "New Note",
                        icon = Icons.AutoMirrored.Filled.NoteAdd,
                        color = extended.success,
                        onClick = onNavigateToNotes
                    )
                    QuickActionItem(
                        label = "New Plan",
                        icon = Icons.Default.EventAvailable,
                        color = extended.info,
                        onClick = onNavigateToPlans
                    )
                    QuickActionItem(
                        label = "Add File",
                        icon = Icons.Default.UploadFile,
                        color = extended.violet,
                        onClick = onNavigateToFiles
                    )
                }
            }
        }

        // 4. Today's Focus Section
        item {
            SectionHeader(
                title = "Today's Focus",
                count = pendingTodayTasks.size,
                actionText = "All Tasks",
                onActionClick = onNavigateToTasks,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.none)
            )
        }

        if (pendingTodayTasks.isEmpty()) {
            item {
                JlabCard(testTag = "home_today_empty_card") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(AppTheme.spacing.avatarSm)
                                    .clip(CircleShape)
                                    .background(extended.successContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = extended.onSuccessContainer,
                                    modifier = Modifier.size(AppTheme.spacing.iconSm)
                                )
                            }
                            Spacer(modifier = Modifier.width(AppTheme.spacing.md))
                            Column {
                                Text(
                                    text = "All clear for today",
                                    style = AppTheme.typography.titleSmall,
                                    color = AppTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "No pending tasks scheduled right now.",
                                    style = AppTheme.typography.bodySmall,
                                    color = AppTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(AppTheme.spacing.sm))

                        JlabTonalButton(
                            text = "Add Task",
                            icon = Icons.Default.Add,
                            onClick = onNavigateToTasks,
                            testTag = "home_add_task_cta"
                        )
                    }
                }
            }
        } else {
            items(pendingTodayTasks.take(4), key = { it.id }) { task ->
                val project = remember(task.projectId, activeProjects) {
                    activeProjects.find { it.id == task.projectId }
                }
                HomeTaskRow(
                    task = task,
                    projectName = project?.name,
                    onComplete = { onCompleteTask(task.id, task.title) }
                )
            }
        }

        // 5. Continue Working (Projects)
        if (activeProjects.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Continue Working",
                    actionText = "View All",
                    onActionClick = onNavigateToProjects,
                    modifier = Modifier.padding(horizontal = AppTheme.spacing.none)
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(activeProjects.take(5), key = { "proj_${it.id}" }) { project ->
                        ProjectCommandCard(
                            project = project,
                            onClick = { onNavigateToProjectDetail(project.id) }
                        )
                    }
                }
            }
        }

        // 6. Recent Local Activity
        if (recentActivities.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Recent Local Activity",
                    modifier = Modifier.padding(horizontal = AppTheme.spacing.none)
                )
            }

            items(recentActivities.take(5), key = { it.id }) { act ->
                val (badgeBg, badgeTint, badgeIcon) = when (act.actionType) {
                    "COMPLETED" -> Triple(extended.successContainer, extended.onSuccessContainer, Icons.Default.Check)
                    "CREATED" -> Triple(extended.infoContainer, extended.onInfoContainer, Icons.Default.Add)
                    "REOPENED" -> Triple(extended.warningContainer, extended.onWarningContainer, Icons.Default.Refresh)
                    else -> Triple(extended.neutralContainer, extended.onNeutralContainer, Icons.Default.Info)
                }

                JlabCard(
                    shape = AppTheme.extendedShapes.compactCard,
                    contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(AppTheme.spacing.avatarSm)
                                .clip(CircleShape)
                                .background(badgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = badgeIcon,
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.spacing.iconSm),
                                tint = badgeTint
                            )
                        }

                        Spacer(modifier = Modifier.width(AppTheme.spacing.smPlus))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = act.title,
                                style = AppTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (act.summary.isNotBlank()) {
                                Text(
                                    text = act.summary,
                                    style = AppTheme.typography.labelSmall,
                                    color = AppTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        val timeAgo = remember(act.timestamp) { formatTimeAgo(act.timestamp) }
                        Text(
                            text = timeAgo,
                            style = AppTheme.extendedTypography.caption,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = AppTheme.extendedShapes.button,
        color = AppTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(AppTheme.extendedShapes.button)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.smPlus,
                vertical = AppTheme.spacing.sm
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(AppTheme.spacing.iconSm)
            )
            Spacer(modifier = Modifier.width(AppTheme.spacing.compactGap))
            Column {
                Text(
                    text = value,
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = label,
                    style = AppTheme.extendedTypography.caption,
                    color = AppTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(AppTheme.extendedShapes.chip)
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.compactGap, vertical = AppTheme.spacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(AppTheme.spacing.avatarMd)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(AppTheme.spacing.iconMd)
            )
        }
        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
        Text(
            text = label,
            style = AppTheme.extendedTypography.caption,
            color = AppTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun HomeTaskRow(
    task: TaskEntity,
    projectName: String?,
    onComplete: () -> Unit
) {
    JlabCard(
        shape = AppTheme.extendedShapes.compactCard,
        contentPadding = PaddingValues(
            horizontal = AppTheme.spacing.compactCardPadding,
            vertical = AppTheme.spacing.sm
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onComplete,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("home_complete_task_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Mark ${task.title} complete",
                    tint = AppTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(AppTheme.spacing.xs))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = AppTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colorScheme.onSurface
                )
                if (!projectName.isNullOrBlank()) {
                    Text(
                        text = "Project: $projectName",
                        style = AppTheme.extendedTypography.caption,
                        color = AppTheme.colorScheme.primary
                    )
                }
            }

            PriorityBadge(priority = task.priority)
        }
    }
}

@Composable
private fun ProjectCommandCard(
    project: ProjectEntity,
    onClick: () -> Unit
) {
    val prog = project.progressPercent.coerceIn(0, 100)

    JlabCard(
        modifier = Modifier.width(228.dp),
        contentPadding = PaddingValues(AppTheme.spacing.none),
        onClick = onClick,
        testTag = "home_project_card_${project.id}"
    ) {
        if (project.coverUri.isNotBlank()) {
            AsyncImage(
                model = project.coverUri,
                contentDescription = "${project.name} cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
            )
        }

        Column(modifier = Modifier.padding(AppTheme.spacing.compactCardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                JlabTagBadge(
                    text = project.category,
                    containerColor = AppTheme.colorScheme.primaryContainer,
                    contentColor = AppTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "$prog%",
                    style = AppTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (prog >= 100) {
                        AppTheme.extendedColors.success
                    } else {
                        AppTheme.colorScheme.primary
                    }
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

            Text(
                text = project.name,
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = project.objective.ifBlank { project.description.ifBlank { "Active project" } },
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.smPlus))

            JlabLinearProgress(progress = prog / 100f)
        }
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val mins = diff / (60 * 1000)
    return when {
        mins < 1 -> "Just now"
        mins < 60 -> "${mins}m ago"
        mins < 1440 -> "${mins / 60}h ago"
        else -> "${mins / 1440}d ago"
    }
}
