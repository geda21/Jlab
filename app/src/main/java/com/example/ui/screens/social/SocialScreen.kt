package com.example.ui.screens.social

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.entity.SocialContentEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme

@Composable
fun SocialScreen(
    items: List<SocialContentEntity>,
    onSaveItem: (SocialContentEntity) -> Unit,
    onDeleteItem: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedStage by remember { mutableStateOf("ALL") }
    var selectedItem by remember { mutableStateOf<SocialContentEntity?>(null) }
    var editingItem by remember { mutableStateOf<SocialContentEntity?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    val stages = listOf("ALL", "IDEA", "DRAFTING", "READY", "SCHEDULED", "PUBLISHED")

    BackHandler {
        when {
            editingItem != null || isCreatingNew -> {
                editingItem = null
                isCreatingNew = false
            }
            selectedItem != null -> selectedItem = null
            else -> onNavigateBack()
        }
    }

    if (editingItem != null || isCreatingNew) {
        SocialEditorScreen(
            initialItem = editingItem,
            onSave = { saved ->
                onSaveItem(saved)
                selectedItem = saved
                editingItem = null
                isCreatingNew = false
            },
            onCancel = {
                editingItem = null
                isCreatingNew = false
            }
        )
        return
    }

    if (selectedItem != null) {
        val current = items.find { it.id == selectedItem!!.id } ?: selectedItem!!
        SocialDetailScreen(
            item = current,
            onEdit = { editingItem = current },
            onDelete = {
                onDeleteItem(current.id)
                selectedItem = null
            },
            onAdvanceStatus = { nextStatus ->
                val updated = current.copy(status = nextStatus, updatedAt = System.currentTimeMillis())
                onSaveItem(updated)
                selectedItem = updated
            },
            onBack = { selectedItem = null }
        )
        return
    }

    val filtered = remember(items, selectedStage) {
        if (selectedStage == "ALL") items else items.filter { it.status.equals(selectedStage, ignoreCase = true) }
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Social Content Pipeline",
                subtitle = "${items.size} pieces in content engine",
                onNavigationClick = onNavigateBack,
                testTag = "social_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "New Content",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { isCreatingNew = true },
                        testTag = "social_add_button"
                    )
                }
            )
        },
        floatingActionButton = {
            JlabFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "Create Content Idea",
                onClick = { isCreatingNew = true },
                testTag = "social_fab"
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
                    .horizontalScroll(rememberScrollState())
                    .padding(
                        horizontal = AppTheme.spacing.screenHorizontal,
                        vertical = AppTheme.spacing.xs
                    ),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                stages.forEach { stage ->
                    val isSelected = selectedStage.equals(stage, ignoreCase = true)
                    val count = if (stage == "ALL") items.size else items.count { it.status.equals(stage, ignoreCase = true) }
                    val label = if (count > 0) "$stage ($count)" else stage
                    JlabFilterChip(
                        label = label,
                        selected = isSelected,
                        onClick = { selectedStage = stage },
                        testTag = "social_stage_chip_${stage.lowercase()}"
                    )
                }
            }

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.Share,
                        title = if (selectedStage != "ALL") "No $selectedStage content" else "No content yet",
                        message = "Turn raw ideas into published posts, videos, threads, and newsletters across platforms.",
                        actionButtonText = "Create Content Idea",
                        onActionClick = { isCreatingNew = true },
                        testTag = "social_empty_state"
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
                    items(filtered, key = { it.id }) { item ->
                        SocialCard(
                            item = item,
                            onClick = { selectedItem = item }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialCard(
    item: SocialContentEntity,
    onClick: () -> Unit
) {
    val platformColor = socialPlatformColor(item.platform)
    val (statusBg, statusFg) = socialStageColor(item.status)

    JlabCard(
        onClick = onClick,
        testTag = "social_card_${item.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
            ) {
                JlabTagBadge(
                    text = item.platform,
                    containerColor = platformColor.copy(alpha = 0.16f),
                    contentColor = platformColor
                )

                JlabTagBadge(
                    text = item.contentType,
                    containerColor = AppTheme.colorScheme.surfaceContainerHighest,
                    contentColor = AppTheme.colorScheme.onSurfaceVariant
                )
            }

            JlabTagBadge(
                text = item.status,
                containerColor = statusBg,
                contentColor = statusFg
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))

        Text(
            text = item.title,
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        val previewText = item.scriptOrCaption.ifBlank { item.idea }
        if (previewText.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
            Text(
                text = previewText,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (item.tags.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                item.tags.split(",").take(3).forEach { t ->
                    val clean = t.trim()
                    if (clean.isNotBlank()) {
                        Text(
                            text = "#$clean",
                            style = AppTheme.extendedTypography.badge,
                            color = AppTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
