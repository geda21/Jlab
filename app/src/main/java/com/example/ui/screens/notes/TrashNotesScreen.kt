package com.example.ui.screens.notes

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.entity.NoteEntity
import com.example.ui.components.*
import com.example.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashNotesScreen(
    deletedNotes: List<NoteEntity>,
    onRestoreNote: (String) -> Unit,
    onRestoreNotes: (List<String>) -> Unit,
    onDeletePermanently: (String) -> Unit,
    onDeleteNotesPermanently: (List<String>) -> Unit,
    onEmptyTrash: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val selectedIds = remember { mutableStateListOf<String>() }
    val isSelectionMode = selectedIds.isNotEmpty()

    var showEmptyTrashConfirmDialog by remember { mutableStateOf(false) }
    var noteToDeletePermanently by remember { mutableStateOf<NoteEntity?>(null) }
    var showBulkDeleteConfirmDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (isSelectionMode) {
            selectedIds.clear()
        } else {
            onBack()
        }
    }

    val allSelected = deletedNotes.isNotEmpty() && selectedIds.size == deletedNotes.size

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            if (isSelectionMode) {
                JlabSelectionTopBar(
                    selectedCount = selectedIds.size,
                    allSelected = allSelected,
                    onClearSelection = { selectedIds.clear() },
                    onToggleSelectAll = {
                        if (allSelected) {
                            selectedIds.clear()
                        } else {
                            selectedIds.clear()
                            selectedIds.addAll(deletedNotes.map { it.id })
                        }
                    },
                    testTag = "trash_notes_selection_top_bar"
                )
            } else {
                JlabTopBar(
                    title = "Trash",
                    subtitle = "${deletedNotes.size} deleted notes",
                    onNavigationClick = onBack,
                    testTag = "trash_notes_top_bar",
                    actions = {
                        if (deletedNotes.isNotEmpty()) {
                            TextButton(
                                onClick = { showEmptyTrashConfirmDialog = true },
                                colors = ButtonDefaults.textButtonColors(contentColor = AppTheme.colorScheme.error),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("empty_trash_button")
                            ) {
                                Text("Empty Trash")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (isSelectionMode) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = AppTheme.colorScheme.surface,
                    border = BorderStroke(AppTheme.spacing.borderThin, AppTheme.extendedColors.cardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = AppTheme.spacing.screenHorizontal,
                                vertical = AppTheme.spacing.smPlus
                            ),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        JlabPrimaryButton(
                            text = "Restore (${selectedIds.size})",
                            icon = Icons.Default.Restore,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onRestoreNotes(selectedIds.toList())
                                Toast.makeText(context, "Restored ${selectedIds.size} notes", Toast.LENGTH_SHORT).show()
                                selectedIds.clear()
                            },
                            testTag = "trash_bulk_restore_button"
                        )

                        JlabDestructiveButton(
                            text = "Delete (${selectedIds.size})",
                            icon = Icons.Default.DeleteForever,
                            modifier = Modifier.weight(1f),
                            onClick = { showBulkDeleteConfirmDialog = true },
                            testTag = "trash_bulk_delete_button"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (deletedNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Outlined.Delete,
                    title = "Trash is empty",
                    message = "Deleted notes will appear here. You can restore or delete them permanently.",
                    testTag = "trash_empty_state"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(AppTheme.colorScheme.background),
                contentPadding = PaddingValues(
                    start = AppTheme.spacing.screenHorizontal,
                    end = AppTheme.spacing.screenHorizontal,
                    top = AppTheme.spacing.md,
                    bottom = AppTheme.spacing.listBottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
            ) {
                items(deletedNotes, key = { it.id }) { note ->
                    val isSelected = selectedIds.contains(note.id)
                    val dateStr = remember(note.updatedAt) {
                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(note.updatedAt))
                    }

                    JlabCard(
                        isSelected = isSelected,
                        onClick = {
                            if (isSelectionMode) {
                                if (isSelected) selectedIds.remove(note.id) else selectedIds.add(note.id)
                            } else {
                                selectedIds.add(note.id)
                            }
                        },
                        testTag = "trash_note_card_${note.id}"
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isSelectionMode) {
                                    Box(
                                        modifier = Modifier
                                            .size(AppTheme.spacing.iconLg)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) AppTheme.colorScheme.primary
                                                else AppTheme.colorScheme.surfaceContainerHighest
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = AppTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(AppTheme.spacing.iconXs)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(AppTheme.spacing.smPlus))
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = note.title.ifBlank { "Untitled Note" },
                                        style = AppTheme.typography.titleMedium,
                                        color = AppTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
                                    Text(
                                        text = "Category: ${note.category} • $dateStr",
                                        style = AppTheme.extendedTypography.caption,
                                        color = AppTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (!isSelectionMode) {
                                Row {
                                    JlabIconButton(
                                        icon = Icons.Default.Restore,
                                        contentDescription = "Restore Note",
                                        tint = AppTheme.colorScheme.primary,
                                        onClick = {
                                            onRestoreNote(note.id)
                                            Toast.makeText(context, "Note restored", Toast.LENGTH_SHORT).show()
                                        },
                                        testTag = "restore_note_${note.id}"
                                    )
                                    JlabIconButton(
                                        icon = Icons.Default.DeleteForever,
                                        contentDescription = "Delete Note Permanently",
                                        tint = AppTheme.colorScheme.error,
                                        onClick = { noteToDeletePermanently = note },
                                        testTag = "delete_permanent_note_${note.id}"
                                    )
                                }
                            }
                        }

                        if (note.content.isNotBlank()) {
                            Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
                            Text(
                                text = RichTextParser.toPlainText(note.content),
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEmptyTrashConfirmDialog) {
        JlabConfirmDialog(
            title = "Empty Trash?",
            message = "Permanently delete all notes in Trash? This action cannot be undone.",
            confirmText = "Empty Trash",
            isDestructive = true,
            icon = Icons.Default.DeleteForever,
            onConfirm = {
                showEmptyTrashConfirmDialog = false
                onEmptyTrash()
                Toast.makeText(context, "Trash emptied", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showEmptyTrashConfirmDialog = false },
            testTag = "empty_trash_confirm_dialog"
        )
    }

    noteToDeletePermanently?.let { note ->
        JlabConfirmDialog(
            title = "Delete Permanently?",
            message = "Permanently delete \"${note.title.ifBlank { "Untitled Note" }}\"? This action cannot be undone.",
            confirmText = "Delete",
            isDestructive = true,
            icon = Icons.Default.DeleteForever,
            onConfirm = {
                val id = note.id
                noteToDeletePermanently = null
                onDeletePermanently(id)
                Toast.makeText(context, "Note permanently deleted", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { noteToDeletePermanently = null },
            testTag = "delete_single_note_permanent_dialog"
        )
    }

    if (showBulkDeleteConfirmDialog) {
        JlabConfirmDialog(
            title = "Delete ${selectedIds.size} Notes Permanently?",
            message = "This will permanently remove the selected notes from the database. This action cannot be undone.",
            confirmText = "Delete Permanently",
            isDestructive = true,
            icon = Icons.Default.DeleteForever,
            onConfirm = {
                showBulkDeleteConfirmDialog = false
                val ids = selectedIds.toList()
                selectedIds.clear()
                onDeleteNotesPermanently(ids)
                Toast.makeText(context, "Deleted ${ids.size} notes", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showBulkDeleteConfirmDialog = false },
            testTag = "delete_bulk_notes_permanent_dialog"
        )
    }
}
