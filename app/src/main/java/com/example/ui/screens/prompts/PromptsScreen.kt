package com.example.ui.screens.prompts

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.entity.PromptEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class PromptFilterTab(val label: String) {
    ALL("All"),
    FAVORITES("Favorites"),
    RECENT("Recent")
}

@Composable
fun PromptsScreen(
    prompts: List<PromptEntity>,
    onSavePrompt: (PromptEntity) -> Unit,
    onMarkCopied: (String) -> Unit,
    onDeletePrompt: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableStateOf(PromptFilterTab.ALL) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    var showEditor by remember { mutableStateOf(false) }
    var editingPrompt by remember { mutableStateOf<PromptEntity?>(null) }
    var viewingPromptDetail by remember { mutableStateOf<PromptEntity?>(null) }
    var promptToDelete by remember { mutableStateOf<PromptEntity?>(null) }
    var promptToFillVariables by remember { mutableStateOf<PromptEntity?>(null) }

    BackHandler {
        when {
            showEditor || editingPrompt != null -> {
                showEditor = false
                editingPrompt = null
            }
            viewingPromptDetail != null -> viewingPromptDetail = null
            else -> onNavigateBack()
        }
    }

    val userCategories = remember(prompts) {
        prompts.map { it.category.trim() }.filter { it.isNotBlank() }.distinct().sorted()
    }

    val filteredPrompts = remember(prompts, searchQuery, selectedFilterTab, selectedCategoryFilter) {
        var list = when (selectedFilterTab) {
            PromptFilterTab.ALL -> prompts
            PromptFilterTab.FAVORITES -> prompts.filter { it.isFavorite }
            PromptFilterTab.RECENT -> prompts.sortedByDescending { it.lastCopiedAt ?: it.updatedAt }
        }

        if (selectedCategoryFilter != null) {
            list = list.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                    it.fullPrompt.lowercase().contains(q) ||
                    it.category.lowercase().contains(q) ||
                    it.tags.lowercase().contains(q) ||
                    it.notes.lowercase().contains(q)
            }
        }
        list
    }

    if (showEditor || editingPrompt != null) {
        PromptEditorScreen(
            initialPrompt = editingPrompt,
            existingCategories = userCategories,
            onSave = { saved ->
                onSavePrompt(saved)
                showEditor = false
                editingPrompt = null
            },
            onDismiss = {
                showEditor = false
                editingPrompt = null
            }
        )
        return
    }

    if (viewingPromptDetail != null) {
        val currentDetail = prompts.find { it.id == viewingPromptDetail!!.id } ?: viewingPromptDetail!!
        PromptDetailView(
            prompt = currentDetail,
            onCopy = {
                val variables = extractPromptVariables(currentDetail.fullPrompt)
                if (variables.isNotEmpty()) {
                    promptToFillVariables = currentDetail
                } else {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Jlab Prompt", currentDetail.fullPrompt))
                    onMarkCopied(currentDetail.id)
                    Toast.makeText(context, "Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
            },
            onFillVariables = { promptToFillVariables = currentDetail },
            onEdit = {
                editingPrompt = currentDetail
                viewingPromptDetail = null
            },
            onToggleFavorite = {
                val updated = currentDetail.copy(isFavorite = !currentDetail.isFavorite)
                onSavePrompt(updated)
                viewingPromptDetail = updated
            },
            onDuplicate = {
                val duplicate = currentDetail.copy(
                    id = UUID.randomUUID().toString(),
                    title = "${currentDetail.title} (Copy)",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                onSavePrompt(duplicate)
                Toast.makeText(context, "Prompt duplicated!", Toast.LENGTH_SHORT).show()
                viewingPromptDetail = null
            },
            onDelete = {
                promptToDelete = currentDetail
                viewingPromptDetail = null
            },
            onBack = { viewingPromptDetail = null }
        )
        return
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Prompt Library",
                subtitle = if (prompts.isEmpty()) "Private storage" else "${prompts.size} saved prompts",
                onNavigationClick = onNavigateBack,
                testTag = "prompts_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "Add Prompt",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { showEditor = true },
                        testTag = "prompts_add_button"
                    )
                }
            )
        },
        floatingActionButton = {
            JlabFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "Add Prompt",
                onClick = { showEditor = true },
                testTag = "prompts_fab"
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
                placeholder = "Search prompts, tags, notes...",
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.xs
                ),
                testTag = "prompts_search_bar"
            )

            LazyRow(
                contentPadding = PaddingValues(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.xs
                ),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                items(PromptFilterTab.entries) { tab ->
                    JlabFilterChip(
                        label = tab.label,
                        selected = selectedFilterTab == tab && selectedCategoryFilter == null,
                        onClick = {
                            selectedFilterTab = tab
                            selectedCategoryFilter = null
                        },
                        testTag = "prompt_tab_${tab.name.lowercase()}"
                    )
                }

                items(userCategories) { cat ->
                    JlabFilterChip(
                        label = cat,
                        selected = selectedCategoryFilter == cat,
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat
                        }
                    )
                }
            }

            if (filteredPrompts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.AutoAwesome,
                        title = if (searchQuery.isNotBlank() || selectedCategoryFilter != null) "No matching prompts" else "Prompt Library",
                        message = if (searchQuery.isNotBlank()) {
                            "Try different keywords or clear filters."
                        } else {
                            "Save reusable templates with dynamic {variables}. Your prompts stay private on this device."
                        },
                        actionButtonText = "Add Prompt",
                        onActionClick = { showEditor = true },
                        testTag = "prompts_empty_state"
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
                    items(filteredPrompts, key = { it.id }) { prompt ->
                        val variables = remember(prompt.fullPrompt) { extractPromptVariables(prompt.fullPrompt) }
                        PromptCompactCard(
                            prompt = prompt,
                            variableCount = variables.size,
                            onClick = { viewingPromptDetail = prompt },
                            onCopy = {
                                if (variables.isNotEmpty()) {
                                    promptToFillVariables = prompt
                                } else {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Jlab Prompt", prompt.fullPrompt))
                                    onMarkCopied(prompt.id)
                                    Toast.makeText(context, "Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onToggleFavorite = {
                                onSavePrompt(prompt.copy(isFavorite = !prompt.isFavorite))
                            }
                        )
                    }
                }
            }
        }
    }

    promptToFillVariables?.let { prompt ->
        FillVariablesDialog(
            prompt = prompt,
            onDismiss = { promptToFillVariables = null },
            onCopyFilled = { filledText ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Jlab Filled Prompt", filledText))
                onMarkCopied(prompt.id)
                Toast.makeText(context, "Filled prompt copied!", Toast.LENGTH_SHORT).show()
                promptToFillVariables = null
            },
            onCopyRaw = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Jlab Prompt Template", prompt.fullPrompt))
                onMarkCopied(prompt.id)
                Toast.makeText(context, "Raw template copied!", Toast.LENGTH_SHORT).show()
                promptToFillVariables = null
            }
        )
    }

    promptToDelete?.let { prompt ->
        JlabConfirmDialog(
            title = "Delete Prompt?",
            message = "Are you sure you want to delete '${prompt.title}'?",
            confirmText = "Delete",
            isDestructive = true,
            icon = Icons.Default.DeleteOutline,
            onConfirm = {
                onDeletePrompt(prompt.id)
                promptToDelete = null
            },
            onDismiss = { promptToDelete = null },
            testTag = "delete_prompt_confirm_dialog"
        )
    }
}

