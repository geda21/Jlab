package com.example.ui.screens.reviews

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.local.entity.DailyReviewEntity
import com.example.data.local.entity.WeeklyReviewEntity
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSectionCard
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    completedTodayCount: Int,
    onSaveDailyReview: (DailyReviewEntity) -> Unit,
    onSaveWeeklyReview: (WeeklyReviewEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val todayDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val currentWeekStr = remember { SimpleDateFormat("yyyy-'W'ww", Locale.getDefault()).format(Date()) }

    var whatWentWell by remember { mutableStateOf("") }
    var whatNeedsAttention by remember { mutableStateOf("") }

    var weeklyReflection by remember { mutableStateOf("") }
    var weeklyPriorities by remember { mutableStateOf("") }

    BackHandler { onNavigateBack() }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Reflections & Reviews",
                subtitle = "Close the loop on execution",
                onNavigationClick = onNavigateBack,
                testTag = "reviews_top_bar"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = AppTheme.colorScheme.background,
                contentColor = AppTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("reviews_tab_daily"),
                    text = { Text("Daily Review", style = AppTheme.typography.labelLarge) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("reviews_tab_weekly"),
                    text = { Text("Weekly Review", style = AppTheme.typography.labelLarge) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(AppTheme.spacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap)
            ) {
                if (selectedTab == 0) {
                    JlabSectionCard(
                        overline = "DAILY EXECUTION SUMMARY",
                        title = "Today's Output: $todayDateStr",
                        testTag = "daily_review_summary_card"
                    ) {
                        Text(
                            text = "Tasks completed today: $completedTodayCount",
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    JlabTextField(
                        value = whatWentWell,
                        onValueChange = { whatWentWell = it },
                        label = "What went well today?",
                        singleLine = false,
                        minLines = 3,
                        maxLines = 6,
                        testTag = "daily_review_went_well_input"
                    )

                    JlabTextField(
                        value = whatNeedsAttention,
                        onValueChange = { whatNeedsAttention = it },
                        label = "What needs attention tomorrow?",
                        singleLine = false,
                        minLines = 3,
                        maxLines = 6,
                        testTag = "daily_review_needs_attention_input"
                    )

                    JlabPrimaryButton(
                        text = "Save Daily Review",
                        icon = Icons.Default.Save,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onSaveDailyReview(
                                DailyReviewEntity(
                                    dateStr = todayDateStr,
                                    completedTasksCount = completedTodayCount,
                                    focusMinutes = 0,
                                    whatWentWell = whatWentWell.trim(),
                                    whatNeedsAttention = whatNeedsAttention.trim()
                                )
                            )
                            Toast.makeText(context, "Daily review saved locally!", Toast.LENGTH_SHORT).show()
                        },
                        testTag = "save_daily_review_button"
                    )
                } else {
                    JlabSectionCard(
                        overline = "WEEKLY SPRINT",
                        title = "Week: $currentWeekStr",
                        testTag = "weekly_review_summary_card"
                    ) {
                        Text(
                            text = "Review wins, blockers, and establish next week's key outcomes.",
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    JlabTextField(
                        value = weeklyReflection,
                        onValueChange = { weeklyReflection = it },
                        label = "Weekly Reflection (Wins, Roadblocks, Learnings)",
                        singleLine = false,
                        minLines = 4,
                        maxLines = 8,
                        testTag = "weekly_review_reflection_input"
                    )

                    JlabTextField(
                        value = weeklyPriorities,
                        onValueChange = { weeklyPriorities = it },
                        label = "Next Week's 3 Non-Negotiable Priorities",
                        singleLine = false,
                        minLines = 4,
                        maxLines = 8,
                        testTag = "weekly_review_priorities_input"
                    )

                    JlabPrimaryButton(
                        text = "Save Weekly Review",
                        icon = Icons.Default.Save,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onSaveWeeklyReview(
                                WeeklyReviewEntity(
                                    weekStr = currentWeekStr,
                                    tasksCompletedCount = completedTodayCount,
                                    focusMinutes = 0,
                                    reflection = weeklyReflection.trim(),
                                    nextWeekPriorities = weeklyPriorities.trim()
                                )
                            )
                            Toast.makeText(context, "Weekly review saved locally!", Toast.LENGTH_SHORT).show()
                        },
                        testTag = "save_weekly_review_button"
                    )
                }
            }
        }
    }
}
