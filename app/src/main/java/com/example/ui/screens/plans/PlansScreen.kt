package com.example.ui.screens.plans

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.entity.PlanEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme

@Composable
fun PlansScreen(
    plans: List<PlanEntity>,
    onSavePlan: (PlanEntity) -> Unit,
    onLockPlan: (String, String) -> Unit,
    onUnlockPlan: (String, String) -> Unit,
    onDeletePlan: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var isCreatingPlan by remember { mutableStateOf(false) }
    var viewingPlan by remember { mutableStateOf<PlanEntity?>(null) }
    var lockingPlan by remember { mutableStateOf<PlanEntity?>(null) }
    var unlockingPlan by remember { mutableStateOf<PlanEntity?>(null) }

    var selectedTypeFilter by remember { mutableStateOf("ALL") }
    val typeFilters = listOf("ALL", "DAILY", "WEEKLY", "MONTHLY", "LOCKED")

    BackHandler {
        when {
            isCreatingPlan -> isCreatingPlan = false
            viewingPlan != null -> viewingPlan = null
            else -> onNavigateBack()
        }
    }

    if (isCreatingPlan) {
        PlanEditorScreen(
            initialPlan = null,
            onSave = { plan ->
                onSavePlan(plan)
                isCreatingPlan = false
            },
            onCancel = { isCreatingPlan = false }
        )
        return
    }

    if (viewingPlan != null) {
        val current = plans.find { it.id == viewingPlan!!.id } ?: viewingPlan!!
        PlanDetailScreen(
            plan = current,
            onLockClick = { lockingPlan = current },
            onUnlockClick = { unlockingPlan = current },
            onDelete = {
                onDeletePlan(current.id)
                viewingPlan = null
            },
            onBack = { viewingPlan = null }
        )
        return
    }

    val filteredPlans = remember(plans, selectedTypeFilter) {
        when (selectedTypeFilter) {
            "ALL" -> plans
            "LOCKED" -> plans.filter { it.isLocked }
            else -> plans.filter { it.planType.equals(selectedTypeFilter, ignoreCase = true) }
        }
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Plans & Commitments",
                subtitle = "Disciplined daily, weekly & locked schedules",
                onNavigationClick = onNavigateBack,
                testTag = "plans_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "New Plan",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { isCreatingPlan = true },
                        testTag = "plans_add_button"
                    )
                }
            )
        },
        floatingActionButton = {
            JlabFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "Create Plan",
                onClick = { isCreatingPlan = true },
                testTag = "plans_fab"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
        ) {
            JlabFilterChipRow(
                options = typeFilters,
                selectedOption = selectedTypeFilter,
                onSelect = { selectedTypeFilter = it },
                modifier = Modifier.padding(vertical = AppTheme.spacing.xs)
            )

            if (filteredPlans.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.EventNote,
                        title = if (selectedTypeFilter != "ALL") "No $selectedTypeFilter plans" else "No active plans",
                        message = "Create actionable daily and weekly sprint plans with top 3 priorities, schedule blocks, and commitments.",
                        actionButtonText = "Create Plan",
                        onActionClick = { isCreatingPlan = true },
                        testTag = "plans_empty_state"
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
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
                ) {
                    items(filteredPlans, key = { it.id }) { plan ->
                        PlanCard(
                            plan = plan,
                            onClick = { viewingPlan = plan },
                            onLockClick = { lockingPlan = plan },
                            onUnlockClick = { unlockingPlan = plan },
                            onDelete = { onDeletePlan(plan.id) }
                        )
                    }
                }
            }
        }
    }

    lockingPlan?.let { plan ->
        LockPlanConfirmDialog(
            planTitle = plan.title,
            onDismiss = { lockingPlan = null },
            onConfirmLock = {
                onLockPlan(plan.id, plan.title)
                lockingPlan = null
            }
        )
    }

    unlockingPlan?.let { plan ->
        UnlockPlanDialog(
            planTitle = plan.title,
            onDismiss = { unlockingPlan = null },
            onConfirmUnlock = { reason ->
                onUnlockPlan(plan.id, reason)
                unlockingPlan = null
            }
        )
    }
}

@Composable
private fun PlanCard(
    plan: PlanEntity,
    onClick: () -> Unit,
    onLockClick: () -> Unit,
    onUnlockClick: () -> Unit,
    onDelete: () -> Unit
) {
    val (typeBg, typeFg) = when (plan.planType.uppercase()) {
        "DAILY" -> AppTheme.extendedColors.warningContainer to AppTheme.extendedColors.onWarningContainer
        "WEEKLY" -> AppTheme.colorScheme.primaryContainer to AppTheme.colorScheme.onPrimaryContainer
        else -> AppTheme.colorScheme.secondaryContainer to AppTheme.colorScheme.onSecondaryContainer
    }

    JlabCard(
        borderColor = if (plan.isLocked) AppTheme.extendedColors.warning.copy(alpha = 0.55f) else null,
        onClick = onClick,
        testTag = "plan_card_${plan.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                JlabTagBadge(
                    text = plan.planType,
                    containerColor = typeBg,
                    contentColor = typeFg
                )

                if (plan.isLocked) {
                    JlabTagBadge(
                        text = "LOCKED",
                        icon = Icons.Default.Lock,
                        containerColor = AppTheme.extendedColors.warningContainer,
                        contentColor = AppTheme.extendedColors.onWarningContainer
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                JlabIconButton(
                    icon = if (plan.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = if (plan.isLocked) "Unlock Plan" else "Lock Plan",
                    tint = if (plan.isLocked) AppTheme.extendedColors.warning else AppTheme.colorScheme.onSurfaceVariant,
                    onClick = { if (plan.isLocked) onUnlockClick() else onLockClick() },
                    testTag = "plan_lock_toggle_${plan.id}"
                )
                JlabIconButton(
                    icon = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Plan",
                    tint = AppTheme.colorScheme.error,
                    onClick = onDelete,
                    testTag = "plan_delete_${plan.id}"
                )
            }
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))

        Text(
            text = plan.title,
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.onSurface
        )

        if (plan.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
            Text(
                text = plan.description,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