@Composable
fun PromptCompactCard(
    prompt: PromptEntity,
    variableCount: Int,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val relativeTime = remember(prompt.updatedAt) { formatPromptRelativeTime(prompt.updatedAt) }

    JlabCard(
        onClick = onClick,
        testTag = "prompt_card_${prompt.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = prompt.title,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            JlabIconButton(
                icon = if (prompt.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = if (prompt.isFavorite) "Remove Favorite" else "Mark Favorite",
                tint = if (prompt.isFavorite) AppTheme.extendedColors.warning else AppTheme.colorScheme.onSurfaceVariant,
                onClick = onToggleFavorite,
                testTag = "prompt_fav_${prompt.id}"
            )
        }

        Row(
            modifier = Modifier.padding(vertical = AppTheme.spacing.xxs),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            JlabTagBadge(
                text = prompt.category,
                containerColor = AppTheme.colorScheme.primaryContainer,
                contentColor = AppTheme.colorScheme.onPrimaryContainer
            )

            if (variableCount > 0) {
                JlabTagBadge(
                    text = "$variableCount var${if (variableCount > 1) "s" else ""}",
                    containerColor = AppTheme.extendedColors.violetContainer,
                    contentColor = AppTheme.extendedColors.onVioletContainer
                )
            }

            if (prompt.tags.isNotBlank()) {
                Text(
                    text = prompt.tags,
                    style = AppTheme.extendedTypography.caption,
                    color = AppTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Text(
            text = prompt.fullPrompt,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                top = AppTheme.spacing.xs,
                bottom = AppTheme.spacing.sm
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Updated $relativeTime",
                style = AppTheme.extendedTypography.caption,
                color = AppTheme.colorScheme.onSurfaceVariant
            )

            JlabTonalButton(
                text = if (variableCount > 0) "Fill & Copy" else "Copy",
                icon = if (variableCount > 0) Icons.Default.Tune else Icons.Default.ContentCopy,
                onClick = onCopy,
                testTag = "prompt_copy_${prompt.id}"
            )
        }
    }
}

private fun formatPromptRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val mins = diff / (60 * 1000)
    val hours = diff / (60 * 60 * 1000)
    val days = diff / (24 * 60 * 60 * 1000)

    return when {
        mins < 1 -> "just now"
        mins < 60 -> "${mins}m ago"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "yesterday"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
