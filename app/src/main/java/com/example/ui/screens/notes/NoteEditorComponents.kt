package com.example.ui.screens.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.example.data.local.entity.NoteEntity
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabTextField
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HighlightInk
import com.example.ui.theme.JetBrainsMonoFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun NoteEditorFormattingBar(
    isBoldActive: Boolean,
    isItalicActive: Boolean,
    isUnderlineActive: Boolean,
    isStrikethroughActive: Boolean,
    isHighlightActive: Boolean,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    onToggleUnderline: () -> Unit,
    onToggleStrikethrough: () -> Unit,
    onApplyHighlight: (String?) -> Unit,
    onApplyBlockStyle: (String?) -> Unit,
    onInsertLinePrefix: (String) -> Unit
) {
    var showHighlightPicker by remember { mutableStateOf(false) }
    var showStyleMenu by remember { mutableStateOf(false) }
    var showListMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding(),
        color = AppTheme.colorScheme.surface,
        border = BorderStroke(AppTheme.spacing.borderThin, AppTheme.extendedColors.cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = AppTheme.spacing.sm, vertical = AppTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
        ) {
            FormatToggleButton(
                label = "B",
                contentDescription = "Bold",
                isActive = isBoldActive,
                fontWeight = FontWeight.Bold,
                onClick = onToggleBold,
                testTag = "note_format_bold"
            )

            FormatToggleButton(
                label = "I",
                contentDescription = "Italic",
                isActive = isItalicActive,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                onClick = onToggleItalic,
                testTag = "note_format_italic"
            )

            FormatToggleButton(
                label = "U",
                contentDescription = "Underline",
                isActive = isUnderlineActive,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
                onClick = onToggleUnderline,
                testTag = "note_format_underline"
            )

            // Highlight Marker
            Box {
                Surface(
                    shape = AppTheme.extendedShapes.chip,
                    color = if (isHighlightActive) RichTextDocument.SwatchYellow.copy(alpha = 0.55f) else Color.Transparent,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(AppTheme.extendedShapes.chip)
                        .clickable { showHighlightPicker = true }
                        .testTag("note_format_highlight")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = AppTheme.spacing.sm, vertical = AppTheme.spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BorderColor,
                            contentDescription = "Highlight Color",
                            modifier = Modifier.size(AppTheme.spacing.iconSm),
                            tint = if (isHighlightActive) HighlightInk else AppTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(AppTheme.spacing.iconSm),
                            tint = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                DropdownMenu(
                    expanded = showHighlightPicker,
                    onDismissRequest = { showHighlightPicker = false }
                ) {
                    Text(
                        text = "Highlight Color",
                        style = AppTheme.extendedTypography.overline,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = AppTheme.spacing.md,
                            vertical = AppTheme.spacing.xs
                        )
                    )
                    Row(
                        modifier = Modifier.padding(
                            horizontal = AppTheme.spacing.smPlus,
                            vertical = AppTheme.spacing.xs
                        ),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
                    ) {
                        ColorSwatch(
                            color = AppTheme.colorScheme.surfaceContainerHighest,
                            hasBorder = true,
                            contentDescription = "Clear highlight",
                            onClick = {
                                onApplyHighlight(null)
                                showHighlightPicker = false
                            }
                        )
                        ColorSwatch(
                            color = RichTextDocument.SwatchYellow,
                            contentDescription = "Yellow highlight",
                            onClick = {
                                onApplyHighlight("#FFF176")
                                showHighlightPicker = false
                            }
                        )
                        ColorSwatch(
                            color = RichTextDocument.SwatchGreen,
                            contentDescription = "Green highlight",
                            onClick = {
                                onApplyHighlight("#A5D6A7")
                                showHighlightPicker = false
                            }
                        )
                        ColorSwatch(
                            color = RichTextDocument.SwatchBlue,
                            contentDescription = "Blue highlight",
                            onClick = {
                                onApplyHighlight("#90CAF9")
                                showHighlightPicker = false
                            }
                        )
                        ColorSwatch(
                            color = RichTextDocument.SwatchPink,
                            contentDescription = "Pink highlight",
                            onClick = {
                                onApplyHighlight("#F48FB1")
                                showHighlightPicker = false
                            }
                        )
                        ColorSwatch(
                            color = RichTextDocument.SwatchPurple,
                            contentDescription = "Purple highlight",
                            onClick = {
                                onApplyHighlight("#CE93D8")
                                showHighlightPicker = false
                            }
                        )
                    }
                }
            }

            VerticalDivider(
                modifier = Modifier
                    .height(AppTheme.spacing.lg)
                    .padding(horizontal = AppTheme.spacing.xxs),
                color = AppTheme.extendedColors.cardBorder
            )

            // Block Text Style (Aa)
            Box {
                Surface(
                    shape = AppTheme.extendedShapes.chip,
                    color = Color.Transparent,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(AppTheme.extendedShapes.chip)
                        .clickable { showStyleMenu = true }
                        .testTag("note_format_block_style")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = AppTheme.spacing.sm, vertical = AppTheme.spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Aa",
                            style = AppTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Paragraph style",
                            modifier = Modifier.size(AppTheme.spacing.iconSm)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showStyleMenu,
                    onDismissRequest = { showStyleMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Normal Text", style = AppTheme.typography.bodyMedium) },
                        onClick = {
                            onApplyBlockStyle(null)
                            showStyleMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Heading 1", style = AppTheme.typography.titleLarge) },
                        onClick = {
                            onApplyBlockStyle(DocumentSpan.TYPE_HEADING_1)
                            showStyleMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Heading 2", style = AppTheme.typography.titleMedium) },
                        onClick = {
                            onApplyBlockStyle(DocumentSpan.TYPE_HEADING_2)
                            showStyleMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Heading 3", style = AppTheme.typography.titleSmall) },
                        onClick = {
                            onApplyBlockStyle(DocumentSpan.TYPE_HEADING_3)
                            showStyleMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Quote",
                                style = AppTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
                            )
                        },
                        onClick = {
                            onApplyBlockStyle(DocumentSpan.TYPE_QUOTE)
                            showStyleMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Code Block",
                                style = AppTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMonoFontFamily)
                            )
                        },
                        onClick = {
                            onApplyBlockStyle(DocumentSpan.TYPE_CODE)
                            showStyleMenu = false
                        }
                    )
                }
            }

            // Lists
            Box {
                JlabIconButton(
                    icon = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Insert List",
                    onClick = { showListMenu = true },
                    testTag = "note_format_lists"
                )

                DropdownMenu(
                    expanded = showListMenu,
                    onDismissRequest = { showListMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Bullet List") },
                        onClick = {
                            onInsertLinePrefix("• ")
                            showListMenu = false
                        },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Numbered List") },
                        onClick = {
                            onInsertLinePrefix("1. ")
                            showListMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.FormatListNumbered, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Checklist") },
                        onClick = {
                            onInsertLinePrefix("- [ ] ")
                            showListMenu = false
                        },
                        leadingIcon = { Icon(Icons.Outlined.CheckBox, contentDescription = null) }
                    )
                }
            }

            // Strikethrough
            JlabIconButton(
                icon = Icons.Default.FormatStrikethrough,
                contentDescription = "Strikethrough",
                onClick = onToggleStrikethrough,
                tint = if (isStrikethroughActive) AppTheme.colorScheme.primary else AppTheme.colorScheme.onSurface,
                testTag = "note_format_strikethrough"
            )

            // Divider
            JlabIconButton(
                icon = Icons.Default.HorizontalRule,
                contentDescription = "Insert Divider",
                onClick = { onInsertLinePrefix("\n---\n") },
                testTag = "note_format_divider"
            )
        }
    }
}

