package com.example.ui.screens.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.entity.*
import com.example.ui.components.*
import com.example.ui.screens.notes.RichTextParser
import com.example.ui.theme.AppTheme

data class SearchResult(
    val id: String,
    val type: String, // "TASK", "PROJECT", "NOTE", "PROMPT", "INFORMATION", "LEARNING", "BUSINESS"
    val title: String,
    val subtitle: String,
    val category: String? = null
)

@Composable
fun GlobalSearchScreen(
    tasks: List<TaskEntity>,
    projects: List<ProjectEntity>,
    notes: List<NoteEntity>,
    prompts: List<PromptEntity>,
    infoList: List<InformationEntity>,
    learningList: List<LearningEntity>,
    businessList: List<BusinessEntity>,
    onSelectResult: (SearchResult) -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    BackHandler { onNavigateBack() }

    val results = remember(searchQuery, tasks, projects, notes, prompts, infoList, learningList, businessList) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val q = searchQuery.trim().lowercase()
            val list = mutableListOf<SearchResult>()

            tasks.filter { it.title.lowercase().contains(q) || it.description.lowercase().contains(q) }
                .forEach { list.add(SearchResult(it.id, "TASK", it.title, it.description, it.priority)) }

            projects.filter { it.name.lowercase().contains(q) || it.objective.lowercase().contains(q) }
                .forEach { list.add(SearchResult(it.id, "PROJECT", it.name, it.objective, it.status)) }

            notes.filter { it.title.lowercase().contains(q) || it.content.lowercase().contains(q) }
                .forEach {
                    list.add(
                        SearchResult(
                            it.id,
                            "NOTE",
                            it.title.ifBlank { "Untitled Note" },
                            RichTextParser.toPlainText(it.content),
                            it.category
                        )
                    )
                }

            prompts.filter { it.title.lowercase().contains(q) || it.fullPrompt.lowercase().contains(q) }
                .forEach { list.add(SearchResult(it.id, "PROMPT", it.title, it.fullPrompt, it.category)) }

            infoList.filter { it.title.lowercase().contains(q) || it.information.lowercase().contains(q) }
                .forEach { list.add(SearchResult(it.id, "INFORMATION", it.title, it.information, it.category)) }

            learningList.filter { it.title.lowercase().contains(q) || it.goal.lowercase().contains(q) }
                .forEach { list.add(SearchResult(it.id, "LEARNING", it.title, it.goal, it.type)) }

            businessList.filter { it.name.lowercase().contains(q) || it.description.lowercase().contains(q) }
                .forEach { list.add(SearchResult(it.id, "BUSINESS", it.name, it.description)) }

            list
        }
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Global Search",
                subtitle = "Search everything offline",
                onNavigationClick = onNavigateBack,
                testTag = "global_search_top_bar"
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
                placeholder = "Search tasks, projects, notes, prompts, knowledge...",
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.screenHorizontal,
                    vertical = AppTheme.spacing.sm
                ),
                testTag = "global_search_input"
            )

            when {
                searchQuery.isBlank() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyState(
                            icon = Icons.Default.ManageSearch,
                            title = "Search Across Workspace",
                            message = "Type any keyword to search across tasks, projects, notes, prompts, learning, and vaults.",
                            testTag = "global_search_idle_state"
                        )
                    }
                }
                results.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyState(
                            icon = Icons.Default.SearchOff,
                            title = "No results found",
                            message = "No matching items for '$searchQuery'.",
                            testTag = "global_search_empty_state"
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = AppTheme.spacing.screenHorizontal,
                            end = AppTheme.spacing.screenHorizontal,
                            top = AppTheme.spacing.xs,
                            bottom = AppTheme.spacing.listBottomPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap)
                    ) {
                        items(results, key = { "${it.type}_${it.id}" }) { res ->
                            JlabCard(
                                shape = AppTheme.extendedShapes.compactCard,
                                contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding),
                                onClick = { onSelectResult(res) },
                                testTag = "search_result_${res.type.lowercase()}_${res.id}"
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.smPlus)
                                ) {
                                    JlabTagBadge(
                                        text = res.type,
                                        containerColor = AppTheme.colorScheme.primaryContainer,
                                        contentColor = AppTheme.colorScheme.onPrimaryContainer
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = res.title,
                                            style = AppTheme.typography.titleMedium,
                                            color = AppTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (res.subtitle.isNotBlank()) {
                                            Text(
                                                text = res.subtitle,
                                                style = AppTheme.typography.bodySmall,
                                                color = AppTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
