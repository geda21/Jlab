package com.example.ui.screens.social

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.SocialContentEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme

@Composable
internal fun socialPlatformColor(platform: String): Color {
    return when (platform.trim().lowercase()) {
        "youtube" -> AppTheme.extendedColors.platformYoutube
        "linkedin" -> AppTheme.extendedColors.platformLinkedin
        "tiktok" -> AppTheme.extendedColors.platformTiktok
        "instagram" -> AppTheme.extendedColors.platformInstagram
        "x", "twitter" -> AppTheme.extendedColors.platformX
        "telegram" -> AppTheme.extendedColors.info
        else -> AppTheme.colorScheme.primary
    }
}

@Composable
internal fun socialStageColor(stage: String): Pair<Color, Color> {
    return when (stage.trim().uppercase()) {
        "IDEA" -> AppTheme.extendedColors.warningContainer to AppTheme.extendedColors.onWarningContainer
        "DRAFTING" -> AppTheme.extendedColors.violetContainer to AppTheme.extendedColors.onVioletContainer
        "READY" -> AppTheme.extendedColors.infoContainer to AppTheme.extendedColors.onInfoContainer
        "SCHEDULED" -> AppTheme.colorScheme.primaryContainer to AppTheme.colorScheme.onPrimaryContainer
        "PUBLISHED" -> AppTheme.extendedColors.successContainer to AppTheme.extendedColors.onSuccessContainer
        else -> AppTheme.colorScheme.secondaryContainer to AppTheme.colorScheme.onSecondaryContainer
    }
}

