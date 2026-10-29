package com.example.ui.screens.knowledge

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.data.local.entity.KnowledgeEntity
import com.example.data.local.entity.LearningEntity
import com.example.data.local.entity.ProjectEntity
import com.example.ui.components.JlabConfirmDialog
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSectionCard
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTonalButton
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme
import org.json.JSONArray

@Composable
internal fun KnowledgeDetailScreen(
    item: KnowledgeEntity,
    projectName: String?,
    courseName: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    val keyPoints = remember(item.keyPointsJson) {
        try {
            val arr = JSONArray(item.keyPointsJson)
            val list = mutableListOf<String>()
            for (i in 0 until arr.length()) list.add(arr.getString(i))
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = item.title,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                testTag = "knowledge_detail_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit knowledge",
                        onClick = onEdit,
                        testTag = "knowledge_detail_edit"
                    )
                    JlabIconButton(
                        icon = Icons.Default.DeleteOutline,
                        contentDescription = "Delete knowledge",
                        tint = AppTheme.extendedColors.danger,
                        onClick = { showDeleteConfirm = true },
                        testTag = "knowledge_detail_delete"
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                JlabTagBadge(
                    text = item.category,
                    containerColor = AppTheme.colorScheme.primaryContainer,
                    contentColor = AppTheme.colorScheme.onPrimaryContainer
                )

                if (projectName != null) {
                    JlabTagBadge(
                        text = projectName,
                        icon = Icons.Default.Folder,
                        containerColor = AppTheme.colorScheme.secondaryContainer,
                        contentColor = AppTheme.colorScheme.onSecondaryContainer
                    )
                }

                if (courseName != null) {
                    JlabTagBadge(
                        text = courseName,
                        icon = Icons.Default.School,
                        containerColor = AppTheme.colorScheme.tertiaryContainer,
                        contentColor = AppTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            if (item.summary.isNotBlank()) {
                JlabSectionCard(
                    overline = "SUMMARY",
                    containerColor = AppTheme.colorScheme.surfaceContainerLow,
                    testTag = "knowledge_summary_card"
                ) {
                    Text(
                        text = item.summary,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                }
            }

            if (keyPoints.isNotEmpty()) {
                JlabSectionCard(
                    overline = "KEY TAKEAWAYS & PRINCIPLES",
                    testTag = "knowledge_key_points_card"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                        keyPoints.forEachIndexed { idx, pt ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(AppTheme.spacing.iconMd)
                                        .clip(CircleShape)
                                        .background(AppTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = AppTheme.extendedTypography.badge,
                                        color = AppTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    text = pt,
                                    style = AppTheme.typography.bodyMedium,
                                    color = AppTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            if (item.content.isNotBlank()) {
                JlabSectionCard(
                    overline = "MAIN KNOWLEDGE BODY",
                    testTag = "knowledge_body_card"
                ) {
                    Text(
                        text = item.content,
                        style = AppTheme.typography.bodyLarge,
                        color = AppTheme.colorScheme.onSurface
                    )
                }
            }

            if (item.sourceReference.isNotBlank()) {
                HorizontalDivider(color = AppTheme.extendedColors.cardBorder)
                Text(
                    text = "SOURCE / REFERENCE: ${item.sourceReference}",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.xxl))
        }
    }

    if (showDeleteConfirm) {
        JlabConfirmDialog(
            title = "Delete Knowledge Record?",
            message = "Are you sure you want to permanently delete \"${item.title}\"?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                showDeleteConfirm = false
                onDelete()
            },
            onDismiss = { showDeleteConfirm = false },
            testTag = "delete_knowledge_dialog"
        )
    }
}

@Composable
internal fun KnowledgeEditorScreen(
    initialItem: KnowledgeEntity?,
    projects: List<ProjectEntity>,
    courses: List<LearningEntity>,
    onSave: (KnowledgeEntity) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "Technical") }
    var summary by remember { mutableStateOf(initialItem?.summary ?: "") }
    var content by remember { mutableStateOf(initialItem?.content ?: "") }
    var sourceRef by remember { mutableStateOf(initialItem?.sourceReference ?: "") }
    var tags by remember { mutableStateOf(initialItem?.tags ?: "") }
    var relatedProjectId by remember { mutableStateOf(initialItem?.relatedProjectId) }
    var relatedCourseId by remember { mutableStateOf(initialItem?.relatedCourseId) }

    val initialKeyPoints = remember(initialItem?.keyPointsJson) {
        try {
            val arr = JSONArray(initialItem?.keyPointsJson ?: "[]")
            val list = mutableListOf<String>()
            for (i in 0 until arr.length()) list.add(arr.getString(i))
            list
        } catch (_: Exception) {
            mutableListOf()
        }
    }
    val keyPoints = remember { mutableStateListOf<String>().apply { addAll(initialKeyPoints) } }
    var newPointText by remember { mutableStateOf("") }

    val categories = listOf(
        "Technical",
        "Business",
        "Learning",
        "Reference",
        "Process",
        "Research",
        "Idea",
        "Other"
    )

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = if (initialItem == null) "New Knowledge" else "Edit Knowledge",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onCancel,
                testTag = "knowledge_editor_top_bar",
                actions = {
                    JlabPrimaryButton(
                        text = "Save",
                        enabled = title.isNotBlank(),
                        onClick = {
                            if (title.isNotBlank()) {
                                val keyPointsArray = JSONArray()
                                keyPoints.forEach { keyPointsArray.put(it) }

                                val entity = (initialItem ?: KnowledgeEntity(title = title)).copy(
                                    title = title.trim(),
                                    category = category,
                                    summary = summary.trim(),
                                    content = content.trim(),
                                    keyPointsJson = keyPointsArray.toString(),
                                    sourceReference = sourceRef.trim(),
                                    tags = tags.trim(),
                                    relatedProjectId = relatedProjectId,
                                    relatedCourseId = relatedCourseId,
                                    updatedAt = System.currentTimeMillis()
                                )
                                onSave(entity)
                            }
                        },
                        modifier = Modifier.padding(end = AppTheme.spacing.sm),
                        testTag = "knowledge_editor_save"
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
                .testTag("knowledge_editor_form"),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.mdPlus)
        ) {
            JlabTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title *",
                singleLine = true,
                testTag = "knowledge_title_input"
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

            if (projects.isNotEmpty()) {
                Text(
                    text = "LINKED PROJECT (OPTIONAL)",
                    style = AppTheme.extendedTypography.overline,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                val projectOptions = listOf("NONE") + projects.map { it.id }
                JlabFilterChipRow(
                    options = projectOptions,
                    selectedOption = relatedProjectId ?: "NONE",
                    onSelect = { selected ->
                        relatedProjectId = if (selected == "NONE") null else selected
                    },
                    contentPadding = PaddingValues(AppTheme.spacing.none),
                    labelFormatter = { id ->
                        if (id == "NONE") "None" else projects.find { it.id == id }?.name ?: id
                    }
                )
            }

            if (courses.isNotEmpty()) {
                Text(
                    text = "LINKED COURSE (OPTIONAL)",
                    style = AppTheme.extendedTypography.overline,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                val courseOptions = listOf("NONE") + courses.map { it.id }
                JlabFilterChipRow(
                    options = courseOptions,
                    selectedOption = relatedCourseId ?: "NONE",
                    onSelect = { selected ->
                        relatedCourseId = if (selected == "NONE") null else selected
                    },
                    contentPadding = PaddingValues(AppTheme.spacing.none),
                    labelFormatter = { id ->
                        if (id == "NONE") "None" else courses.find { it.id == id }?.title ?: id
                    }
                )
            }

            JlabTextField(
                value = summary,
                onValueChange = { summary = it },
                label = "Summary / Executive Takeaway",
                singleLine = false,
                minLines = 2,
                maxLines = 4,
                testTag = "knowledge_summary_input"
            )

            Text(
                text = "KEY TAKEAWAYS & PRINCIPLES",
                style = AppTheme.extendedTypography.overline,
                color = AppTheme.colorScheme.onSurfaceVariant
            )

            keyPoints.forEachIndexed { index, pt ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = AppTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colorScheme.primary
                    )
                    Text(
                        text = pt,
                        modifier = Modifier.weight(1f),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                    JlabIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Remove key point",
                        onClick = { keyPoints.removeAt(index) },
                        testTag = "knowledge_remove_point_$index"
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
            ) {
                JlabTextField(
                    value = newPointText,
                    onValueChange = { newPointText = it },
                    placeholder = "Add key point...",
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    testTag = "knowledge_new_point_input"
                )
                JlabTonalButton(
                    text = "Add",
                    enabled = newPointText.isNotBlank(),
                    onClick = {
                        if (newPointText.isNotBlank()) {
                            keyPoints.add(newPointText.trim())
                            newPointText = ""
                        }
                    },
                    testTag = "knowledge_add_point_button"
                )
            }

            JlabTextField(
                value = content,
                onValueChange = { content = it },
                label = "Detailed Knowledge / Framework Content",
                singleLine = false,
                minLines = 6,
                maxLines = 16,
                testTag = "knowledge_content_input"
            )

            JlabTextField(
                value = sourceRef,
                onValueChange = { sourceRef = it },
                label = "Source / Book / Author / URL",
                singleLine = true,
                testTag = "knowledge_source_input"
            )

            JlabTextField(
                value = tags,
                onValueChange = { tags = it },
                label = "Tags (comma separated)",
                singleLine = true,
                testTag = "knowledge_tags_input"
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.xxl))
        }
    }
}
