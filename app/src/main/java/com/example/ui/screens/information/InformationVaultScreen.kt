package com.example.ui.screens.information

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.entity.InformationEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabFilterChipRow
import com.example.ui.components.JlabFloatingActionButton
import com.example.ui.components.JlabIconButton
import com.example.ui.components.JlabSearchBar
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTopBar
import com.example.ui.theme.AppTheme

@Composable
fun InformationVaultScreen(
    items: List<InformationEntity>,
    onSaveItem: (InformationEntity) -> Unit,
    onDeleteItem: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf<InformationEntity?>(null) }
    var editingItem by remember { mutableStateOf<InformationEntity?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val categories = listOf(
        "ALL",
        "Credentials",
        "Configuration",
        "Service",
        "Account",
        "Reference",
        "Other"
    )

    BackHandler {
        when {
            editingItem != null || isCreatingNew -> {
                editingItem = null
                isCreatingNew = false
            }
            selectedItem != null -> {
                selectedItem = null
            }
            else -> onNavigateBack()
        }
    }

    if (editingItem != null || isCreatingNew) {
        InformationEditorScreen(
            initialItem = editingItem,
            onSave = { saved ->
                onSaveItem(saved)
                selectedItem = saved
                editingItem = null
                isCreatingNew = false
            },
            onCancel = {
                editingItem = null
                isCreatingNew = false
            }
        )
        return
    }

    if (selectedItem != null) {
        val currentItem = items.find { it.id == selectedItem!!.id } ?: selectedItem!!
        InformationDetailScreen(
            item = currentItem,
            onEdit = { editingItem = currentItem },
            onDelete = {
                onDeleteItem(currentItem.id)
                selectedItem = null
            },
            onCopyValue = { text ->
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Vault Info", text))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            onBack = { selectedItem = null }
        )
        return
    }

    val filteredItems = remember(items, searchQuery, selectedCategoryFilter) {
        var list = items
        if (selectedCategoryFilter != "ALL") {
            list = list.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                    it.summary.lowercase().contains(q) ||
                    it.information.lowercase().contains(q) ||
                    it.tags.lowercase().contains(q)
            }
        }
        list
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Information Vault",
                subtitle = "Facts, technical configs & service setups",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
                testTag = "information_vault_top_bar",
                actions = {
                    JlabIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "Add information",
                        tint = AppTheme.colorScheme.primary,
                        onClick = { isCreatingNew = true },
                        testTag = "information_add_action"
                    )
                }
            )
        },
        floatingActionButton = {
            JlabFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "New information record",
                onClick = { isCreatingNew = true },
                testTag = "information_fab"
            )
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
                placeholder = "Search information, credentials, configs...",
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.compactGap
                ),
                testTag = "information_search_bar"
            )

            JlabFilterChipRow(
                options = categories,
                selectedOption = selectedCategoryFilter,
                onSelect = { selectedCategoryFilter = it },
                modifier = Modifier.padding(vertical = AppTheme.spacing.xs)
            )

            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.LibraryBooks,
                        title = if (searchQuery.isNotEmpty()) {
                            "No matching information"
                        } else {
                            "No information records yet"
                        },
                        message = if (searchQuery.isNotEmpty()) {
                            "Try a different search term or category."
                        } else {
                            "Save server IPs, API setups, service logins, account reference data, and configuration facts."
                        },
                        actionButtonText = "Add Information Record",
                        onActionClick = { isCreatingNew = true },
                        testTag = "information_empty_state"
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
                    items(filteredItems, key = { it.id }) { item ->
                        CompactInformationCard(
                            item = item,
                            onClick = { selectedItem = item }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactInformationCard(
    item: InformationEntity,
    onClick: () -> Unit
) {
    JlabCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(AppTheme.spacing.mdPlus),
        onClick = onClick,
        testTag = "information_card_${item.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            JlabTagBadge(
                text = item.category,
                containerColor = AppTheme.colorScheme.secondaryContainer,
                contentColor = AppTheme.colorScheme.onSecondaryContainer
            )

            if (item.sourceText.isNotBlank()) {
                Text(
                    text = item.sourceText.take(25),
                    style = AppTheme.extendedTypography.caption,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.compactGap))

        Text(
            text = item.title,
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (item.summary.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
            Text(
                text = item.summary,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
