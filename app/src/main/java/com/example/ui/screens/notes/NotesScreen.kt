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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.PushPin
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
fun NotesScreen(
    notes: List<NoteEntity>,
    deletedNotes: List<NoteEntity> = emptyList(),
    onCreateNote: (NoteEntity) -> Unit,
    onUpdateNote: (NoteEntity) -> Unit,
    onMoveNoteToTrash: (String) -> Unit,
    onMoveNotesToTrash: (List<String>) -> Unit,
    onArchiveNotes: (List<String>) -> Unit,
    onUpdateNotesCategory: (List<String>, String) -> Unit,
    onSetNotesPinned: (List<String>, Boolean) -> Unit,
    onRestoreNote: (String) -> Unit,
    onRestoreNotes: (List<String>) -> Unit,
    onDeleteNotePermanently: (String) -> Unit,
    onDeleteNotesPermanently: (List<String>) -> Unit,
    onEmptyTrash: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<NoteEntity?>(null) }
    var isTrashView by remember { mutableStateOf(false) }

    val selectedIds = remember { mutableStateListOf<String>() }
    val isSelectionMode = selectedIds.isNotEmpty()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    var showBulkTrashDialog by remember { mutableStateOf(false) }
    var showBulkCategoryDialog by remember { mutableStateOf(false) }

    BackHandler {
        when {
            isTrashView -> isTrashView = false
            isSelectionMode -> selectedIds.clear()
            else -> onNavigateBack()
        }
    }

    val categories = remember(notes) {
        listOf("All") + (listOf("Personal", "Learning", "Business", "Development", "Ideas", "Reference") +
            notes.map { it.category.trim() }.filter { it.isNotBlank() }).distinct()
    }

    val filteredNotes = remember(notes, selectedCategory, searchQuery) {
        var list = notes
        if (selectedCategory != "All") {
            list = list.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                    it.content.lowercase().contains(q) ||
                    it.category.lowercase().contains(q) ||
                    it.tags.lowercase().contains(q)
            }
        }
        list
    }

    if (isEditing) {
        NoteEditorScreen(
            initialNote = editingNote,
            existingCategories = categories.filter { it != "All" },
            onSaveNote = { note, isNew ->
                if (isNew) onCreateNote(note) else onUpdateNote(note)
                editingNote = note
            },
            onMoveToTrash = {
                editingNote?.let {
                    onMoveNoteToTrash(it.id)
                    Toast.makeText(context, "Moved to Trash", Toast.LENGTH_SHORT).show()
                }
                isEditing = false
                editingNote = null
            },
            onBack = {
                isEditing = false
                editingNote = null
            }
        )
        return
    }

    if (isTrashView) {
        TrashNotesScreen(
            deletedNotes = deletedNotes,
            onRestoreNote = onRestoreNote,
            onRestoreNotes = onRestoreNotes,
            onDeletePermanently = onDeleteNotePermanently,
            onDeleteNotesPermanently = onDeleteNotesPermanently,
            onEmptyTrash = onEmptyTrash,
            onBack = { isTrashView = false }
        )
        return
    }

    val allSelected = filteredNotes.isNotEmpty() && selectedIds.size == filteredNotes.size

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
                            selectedIds.addAll(filteredNotes.map { it.id })
                        }
                    },
                    testTag = "notes_selection_top_bar"
                )
            } else {
                JlabTopBar(
                    title = "Notes",
                    subtitle = "${notes.size} notes",
                    onNavigationClick = onNavigateBack,
                    testTag = "notes_top_bar",
                    actions = {
                        IconButton(
                            onClick = { isTrashView = true },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("notes_trash_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (deletedNotes.isNotEmpty()) {
                                        Badge { Text("${deletedNotes.size}") }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Open Trash",
                                    tint = AppTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        JlabIconButton(
                            icon = Icons.Default.Add,
                            contentDescription = "Create New Note",
                            onClick = {
                                editingNote = null
                                isEditing = true
                            },
                            tint = AppTheme.colorScheme.primary,
                            testTag = "notes_header_add_button"
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (isSelectionMode) {
                val anyUnpinned = selectedIds.any { id -> notes.find { it.id == id }?.isPinned == false }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .testTag("notes_selection_bottom_bar"),
                    color = AppTheme.colorScheme.surface,
                    border = BorderStroke(AppTheme.spacing.borderThin, AppTheme.extendedColors.cardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = AppTheme.spacing.sm,
                                vertical = AppTheme.spacing.xs
                            ),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                onSetNotesPinned(selectedIds.toList(), anyUnpinned)
                                Toast.makeText(
                                    context,
                                    if (anyUnpinned) "Pinned ${selectedIds.size} notes" else "Unpinned ${selectedIds.size} notes",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedIds.clear()
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = if (anyUnpinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.spacing.iconSm)
                            )
                            Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                            Text(if (anyUnpinned) "Pin" else "Unpin")
                        }

                        TextButton(
                            onClick = { showBulkCategoryDialog = true },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.spacing.iconSm)
                            )
                            Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                            Text("Category")
                        }

                        TextButton(
                            onClick = {
                                onArchiveNotes(selectedIds.toList())
                                Toast.makeText(context, "Archived ${selectedIds.size} notes", Toast.LENGTH_SHORT).show()
                                selectedIds.clear()
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Archive,
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.spacing.iconSm)
                            )
                            Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                            Text("Archive")
                        }

                        TextButton(
                            onClick = { showBulkTrashDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = AppTheme.colorScheme.error),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.spacing.iconSm)
                            )
                            Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                            Text("Trash")
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                JlabFloatingActionButton(
                    icon = Icons.Default.Add,
                    contentDescription = "New Note",
                    onClick = {
                        editingNote = null
                        isEditing = true
                    },
                    testTag = "notes_fab"
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
                placeholder = "Search title, content, tags…",
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.xs
                ),
                testTag = "notes_search_bar"
            )

            JlabFilterChipRow(
                options = categories,
                selectedOption = selectedCategory,
                onSelect = { selectedCategory = it },
                modifier = Modifier.padding(vertical = AppTheme.spacing.xxs)
            )

            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppTheme.spacing.xl),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Outlined.NoteAlt,
                        title = if (searchQuery.isNotEmpty()) "No matching notes" else "No notes yet",
                        message = if (searchQuery.isNotEmpty()) {
                            "Try adjusting your search query or category filter."
                        } else {
                            "Tap + to capture your first note. Notes are saved cleanly with rich formatting."
                        },
                        actionButtonText = if (searchQuery.isEmpty()) "Create Note" else null,
                        onActionClick = if (searchQuery.isEmpty()) {
                            {
                                editingNote = null
                                isEditing = true
                            }
                        } else null,
                        testTag = "notes_empty_state"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = AppTheme.spacing.screenHorizontal,
                        end = AppTheme.spacing.screenHorizontal,
                        top = AppTheme.spacing.sm,
                        bottom = AppTheme.spacing.listBottomPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        val isSelected = selectedIds.contains(note.id)
                        NoteCardItem(
                            note = note,
                            isSelected = isSelected,
                            isSelectionMode = isSelectionMode,
                            onClick = {
                                if (isSelectionMode) {
                                    if (isSelected) selectedIds.remove(note.id) else selectedIds.add(note.id)
                                } else {
                                    editingNote = note
                                    isEditing = true
                                }
                            },
                            onLongClick = {
                                if (!isSelectionMode) {
                                    selectedIds.add(note.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showBulkTrashDialog) {
        JlabConfirmDialog(
            title = "Move ${selectedIds.size} notes to Trash?",
            message = "Selected notes will be moved to the Trash. You can restore them at any time.",
            confirmText = "Move to Trash",
            isDestructive = true,
            icon = Icons.Outlined.Delete,
            onConfirm = {
                val ids = selectedIds.toList()
                selectedIds.clear()
                showBulkTrashDialog = false
                onMoveNotesToTrash(ids)
                Toast.makeText(context, "Moved ${ids.size} notes to Trash", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showBulkTrashDialog = false },
            testTag = "notes_bulk_trash_dialog"
        )
    }

    if (showBulkCategoryDialog) {
        val targetCategories = remember(categories) { categories.filter { it != "All" } }
        AlertDialog(
            onDismissRequest = { showBulkCategoryDialog = false },
            shape = AppTheme.shapes.large,
            containerColor = AppTheme.colorScheme.surfaceContainerHigh,
            title = { Text("Move to Category", style = AppTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                    targetCategories.forEach { cat ->
                        JlabCard(
                            shape = AppTheme.extendedShapes.compactCard,
                            contentPadding = PaddingValues(
                                horizontal = AppTheme.spacing.md,
                                vertical = AppTheme.spacing.smPlus
                            ),
                            onClick = {
                                val ids = selectedIds.toList()
                                selectedIds.clear()
                                showBulkCategoryDialog = false
                                onUpdateNotesCategory(ids, cat)
                                Toast.makeText(context, "Moved ${ids.size} notes to $cat", Toast.LENGTH_SHORT).show()
                            },
                            testTag = "bulk_category_option_${cat.lowercase()}"
                        ) {
                            Text(
                                text = cat,
                                style = AppTheme.typography.bodyLarge,
                                color = AppTheme.colorScheme.onSurface
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
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun NoteCardItem(
    note: NoteEntity,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val relativeTime = remember(note.updatedAt) {
        val diff = System.currentTimeMillis() - note.updatedAt
        val minutes = diff / (60 * 1000)
        val hours = minutes / 60
        val days = hours / 24
        when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(note.updatedAt))
        }
    }

    val plainSnippet = remember(note.content) {
        RichTextParser.toPlainText(note.content).trim().lines().joinToString(" ")
    }

    JlabCard(
        isSelected = isSelected,
        onClick = onClick,
        onLongClick = onLongClick,
        testTag = "note_card_${note.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            JlabTagBadge(
                text = note.category.ifBlank { "General" },
                containerColor = AppTheme.colorScheme.secondaryContainer,
                contentColor = AppTheme.colorScheme.onSecondaryContainer
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (note.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = AppTheme.colorScheme.primary,
                        modifier = Modifier.size(AppTheme.spacing.iconXs)
                    )
                }

                if (isSelectionMode) {
                    Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
                    Box(
                        modifier = Modifier
                            .size(AppTheme.spacing.iconMd)
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
                }
            }
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))

        Text(
            text = note.title.ifBlank { "Untitled Note" },
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (plainSnippet.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
            Text(
                text = plainSnippet,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.smPlus))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Updated $relativeTime",
                style = AppTheme.extendedTypography.caption,
                color = AppTheme.colorScheme.onSurfaceVariant
            )

            if (note.wordCount > 0) {
                Text(
                    text = "${note.wordCount} words",
                    style = AppTheme.extendedTypography.caption,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
