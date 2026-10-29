package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.ui.theme.AppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JlabCard(
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    isSelected: Boolean = false,
    borderColor: Color? = null,
    shape: Shape = AppTheme.extendedShapes.card,
    contentPadding: PaddingValues = PaddingValues(AppTheme.spacing.cardPadding),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val resolvedContainer = containerColor ?: if (isSelected) {
        AppTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    } else {
        AppTheme.colorScheme.surface
    }
    val resolvedBorderWidth = if (isSelected) AppTheme.spacing.borderSelected else AppTheme.spacing.borderThin
    val resolvedBorderColor = borderColor ?: if (isSelected) {
        AppTheme.colorScheme.primary
    } else {
        AppTheme.extendedColors.cardBorder
    }

    val interactionModifier = when {
        onLongClick != null && onClick != null -> Modifier
            .clip(shape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        onClick != null -> Modifier
            .clip(shape)
            .clickable(onClick = onClick)
        else -> Modifier
    }

    Card(
        modifier = modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .then(interactionModifier),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = resolvedContainer),
        border = BorderStroke(resolvedBorderWidth, resolvedBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.spacing.none)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun JlabSectionCard(
    modifier: Modifier = Modifier,
    overline: String? = null,
    title: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = AppTheme.colorScheme.primary,
    containerColor: Color = AppTheme.colorScheme.surface,
    headerAction: (@Composable () -> Unit)? = null,
    testTag: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    JlabCard(
        modifier = modifier,
        containerColor = containerColor,
        testTag = testTag
    ) {
        if (!overline.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = overline.uppercase(),
                    style = AppTheme.extendedTypography.overline,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                headerAction?.invoke()
            }
            Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
        } else if (!title.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(AppTheme.spacing.iconMd)
                        )
                        Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
                    }
                    Text(
                        text = title,
                        style = AppTheme.typography.titleMedium,
                        color = AppTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                headerAction?.invoke()
            }
            Spacer(modifier = Modifier.height(AppTheme.spacing.md))
        }
        content()
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    testTag: String? = null,
    onClick: (() -> Unit)? = null
) {
    JlabCard(
        modifier = modifier,
        contentPadding = PaddingValues(AppTheme.spacing.mdPlus),
        onClick = onClick,
        testTag = testTag
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.spacing.avatarMd)
                    .clip(AppTheme.extendedShapes.button)
                    .background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(AppTheme.spacing.iconMd)
                )
            }
            Spacer(modifier = Modifier.width(AppTheme.spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = value,
                    style = AppTheme.extendedTypography.statValue,
                    color = AppTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = title,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = AppTheme.extendedTypography.caption,
                        color = accentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun JlabMetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color = AppTheme.colorScheme.primary,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    testTag: String? = null
) {
    JlabCard(
        modifier = modifier,
        shape = AppTheme.extendedShapes.compactCard,
        contentPadding = PaddingValues(AppTheme.spacing.compactCardPadding),
        onClick = onClick,
        testTag = testTag
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(AppTheme.spacing.iconSm)
                )
                Spacer(modifier = Modifier.height(AppTheme.spacing.xs))
            }
            Text(
                text = value,
                style = AppTheme.extendedTypography.statValue,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.xxs))
            Text(
                text = label,
                style = AppTheme.extendedTypography.caption,
                color = AppTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun JlabLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color? = null,
    trackColor: Color = AppTheme.colorScheme.surfaceContainerHighest,
    height: Dp = AppTheme.spacing.progressHeight
) {
    val clamped = progress.coerceIn(0f, 1f)
    val resolvedColor = color ?: if (clamped >= 1f) {
        AppTheme.extendedColors.success
    } else {
        AppTheme.colorScheme.primary
    }
    LinearProgressIndicator(
        progress = { clamped },
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(AppTheme.extendedShapes.pill),
        color = resolvedColor,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round
    )
}
