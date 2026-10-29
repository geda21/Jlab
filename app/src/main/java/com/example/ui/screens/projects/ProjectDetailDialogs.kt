package com.example.ui.screens.projects

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.entity.FileAttachmentEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.ProjectDecisionEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.JlabFilterChip
import com.example.ui.components.JlabOutlinedButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabTextField
import com.example.ui.theme.AppTheme
import com.example.util.FileStorageHelper
import java.io.File

@Composable
internal fun ProjectProgressDialog(
    currentMode: String,
    currentManualProgress: Int,
    autoCalculatedProgress: Int,
    onSave: (String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var mode by remember { mutableStateOf(currentMode) }
    var sliderValue by remember { mutableFloatStateOf(currentManualProgress.toFloat()) }
    var textInput by remember { mutableStateOf(currentManualProgress.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "Interactive Progress Control",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.mdPlus)) {
                Text(
                    text = "Select how project progress should be computed:",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    JlabFilterChip(
                        label = "Manual Slider",
                        selected = mode == "MANUAL",
                        onClick = { mode = "MANUAL" },
                        modifier = Modifier.weight(1f)
                    )
                    JlabFilterChip(
                        label = "Auto (Tasks)",
                        selected = mode == "AUTOMATIC",
                        onClick = { mode = "AUTOMATIC" },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (mode == "MANUAL") {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Completion: ${sliderValue.toInt()}%",
                                style = AppTheme.typography.titleSmall,
                                color = AppTheme.colorScheme.onSurface
                            )
                            JlabTextField(
                                value = textInput,
                                onValueChange = { str ->
                                    textInput = str
                                    val parsed = str.toIntOrNull()
                                    if (parsed != null && parsed in 0..100) {
                                        sliderValue = parsed.toFloat()
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.width(88.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

                        Slider(
                            value = sliderValue,
                            onValueChange = {
                                sliderValue = it
                                textInput = it.toInt().toString()
                            },
                            valueRange = 0f..100f
                        )
                    }
                } else {
                    Surface(
                        shape = AppTheme.extendedShapes.compactCard,
                        color = AppTheme.extendedColors.infoContainer,
                        border = BorderStroke(
                            AppTheme.spacing.borderThin,
                            AppTheme.extendedColors.info.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(AppTheme.spacing.mdPlus)) {
                            Text(
                                text = "Automatic Mode Active",
                                style = AppTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.extendedColors.onInfoContainer
                            )
                            Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                            Text(
                                text = "Calculated automatically from completed tasks:\nProgress = $autoCalculatedProgress%",
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                            Text(
                                text = "Your manual progress ($currentManualProgress%) is preserved safely and will not be overwritten.",
                                style = AppTheme.extendedTypography.caption,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Confirm",
                onClick = { onSave(mode, sliderValue.toInt().coerceIn(0, 100)) }
            )
        },
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
internal fun ProjectEditFullDialog(
    project: ProjectEntity,
    onSave: (ProjectEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(project.name) }
    var objective by remember { mutableStateOf(project.objective) }
    var description by remember { mutableStateOf(project.description) }
    var category by remember { mutableStateOf(project.category) }
    var priority by remember { mutableStateOf(project.priority) }
    var status by remember { mutableStateOf(project.status) }
    var coverUri by remember { mutableStateOf(project.coverUri) }
    var avatarUri by remember { mutableStateOf(project.avatarUri) }

    val coverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val copied = FileStorageHelper.copyUriToInternalStorage(context, uri, "project_covers")
            if (copied != null) {
                coverUri = copied.absolutePath
            }
        }
    }

    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val copied = FileStorageHelper.copyUriToInternalStorage(context, uri, "project_avatars")
            if (copied != null) {
                avatarUri = copied.absolutePath
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "Edit Project Details",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)
            ) {
                JlabTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Project Title *",
                    singleLine = true
                )

                JlabTextField(
                    value = objective,
                    onValueChange = { objective = it },
                    label = "Objective / Target",
                    placeholder = "Main goal this project achieves",
                    singleLine = false,
                    minLines = 2
                )

                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description & Notes",
                    singleLine = false,
                    minLines = 3
                )

                Text(
                    text = "Visuals & Branding",
                    style = AppTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    JlabOutlinedButton(
                        text = if (coverUri.isBlank()) "Set Banner" else "Change Banner",
                        icon = Icons.Default.Image,
                        onClick = {
                            coverPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    JlabOutlinedButton(
                        text = if (avatarUri.isBlank()) "Set Avatar" else "Change Avatar",
                        icon = Icons.Default.AccountCircle,
                        onClick = {
                            avatarPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (coverUri.isNotBlank() || avatarUri.isNotBlank()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                        if (coverUri.isNotBlank()) {
                            TextButton(
                                onClick = { coverUri = "" },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text(
                                    text = "Remove Banner",
                                    style = AppTheme.typography.labelSmall,
                                    color = AppTheme.extendedColors.danger
                                )
                            }
                        }
                        if (avatarUri.isNotBlank()) {
                            TextButton(
                                onClick = { avatarUri = "" },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text(
                                    text = "Remove Avatar",
                                    style = AppTheme.typography.labelSmall,
                                    color = AppTheme.extendedColors.danger
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Status",
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)) {
                    items(listOf("PLANNING", "ACTIVE", "PAUSED", "COMPLETED")) { s ->
                        JlabFilterChip(
                            label = s,
                            selected = status == s,
                            onClick = { status = s }
                        )
                    }
                }

                Text(
                    text = "Priority",
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)) {
                    items(listOf("LOW", "MEDIUM", "HIGH", "CRITICAL")) { p ->
                        JlabFilterChip(
                            label = p,
                            selected = priority == p,
                            onClick = { priority = p }
                        )
                    }
                }

                JlabTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = "Category",
                    singleLine = true
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Save Changes",
                enabled = name.isNotBlank(),
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            project.copy(
                                name = name.trim(),
                                objective = objective.trim(),
                                description = description.trim(),
                                category = category.trim(),
                                priority = priority,
                                status = status,
                                coverUri = coverUri,
                                avatarUri = avatarUri,
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
            )
        },
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
internal fun ProjectTaskDialog(
    initialTask: TaskEntity?,
    projectId: String,
    onSave: (TaskEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }
    var priority by remember { mutableStateOf(initialTask?.priority ?: "MEDIUM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = if (initialTask == null) "New Project Task" else "Edit Task",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Task Title *",
                    singleLine = true
                )
                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description (optional)",
                    singleLine = false,
                    minLines = 2
                )
                Text(
                    text = "Priority",
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.compactGap)) {
                    items(listOf("LOW", "MEDIUM", "HIGH", "CRITICAL")) { p ->
                        JlabFilterChip(
                            label = p,
                            selected = priority == p,
                            onClick = { priority = p }
                        )
                    }
                }
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Save Task",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        val task = (initialTask ?: TaskEntity(projectId = projectId, title = title)).copy(
                            title = title.trim(),
                            description = description.trim(),
                            priority = priority,
                            projectId = projectId,
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(task)
                    }
                }
            )
        },
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
internal fun ProjectNoteCreateDialog(
    projectId: String,
    onSave: (NoteEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Project") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "New Project Note",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Note Title *",
                    singleLine = true
                )
                JlabTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = "Content *",
                    singleLine = false,
                    minLines = 4
                )
                JlabTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = "Category",
                    singleLine = true
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Create Note",
                enabled = title.isNotBlank() || content.isNotBlank(),
                onClick = {
                    if (title.isNotBlank() || content.isNotBlank()) {
                        onSave(
                            NoteEntity(
                                title = title.trim().ifBlank { "Project Note" },
                                content = content.trim(),
                                projectId = projectId,
                                category = category.trim()
                            )
                        )
                    }
                }
            )
        },
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
internal fun DecisionDialog(
    initialDecision: ProjectDecisionEntity?,
    projectId: String,
    onSave: (ProjectDecisionEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialDecision?.title ?: "") }
    var description by remember { mutableStateOf(initialDecision?.description ?: "") }
    var reason by remember { mutableStateOf(initialDecision?.reason ?: "") }
    var outcome by remember { mutableStateOf(initialDecision?.outcome ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = if (initialDecision == null) "Log Project Decision" else "Edit Decision",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)) {
                JlabTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Decision Title *",
                    placeholder = "e.g. Changed architecture to Room",
                    singleLine = true
                )
                JlabTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = "Reason / Problem *",
                    placeholder = "e.g. Need durable offline persistence",
                    singleLine = false,
                    minLines = 2
                )
                JlabTextField(
                    value = outcome,
                    onValueChange = { outcome = it },
                    label = "Outcome / Resolution",
                    placeholder = "e.g. Implemented Room migration",
                    singleLine = false,
                    minLines = 2
                )
                JlabTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Additional Context (optional)",
                    singleLine = true
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Save Decision",
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        val decision = (initialDecision ?: ProjectDecisionEntity(projectId = projectId, title = title)).copy(
                            title = title.trim(),
                            description = description.trim(),
                            reason = reason.trim(),
                            outcome = outcome.trim(),
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(decision)
                    }
                }
            )
        },
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
internal fun ProjectImagePreviewDialog(
    file: FileAttachmentEntity,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.extendedColors.mediaScrim)
        ) {
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            AsyncImage(
                model = File(file.localFilePath),
                contentDescription = file.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            offset += pan
                        }
                    }
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppTheme.spacing.lg)
                    .minimumInteractiveComponentSize()
                    .background(AppTheme.extendedColors.mediaScrim, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close preview",
                    tint = AppTheme.extendedColors.onMediaOverlay
                )
            }
        }
    }
}

@Composable
internal fun ProjectTextPreviewDialog(
    file: FileAttachmentEntity,
    onDismiss: () -> Unit
) {
    val textContent = remember(file.localFilePath) {
        try {
            File(file.localFilePath).readText().take(5000)
        } catch (e: Exception) {
            "Unable to read file contents: ${e.message}"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = file.name,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = textContent,
                    style = AppTheme.extendedTypography.codeSmall,
                    color = AppTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Close", style = AppTheme.typography.labelLarge)
            }
        }
    )
}
