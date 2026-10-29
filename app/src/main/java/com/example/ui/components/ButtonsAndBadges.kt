package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.AppTheme

@Composable
fun JlabPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = AppTheme.extendedShapes.button,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.colorScheme.primary,
            contentColor = AppTheme.colorScheme.onPrimary
        ),
        modifier = modifier
            .heightIn(min = AppTheme.spacing.buttonHeight)
            .minimumInteractiveComponentSize()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(AppTheme.spacing.iconSm)
            )
            Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
        }
        Text(
            text = text,
            style = AppTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun JlabOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    contentColor: Color = AppTheme.colorScheme.onSurface,
    testTag: String? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = AppTheme.extendedShapes.button,
        border = BorderStroke(AppTheme.spacing.borderThin, AppTheme.extendedColors.cardBorder),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
        modifier = modifier
            .heightIn(min = AppTheme.spacing.buttonHeight)
            .minimumInteractiveComponentSize()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(AppTheme.spacing.iconSm)
            )
            Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
        }
        Text(
            text = text,
            style = AppTheme.typography.labelLarge
        )
    }
}

@Composable
fun JlabTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        shape = AppTheme.extendedShapes.button,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = AppTheme.colorScheme.primaryContainer,
            contentColor = AppTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
            .heightIn(min = AppTheme.spacing.buttonHeight)
            .minimumInteractiveComponentSize()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(AppTheme.spacing.iconSm)
            )
            Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
        }
        Text(
            text = text,
            style = AppTheme.typography.labelLarge
        )
    }
}

@Composable
fun JlabDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = AppTheme.extendedShapes.button,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.extendedColors.dangerContainer,
            contentColor = AppTheme.extendedColors.onDangerContainer
        ),
        modifier = modifier
            .heightIn(min = AppTheme.spacing.buttonHeight)
            .minimumInteractiveComponentSize()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(AppTheme.spacing.iconSm)
            )
            Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
        }
        Text(
            text = text,
            style = AppTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun JlabIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = AppTheme.colorScheme.onSurfaceVariant,
    enabled: Boolean = true,
    testTag: String? = null
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@Composable
fun JlabFloatingActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "screen_fab"
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = AppTheme.colorScheme.primary,
        contentColor = AppTheme.colorScheme.onPrimary,
        shape = AppTheme.shapes.large,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun PriorityBadge(
    priority: String,
    modifier: Modifier = Modifier
) {
    val extended = AppTheme.extendedColors
    val (bgColor, textColor) = when (priority.uppercase()) {
        "CRITICAL", "URGENT" -> extended.dangerContainer to extended.onDangerContainer
        "HIGH" -> extended.warningContainer to extended.onWarningContainer
        "MEDIUM" -> AppTheme.colorScheme.primaryContainer to AppTheme.colorScheme.onPrimaryContainer
        else -> extended.neutralContainer to extended.onNeutralContainer
    }

    JlabTagBadge(
        text = priority.uppercase(),
        containerColor = bgColor,
        contentColor = textColor,
        modifier = modifier
    )
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val extended = AppTheme.extendedColors
    val (bgColor, textColor) = when (status.uppercase()) {
        "COMPLETED", "PUBLISHED", "DONE", "RESOLVED", "ACHIEVED" ->
            extended.successContainer to extended.onSuccessContainer
        "IN_PROGRESS", "ACTIVE", "LEARNING", "SCHEDULED" ->
            extended.infoContainer to extended.onInfoContainer
        "WAITING", "PAUSED", "PLANNED", "PLANNING", "DRAFT", "IDEA", "REVIEW", "NOT_STARTED" ->
            extended.warningContainer to extended.onWarningContainer
        "BLOCKED", "OVERDUE" ->
            extended.dangerContainer to extended.onDangerContainer
        else ->
            extended.neutralContainer to extended.onNeutralContainer
    }

    JlabTagBadge(
        text = status.replace("_", " "),
        containerColor = bgColor,
        contentColor = textColor,
        modifier = modifier
    )
}

@Composable
fun JlabTagBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = AppTheme.colorScheme.surfaceVariant,
    contentColor: Color = AppTheme.colorScheme.onSurfaceVariant,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    val clickMod = if (onClick != null) {
        Modifier
            .clip(AppTheme.extendedShapes.badge)
            .clickable(onClick = onClick)
    } else {
        Modifier
    }
    Surface(
        color = containerColor,
        shape = AppTheme.extendedShapes.badge,
        modifier = modifier.then(clickMod)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.badgeHorizontal,
                vertical = AppTheme.spacing.badgeVertical
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(AppTheme.spacing.iconXs)
                )
                Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
            }
            Text(
                text = text,
                color = contentColor,
                style = AppTheme.extendedTypography.badge
            )
        }
    }
}

@Composable
fun JlabFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    testTag: String? = null
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = AppTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        },
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.spacing.iconSm)
                )
            }
        } else {
            null
        },
        shape = AppTheme.extendedShapes.chip,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = AppTheme.colorScheme.surface,
            labelColor = AppTheme.colorScheme.onSurfaceVariant,
            iconColor = AppTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = AppTheme.colorScheme.primaryContainer,
            selectedLabelColor = AppTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = AppTheme.colorScheme.onPrimaryContainer
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = AppTheme.extendedColors.cardBorder,
            selectedBorderColor = AppTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    )
}

@Composable
fun JlabFilterChipRow(
    options: List<String>,
    selectedOption: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = AppTheme.spacing.screenHorizontal),
    labelFormatter: (String) -> String = { it }
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
    ) {
        items(options) { option ->
            JlabFilterChip(
                label = labelFormatter(option),
                selected = selectedOption == option,
                onClick = { onSelect(option) },
                testTag = "filter_chip_${option.lowercase().replace(" ", "_")}"
            )
        }
    }
}