@Composable
private fun FormatToggleButton(
    label: String,
    contentDescription: String,
    isActive: Boolean,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = AppTheme.extendedShapes.chip,
        color = if (isActive) AppTheme.colorScheme.primaryContainer else Color.Transparent,
        border = if (isActive) BorderStroke(AppTheme.spacing.borderThin, AppTheme.colorScheme.primary) else null,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .size(AppTheme.spacing.avatarMd)
            .clip(AppTheme.extendedShapes.chip)
            .clickable(onClickLabel = contentDescription, onClick = onClick)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = AppTheme.typography.titleMedium.copy(
                    fontWeight = fontWeight ?: FontWeight.Normal,
                    fontStyle = fontStyle,
                    textDecoration = textDecoration,
                    color = if (isActive) AppTheme.colorScheme.onPrimaryContainer else AppTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    hasBorder: Boolean = false,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clickable(onClickLabel = contentDescription, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(AppTheme.spacing.avatarSm)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            if (hasBorder) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(AppTheme.spacing.iconXs),
                    tint = AppTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteCategoryBottomSheet(
    currentCategory: String,
    existingCategories: List<String>,
    onSelectCategory: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newCategoryName by remember { mutableStateOf("") }
    var showAddCustomCategory by remember { mutableStateOf(false) }

    val allCategories = remember(existingCategories) {
        (listOf("Personal", "Learning", "Business", "Development", "Ideas", "Reference") + existingCategories).distinct()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        shape = AppTheme.extendedShapes.sheetTop,
        containerColor = AppTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = AppTheme.spacing.sheetHorizontal,
                    end = AppTheme.spacing.sheetHorizontal,
                    bottom = AppTheme.spacing.sheetBottom
                ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)
        ) {
            Text(
                text = "Select Category",
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colorScheme.onSurface
            )

            allCategories.forEach { cat ->
                val isSelected = cat.equals(currentCategory, ignoreCase = true)
                JlabCard(
                    isSelected = isSelected,
                    shape = AppTheme.extendedShapes.compactCard,
                    contentPadding = PaddingValues(
                        horizontal = AppTheme.spacing.md,
                        vertical = AppTheme.spacing.smPlus
                    ),
                    onClick = { onSelectCategory(cat) },
                    testTag = "note_category_option_${cat.lowercase()}"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = cat,
                            style = AppTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AppTheme.colorScheme.primary else AppTheme.colorScheme.onSurface
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.primary,
                                modifier = Modifier.size(AppTheme.spacing.iconSm)
                            )
                        }
                    }
                }
            }

            if (!showAddCustomCategory) {
                TextButton(
                    onClick = { showAddCustomCategory = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("add_custom_category_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(AppTheme.spacing.iconSm)
                    )
                    Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                    Text("Add Custom Category")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    JlabTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        placeholder = "Category name",
                        modifier = Modifier.weight(1f),
                        testTag = "custom_category_input"
                    )
                    JlabPrimaryButton(
                        text = "Use",
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                onSelectCategory(newCategoryName.trim())
                            }
                        },
                        enabled = newCategoryName.isNotBlank(),
                        testTag = "use_custom_category_button"
                    )
                }
            }
        }
    }
}

