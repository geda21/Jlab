package com.example.ui.screens.vault

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.BusinessEntity
import com.example.data.local.entity.FileAttachmentEntity
import com.example.data.local.entity.LearningEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.ProjectEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.JlabConfirmDialog
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabOutlinedButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSearchBar
import com.example.ui.components.JlabSelectionBottomBar
import com.example.ui.components.JlabSelectionTopBar
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FilesVaultScreen(
    files: List<FileAttachmentEntity>,
    projects: List<ProjectEntity> = emptyList(),
    learningItems: List<LearningEntity> = emptyList(),
    businesses: List<BusinessEntity> = emptyList(),
    notes: List<NoteEntity> = emptyList(),
    onSaveFile: (FileAttachmentEntity) -> Unit,
    onUpdateFile: (FileAttachmentEntity) -> Unit,
    onDeleteFile: (FileAttachmentEntity) -> Unit,
    onUpdateFileName: (String, String) -> Unit,
    onUpdateFileCategory: (String, String) -> Unit,
    onUpdateFilesCategory: (List<String>, String) -> Unit,
    onLinkFile: (String, String?, String?, String?, String?, String?) -> Unit,
    onLinkFiles: (List<String>, String?, String?, String?, String?, String?) -> Unit,
    onDeleteFiles: (List<String>) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isGridView by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    val categoryTabs = listOf("ALL", "SCREENSHOTS", "IMAGES", "DOCUMENTS", "PDF", "OTHER")

    var viewingImageFile by remember { mutableStateOf<FileAttachmentEntity?>(null) }
    var viewingTextFile by remember { mutableStateOf<FileAttachmentEntity?>(null) }
    var fileDetailTarget by remember { mutableStateOf<FileAttachmentEntity?>(null) }
    var renamingFile by remember { mutableStateOf<FileAttachmentEntity?>(null) }
    var attachingFileTarget by remember { mutableStateOf<FileAttachmentEntity?>(null) }

    val selectedIds = remember { mutableStateListOf<String>() }
    val isSelectionMode = selectedIds.isNotEmpty()
    var showBulkCategoryDialog by remember { mutableStateOf(false) }
    var showBulkAttachDialog by remember { mutableStateOf(false) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    BackHandler {
        when {
            viewingImageFile != null -> viewingImageFile = null
            viewingTextFile != null -> viewingTextFile = null
            isSelectionMode -> selectedIds.clear()
            else -> onNavigateBack()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                importFileToVault(context, uri, "Screenshot", onSaveFile)
            }
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                val mime = context.contentResolver.getType(uri) ?: ""
                val cat = if (mime.contains("pdf", ignoreCase = true)) "PDF" else "Document"
                importFileToVault(context, uri, cat, onSaveFile)
            }
        }
    }

    val filteredFiles = remember(files, searchQuery, selectedCategoryFilter) {
        var list = files
        if (selectedCategoryFilter != "ALL") {
            list = list.filter {
                when (selectedCategoryFilter) {
                    "SCREENSHOTS" -> it.category.equals("Screenshot", ignoreCase = true)
                    "IMAGES" -> it.category.equals("Image", ignoreCase = true) || it.mimeType.startsWith("image")
                    "DOCUMENTS" -> it.category.equals("Document", ignoreCase = true)
                    "PDF" -> it.category.equals("PDF", ignoreCase = true) || it.name.endsWith(".pdf", ignoreCase = true)
                    else -> !it.category.equals("Screenshot", ignoreCase = true) && !it.category.equals("Image", ignoreCase = true)
                }
            }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                    it.category.lowercase().contains(q) ||
                    formatHumanFileName(it.name, it.createdAt).lowercase().contains(q)
            }
        }
        list
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            if (isSelectionMode) {
                JlabSelectionTopBar(
                    selectedCount = selectedIds.size,
                    allSelected = filteredFiles.isNotEmpty() && selectedIds.size == filteredFiles.size,
                    onClearSelection = { selectedIds.clear() },
                    onToggleSelectAll = {
                        if (selectedIds.size == filteredFiles.size) {
                            selectedIds.clear()
                        } else {
                            selectedIds.clear()
                            selectedIds.addAll(filteredFiles.map { it.id })
                        }
                    },
                    testTag = "vault_selection_top_bar"
                )
            } else {
                JlabTopBar(
                    title = "File & Screenshot Vault",
                    subtitle = "${files.size} offline files stored",
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    onNavigationClick = onNavigateBack,
                    testTag = "vault_top_bar",
                    actions = {
                        JlabIconButton(
                            icon = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridView) "Switch to list view" else "Switch to grid view",
                            onClick = { isGridView = !isGridView },
                            testTag = "vault_toggle_view"
                        )
                        JlabIconButton(
                            icon = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Import screenshot",
                            tint = AppTheme.colorScheme.primary,
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            testTag = "vault_import_photo"
                        )
                        JlabIconButton(
                            icon = Icons.Default.UploadFile,
                            contentDescription = "Import file",
                            onClick = { documentPickerLauncher.launch(arrayOf("*/*")) },
                            testTag = "vault_import_document"
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (isSelectionMode) {
                JlabSelectionBottomBar(
                    deleteText = "Delete",
                    onDelete = { showBulkDeleteDialog = true },
                    testTag = "vault_selection_bottom_bar",
                    extraActions = {
                        JlabOutlinedButton(
                            text = "Category",
                            icon = Icons.Default.Folder,
                            onClick = { showBulkCategoryDialog = true },
                            modifier = Modifier.weight(1f),
                            testTag = "vault_bulk_category"
                        )
                        JlabOutlinedButton(
                            text = "Attach",
                            icon = Icons.Default.Link,
                            onClick = { showBulkAttachDialog = true },
                            modifier = Modifier.weight(1f),
                            testTag = "vault_bulk_attach"
                        )
                    }
                )
            }
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
                placeholder = "Search files by name or category...",
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.compactGap
                ),
                testTag = "vault_search_bar"
            )

            JlabFilterChipRow(
                options = categoryTabs,
                selectedOption = selectedCategoryFilter,
                onSelect = { selectedCategoryFilter = it },
                modifier = Modifier.padding(vertical = AppTheme.spacing.xs)
            )

            if (filteredFiles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.FolderZip,
                        title = if (searchQuery.isNotEmpty()) "No matching files" else "Vault is empty",
                        message = "Import screenshots, research PDFs, and files directly into JLab local storage.",
                        actionButtonText = "Import Screenshot",
                        onActionClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        testTag = "vault_empty_state"
                    )
                }
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = AppTheme.spacing.screenHorizontal,
                        end = AppTheme.spacing.screenHorizontal,
                        top = AppTheme.spacing.md,
                        bottom = AppTheme.spacing.listBottomPadding
                    ),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
                ) {
                    items(filteredFiles, key = { it.id }) { file ->
                        val isSelected = selectedIds.contains(file.id)
                        VaultGridCard(
                            file = file,
                            isSelected = isSelected,
                            isSelectionMode = isSelectionMode,
                            onClick = {
                                if (isSelectionMode) {
                                    if (isSelected) selectedIds.remove(file.id) else selectedIds.add(file.id)
                                } else {
                                    openFileInternal(
                                        file = file,
                                        context = context,
                                        onOpenImage = { viewingImageFile = it },
                                        onOpenText = { viewingTextFile = it },
                                        onOpenInfo = { fileDetailTarget = it }
                                    )
                                }
                            },
                            onLongClick = {
                                if (!isSelectionMode) selectedIds.add(file.id)
                            },
                            onOpenInfo = { fileDetailTarget = file }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = AppTheme.spacing.screenHorizontal,
                        end = AppTheme.spacing.screenHorizontal,
                        top = AppTheme.spacing.md,
                        bottom = AppTheme.spacing.listBottomPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    items(filteredFiles, key = { it.id }) { file ->
                        val isSelected = selectedIds.contains(file.id)
                        VaultListRow(
                            file = file,
                            isSelected = isSelected,
                            isSelectionMode = isSelectionMode,
                            onClick = {
                                if (isSelectionMode) {
                                    if (isSelected) selectedIds.remove(file.id) else selectedIds.add(file.id)
                                } else {
                                    openFileInternal(
                                        file = file,
                                        context = context,
                                        onOpenImage = { viewingImageFile = it },
                                        onOpenText = { viewingTextFile = it },
                                        onOpenInfo = { fileDetailTarget = it }
                                    )
                                }
                            },
                            onLongClick = {
                                if (!isSelectionMode) selectedIds.add(file.id)
                            },
                            onOpenInfo = { fileDetailTarget = file }
                        )
                    }
                }
            }
        }
    }

    viewingImageFile?.let { fileEntity ->
        val localFile = File(fileEntity.localFilePath)
        NativeImageViewerDialog(
            file = fileEntity,
            localFile = localFile,
            onDismiss = { viewingImageFile = null },
            onOpenInfo = { fileDetailTarget = fileEntity },
            onRename = { renamingFile = fileEntity },
            onAttachTo = { attachingFileTarget = fileEntity },
            onDelete = {
                onDeleteFile(fileEntity)
                localFile.delete()
                viewingImageFile = null
            }
        )
    }

    viewingTextFile?.let { fileEntity ->
        val localFile = File(fileEntity.localFilePath)
        val textContent = remember(localFile) {
            try {
                if (localFile.exists()) localFile.readText() else "File not found"
            } catch (e: Exception) {
                "Unable to read text file: ${e.message}"
            }
        }

        AlertDialog(
            onDismissRequest = { viewingTextFile = null },
            shape = AppTheme.shapes.extraLarge,
            containerColor = AppTheme.colorScheme.surface,
            title = {
                Text(
                    text = formatHumanFileName(fileEntity.name, fileEntity.createdAt),
                    style = AppTheme.typography.titleLarge,
                    color = AppTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            },
            text = {
                Surface(
                    shape = AppTheme.extendedShapes.compactCard,
                    color = AppTheme.extendedColors.codeSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(AppTheme.spacing.compactCardPadding)
                    ) {
                        Text(
                            text = textContent,
                            style = AppTheme.extendedTypography.codeMedium,
                            color = AppTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            confirmButton = {
                JlabPrimaryButton(
                    text = "Close",
                    onClick = { viewingTextFile = null },
                    testTag = "vault_text_viewer_close"
                )
            }
        )
    }

    fileDetailTarget?.let { file ->
        val localFile = File(file.localFilePath)
        FileDetailDialog(
            file = file,
            localFile = localFile,
            projects = projects,
            learningItems = learningItems,
            businesses = businesses,
            notes = notes,
            onDismiss = { fileDetailTarget = null },
            onRename = {
                fileDetailTarget = null
                renamingFile = file
            },
            onAttachTo = {
                fileDetailTarget = null
                attachingFileTarget = file
            },
            onMoveCategory = { newCat ->
                onUpdateFileCategory(file.id, newCat)
                onUpdateFile(file.copy(category = newCat))
                fileDetailTarget = null
            },
            onDelete = {
                onDeleteFile(file)
                localFile.delete()
                fileDetailTarget = null
            }
        )
    }

    renamingFile?.let { file ->
        var newName by remember { mutableStateOf(file.name) }
        AlertDialog(
            onDismissRequest = { renamingFile = null },
            shape = AppTheme.shapes.extraLarge,
            containerColor = AppTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Rename File",
                    style = AppTheme.typography.titleLarge,
                    color = AppTheme.colorScheme.onSurface
                )
            },
            text = {
                JlabTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = "Display Name",
                    singleLine = true,
                    testTag = "vault_rename_input"
                )
            },
            confirmButton = {
                JlabPrimaryButton(
                    text = "Rename",
                    enabled = newName.isNotBlank(),
                    onClick = {
                        if (newName.isNotBlank()) {
                            onUpdateFileName(file.id, newName.trim())
                            renamingFile = null
                        }
                    },
                    testTag = "vault_rename_confirm"
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { renamingFile = null },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Cancel", style = AppTheme.typography.labelLarge)
                }
            }
        )
    }

    attachingFileTarget?.let { file ->
        AttachToDialog(
            projects = projects,
            learningItems = learningItems,
            businesses = businesses,
            notes = notes,
            onDismiss = { attachingFileTarget = null },
            onConfirmLink = { projId, noteId, taskId, bizId, learnId ->
                onLinkFile(file.id, projId, noteId, taskId, bizId, learnId)
                attachingFileTarget = null
                Toast.makeText(context, "Attachment linked successfully", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showBulkAttachDialog) {
        AttachToDialog(
            title = "Attach ${selectedIds.size} Files To Workspace",
            projects = projects,
            learningItems = learningItems,
            businesses = businesses,
            notes = notes,
            onDismiss = { showBulkAttachDialog = false },
            onConfirmLink = { projId, noteId, taskId, bizId, learnId ->
                onLinkFiles(selectedIds.toList(), projId, noteId, taskId, bizId, learnId)
                showBulkAttachDialog = false
                selectedIds.clear()
                Toast.makeText(context, "Attachments linked successfully", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showBulkCategoryDialog) {
        val categories = listOf("Screenshot", "Image", "Document", "PDF", "Other")
        AlertDialog(
            onDismissRequest = { showBulkCategoryDialog = false },
            shape = AppTheme.shapes.extraLarge,
            containerColor = AppTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Move Category (${selectedIds.size} files)",
                    style = AppTheme.typography.titleLarge,
                    color = AppTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                    categories.forEach { cat ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(AppTheme.extendedShapes.chip)
                                .clickable {
                                    onUpdateFilesCategory(selectedIds.toList(), cat)
                                    showBulkCategoryDialog = false
                                    selectedIds.clear()
                                    Toast.makeText(context, "Updated category to $cat", Toast.LENGTH_SHORT).show()
                                }
                                .minimumInteractiveComponentSize(),
                            shape = AppTheme.extendedShapes.chip,
                            color = AppTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Text(
                                text = cat,
                                style = AppTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(AppTheme.spacing.compactCardPadding)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showBulkCategoryDialog = false },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Cancel", style = AppTheme.typography.labelLarge)
                }
            }
        )
    }

    if (showBulkDeleteDialog) {
        JlabConfirmDialog(
            title = "Delete ${selectedIds.size} files?",
            message = "This will permanently remove the selected files from your offline vault.",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                val idsToDelete = selectedIds.toList()
                files.filter { it.id in idsToDelete }.forEach { File(it.localFilePath).delete() }
                onDeleteFiles(idsToDelete)
                showBulkDeleteDialog = false
                selectedIds.clear()
                Toast.makeText(context, "Deleted files", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showBulkDeleteDialog = false },
            testTag = "vault_bulk_delete_dialog"
        )
    }
}
