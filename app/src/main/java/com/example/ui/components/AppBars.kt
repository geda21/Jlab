package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JlabTopBar(
    title: String,
    subtitle: String? = null,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    isDarkTheme: Boolean? = null,
    onToggleTheme: (() -> Unit)? = null,
    testTag: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val resolvedIsDark = isDarkTheme ?: AppTheme.isDark
    val resolvedNavIcon = navigationIcon ?: if (onNavigationClick != null) {
        Icons.AutoMirrored.Filled.ArrowBack
    } else {
        null
    }

    TopAppBar(
        modifier = if (testTag != null) Modifier.testTag(testTag) else Modifier,
        title = {
            Column {
                Text(
                    text = title,
                    style = AppTheme.typography.titleLarge,
                    color = AppTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = AppTheme.typography.labelMedium,
                        color = AppTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            if (resolvedNavIcon != null && onNavigationClick != null) {
                IconButton(
                    onClick = onNavigationClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("top_bar_nav_button")
                ) {
                    Icon(
                        imageVector = resolvedNavIcon,
                        contentDescription = "Navigate back",
                        tint = AppTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            if (onToggleTheme != null) {
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("top_bar_theme_toggle")
                ) {
                    Icon(
                        imageVector = if (resolvedIsDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (resolvedIsDark) "Switch to light theme" else "Switch to dark theme",
                        tint = AppTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppTheme.colorScheme.background,
            scrolledContainerColor = AppTheme.colorScheme.surface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JlabSearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    onClose: () -> Unit,
    testTag: String = "search_top_bar"
) {
    TopAppBar(
        modifier = Modifier.testTag(testTag),
        navigationIcon = {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("${testTag}_close")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Close search",
                    tint = AppTheme.colorScheme.onSurface
                )
            }
        },
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = AppTheme.typography.bodyLarge,
                        color = AppTheme.colorScheme.onSurfaceVariant
                    )
                },
                textStyle = AppTheme.typography.bodyLarge.copy(color = AppTheme.colorScheme.onSurface),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${testTag}_input")
            )
        },
        actions = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("${testTag}_clear")
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search query",
                        tint = AppTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppTheme.colorScheme.surface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JlabSelectionTopBar(
    selectedCount: Int,
    allSelected: Boolean,
    onClearSelection: () -> Unit,
    onToggleSelectAll: () -> Unit,
    testTag: String = "selection_top_bar",
    extraActions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = Modifier.testTag(testTag),
        navigationIcon = {
            IconButton(
                onClick = onClearSelection,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("${testTag}_close")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear selection",
                    tint = AppTheme.colorScheme.onSurface
                )
            }
        },
        title = {
            Text(
                text = "$selectedCount selected",
                style = AppTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colorScheme.onSurface
            )
        },
        actions = {
            extraActions()
            TextButton(
                onClick = onToggleSelectAll,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("${testTag}_select_all")
            ) {
                Text(
                    text = if (allSelected) "Deselect" else "Select all",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppTheme.colorScheme.surfaceContainerHigh
        )
    )
}

@Composable
fun JlabSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    testTag: String = "inline_search_bar"
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = placeholder,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = AppTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(AppTheme.spacing.iconMd)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("${testTag}_clear")
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = AppTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(AppTheme.spacing.iconSm)
                    )
                }
            } else {
                trailingContent?.invoke()
            }
        },
        singleLine = true,
        textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colorScheme.onSurface),
        shape = AppTheme.extendedShapes.input,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AppTheme.colorScheme.surface,
            unfocusedContainerColor = AppTheme.colorScheme.surface,
            focusedBorderColor = AppTheme.colorScheme.primary,
            unfocusedBorderColor = AppTheme.extendedColors.cardBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}
