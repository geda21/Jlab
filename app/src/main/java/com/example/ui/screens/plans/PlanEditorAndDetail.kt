package com.example.ui.screens.plans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.PlanEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun PlanDetailScreen(
    plan: PlanEntity,
    onLockClick: () -> Unit,
    onUnlockClick: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = plan.title,
                onNavigationClick = onBack,
                testTag = "plan_detail_top_bar",
                actions = {
                    JlabIconButton(
                        icon = if (plan.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = if (plan.isLocked) "Unlock Plan" else "Lock Plan",
                        tint = if (plan.isLocked) AppTheme.extendedColors.warning else AppTheme.colorScheme.onSurfaceVariant,
                        onClick = { if (plan.isLocked) onUnlockClick() else onLockClick() },
                        testTag = "plan_detail_lock_toggle"
                    )
                    JlabIconButton(
                        icon = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Plan",
                        tint = AppTheme.colorScheme.error,
                        onClick = onDelete,
                        testTag = "plan_detail_delete_button"
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.md
                ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                JlabTagBadge(
                    text = "${plan.planType} PLAN",
                    containerColor = AppTheme.colorScheme.primaryContainer,
                    contentColor = AppTheme.colorScheme.onPrimaryContainer
                )
                if (plan.isLocked) {
                    JlabTagBadge(
                        text = "LOCKED COMMITMENT",
                        icon = Icons.Default.Lock,
                        containerColor = AppTheme.extendedColors.warningContainer,
                        contentColor = AppTheme.extendedColors.onWarningContainer
                    )
                }
            }

            if (plan.description.isNotBlank()) {
                Text(
                    text = plan.description,
                    style = AppTheme.typography.bodyLarge,
                    color = AppTheme.colorScheme.onSurface
                )
            }

            if (plan.goals.isNotBlank()) {
                JlabSectionCard(
                    overline = "PRIORITIES & SCHEDULE",
                    testTag = "plan_detail_priorities_card"
                ) {
                    Text(
                        text = plan.goals,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                }
            }

            if (plan.notes.isNotBlank()) {
                JlabSectionCard(
                    overline = "COMMITMENTS & NOTES",
                    containerColor = AppTheme.colorScheme.surfaceContainerLow,
                    testTag = "plan_detail_commitments_card"
                ) {
                    Text(
                        text = plan.notes,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
internal fun PlanEditorScreen(
    initialPlan: PlanEntity?,
    onSave: (PlanEntity) -> Unit,
    onCancel: () -> Unit
) {
    var planType by remember { mutableStateOf(initialPlan?.planType ?: "DAILY") }
    var title by remember { mutableStateOf(initialPlan?.title ?: "") }
    var objective by remember { mutableStateOf(initialPlan?.description ?: "") }

    var priority1 by remember { mutableStateOf("") }
    var priority2 by remember { mutableStateOf("") }
    var priority3 by remember { mutableStateOf("") }

    var morningFocus by remember { mutableStateOf("") }
    var afternoonFocus by remember { mutableStateOf("") }
    var eveningFocus by remember { mutableStateOf("") }

    var commitments by remember { mutableStateOf(initialPlan?.notes ?: "") }

    val planTypes = listOf("DAILY", "WEEKLY", "MONTHLY", "CUSTOM")

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = if (initialPlan == null) "Create Plan" else "Edit Plan",
                onNavigationClick = onCancel,
                testTag = "plan_editor_top_bar",
                actions = {
                    JlabPrimaryButton(
                        text = "Save Plan",
                        icon = Icons.Default.Check,
                        enabled = title.isNotBlank(),
                        onClick = {
                            if (title.isNotBlank()) {
                                val goalsBuilder = StringBuilder()
                                if (priority1.isNotBlank()) goalsBuilder.append("1. $priority1\n")
                                if (priority2.isNotBlank()) goalsBuilder.append("2. $priority2\n")
                                if (priority3.isNotBlank()) goalsBuilder.append("3. $priority3\n")
                                if (morningFocus.isNotBlank()) goalsBuilder.append("\n• Morning: $morningFocus\n")
                                if (afternoonFocus.isNotBlank()) goalsBuilder.append("• Afternoon: $afternoonFocus\n")
                                if (eveningFocus.isNotBlank()) goalsBuilder.append("• Evening: $eveningFocus\n")

                                val plan = (initialPlan ?: PlanEntity(title = title)).copy(
                                    title = title.trim(),
                                    planType = planType,
                                    description = objective.trim(),
                                    goals = goalsBuilder.toString().trim(),
                                    notes = commitments.trim(),
                                    updatedAt = System.currentTimeMillis()
                                )
                                onSave(plan)
                            }
                        },
                        testTag = "plan_editor_save_button"
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.md
                ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            Text(
                text = "PLAN TYPE",
                style = AppTheme.extendedTypography.overline,
                color = AppTheme.colorScheme.primary
            )

            JlabFilterChipRow(
                options = planTypes,
                selectedOption = planType,
                onSelect = { type ->
                    planType = type
                    if (title.isBlank()) {
                        val today = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
                        title = when (type) {
                            "DAILY" -> "Daily Plan - $today"
                            "WEEKLY" -> "Weekly Sprint Plan"
                            else -> "$type Plan"
                        }
                    }
                },
                contentPadding = PaddingValues(0.dp)
            )

            JlabTextField(
                value = title,
                onValueChange = { title = it },
                label = "Plan Title *",
                testTag = "plan_title_input"
            )

            JlabTextField(
                value = objective,
                onValueChange = { objective = it },
                label = "Core Objective / Purpose",
                testTag = "plan_objective_input"
            )

            Text(
                text = "TOP 3 PRIORITIES",
                style = AppTheme.extendedTypography.overline,
                color = AppTheme.colorScheme.primary
            )

            JlabTextField(
                value = priority1,
                onValueChange = { priority1 = it },
                label = "1. Highest Priority Outcome *",
                testTag = "plan_priority_1_input"
            )
            JlabTextField(
                value = priority2,
                onValueChange = { priority2 = it },
                label = "2. Second Priority",
                testTag = "plan_priority_2_input"
            )
            JlabTextField(
                value = priority3,
                onValueChange = { priority3 = it },
                label = "3. Third Priority",
                testTag = "plan_priority_3_input"
            )

            Text(
                text = "TIME & EXECUTION BLOCKS",
                style = AppTheme.extendedTypography.overline,
                color = AppTheme.colorScheme.primary
            )

            JlabTextField(
                value = morningFocus,
                onValueChange = { morningFocus = it },
                label = "Morning Focus / Deep Work",
                testTag = "plan_morning_input"
            )
            JlabTextField(
                value = afternoonFocus,
                onValueChange = { afternoonFocus = it },
                label = "Afternoon Execution / Tasks",
                testTag = "plan_afternoon_input"
            )
            JlabTextField(
                value = eveningFocus,
                onValueChange = { eveningFocus = it },
                label = "Evening Review & Shutdown",
                testTag = "plan_evening_input"
            )

            JlabTextField(
                value = commitments,
                onValueChange = { commitments = it },
                label = "Non-Negotiable Commitments & Habits",
                placeholder = "e.g. No social media before noon; 30m code review",
                singleLine = false,
                minLines = 3,
                maxLines = 6,
                testTag = "plan_commitments_input"
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.xl))
        }
    }
}

@Composable
internal fun LockPlanConfirmDialog(
    planTitle: String,
    onDismiss: () -> Unit,
    onConfirmLock: () -> Unit
) {
    JlabConfirmDialog(
        title = "Lock Plan?",
        message = "Locking \"$planTitle\" prevents casual edits. If you need to edit later, you will be required to write down a deliberate unlock reason.",
        confirmText = "Lock Commitment",
        icon = Icons.Default.Lock,
        onConfirm = onConfirmLock,
        onDismiss = onDismiss,
        testTag = "lock_plan_confirm_dialog"
    )
}

@Composable
internal fun UnlockPlanDialog(
    planTitle: String,
    onDismiss: () -> Unit,
    onConfirmUnlock: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.large,
        containerColor = AppTheme.colorScheme.surfaceContainerHigh,
        title = { Text("Unlock Plan", style = AppTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)) {
                Text(
                    text = "Why are you modifying locked plan \"$planTitle\"?",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                JlabTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = "Reason for Unlock *",
                    placeholder = "e.g. Unforeseen production bug",
                    testTag = "unlock_plan_reason_input"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Unlock",
                enabled = reason.isNotBlank(),
                onClick = { if (reason.isNotBlank()) onConfirmUnlock(reason.trim()) },
                testTag = "unlock_plan_confirm_button"
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Cancel")
            }
        }
    )
}
