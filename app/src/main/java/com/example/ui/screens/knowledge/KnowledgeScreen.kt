package com.example.ui.screens.knowledge

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.entity.KnowledgeEntity
import com.example.data.local.entity.LearningEntity
import com.example.data.local.entity.ProjectEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabFloatingActionButton
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabSearchBar
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KnowledgeScreen(
    items: List<KnowledgeEntity>,
    projects: List<ProjectEntity> = emptyList(),
    courses: List<LearningEntity> = emptyList(),
    onSaveItem: (KnowledgeEntity) -> Unit,
    onDeleteItem: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<KnowledgeEntity?>(null) }
    var editingItem by remember { mutableStateOf<KnowledgeEntity?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val categories = listOf(
        "ALL",
        "Technical",
        "Business",
        "Learning",
        "Reference",
        "Process",
        "Research",
        "Idea",
        "Other"
    )

    BackHandler {
        when {
            editingItem != null || isCreatingNew -> {
                editingItem = null
                isCreatingNew = false
            }
            selectedItem != null -> {
                selectedItem = null
            }
            else -> onNavigateBack()
        }
    }

    if (editingItem != null || isCreatingNew) {
        KnowledgeEditorScreen(
            initialItem = editingItem,
            projects = projects,
            courses = courses,
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
        val currentItem = items.find { it.id == selectedItem!!.id } ?: selectedItem!!
        KnowledgeDetailScreen(
            item = currentItem,
            projectName = projects.find { it.id == currentItem.relatedProjectId }?.name,
            courseName = courses.find { it.id == currentItem.relatedCourseId }?.title,
            onEdit = { editingItem = currentItem },
            onDelete = {
                onDeleteItem(currentItem.id)
                selectedItem = null
            },
            onBack = { selectedItem = null }
        )
        return
    }

    val filteredItems = remember(items, searchQuery, selectedCategoryFilter) {
        var list = items
        if (selectedCategoryFilter != "ALL") {
            list = list.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                    it.summary.lowercase().contains(q) ||
                    it.content.lowercase().contains(q) ||
                    it.tags.lowercase().contains(q)
            }
        }
        list
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Knowledge Vault",
                subtitle = "Durable concepts, frameworks & key takeaways",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
                testTag = "knowledge_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "Add knowledge",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { isCreatingNew = true },
                        testTag = "knowledge_add_action"
                    )
                }
            )
        },
        floatingActionButton = {
            JlabFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "New knowledge record",
                onClick = { isCreatingNew = true },
                testTag = "knowledge_fab"
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
                placeholder = "Search knowledge, tags, takeaways...",
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.compactGap
                ),
                testTag = "knowledge_search_bar"
            )

            JlabFilterChipRow(
                options = categories,
                selectedOption = selectedCategoryFilter,
                onSelect = { selectedCategoryFilter = it },
                modifier = Modifier.padding(vertical = AppTheme.spacing.xs)
            )

            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.MenuBook,
                        title = if (searchQuery.isNotEmpty()) {
                            "No knowledge matches"
                        } else {
                            "Knowledge Vault is empty"
                        },
                        message = if (searchQuery.isNotEmpty()) {
                            "Try a different search keyword or category."
                        } else {
                            "Capture durable principles, algorithms, frameworks, and actionable key takeaways."
                        },
                        actionButtonText = "Add Knowledge Record",
                        onActionClick = { isCreatingNew = true },
                        testTag = "knowledge_empty_state"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = AppTheme.spacing.screenHorizontal,
                        end = AppTheme.spacing.screenHorizontal,
                        top = AppTheme.spacing.sm,
                        bottom = AppTheme.spacing.listBottomPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        CompactKnowledgeCard(
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
private fun CompactKnowledgeCard(
    item: KnowledgeEntity,
    onClick: () -> Unit
) {
    val dateStr = remember(item.updatedAt) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(item.updatedAt))
    }

    JlabCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(AppTheme.spacing.mdPlus),
        onClick = onClick,
        testTag = "knowledge_card_${item.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            JlabTagBadge(
                text = item.category,
                containerColor = AppTheme.colorScheme.primaryContainer,
                contentColor = AppTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = dateStr,
                style = AppTheme.extendedTypography.caption,
                color = AppTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.compactGap))

        Text(
            text = item.title,
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (item.summary.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
            Text(
                text = item.summary,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (item.tags.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)
            ) {
                item.tags.split(",").take(3).forEach { rawTag ->
                    val t = rawTag.trim()
                    if (t.isNotBlank()) {
                        JlabTagBadge(
                            text = "#$t",
                            containerColor = AppTheme.colorScheme.surfaceContainerHigh,
                            contentColor = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
