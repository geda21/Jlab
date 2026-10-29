package com.example.ui.screens.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.entity.BusinessEntity
import com.example.data.local.entity.FileAttachmentEntity
import com.example.data.local.entity.LearningEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.ProjectEntity
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabOutlinedButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.theme.AppTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun NativeImageViewerDialog(
    file: FileAttachmentEntity,
    localFile: File,
    onDismiss: () -> Unit,
    onOpenInfo: () -> Unit,
    onRename: () -> Unit,
    onAttachTo: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showMoreMenu by remember { mutableStateOf(false) }

    val scrimColor = AppTheme.extendedColors.mediaScrim
    val overlayFg = AppTheme.extendedColors.onMediaOverlay

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor)
                .testTag("native_image_viewer")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (scale > 1.2f) {
                                    scale = 1f
                                    offsetX = 0f
                                    offsetY = 0f
                                } else {
                                    scale = 2.5f
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            if (scale > 1f) {
                                offsetX += pan.x
                                offsetY += pan.y
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (localFile.exists()) {
                    AsyncImage(
                        model = localFile,
                        contentDescription = file.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY
                            ),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = "Image not found on device storage",
                        style = AppTheme.typography.bodyMedium,
                        color = overlayFg
                    )
                }
            }

            // Top Bar Overlay
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding(),
                color = scrimColor.copy(alpha = 0.72f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.spacing.sm,
                            vertical = AppTheme.spacing.compactGap
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    JlabIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Close viewer",
                        tint = overlayFg,
                        onClick = onDismiss,
                        testTag = "viewer_close"
                    )

                    Text(
                        text = formatHumanFileName(file.name, file.createdAt),
                        style = AppTheme.typography.titleMedium,
                        color = overlayFg,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = AppTheme.spacing.sm)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        JlabIconButton(
                            icon = Icons.Default.Share,
                            contentDescription = "Share file",
                            tint = overlayFg,
                            onClick = { shareFile(context, localFile, file.mimeType) },
                            testTag = "viewer_share"
                        )

                        JlabIconButton(
                            icon = Icons.Outlined.Info,
                            contentDescription = "File details",
                            tint = overlayFg,
                            onClick = onOpenInfo,
                            testTag = "viewer_info"
                        )

                        Box {
                            JlabIconButton(
                                icon = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = overlayFg,
                                onClick = { showMoreMenu = true },
                                testTag = "viewer_more"
                            )
                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Rename") },
                                    onClick = {
                                        showMoreMenu = false
                                        onRename()
                                    },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Attach To...") },
                                    onClick = {
                                        showMoreMenu = false
                                        onAttachTo()
                                    },
                                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Open in External App") },
                                    onClick = {
                                        showMoreMenu = false
                                        openInExternalApp(context, localFile, file.mimeType)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Delete", color = AppTheme.extendedColors.danger) },
                                    onClick = {
                                        showMoreMenu = false
                                        onDelete()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = AppTheme.extendedColors.danger
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Bar Overlay
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                color = scrimColor.copy(alpha = 0.72f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.spacing.screenHorizontal,
                            vertical = AppTheme.spacing.sm
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${file.sizeBytes / 1024} KB • Double tap to zoom",
                        style = AppTheme.typography.bodySmall,
                        color = overlayFg.copy(alpha = 0.78f)
                    )

                    if (scale > 1.05f) {
                        TextButton(
                            onClick = {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Text(
                                text = "Reset Zoom",
                                style = AppTheme.typography.labelLarge,
                                color = overlayFg,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun FileDetailDialog(
    file: FileAttachmentEntity,
    localFile: File,
    projects: List<ProjectEntity>,
    learningItems: List<LearningEntity>,
    businesses: List<BusinessEntity>,
    notes: List<NoteEntity>,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onAttachTo: () -> Unit,
    onMoveCategory: (String) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var showCategoryMenu by remember { mutableStateOf(false) }

    val linkedItemName = remember(file, projects, learningItems, businesses, notes) {
        when {
            file.projectId != null -> projects.find { it.id == file.projectId }?.name?.let { "Project: $it" }
            file.learningId != null -> learningItems.find { it.id == file.learningId }?.title?.let { "Course: $it" }
            file.businessId != null -> businesses.find { it.id == file.businessId }?.name?.let { "Business: $it" }
            file.noteId != null -> notes.find { it.id == file.noteId }?.title?.let { "Note: $it" }
            else -> "Unattached"
        } ?: "Unattached"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = formatHumanFileName(file.name, file.createdAt),
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)) {
                DetailRow(label = "Category", value = file.category)
                DetailRow(label = "Size", value = "${file.sizeBytes / 1024} KB")
                val dateStr = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault()).format(Date(file.createdAt))
                DetailRow(label = "Imported", value = dateStr)
                DetailRow(label = "Linked To", value = linkedItemName)
                DetailRow(label = "Raw Path", value = localFile.name)

                Spacer(modifier = Modifier.height(AppTheme.spacing.xs))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    JlabOutlinedButton(
                        text = "Rename",
                        onClick = onRename,
                        modifier = Modifier.weight(1f),
                        testTag = "file_detail_rename"
                    )
                    JlabOutlinedButton(
                        text = "Attach To",
                        onClick = onAttachTo,
                        modifier = Modifier.weight(1f),
                        testTag = "file_detail_attach"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        JlabOutlinedButton(
                            text = "Category",
                            onClick = { showCategoryMenu = true },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "file_detail_category"
                        )
                        DropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
                        ) {
                            listOf("Screenshot", "Image", "Document", "PDF", "Other").forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        showCategoryMenu = false
                                        onMoveCategory(cat)
                                    }
                                )
                            }
                        }
                    }

                    JlabPrimaryButton(
                        text = "Share",
                        icon = Icons.Default.Share,
                        onClick = { shareFile(context, localFile, file.mimeType) },
                        modifier = Modifier.weight(1f),
                        testTag = "file_detail_share"
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Close", style = AppTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDelete,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text(
                    text = "Delete",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.extendedColors.danger
                )
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = AppTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = AppTheme.spacing.md)
        )
    }
}

