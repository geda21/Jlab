package com.example.ui.screens.calendar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    tasks: List<TaskEntity>,
    onNavigateBack: () -> Unit
) {
    var selectedCalendarDate by remember { mutableStateOf(Calendar.getInstance()) }

    BackHandler { onNavigateBack() }

    val daysInMonth = remember(selectedCalendarDate) {
        val cal = selectedCalendarDate.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val list = mutableListOf<Calendar>()
        for (i in 1..maxDays) {
            val d = cal.clone() as Calendar
            d.set(Calendar.DAY_OF_MONTH, i)
            list.add(d)
        }
        list
    }

    val selectedDateYearMonthDay = remember(selectedCalendarDate) {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCalendarDate.time)
    }

    val dayTasks = remember(tasks, selectedDateYearMonthDay) {
        tasks.filter { task ->
            if (task.dueDate == null) false
            else {
                val taskDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(task.dueDate))
                taskDateStr == selectedDateYearMonthDay
            }
        }
    }

    val monthTitle = remember(selectedCalendarDate) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedCalendarDate.time)
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Local Calendar",
                subtitle = monthTitle,
                onNavigationClick = onNavigateBack,
                testTag = "calendar_top_bar"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppTheme.spacing.screenHorizontal,
                        vertical = AppTheme.spacing.xs
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                JlabIconButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    onClick = {
                        val cal = selectedCalendarDate.clone() as Calendar
                        cal.add(Calendar.MONTH, -1)
                        selectedCalendarDate = cal
                    },
                    testTag = "calendar_prev_month"
                )

                Text(
                    text = monthTitle,
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colorScheme.onSurface
                )

                JlabIconButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    onClick = {
                        val cal = selectedCalendarDate.clone() as Calendar
                        cal.add(Calendar.MONTH, 1)
                        selectedCalendarDate = cal
                    },
                    testTag = "calendar_next_month"
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = AppTheme.spacing.screenHorizontal),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                items(daysInMonth) { dateCal ->
                    val isSelected = dateCal.get(Calendar.DAY_OF_MONTH) == selectedCalendarDate.get(Calendar.DAY_OF_MONTH) &&
                        dateCal.get(Calendar.MONTH) == selectedCalendarDate.get(Calendar.MONTH)
                    val dayNum = dateCal.get(Calendar.DAY_OF_MONTH)
                    val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(dateCal.time)

                    Surface(
                        modifier = Modifier
                            .width(56.dp)
                            .minimumInteractiveComponentSize()
                            .clip(AppTheme.extendedShapes.compactCard)
                            .clickable { selectedCalendarDate = dateCal.clone() as Calendar }
                            .testTag("calendar_day_$dayNum"),
                        shape = AppTheme.extendedShapes.compactCard,
                        color = if (isSelected) AppTheme.colorScheme.primary else AppTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = if (isSelected) AppTheme.spacing.borderSelected else AppTheme.spacing.borderThin,
                            color = if (isSelected) AppTheme.colorScheme.primary else AppTheme.extendedColors.cardBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = AppTheme.spacing.smPlus),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayName,
                                style = AppTheme.extendedTypography.caption,
                                color = if (isSelected) AppTheme.colorScheme.onPrimary else AppTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                            Text(
                                text = "$dayNum",
                                style = AppTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) AppTheme.colorScheme.onPrimary else AppTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.md))

            SectionHeader(
                title = "Agenda: ${SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(selectedCalendarDate.time)}",
                count = dayTasks.size,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.screenHorizontal)
            )

            if (dayTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.EventAvailable,
                        title = "No events on this day",
                        message = "No deadlines or tasks scheduled. Enjoy your clear schedule.",
                        testTag = "calendar_empty_state"
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
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap),
                    modifier = Modifier.weight(1f)
                ) {
                    items(dayTasks, key = { it.id }) { task ->
                        JlabCard(testTag = "calendar_task_${task.id}") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)
                            ) {
                                PriorityBadge(priority = task.priority)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = AppTheme.colorScheme.onSurface
                                    )
                                    if (task.description.isNotBlank()) {
                                        Text(
                                            text = task.description,
                                            style = AppTheme.typography.bodySmall,
                                            color = AppTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                StatusBadge(status = task.status)
                            }
                        }
                    }
                }
            }
        }
    }
}
