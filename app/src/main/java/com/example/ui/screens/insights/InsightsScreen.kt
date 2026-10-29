package com.example.ui.screens.insights

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun InsightsScreen(
    completedTasksCount: Int,
    totalTasksCount: Int,
    pendingTasksCount: Int = 0,
    activeProjectsCount: Int,
    activities: List<ActivityEntity>,
    completedTasks: List<TaskEntity> = emptyList(),
    allTasks: List<TaskEntity> = emptyList(),
    activeProjects: List<ProjectEntity> = emptyList(),
    onNavigateBack: (() -> Unit)? = null
) {
    if (onNavigateBack != null) {
        BackHandler { onNavigateBack() }
    }

    val completionRate = if (totalTasksCount > 0) {
        ((completedTasksCount.toFloat() / totalTasksCount.toFloat()) * 100).toInt()
    } else 0

    val trendData = remember(completedTasks) {
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dayKeyFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        val days = mutableListOf<Pair<String, Int>>()
        for (i in 6 downTo 0) {
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
            }
            val dayKey = dayKeyFormat.format(cal.time)
            val dayLabel = dayFormat.format(cal.time)

            val count = completedTasks.count { task ->
                val taskCal = Calendar.getInstance().apply {
                    timeInMillis = task.completedAt ?: task.updatedAt
                }
                dayKeyFormat.format(taskCal.time) == dayKey
            }
            days.add(Pair(dayLabel, count))
        }
        days
    }

    val overdueCount = remember(allTasks) {
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        allTasks.count {
            it.status != "COMPLETED" && it.status != "CANCELLED" &&
                it.dueDate != null && it.dueDate < startOfToday
        }
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Insights & Analytics",
                subtitle = "Your productivity overview",
                onNavigationClick = onNavigateBack,
                testTag = "insights_top_bar"
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
                top = AppTheme.spacing.md,
                bottom = AppTheme.spacing.listBottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap)
        ) {
            // 1. KPI Stats Grid (Row 1)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
                ) {
                    StatCard(
                        title = "Tasks Done",
                        value = "$completedTasksCount",
                        icon = Icons.Default.CheckCircle,
                        accentColor = AppTheme.extendedColors.success,
                        modifier = Modifier.weight(1f),
                        testTag = "insights_stat_tasks_done"
                    )
                    StatCard(
                        title = "Pending Tasks",
                        value = "$pendingTasksCount",
                        icon = Icons.Default.PendingActions,
                        accentColor = AppTheme.extendedColors.warning,
                        modifier = Modifier.weight(1f),
                        testTag = "insights_stat_pending_tasks"
                    )
                }
            }

            // 1. KPI Stats Grid (Row 2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
                ) {
                    StatCard(
                        title = "Completion Rate",
                        value = "$completionRate%",
                        icon = Icons.Default.DonutLarge,
                        accentColor = AppTheme.extendedColors.info,
                        modifier = Modifier.weight(1f),
                        testTag = "insights_stat_completion_rate"
                    )
                    StatCard(
                        title = "Active Projects",
                        value = "$activeProjectsCount",
                        icon = Icons.Default.Folder,
                        accentColor = AppTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        testTag = "insights_stat_active_projects"
                    )
                }
            }

            // 2. Chart 1: 7-Day Completion Trend
            item {
                JlabSectionCard(
                    title = "Task Completion Trend",
                    headerAction = {
                        Text(
                            text = "Last 7 days",
                            style = AppTheme.extendedTypography.caption,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    testTag = "insights_trend_card"
                ) {
                    Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                    val maxVal = (trendData.maxOfOrNull { it.second } ?: 1).coerceAtLeast(4)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        trendData.forEach { (dayLabel, count) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (count > 0) {
                                    Text(
                                        text = "$count",
                                        style = AppTheme.extendedTypography.badge,
                                        color = AppTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                                }

                                val barHeightRatio = (count.toFloat() / maxVal.toFloat()).coerceIn(0.08f, 1f)
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .fillMaxHeight(barHeightRatio)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (count > 0) AppTheme.colorScheme.primary
                                            else AppTheme.colorScheme.surfaceContainerHighest
                                        )
                                )

                                Spacer(modifier = Modifier.height(AppTheme.spacing.xs))

                                Text(
                                    text = dayLabel,
                                    style = AppTheme.extendedTypography.caption,
                                    color = AppTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 3. Chart 2: Task Status Donut Chart
            item {
                JlabSectionCard(
                    title = "Task Status Distribution",
                    testTag = "insights_donut_card"
                ) {
                    val completedColor = AppTheme.extendedColors.success
                    val pendingColor = AppTheme.extendedColors.warning
                    val overdueColor = AppTheme.colorScheme.error
                    val emptyColor = AppTheme.colorScheme.surfaceContainerHighest

                    val total = (completedTasksCount + pendingTasksCount).coerceAtLeast(1)
                    val completedSweep = (completedTasksCount.toFloat() / total.toFloat()) * 360f
                    val pendingSweep = (pendingTasksCount.toFloat() / total.toFloat()) * 360f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(116.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 18.dp.toPx()
                                val radius = (size.minDimension - strokeWidth) / 2
                                val center = Offset(size.width / 2, size.height / 2)

                                if (completedTasksCount == 0 && pendingTasksCount == 0) {
                                    drawCircle(
                                        color = emptyColor,
                                        radius = radius,
                                        center = center,
                                        style = Stroke(width = strokeWidth)
                                    )
                                } else {
                                    var currentStartAngle = -90f
                                    if (completedSweep > 0) {
                                        drawArc(
                                            color = completedColor,
                                            startAngle = currentStartAngle,
                                            sweepAngle = completedSweep,
                                            useCenter = false,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )
                                        currentStartAngle += completedSweep
                                    }
                                    if (pendingSweep > 0) {
                                        drawArc(
                                            color = pendingColor,
                                            startAngle = currentStartAngle,
                                            sweepAngle = pendingSweep,
                                            useCenter = false,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${completedTasksCount + pendingTasksCount}",
                                    style = AppTheme.typography.titleLarge,
                                    color = AppTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tasks",
                                    style = AppTheme.extendedTypography.caption,
                                    color = AppTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                            LegendItem(color = completedColor, label = "Completed", value = "$completedTasksCount")
                            LegendItem(color = pendingColor, label = "Pending", value = "$pendingTasksCount")
                            if (overdueCount > 0) {
                                LegendItem(color = overdueColor, label = "Overdue", value = "$overdueCount")
                            }
                        }
                    }
                }
            }

            // 4. Chart 3: Active Projects Progress
            if (activeProjects.isNotEmpty()) {
                item {
                    JlabSectionCard(
                        title = "Project Execution Progress",
                        testTag = "insights_projects_progress_card"
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)) {
                            activeProjects.take(4).forEach { proj ->
                                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = proj.name,
                                            style = AppTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
                                        Text(
                                            text = "${proj.progressPercent}%",
                                            style = AppTheme.typography.labelMedium,
                                            color = AppTheme.colorScheme.primary
                                        )
                                    }
                                    JlabLinearProgress(
                                        progress = proj.progressPercent / 100f
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Activity History Log
            item {
                SectionHeader(
                    title = "Activity History",
                    count = activities.size
                )
            }

            if (activities.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.History,
                        title = "No activity logged yet",
                        message = "Your completed tasks, locked plans, and saved notes will appear here.",
                        testTag = "insights_empty_activity"
                    )
                }
            } else {
                items(activities, key = { it.id }) { activity ->
                    val timeStr = remember(activity.timestamp) {
                        SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(activity.timestamp))
                    }
                    val (bgTint, fgTint) = when (activity.actionType) {
                        "COMPLETED" -> AppTheme.extendedColors.successContainer to AppTheme.extendedColors.onSuccessContainer
                        "CREATED" -> AppTheme.colorScheme.primaryContainer to AppTheme.colorScheme.onPrimaryContainer
                        "EDITED", "UPDATED" -> AppTheme.extendedColors.warningContainer to AppTheme.extendedColors.onWarningContainer
                        "DELETED" -> AppTheme.colorScheme.errorContainer to AppTheme.colorScheme.onErrorContainer
                        else -> AppTheme.colorScheme.surfaceContainerHighest to AppTheme.colorScheme.onSurfaceVariant
                    }

                    JlabCard(
                        shape = AppTheme.extendedShapes.compactCard,
                        contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding),
                        testTag = "insights_activity_${activity.id}"
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(AppTheme.spacing.avatarMd)
                                    .clip(CircleShape)
                                    .background(bgTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (activity.actionType) {
                                        "COMPLETED" -> Icons.Default.Check
                                        "CREATED" -> Icons.Default.Add
                                        "EDITED", "UPDATED" -> Icons.Default.Edit
                                        "DELETED" -> Icons.Default.Delete
                                        "LOCKED" -> Icons.Default.Lock
                                        else -> Icons.Outlined.EventNote
                                    },
                                    contentDescription = null,
                                    tint = fgTint,
                                    modifier = Modifier.size(AppTheme.spacing.iconSm)
                                )
                            }
                            Spacer(modifier = Modifier.width(AppTheme.spacing.smPlus))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activity.title,
                                    style = AppTheme.typography.titleSmall,
                                    color = AppTheme.colorScheme.onSurface
                                )
                                if (activity.summary.isNotBlank()) {
                                    Text(
                                        text = activity.summary,
                                        style = AppTheme.typography.bodySmall,
                                        color = AppTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = timeStr,
                                style = AppTheme.extendedTypography.caption,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = AppTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colorScheme.onSurface
        )
    }
}