@Composable
internal fun AttachToDialog(
    title: String = "Attach File To Workspace",
    projects: List<ProjectEntity>,
    learningItems: List<LearningEntity>,
    businesses: List<BusinessEntity>,
    notes: List<NoteEntity>,
    onDismiss: () -> Unit,
    onConfirmLink: (String?, String?, String?, String?, String?) -> Unit
) {
    var selectedType by remember { mutableStateOf("COURSE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = title,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)) {
                Text(
                    text = "Select target module to associate this attachment:",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )

                JlabFilterChipRow(
                    options = listOf("COURSE", "BUSINESS", "PROJECT", "NOTE"),
                    selectedOption = selectedType,
                    onSelect = { selectedType = it },
                    contentPadding = PaddingValues(AppTheme.spacing.none)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)
                ) {
                    when (selectedType) {
                        "COURSE" -> {
                            items(learningItems, key = { it.id }) { course ->
                                AttachmentOptionItem(title = course.title, subtitle = course.type) {
                                    onConfirmLink(null, null, null, null, course.id)
                                }
                            }
                        }
                        "BUSINESS" -> {
                            items(businesses, key = { it.id }) { biz ->
                                AttachmentOptionItem(title = biz.name, subtitle = biz.status) {
                                    onConfirmLink(null, null, null, biz.id, null)
                                }
                            }
                        }
                        "PROJECT" -> {
                            items(projects, key = { it.id }) { proj ->
                                AttachmentOptionItem(title = proj.name, subtitle = proj.status) {
                                    onConfirmLink(proj.id, null, null, null, null)
                                }
                            }
                        }
                        "NOTE" -> {
                            items(notes, key = { it.id }) { note ->
                                AttachmentOptionItem(
                                    title = note.title.ifBlank { "Untitled Note" },
                                    subtitle = note.category
                                ) {
                                    onConfirmLink(null, note.id, null, null, null)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Cancel", style = AppTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
private fun AttachmentOptionItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppTheme.extendedShapes.chip)
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize(),
        shape = AppTheme.extendedShapes.chip,
        color = AppTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.compactCardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Attach",
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