@Composable
internal fun SocialDetailScreen(
    item: SocialContentEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAdvanceStatus: (String) -> Unit,
    onBack: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val pColor = socialPlatformColor(item.platform)
    val (stBg, stFg) = socialStageColor(item.status)

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = item.title,
                onNavigationClick = onBack,
                testTag = "social_detail_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit Content",
                        onClick = onEdit,
                        testTag = "social_detail_edit_button"
                    )
                    JlabIconButton(
                        icon = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Content",
                        tint = AppTheme.colorScheme.error,
                        onClick = { showDeleteConfirm = true },
                        testTag = "social_detail_delete_button"
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
                .padding(AppTheme.spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                JlabTagBadge(
                    text = item.platform,
                    containerColor = pColor.copy(alpha = 0.16f),
                    contentColor = pColor
                )
                JlabTagBadge(
                    text = item.contentType,
                    containerColor = AppTheme.colorScheme.secondaryContainer,
                    contentColor = AppTheme.colorScheme.onSecondaryContainer
                )
                JlabTagBadge(
                    text = item.status,
                    containerColor = stBg,
                    contentColor = stFg
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs)) {
                Text(
                    text = "HOOK / TITLE",
                    style = AppTheme.extendedTypography.overline,
                    color = AppTheme.colorScheme.primary
                )
                Text(
                    text = item.title,
                    style = AppTheme.typography.titleLarge,
                    color = AppTheme.colorScheme.onSurface
                )
            }

            if (item.scriptOrCaption.isNotBlank()) {
                JlabSectionCard(
                    overline = "SCRIPT / CAPTION",
                    headerAction = {
                        Text(
                            text = "${item.scriptOrCaption.length} chars",
                            style = AppTheme.extendedTypography.caption,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    testTag = "social_detail_script_card"
                ) {
                    Text(
                        text = item.scriptOrCaption,
                        style = AppTheme.typography.bodyLarge,
                        color = AppTheme.colorScheme.onSurface
                    )
                }
            }

            if (item.callToAction.isNotBlank()) {
                JlabSectionCard(
                    overline = "CALL TO ACTION (CTA)",
                    containerColor = AppTheme.colorScheme.surfaceContainerLow,
                    testTag = "social_detail_cta_card"
                ) {
                    Text(
                        text = item.callToAction,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = "ADVANCE PIPELINE STATUS",
                style = AppTheme.extendedTypography.overline,
                color = AppTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                listOf("IDEA", "DRAFTING", "READY", "SCHEDULED", "PUBLISHED").forEach { st ->
                    JlabOutlinedButton(
                        text = st,
                        enabled = !item.status.equals(st, ignoreCase = true),
                        onClick = { onAdvanceStatus(st) },
                        testTag = "social_advance_${st.lowercase()}"
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        JlabConfirmDialog(
            title = "Delete Content Draft?",
            message = "Delete \"${item.title}\"?",
            confirmText = "Delete",
            isDestructive = true,
            icon = Icons.Default.DeleteOutline,
            onConfirm = {
                showDeleteConfirm = false
                onDelete()
            },
            onDismiss = { showDeleteConfirm = false },
            testTag = "social_delete_confirm_dialog"
        )
    }
}

@Composable
internal fun SocialEditorScreen(
    initialItem: SocialContentEntity?,
    onSave: (SocialContentEntity) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var platform by remember { mutableStateOf(initialItem?.platform ?: "LinkedIn") }
    var contentType by remember { mutableStateOf(initialItem?.contentType ?: "Post") }
    var status by remember { mutableStateOf(initialItem?.status ?: "IDEA") }
    var idea by remember { mutableStateOf(initialItem?.idea ?: "") }
    var script by remember { mutableStateOf(initialItem?.scriptOrCaption ?: "") }
    var cta by remember { mutableStateOf(initialItem?.callToAction ?: "") }
    var tags by remember { mutableStateOf(initialItem?.tags ?: "") }

    val platforms = listOf("LinkedIn", "YouTube", "TikTok", "X", "Instagram", "Telegram", "Facebook", "Other")
    val contentTypes = listOf("Post", "Short Video", "Long Video", "Carousel", "Thread", "Story", "Live", "Educational", "Promo")
    val statuses = listOf("IDEA", "DRAFTING", "READY", "SCHEDULED", "PUBLISHED")

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = if (initialItem == null) "New Content" else "Edit Content",
                onNavigationClick = onCancel,
                testTag = "social_editor_top_bar",
                actions = {
                    JlabPrimaryButton(
                        text = "Save Draft",
                        icon = Icons.Default.Check,
                        enabled = title.isNotBlank(),
                        onClick = {
                            if (title.isNotBlank()) {
                                val entity = (initialItem ?: SocialContentEntity(title = title)).copy(
                                    title = title.trim(),
                                    platform = platform,
                                    contentType = contentType,
                                    status = status,
                                    idea = idea.trim(),
                                    scriptOrCaption = script.trim(),
                                    callToAction = cta.trim(),
                                    tags = tags.trim(),
                                    updatedAt = System.currentTimeMillis()
                                )
                                onSave(entity)
                            }
                        },
                        testTag = "social_editor_save_button"
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
                .padding(AppTheme.spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            JlabTextField(
                value = title,
                onValueChange = { title = it },
                label = "Hook / Working Title *",
                testTag = "social_title_input"
            )

            Text(
                text = "PLATFORM",
                style = AppTheme.extendedTypography.overline,
                color = AppTheme.colorScheme.primary
            )
            JlabFilterChipRow(
                options = platforms,
                selectedOption = platform,
                onSelect = { platform = it },
                contentPadding = PaddingValues(0.dp)
            )

            Text(
                text = "CONTENT FORMAT",
                style = AppTheme.extendedTypography.overline,
                color = AppTheme.colorScheme.primary
            )
            JlabFilterChipRow(
                options = contentTypes,
                selectedOption = contentType,
                onSelect = { contentType = it },
                contentPadding = PaddingValues(0.dp)
            )

            Text(
                text = "PIPELINE STAGE",
                style = AppTheme.extendedTypography.overline,
                color = AppTheme.colorScheme.primary
            )
            JlabFilterChipRow(
                options = statuses,
                selectedOption = status,
                onSelect = { status = it },
                contentPadding = PaddingValues(0.dp)
            )

            JlabTextField(
                value = script,
                onValueChange = { script = it },
                label = "Script / Full Caption Body",
                singleLine = false,
                minLines = 6,
                maxLines = 14,
                modifier = Modifier.heightIn(min = 150.dp),
                testTag = "social_script_input"
            )

            JlabTextField(
                value = cta,
                onValueChange = { cta = it },
                label = "Call to Action (CTA)",
                placeholder = "e.g. Comment 'GUIDE' below to receive the PDF",
                testTag = "social_cta_input"
            )

            JlabTextField(
                value = tags,
                onValueChange = { tags = it },
                label = "Hashtags / Keywords",
                testTag = "social_tags_input"
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.xl))
        }
    }
}