@Composable
internal fun NoteInfoDialog(
    plainText: String,
    category: String,
    initialNote: NoteEntity?,
    onDismiss: () -> Unit
) {
    val wordCount = if (plainText.isBlank()) 0 else plainText.trim().split(Regex("\\s+")).size
    val charCount = plainText.length
    val createdDateStr = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
        .format(Date(initialNote?.createdAt ?: System.currentTimeMillis()))
    val updatedDateStr = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
        .format(Date(initialNote?.updatedAt ?: System.currentTimeMillis()))

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.large,
        containerColor = AppTheme.colorScheme.surfaceContainerHigh,
        title = { Text("Note Details", style = AppTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                Text("• Words: $wordCount", style = AppTheme.typography.bodyMedium)
                Text("• Characters: $charCount", style = AppTheme.typography.bodyMedium)
                Text("• Category: $category", style = AppTheme.typography.bodyMedium)
                Text(
                    "• Created: $createdDateStr",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "• Last Modified: $updatedDateStr",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
internal fun NoteUnsavedChangesDialog(
    onSaveAndExit: () -> Unit,
    onDiscard: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        shape = AppTheme.shapes.large,
        containerColor = AppTheme.colorScheme.surfaceContainerHigh,
        title = { Text("Unsaved changes", style = AppTheme.typography.titleLarge) },
        text = { Text("Save this note before leaving?", style = AppTheme.typography.bodyMedium) },
        confirmButton = {
            JlabPrimaryButton(
                text = "Save",
                onClick = onSaveAndExit,
                testTag = "note_unsaved_save_button"
            )
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Cancel")
                }
                TextButton(
                    onClick = onDiscard,
                    colors = ButtonDefaults.textButtonColors(contentColor = AppTheme.colorScheme.error),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("note_unsaved_discard_button")
                ) {
                    Text("Discard")
                }
            }
        }
    )
}
