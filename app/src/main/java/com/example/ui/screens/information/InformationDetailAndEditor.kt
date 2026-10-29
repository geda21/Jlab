package com.example.ui.screens.information

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.local.entity.InformationEntity
import com.example.ui.components.JlabConfirmDialog
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSectionCard
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme

@Composable
internal fun InformationDetailScreen(
    item: InformationEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyValue: (String) -> Unit,
    onBack: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = item.title,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                testTag = "information_detail_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit information",
                        onClick = onEdit,
                        testTag = "information_detail_edit"
                    )
                    JlabIconButton(
                        icon = Icons.Default.DeleteOutline,
                        contentDescription = "Delete information",
                        tint = AppTheme.extendedColors.danger,
                        onClick = { showDeleteConfirm = true },
                        testTag = "information_detail_delete"
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
                    vertical = AppTheme.spacing.screenVertical
                ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap)
        ) {
            JlabTagBadge(
                text = item.category,
                containerColor = AppTheme.colorScheme.secondaryContainer,
                contentColor = AppTheme.colorScheme.onSecondaryContainer
            )

            if (item.summary.isNotBlank()) {
                JlabSectionCard(
                    overline = "SUMMARY",
                    containerColor = AppTheme.colorScheme.surfaceContainerLow,
                    testTag = "information_summary_card"
                ) {
                    Text(
                        text = item.summary,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                }
            }

            JlabSectionCard(
                overline = "VALUE / REFERENCE DATA",
                headerAction = {
                    JlabIconButton(
                        icon = Icons.Default.ContentCopy,
                        contentDescription = "Copy reference value",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { onCopyValue(item.information) },
                        testTag = "information_copy_value"
                    )
                },
                testTag = "information_value_card"
            ) {
                Surface(
                    shape = AppTheme.extendedShapes.compactCard,
                    color = AppTheme.extendedColors.codeSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.information.ifBlank { "No reference content saved." },
                        style = AppTheme.extendedTypography.codeMedium,
                        color = AppTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(AppTheme.spacing.compactCardPadding)
                    )
                }
            }

            if (item.sourceText.isNotBlank() || item.tags.isNotBlank()) {
                JlabSectionCard(
                    overline = "METADATA",
                    testTag = "information_metadata_card"
                ) {
                    if (item.sourceText.isNotBlank()) {
                        Text(
                            text = "Source / Environment: ${item.sourceText}",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (item.sourceText.isNotBlank() && item.tags.isNotBlank()) {
                        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                    }
                    if (item.tags.isNotBlank()) {
                        Text(
                            text = "Tags: ${item.tags}",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.xxl))
        }
    }

    if (showDeleteConfirm) {
        JlabConfirmDialog(
            title = "Delete Information Record?",
            message = "Are you sure you want to delete \"${item.title}\"?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                showDeleteConfirm = false
                onDelete()
            },
            onDismiss = { showDeleteConfirm = false },
            testTag = "delete_information_dialog"
        )
    }
}

@Composable
internal fun InformationEditorScreen(
    initialItem: InformationEntity?,
    onSave: (InformationEntity) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "Credentials") }
    var summary by remember { mutableStateOf(initialItem?.summary ?: "") }
    var information by remember { mutableStateOf(initialItem?.information ?: "") }
    var sourceText by remember { mutableStateOf(initialItem?.sourceText ?: "") }
    var tags by remember { mutableStateOf(initialItem?.tags ?: "") }

    val categories = listOf(
        "Credentials",
        "Configuration",
        "Service",
        "Account",
        "Reference",
        "Other"
    )

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = if (initialItem == null) "New Information" else "Edit Information",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onCancel,
                testTag = "information_editor_top_bar",
                actions = {
                    JlabPrimaryButton(
                        text = "Save",
                        enabled = title.isNotBlank(),
                        onClick = {
                            if (title.isNotBlank()) {
                                val entity = (initialItem ?: InformationEntity(title = title)).copy(
                                    title = title.trim(),
                                    category = category,
                                    summary = summary.trim(),
                                    information = information.trim(),
                                    sourceText = sourceText.trim(),
                                    tags = tags.trim(),
                                    updatedAt = System.currentTimeMillis()
                                )
                                onSave(entity)
                            }
                        },
                        modifier = Modifier.padding(end = AppTheme.spacing.sm),
                        testTag = "information_editor_save"
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
                    vertical = AppTheme.spacing.screenVertical
                )
                .testTag("information_editor_form"),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.mdPlus)
        ) {
            JlabTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title *",
                singleLine = true,
                testTag = "information_title_input"
            )

            Text(
                text = "CATEGORY",
                style = AppTheme.extendedTypography.overline,
                color = AppTheme.colorScheme.onSurfaceVariant
            )

            JlabFilterChipRow(
                options = categories,
                selectedOption = category,
                onSelect = { category = it },
                contentPadding = PaddingValues(AppTheme.spacing.none)
            )

            JlabTextField(
                value = summary,
                onValueChange = { summary = it },
                label = "Brief Purpose / Summary",
                singleLine = true,
                testTag = "information_summary_input"
            )

            JlabTextField(
                value = information,
                onValueChange = { information = it },
                label = "Value / Content / Setup Details *",
                singleLine = false,
                minLines = 5,
                maxLines = 12,
                testTag = "information_content_input"
            )

            JlabTextField(
                value = sourceText,
                onValueChange = { sourceText = it },
                label = "Source / System / Host",
                singleLine = true,
                testTag = "information_source_input"
            )

            JlabTextField(
                value = tags,
                onValueChange = { tags = it },
                label = "Tags (comma separated)",
                singleLine = true,
                testTag = "information_tags_input"
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.xxl))
        }
    }
}
