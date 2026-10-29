package com.example.ui.screens.vault

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.local.entity.FileAttachmentEntity
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabIconButton
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun VaultGridCard(
    file: FileAttachmentEntity,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onOpenInfo: () -> Unit
) {
    val localFile = remember(file.localFilePath) { File(file.localFilePath) }
    val isImage = file.mimeType.startsWith("image") ||
        file.name.endsWith(".jpg", true) ||
        file.name.endsWith(".png", true) ||
        file.name.endsWith(".webp", true) ||
        file.name.endsWith(".jpeg", true)
    val displayName = formatHumanFileName(file.name, file.createdAt)

    JlabCard(
        modifier = Modifier.fillMaxWidth(),
        isSelected = isSelected,
        contentPadding = PaddingValues(AppTheme.spacing.none),
        onClick = onClick,
        onLongClick = onLongClick,
        testTag = "vault_grid_card_${file.id}"
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(116.dp)
                .background(AppTheme.colorScheme.surfaceContainerHigh)
        ) {
            if (isImage && localFile.exists()) {
                AsyncImage(
                    model = localFile,
                    contentDescription = file.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when {
                            file.category == "PDF" || file.name.endsWith(".pdf", true) -> Icons.Default.PictureAsPdf
                            file.name.endsWith(".txt", true) || file.name.endsWith(".md", true) -> Icons.Default.Description
                            else -> Icons.Default.InsertDriveFile
                        },
                        contentDescription = null,
                        tint = AppTheme.colorScheme.primary,
                        modifier = Modifier.size(AppTheme.spacing.avatarMd)
                    )
                }
            }

            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(AppTheme.spacing.sm)
                        .clip(CircleShape)
                        .background(AppTheme.extendedColors.mediaScrim.copy(alpha = 0.55f))
                        .padding(AppTheme.spacing.xxs)
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = if (isSelected) "Selected" else "Not selected",
                        tint = if (isSelected) AppTheme.colorScheme.primary else AppTheme.extendedColors.onMediaOverlay,
                        modifier = Modifier.size(AppTheme.spacing.iconMd)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(
                start = AppTheme.spacing.compactCardPadding,
                end = AppTheme.spacing.xs,
                top = AppTheme.spacing.sm,
                bottom = AppTheme.spacing.xs
            )
        ) {
            Text(
                text = displayName,
                style = AppTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = AppTheme.spacing.sm)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${file.sizeBytes / 1024} KB • ${file.category}",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                JlabIconButton(
                    icon = Icons.Outlined.Info,
                    contentDescription = "File details",
                    onClick = onOpenInfo,
                    testTag = "vault_grid_info_${file.id}"
                )
            }
        }
    }
}

@Composable
internal fun VaultListRow(
    file: FileAttachmentEntity,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onOpenInfo: () -> Unit
) {
    val localFile = remember(file.localFilePath) { File(file.localFilePath) }
    val isImage = file.mimeType.startsWith("image") ||
        file.name.endsWith(".jpg", true) ||
        file.name.endsWith(".png", true) ||
        file.name.endsWith(".webp", true) ||
        file.name.endsWith(".jpeg", true)
    val displayName = formatHumanFileName(file.name, file.createdAt)

    JlabCard(
        modifier = Modifier.fillMaxWidth(),
        isSelected = isSelected,
        shape = AppTheme.extendedShapes.compactCard,
        contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding),
        onClick = onClick,
        onLongClick = onLongClick,
        testTag = "vault_list_row_${file.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.spacing.buttonHeightLg)
                    .clip(AppTheme.extendedShapes.chip)
                    .background(AppTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                if (isImage && localFile.exists()) {
                    AsyncImage(
                        model = localFile,
                        contentDescription = file.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = if (file.category == "PDF") Icons.Default.PictureAsPdf else Icons.Default.InsertDriveFile,
                        contentDescription = null,
                        tint = AppTheme.colorScheme.primary,
                        modifier = Modifier.size(AppTheme.spacing.iconLg)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = AppTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(file.createdAt))
                Text(
                    text = "${file.category} • ${file.sizeBytes / 1024} KB • $dateStr",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelectionMode) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = if (isSelected) "Selected" else "Not selected",
                    tint = if (isSelected) AppTheme.colorScheme.primary else AppTheme.colorScheme.onSurfaceVariant
                )
            } else {
                JlabIconButton(
                    icon = Icons.Outlined.Info,
                    contentDescription = "File details",
                    onClick = onOpenInfo,
                    testTag = "vault_list_info_${file.id}"
                )
            }
        }
    }
}

internal fun openFileInternal(
    file: FileAttachmentEntity,
    context: Context,
    onOpenImage: (FileAttachmentEntity) -> Unit,
    onOpenText: (FileAttachmentEntity) -> Unit,
    onOpenInfo: (FileAttachmentEntity) -> Unit
) {
    val name = file.name.lowercase()
    val mime = file.mimeType.lowercase()

    when {
        mime.startsWith("image") ||
            name.endsWith(".jpg") ||
            name.endsWith(".png") ||
            name.endsWith(".webp") ||
            name.endsWith(".jpeg") -> {
            onOpenImage(file)
        }
        mime.startsWith("text") ||
            name.endsWith(".txt") ||
            name.endsWith(".md") ||
            name.endsWith(".json") ||
            name.endsWith(".csv") -> {
            onOpenText(file)
        }
        else -> {
            onOpenInfo(file)
        }
    }
}

internal fun formatHumanFileName(name: String, createdAt: Long): String {
    return if (name.startsWith("jlab_")) {
        val dateStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(createdAt))
        val ext = name.substringAfterLast('.', "")
        if (ext.isNotBlank()) "Screenshot • $dateStr.$ext" else "Screenshot • $dateStr"
    } else {
        name
    }
}

internal fun shareFile(context: Context, file: File, mimeType: String) {
    if (!file.exists()) {
        Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType.ifBlank { "*/*" }
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share file"))
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to share file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

internal fun openInExternalApp(context: Context, file: File, mimeType: String) {
    if (!file.exists()) {
        Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType.ifBlank { "*/*" })
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(intent, "Open file with"))
    } catch (_: Exception) {
        Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show()
    }
}

internal suspend fun importFileToVault(
    context: Context,
    uri: Uri,
    category: String,
    onSaveFile: (FileAttachmentEntity) -> Unit
) = withContext(Dispatchers.IO) {
    try {
        val vaultDir = File(context.filesDir, "vault")
        vaultDir.mkdirs()

        val time = System.currentTimeMillis()
        val originalName = uri.lastPathSegment?.substringAfterLast('/') ?: "file"
        val fileName = if (originalName.contains(".")) originalName else "file_$time"
        val destFile = File(vaultDir, "jlab_${time}_$fileName")

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }

        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val friendlyName = when (category) {
            "Screenshot" -> "Screenshot • " + SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(time))
            else -> fileName.removePrefix("jlab_${time}_")
        }

        val entity = FileAttachmentEntity(
            name = friendlyName,
            localFilePath = destFile.absolutePath,
            mimeType = mimeType,
            sizeBytes = destFile.length(),
            category = category
        )
        onSaveFile(entity)

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Imported $category to JLab Vault!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Failed to import file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
