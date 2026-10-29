package com.example.ui.screens.notes

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.NoteEntity
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    initialNote: NoteEntity?,
    existingCategories: List<String>,
    onSaveNote: (NoteEntity, Boolean) -> Unit,
    onMoveToTrash: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isDarkTheme = AppTheme.isDark
    val isNewNote = initialNote == null

    val (initialPlain, initialSpans) = remember(initialNote) {
        RichTextDocument.parseLegacyContentToDocument(
            initialNote?.content ?: "",
            initialNote?.formattingJson
        )
    }

    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var contentValue by remember { mutableStateOf(TextFieldValue(initialPlain)) }
    var documentSpans by remember { mutableStateOf(initialSpans) }
    var category by remember { mutableStateOf(initialNote?.category ?: "Personal") }
    var isPinned by remember { mutableStateOf(initialNote?.isPinned ?: false) }
    var isFavorite by remember { mutableStateOf(initialNote?.isFavorite ?: false) }

    var activeTypingBold by remember { mutableStateOf(false) }
    var activeTypingItalic by remember { mutableStateOf(false) }
    var activeTypingUnderline by remember { mutableStateOf(false) }
    var activeTypingStrikethrough by remember { mutableStateOf(false) }
    var activeTypingHighlight by remember { mutableStateOf<String?>(null) }

    var isSaved by remember { mutableStateOf(!isNewNote) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var isPreviewMode by remember { mutableStateOf(false) }

    var showExitConfirmDialog by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showNoteInfoDialog by remember { mutableStateOf(false) }

    val contentFocusRequester = remember { FocusRequester() }
    val draftFile = remember { File(context.cacheDir, "jlab_note_draft.tmp") }

    fun clearDraft() {
        try {
            if (draftFile.exists()) draftFile.delete()
        } catch (_: Exception) {}
    }

    fun saveDraftLocally() {
        try {
            if (hasUnsavedChanges && (title.isNotBlank() || contentValue.text.isNotBlank())) {
                val jsonSpans = RichTextDocument.spansToJson(documentSpans)
                draftFile.writeText("$title\n---JLAB_DRAFT_SPLIT---\n${contentValue.text}\n---JLAB_SPANS_SPLIT---\n$jsonSpans")
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(title, contentValue.text, documentSpans, category, isPinned, isFavorite) {
        val changed = if (isNewNote) {
            title.isNotBlank() || contentValue.text.isNotBlank() || documentSpans.isNotEmpty()
        } else {
            title != (initialNote?.title ?: "") ||
                contentValue.text != initialPlain ||
                documentSpans != initialSpans ||
                category != (initialNote?.category ?: "Personal") ||
                isPinned != (initialNote?.isPinned ?: false) ||
                isFavorite != (initialNote?.isFavorite ?: false)
        }
        hasUnsavedChanges = changed
        if (changed) {
            isSaved = false
            saveDraftLocally()
        }
    }

    fun performSave(shouldExit: Boolean = false) {
        val trimmedTitle = title.trim()
        val plainContent = contentValue.text

        val finalTitle = if (trimmedTitle.isNotBlank()) {
            trimmedTitle
        } else {
            val firstLine = plainContent.lineSequence()
                .map { it.trim() }
                .firstOrNull { it.isNotBlank() }
            if (!firstLine.isNullOrBlank()) firstLine.take(50) else "Untitled Note"
        }

        val words = if (plainContent.isBlank()) 0 else plainContent.trim().split(Regex("\\s+")).size
        val chars = plainContent.length
        val serializedSpans = RichTextDocument.spansToJson(documentSpans)

        val noteToSave = if (isNewNote) {
            NoteEntity(
                id = UUID.randomUUID().toString(),
                title = finalTitle,
                content = plainContent,
                formattingJson = serializedSpans,
                category = category,
                isPinned = isPinned,
                isFavorite = isFavorite,
                wordCount = words,
                charCount = chars,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        } else {
            initialNote!!.copy(
                title = finalTitle,
                content = plainContent,
                formattingJson = serializedSpans,
                category = category,
                isPinned = isPinned,
                isFavorite = isFavorite,
                wordCount = words,
                charCount = chars,
                updatedAt = System.currentTimeMillis()
            )
        }

        onSaveNote(noteToSave, isNewNote)
        hasUnsavedChanges = false
        isSaved = true
        title = finalTitle
        clearDraft()
        Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show()

        if (shouldExit) {
            onBack()
        }
    }

    fun handleBack() {
        if (!hasUnsavedChanges) {
            clearDraft()
            onBack()
            return
        }
        if (isNewNote && title.isBlank() && contentValue.text.isBlank()) {
            clearDraft()
            onBack()
            return
        }
        showExitConfirmDialog = true
    }

    BackHandler { handleBack() }

    val selStart = minOf(contentValue.selection.start, contentValue.selection.end)
    val selEnd = maxOf(contentValue.selection.start, contentValue.selection.end)
    val hasSelection = selStart != selEnd

    val isBoldActive = if (hasSelection) {
        RichTextDocument.isTypeActive(documentSpans, selStart, selEnd, DocumentSpan.TYPE_BOLD)
    } else activeTypingBold

    val isItalicActive = if (hasSelection) {
        RichTextDocument.isTypeActive(documentSpans, selStart, selEnd, DocumentSpan.TYPE_ITALIC)
    } else activeTypingItalic

    val isUnderlineActive = if (hasSelection) {
        RichTextDocument.isTypeActive(documentSpans, selStart, selEnd, DocumentSpan.TYPE_UNDERLINE)
    } else activeTypingUnderline

    val isStrikethroughActive = if (hasSelection) {
        RichTextDocument.isTypeActive(documentSpans, selStart, selEnd, DocumentSpan.TYPE_STRIKETHROUGH)
    } else activeTypingStrikethrough

    val isHighlightActive = if (hasSelection) {
        documentSpans.any { it.type == DocumentSpan.TYPE_HIGHLIGHT && it.start <= selStart && it.end >= selEnd }
    } else (activeTypingHighlight != null)

    fun toggleBold() {
        if (hasSelection) {
            documentSpans = RichTextDocument.toggleSpan(documentSpans, selStart, selEnd, DocumentSpan.TYPE_BOLD)
            hasUnsavedChanges = true
            isSaved = false
        } else {
            activeTypingBold = !activeTypingBold
        }
    }

    fun toggleItalic() {
        if (hasSelection) {
            documentSpans = RichTextDocument.toggleSpan(documentSpans, selStart, selEnd, DocumentSpan.TYPE_ITALIC)
            hasUnsavedChanges = true
            isSaved = false
        } else {
            activeTypingItalic = !activeTypingItalic
        }
    }

    fun toggleUnderline() {
        if (hasSelection) {
            documentSpans = RichTextDocument.toggleSpan(documentSpans, selStart, selEnd, DocumentSpan.TYPE_UNDERLINE)
            hasUnsavedChanges = true
            isSaved = false
        } else {
            activeTypingUnderline = !activeTypingUnderline
        }
    }

    fun toggleStrikethrough() {
        if (hasSelection) {
            documentSpans = RichTextDocument.toggleSpan(documentSpans, selStart, selEnd, DocumentSpan.TYPE_STRIKETHROUGH)
            hasUnsavedChanges = true
            isSaved = false
        } else {
            activeTypingStrikethrough = !activeTypingStrikethrough
        }
    }

    fun applyHighlight(colorHex: String?) {
        if (hasSelection) {
            if (colorHex == null) {
                documentSpans = documentSpans.filterNot {
                    it.type == DocumentSpan.TYPE_HIGHLIGHT && it.start >= selStart && it.end <= selEnd
                }
            } else {
                documentSpans = RichTextDocument.toggleSpan(documentSpans, selStart, selEnd, DocumentSpan.TYPE_HIGHLIGHT, colorHex)
            }
            hasUnsavedChanges = true
            isSaved = false
        } else {
            activeTypingHighlight = colorHex
        }
    }

    fun applyBlockStyle(type: String?) {
        val text = contentValue.text
        val lineStart = text.lastIndexOf('\n', (selStart - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
        val lineEnd = text.indexOf('\n', selEnd).let { if (it == -1) text.length else it }
        if (lineEnd > lineStart) {
            val filtered = documentSpans.filterNot {
                (it.type == DocumentSpan.TYPE_HEADING_1 ||
                    it.type == DocumentSpan.TYPE_HEADING_2 ||
                    it.type == DocumentSpan.TYPE_HEADING_3 ||
                    it.type == DocumentSpan.TYPE_QUOTE ||
                    it.type == DocumentSpan.TYPE_CODE) &&
                    it.start >= lineStart && it.end <= lineEnd
            }
            documentSpans = if (type != null) filtered + DocumentSpan(lineStart, lineEnd, type) else filtered
            hasUnsavedChanges = true
            isSaved = false
        }
    }

    fun insertLinePrefix(prefix: String) {
        val text = contentValue.text
        val lineStart = text.lastIndexOf('\n', (selStart - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
        val newText = text.substring(0, lineStart) + prefix + text.substring(lineStart)
        val shift = prefix.length
        documentSpans = documentSpans.map { s ->
            if (s.start >= lineStart) {
                s.copy(start = s.start + shift, end = s.end + shift)
            } else if (s.end > lineStart) {
                s.copy(end = s.end + shift)
            } else s
        }
        contentValue = TextFieldValue(newText, TextRange(selStart + shift))
        hasUnsavedChanges = true
        isSaved = false
    }

    val isCleanSaved = isSaved && !hasUnsavedChanges

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = if (isNewNote) "New Note" else "Edit Note",
                subtitle = if (isCleanSaved) "Saved" else "Unsaved changes",
                onNavigationClick = { handleBack() },
                testTag = "note_editor_top_bar",
                actions = {
                    JlabIconButton(
                        icon = if (isPreviewMode) Icons.Default.Edit else Icons.Default.Visibility,
                        contentDescription = if (isPreviewMode) "Switch to Edit Mode" else "Switch to Preview Mode",
                        onClick = { isPreviewMode = !isPreviewMode },
                        tint = if (isPreviewMode) AppTheme.colorScheme.primary else AppTheme.colorScheme.onSurfaceVariant,
                        testTag = "note_editor_preview_toggle"
                    )

                    JlabPrimaryButton(
                        text = "Save",
                        icon = Icons.Default.Check,
                        enabled = hasUnsavedChanges || title.isNotBlank() || contentValue.text.isNotBlank(),
                        onClick = { performSave(shouldExit = false) },
                        testTag = "note_editor_save_button"
                    )

                    Box {
                        JlabIconButton(
                            icon = Icons.Default.MoreVert,
                            contentDescription = "More note options",
                            onClick = { showMoreMenu = true },
                            testTag = "note_editor_more_menu"
                        )
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isPinned) "Unpin Note" else "Pin Note") },
                                onClick = {
                                    isPinned = !isPinned
                                    showMoreMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Note Details") },
                                onClick = {
                                    showMoreMenu = false
                                    showNoteInfoDialog = true
                                },
                                leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) }
                            )
                            if (onMoveToTrash != null && !isNewNote) {
                                HorizontalDivider(color = AppTheme.extendedColors.cardBorder)
                                DropdownMenuItem(
                                    text = { Text("Move to Trash", color = AppTheme.colorScheme.error) },
                                    onClick = {
                                        showMoreMenu = false
                                        onMoveToTrash()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = AppTheme.colorScheme.error
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!isPreviewMode) {
                NoteEditorFormattingBar(
                    isBoldActive = isBoldActive,
                    isItalicActive = isItalicActive,
                    isUnderlineActive = isUnderlineActive,
                    isStrikethroughActive = isStrikethroughActive,
                    isHighlightActive = isHighlightActive,
                    onToggleBold = ::toggleBold,
                    onToggleItalic = ::toggleItalic,
                    onToggleUnderline = ::toggleUnderline,
                    onToggleStrikethrough = ::toggleStrikethrough,
                    onApplyHighlight = ::applyHighlight,
                    onApplyBlockStyle = ::applyBlockStyle,
                    onInsertLinePrefix = ::insertLinePrefix
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
            // Metadata Bar: Category selector + status badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppTheme.spacing.screenHorizontal,
                        vertical = AppTheme.spacing.xs
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = AppTheme.extendedShapes.chip,
                    color = AppTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                    border = BorderStroke(AppTheme.spacing.borderThin, AppTheme.extendedColors.cardBorder),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(AppTheme.extendedShapes.chip)
                        .clickable { showCategorySheet = true }
                        .testTag("note_category_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = AppTheme.spacing.smPlus,
                            vertical = AppTheme.spacing.xs
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(AppTheme.spacing.iconXs),
                            tint = AppTheme.colorScheme.primary
                        )
                        Text(
                            text = category,
                            style = AppTheme.typography.labelMedium,
                            color = AppTheme.colorScheme.onSecondaryContainer
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change category",
                            modifier = Modifier.size(AppTheme.spacing.iconXs),
                            tint = AppTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    JlabTagBadge(
                        text = if (isCleanSaved) "Saved" else "Draft",
                        containerColor = if (isCleanSaved) AppTheme.extendedColors.successContainer else AppTheme.colorScheme.surfaceContainerHighest,
                        contentColor = if (isCleanSaved) AppTheme.extendedColors.onSuccessContainer else AppTheme.colorScheme.onSurfaceVariant
                    )
                    if (isPinned) {
                        JlabTagBadge(
                            text = "Pinned",
                            icon = Icons.Default.PushPin,
                            containerColor = AppTheme.colorScheme.primaryContainer,
                            contentColor = AppTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = AppTheme.spacing.screenHorizontal),
                color = AppTheme.extendedColors.cardBorder
            )

            if (isPreviewMode) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(AppTheme.spacing.screenPadding)
                ) {
                    Text(
                        text = title.ifBlank { "Untitled Note" },
                        style = AppTheme.typography.headlineMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.md))

                    if (contentValue.text.isBlank()) {
                        Text(
                            text = "Empty note",
                            style = AppTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val lines = contentValue.text.lines()
                        var charOffset = 0

                        lines.forEachIndexed { index, line ->
                            val lineLen = line.length
                            val lineSpans = documentSpans.filter {
                                it.start < charOffset + lineLen && it.end > charOffset
                            }.map { s ->
                                s.copy(
                                    start = (s.start - charOffset).coerceAtLeast(0),
                                    end = (s.end - charOffset).coerceAtMost(lineLen)
                                )
                            }

                            when {
                                line.startsWith("- [ ] ") || line.startsWith("- [x] ") -> {
                                    val isChecked = line.startsWith("- [x] ")
                                    val checkText = line.removePrefix("- [ ] ").removePrefix("- [x] ")
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .minimumInteractiveComponentSize()
                                            .clickable {
                                                val newPrefix = if (isChecked) "- [ ] " else "- [x] "
                                                val updatedLines = lines.toMutableList()
                                                updatedLines[index] = newPrefix + checkText
                                                contentValue = TextFieldValue(updatedLines.joinToString("\n"))
                                                hasUnsavedChanges = true
                                                isSaved = false
                                            }
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                val newPrefix = if (checked) "- [x] " else "- [ ] "
                                                val updatedLines = lines.toMutableList()
                                                updatedLines[index] = newPrefix + checkText
                                                contentValue = TextFieldValue(updatedLines.joinToString("\n"))
                                                hasUnsavedChanges = true
                                                isSaved = false
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                                        Text(
                                            text = RichTextDocument.documentToAnnotatedString(
                                                checkText,
                                                lineSpans,
                                                isDarkTheme = isDarkTheme
                                            ),
                                            style = AppTheme.typography.bodyLarge,
                                            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                            color = if (isChecked) AppTheme.colorScheme.onSurfaceVariant else AppTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                line == "---" -> {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = AppTheme.spacing.sm),
                                        color = AppTheme.extendedColors.cardBorder
                                    )
                                }
                                line.startsWith("• ") || line.startsWith("- ") -> {
                                    val itemText = line.removePrefix("• ").removePrefix("- ")
                                    Row(modifier = Modifier.padding(vertical = AppTheme.spacing.xxs)) {
                                        Text(
                                            text = "• ",
                                            style = AppTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = RichTextDocument.documentToAnnotatedString(
                                                itemText,
                                                lineSpans,
                                                isDarkTheme = isDarkTheme
                                            ),
                                            style = AppTheme.typography.bodyLarge,
                                            color = AppTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                else -> {
                                    if (line.isNotBlank()) {
                                        Text(
                                            text = RichTextDocument.documentToAnnotatedString(
                                                line,
                                                lineSpans,
                                                isDarkTheme = isDarkTheme
                                            ),
                                            style = AppTheme.typography.bodyLarge,
                                            color = AppTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(vertical = AppTheme.spacing.xxs)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
                                    }
                                }
                            }
                            charOffset += lineLen + 1
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = AppTheme.spacing.screenHorizontal,
                            vertical = AppTheme.spacing.md
                        )
                ) {
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it },
                        textStyle = AppTheme.typography.headlineSmall.copy(
                            color = AppTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(AppTheme.colorScheme.primary),
                        singleLine = false,
                        maxLines = 3,
                        decorationBox = { innerTextField ->
                            if (title.isEmpty()) {
                                Text(
                                    text = "Note title",
                                    style = AppTheme.typography.headlineSmall,
                                    color = AppTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_editor_title_input")
                    )

                    Spacer(modifier = Modifier.height(AppTheme.spacing.md))

                    BasicTextField(
                        value = contentValue,
                        onValueChange = { newTv ->
                            val oldText = contentValue.text
                            val newText = newTv.text
                            if (oldText != newText) {
                                val diff = newText.length - oldText.length
                                val changeIndex = minOf(contentValue.selection.start, newTv.selection.start)
                                val updatedSpans = mutableListOf<DocumentSpan>()

                                for (span in documentSpans) {
                                    if (span.end <= changeIndex) {
                                        updatedSpans.add(span)
                                    } else if (span.start >= changeIndex + (if (diff < 0) -diff else 0)) {
                                        val newStart = (span.start + diff).coerceAtLeast(0)
                                        val newEnd = (span.end + diff).coerceAtLeast(newStart)
                                        if (newEnd > newStart) updatedSpans.add(span.copy(start = newStart, end = newEnd))
                                    } else {
                                        val newEnd = (span.end + diff).coerceAtLeast(span.start)
                                        if (newEnd > span.start) updatedSpans.add(span.copy(end = newEnd))
                                    }
                                }

                                if (diff > 0) {
                                    val typedStart = changeIndex
                                    val typedEnd = changeIndex + diff
                                    if (activeTypingBold) updatedSpans.add(DocumentSpan(typedStart, typedEnd, DocumentSpan.TYPE_BOLD))
                                    if (activeTypingItalic) updatedSpans.add(DocumentSpan(typedStart, typedEnd, DocumentSpan.TYPE_ITALIC))
                                    if (activeTypingUnderline) updatedSpans.add(DocumentSpan(typedStart, typedEnd, DocumentSpan.TYPE_UNDERLINE))
                                    if (activeTypingStrikethrough) updatedSpans.add(DocumentSpan(typedStart, typedEnd, DocumentSpan.TYPE_STRIKETHROUGH))
                                    if (activeTypingHighlight != null) updatedSpans.add(DocumentSpan(typedStart, typedEnd, DocumentSpan.TYPE_HIGHLIGHT, activeTypingHighlight))
                                }

                                documentSpans = RichTextDocument.mergeContiguousSpans(updatedSpans)
                                hasUnsavedChanges = true
                                isSaved = false
                            }
                            contentValue = newTv
                        },
                        visualTransformation = RichTextDocument.DocumentVisualTransformation(
                            spans = documentSpans,
                            isDarkTheme = isDarkTheme
                        ),
                        textStyle = AppTheme.typography.bodyLarge.copy(
                            color = AppTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(AppTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 360.dp)
                            .focusRequester(contentFocusRequester)
                            .testTag("note_editor_body_input"),
                        decorationBox = { innerTextField ->
                            if (contentValue.text.isEmpty()) {
                                Text(
                                    text = "Type your thoughts, ideas, or meeting notes...\nSelect text and use the formatting bar below.",
                                    style = AppTheme.typography.bodyLarge,
                                    color = AppTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }
        }
    }

    if (showExitConfirmDialog) {
        NoteUnsavedChangesDialog(
            onSaveAndExit = {
                showExitConfirmDialog = false
                performSave(shouldExit = true)
            },
            onDiscard = {
                showExitConfirmDialog = false
                clearDraft()
                onBack()
            },
            onCancel = { showExitConfirmDialog = false }
        )
    }

    if (showCategorySheet) {
        NoteCategoryBottomSheet(
            currentCategory = category,
            existingCategories = existingCategories,
            onSelectCategory = { selected ->
                category = selected
                showCategorySheet = false
            },
            onDismiss = { showCategorySheet = false }
        )
    }

    if (showNoteInfoDialog) {
        NoteInfoDialog(
            plainText = contentValue.text,
            category = category,
            initialNote = initialNote,
            onDismiss = { showNoteInfoDialog = false }
        )
    }
}
