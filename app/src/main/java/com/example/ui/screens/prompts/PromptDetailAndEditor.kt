package com.example.ui.screens.prompts

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.PromptEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme

fun extractPromptVariables(text: String): List<String> {
    val regex = Regex("\\{([a-zA-Z0-9_-]+)\\}")
    return regex.findAll(text).map { it.groupValues[1] }.distinct().toList()
}

@Composable
fun PromptDetailView(
    prompt: PromptEntity,
    onCopy: () -> Unit,
    onFillVariables: () -> Unit,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val variables = remember(prompt.fullPrompt) { extractPromptVariables(prompt.fullPrompt) }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = prompt.title,
                onNavigationClick = onBack,
                testTag = "prompt_detail_top_bar",
                actions = {
                    JlabIconButton(
                        icon = if (prompt.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (prompt.isFavorite) "Remove Favorite" else "Mark Favorite",
                        tint = if (prompt.isFavorite) AppTheme.extendedColors.warning else AppTheme.colorScheme.onSurfaceVariant,
                        onClick = onToggleFavorite,
                        testTag = "prompt_detail_favorite_button"
                    )
                    JlabIconButton(
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit Prompt",
                        onClick = onEdit,
                        testTag = "prompt_detail_edit_button"
                    )
                    Box {
                        JlabIconButton(
                            icon = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            onClick = { showMenu = true },
                            testTag = "prompt_detail_more_button"
                        )
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Duplicate") },
                                onClick = {
                                    showMenu = false
                                    onDuplicate()
                                },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Share Template") },
                                onClick = {
                                    showMenu = false
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, prompt.fullPrompt)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Prompt"))
                                },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                            )
                            HorizontalDivider(color = AppTheme.extendedColors.cardBorder)
                            DropdownMenuItem(
                                text = { Text("Delete", color = AppTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = null,
                                        tint = AppTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(AppTheme.spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                JlabTagBadge(
                    text = prompt.category,
                    containerColor = AppTheme.colorScheme.primaryContainer,
                    contentColor = AppTheme.colorScheme.onPrimaryContainer
                )

                if (variables.isNotEmpty()) {
                    JlabTagBadge(
                        text = "${variables.size} Placeholders",
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
            ) {
                if (variables.isNotEmpty()) {
                    JlabPrimaryButton(
                        text = "Fill & Copy",
                        icon = Icons.Default.Tune,
                        modifier = Modifier.weight(1f),
                        onClick = onFillVariables,
                        testTag = "prompt_detail_fill_copy_button"
                    )
                }

                JlabOutlinedButton(
                    text = if (variables.isNotEmpty()) "Copy Raw" else "Copy Prompt",
                    icon = Icons.Default.CopyAll,
                    modifier = Modifier.weight(1f),
                    onClick = onCopy,
                    testTag = "prompt_detail_copy_raw_button"
                )
            }

            if (prompt.notes.isNotBlank() || prompt.purpose.isNotBlank()) {
                JlabSectionCard(
                    overline = "NOTES / PURPOSE",
                    containerColor = AppTheme.colorScheme.surfaceContainerLow,
                    testTag = "prompt_detail_notes_card"
                ) {
                    Text(
                        text = prompt.notes.ifBlank { prompt.purpose },
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            JlabSectionCard(
                overline = "TEMPLATE CONTENT",
                headerAction = {
                    Text(
                        text = "${prompt.fullPrompt.length} chars",
                        style = AppTheme.extendedTypography.caption,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                },
                testTag = "prompt_detail_template_card"
            ) {
                Surface(
                    shape = AppTheme.extendedShapes.compactCard,
                    color = AppTheme.extendedColors.codeSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = prompt.fullPrompt,
                        style = AppTheme.extendedTypography.codeMedium,
                        color = AppTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(AppTheme.spacing.compactCardPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FillVariablesDialog(
    prompt: PromptEntity,
    onDismiss: () -> Unit,
    onCopyFilled: (String) -> Unit,
    onCopyRaw: () -> Unit
) {
    val variableNames = remember(prompt.fullPrompt) { extractPromptVariables(prompt.fullPrompt) }
    val variableValues = remember { mutableStateMapOf<String, String>() }

    val liveFilledPrompt = remember(prompt.fullPrompt, variableValues.toMap()) {
        var result = prompt.fullPrompt
        for (vName in variableNames) {
            val userVal = variableValues[vName]?.trim()
            val replacement = if (!userVal.isNullOrBlank()) userVal else "{$vName}"
            result = result.replace("{$vName}", replacement)
        }
        result
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.large,
        containerColor = AppTheme.colorScheme.surfaceContainerHigh,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
                Text("Fill Prompt Variables", style = AppTheme.typography.titleMedium)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)
            ) {
                Text(
                    text = "Fill in the placeholders for '${prompt.title}':",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )

                variableNames.forEach { vName ->
                    JlabTextField(
                        value = variableValues[vName] ?: "",
                        onValueChange = { variableValues[vName] = it },
                        label = "{$vName}",
                        testTag = "prompt_var_input_$vName"
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                Text(
                    text = "LIVE PREVIEW",
                    style = AppTheme.extendedTypography.overline,
                    color = AppTheme.colorScheme.primary
                )
                Surface(
                    shape = AppTheme.extendedShapes.compactCard,
                    color = AppTheme.extendedColors.codeSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = liveFilledPrompt,
                        style = AppTheme.extendedTypography.codeSmall,
                        color = AppTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(AppTheme.spacing.smPlus),
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Copy Filled",
                onClick = { onCopyFilled(liveFilledPrompt) },
                testTag = "copy_filled_prompt_button"
            )
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onCopyRaw,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Copy Raw")
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun PromptEditorScreen(
    initialPrompt: PromptEntity?,
    existingCategories: List<String>,
    onSave: (PromptEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialPrompt?.title ?: "") }
    var fullPrompt by remember { mutableStateOf(initialPrompt?.fullPrompt ?: "") }
    var category by remember {
        mutableStateOf(
            initialPrompt?.category ?: if (existingCategories.isNotEmpty()) existingCategories.first() else "General"
        )
    }
    var customCategoryInput by remember { mutableStateOf("") }
    var isCustomCategoryMode by remember { mutableStateOf(false) }
    var tags by remember { mutableStateOf(initialPrompt?.tags ?: "") }
    var notes by remember { mutableStateOf(initialPrompt?.notes ?: "") }

    val suggestedCategories = listOf(
        "General", "Coding", "Business", "Marketing", "Research", "Writing", "Design", "Productivity"
    )
    val allCategoryOptions = remember(existingCategories) {
        (suggestedCategories + existingCategories).distinct()
    }
    val quickVariables = listOf("topic", "role", "audience", "tone", "format", "context", "instructions", "language")

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = if (initialPrompt != null) "Edit Prompt" else "Add Prompt",
                navigationIcon = Icons.Default.Close,
                onNavigationClick = onDismiss,
                testTag = "prompt_editor_top_bar",
                actions = {
                    JlabPrimaryButton(
                        text = "Save",
                        icon = Icons.Default.Check,
                        enabled = title.isNotBlank() && fullPrompt.isNotBlank(),
                        onClick = {
                            if (title.isNotBlank() && fullPrompt.isNotBlank()) {
                                val finalCategory = if (isCustomCategoryMode && customCategoryInput.isNotBlank()) {
                                    customCategoryInput.trim()
                                } else {
                                    category.trim().ifBlank { "General" }
                                }

                                val entity = initialPrompt?.copy(
                                    title = title.trim(),
                                    fullPrompt = fullPrompt.trim(),
                                    category = finalCategory,
                                    tags = tags.trim(),
                                    notes = notes.trim(),
                                    updatedAt = System.currentTimeMillis()
                                ) ?: PromptEntity(
                                    title = title.trim(),
                                    fullPrompt = fullPrompt.trim(),
                                    category = finalCategory,
                                    tags = tags.trim(),
                                    notes = notes.trim()
                                )
                                onSave(entity)
                            }
                        },
                        testTag = "prompt_editor_save_button"
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(AppTheme.spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            JlabTextField(
                value = title,
                onValueChange = { title = it },
                label = "Prompt Title *",
                placeholder = "e.g. Clean Code Reviewer",
                testTag = "prompt_title_input"
            )

            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                Text(
                    text = "CATEGORY",
                    style = AppTheme.extendedTypography.overline,
                    color = AppTheme.colorScheme.primary
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                    items(allCategoryOptions) { cat ->
                        JlabFilterChip(
                            label = cat,
                            selected = !isCustomCategoryMode && category == cat,
                            onClick = {
                                category = cat
                                isCustomCategoryMode = false
                            }
                        )
                    }
                    item {
                        JlabFilterChip(
                            label = "+ Custom",
                            selected = isCustomCategoryMode,
                            onClick = { isCustomCategoryMode = true }
                        )
                    }
                }

                if (isCustomCategoryMode) {
                    JlabTextField(
                        value = customCategoryInput,
                        onValueChange = { customCategoryInput = it },
                        label = "Enter Custom Category Name",
                        testTag = "prompt_custom_category_input"
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                Text(
                    text = "INSERT VARIABLES INTO PROMPT",
                    style = AppTheme.extendedTypography.overline,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                    items(quickVariables) { v ->
                        AssistChip(
                            onClick = { fullPrompt += " {$v}" },
                            label = { Text("+$v", style = AppTheme.extendedTypography.badge) },
                            shape = AppTheme.extendedShapes.chip,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("insert_var_$v")
                        )
                    }
                }
            }

            JlabTextField(
                value = fullPrompt,
                onValueChange = { fullPrompt = it },
                label = "Prompt Template * (${fullPrompt.length} chars)",
                placeholder = "Act as a senior engineer. Review the following {language} code for {topic}...\n\nInstructions: {instructions}",
                singleLine = false,
                minLines = 8,
                maxLines = 16,
                modifier = Modifier.heightIn(min = 200.dp),
                testTag = "prompt_template_input"
            )

            JlabTextField(
                value = tags,
                onValueChange = { tags = it },
                label = "Tags (optional)",
                placeholder = "#engineering #review #quality",
                testTag = "prompt_tags_input"
            )

            JlabTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes / Purpose (optional)",
                placeholder = "Guidance or context on when to run this prompt...",
                singleLine = false,
                minLines = 2,
                maxLines = 5,
                testTag = "prompt_notes_input"
            )
        }
    }
}
