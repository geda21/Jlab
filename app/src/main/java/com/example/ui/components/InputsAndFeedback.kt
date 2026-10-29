package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.theme.AppTheme

@Composable
fun JlabTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    testTag: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = if (label != null) {
            { Text(text = label, style = AppTheme.typography.bodySmall) }
        } else {
            null
        },
        placeholder = if (placeholder != null) {
            { Text(text = placeholder, style = AppTheme.typography.bodyMedium) }
        } else {
            null
        },
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(AppTheme.spacing.iconMd)
                )
            }
        } else {
            null
        },
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        readOnly = readOnly,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colorScheme.onSurface),
        shape = AppTheme.extendedShapes.input,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppTheme.colorScheme.primary,
            unfocusedBorderColor = AppTheme.extendedColors.cardBorder,
            focusedContainerColor = AppTheme.colorScheme.surface,
            unfocusedContainerColor = AppTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    )
}

@Composable
fun JlabConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String = "Cancel",
    isDestructive: Boolean = false,
    icon: ImageVector? = null,
    testTag: String = "confirm_dialog"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag(testTag),
        icon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDestructive) AppTheme.extendedColors.danger else AppTheme.colorScheme.primary
                )
            }
        } else {
            null
        },
        title = {
            Text(
                text = title,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = message,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            if (isDestructive) {
                JlabDestructiveButton(
                    text = confirmText,
                    onClick = onConfirm,
                    testTag = "${testTag}_confirm"
                )
            } else {
                JlabPrimaryButton(
                    text = confirmText,
                    onClick = onConfirm,
                    testTag = "${testTag}_confirm"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("${testTag}_dismiss")
            ) {
                Text(
                    text = dismissText,
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface
    )
}

@Composable
fun JlabSelectionBottomBar(
    deleteText: String = "Delete",
    onDelete: () -> Unit,
    testTag: String = "selection_bottom_bar",
    extraActions: (@Composable RowScope.() -> Unit)? = null
) {
    Surface(
        color = AppTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = AppTheme.spacing.xs,
        shadowElevation = AppTheme.spacing.sm,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = AppTheme.spacing.screenHorizontal, vertical = AppTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            extraActions?.invoke(this)
            JlabDestructiveButton(
                text = deleteText,
                icon = Icons.Default.Delete,
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                testTag = "${testTag}_delete"
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    count: Int? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppTheme.spacing.screenHorizontal,
                vertical = AppTheme.spacing.compactGap
            )
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = AppTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colorScheme.onSurface
            )
            if (count != null) {
                Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
                Surface(
                    color = AppTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Text(
                        text = count.toString(),
                        color = AppTheme.colorScheme.onPrimaryContainer,
                        style = AppTheme.extendedTypography.badge,
                        modifier = Modifier.padding(
                            horizontal = AppTheme.spacing.badgeHorizontal,
                            vertical = AppTheme.spacing.xxs
                        )
                    )
                }
            }
        }
        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(
                    horizontal = AppTheme.spacing.sm,
                    vertical = AppTheme.spacing.xxs
                ),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("section_action_${title.lowercase().replace(" ", "_")}")
            ) {
                Text(
                    text = actionText,
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTheme.spacing.xxxl)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(AppTheme.spacing.avatarLg)
                .clip(CircleShape)
                .background(AppTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppTheme.colorScheme.primary,
                modifier = Modifier.size(AppTheme.spacing.iconLg)
            )
        }
        Spacer(modifier = Modifier.height(AppTheme.spacing.mdPlus))
        Text(
            text = title,
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
        Text(
            text = message,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.lg))
            JlabPrimaryButton(
                text = actionButtonText,
                icon = Icons.Default.Add,
                onClick = onActionClick,
                testTag = "empty_state_cta"
            )
        }
    }
}
