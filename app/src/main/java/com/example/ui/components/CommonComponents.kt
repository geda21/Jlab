package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.AppTheme
import com.example.ui.theme.CoverBusinessEnd
import com.example.ui.theme.CoverBusinessStart
import com.example.ui.theme.CoverDefaultEnd
import com.example.ui.theme.CoverDefaultStart
import com.example.ui.theme.CoverLearningEnd
import com.example.ui.theme.CoverLearningStart
import com.example.ui.theme.CoverPersonalEnd
import com.example.ui.theme.CoverPersonalStart
import com.example.ui.theme.CoverTechEnd
import com.example.ui.theme.CoverTechStart

@Composable
fun UserAvatar(
    avatarUri: String,
    name: String,
    size: Dp = AppTheme.spacing.avatarMd,
    testTag: String? = null,
    onClick: (() -> Unit)? = null
) {
    val interactiveModifier = if (onClick != null) {
        Modifier
            .minimumInteractiveComponentSize()
            .clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .then(interactiveModifier),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = AppTheme.colorScheme.surfaceVariant,
            border = BorderStroke(
                AppTheme.spacing.borderThin,
                AppTheme.colorScheme.primary.copy(alpha = 0.45f)
            ),
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
        ) {
            if (avatarUri.isNotBlank()) {
                AsyncImage(
                    model = avatarUri,
                    contentDescription = "$name profile avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val initial = name.trim().take(1).uppercase().ifBlank { "J" }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(AppTheme.colorScheme.primary, AppTheme.extendedColors.violet)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        color = AppTheme.extendedColors.onMediaOverlay,
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.44f).sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectCoverImage(
    coverUri: String,
    category: String,
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
    shape: Shape = AppTheme.extendedShapes.coverTop
) {
    val gradientColors = projectCoverGradient(category)

    Surface(
        shape = shape,
        color = AppTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
    ) {
        if (coverUri.isNotBlank()) {
            AsyncImage(
                model = coverUri,
                contentDescription = "$category project cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FolderSpecial,
                    contentDescription = null,
                    tint = AppTheme.extendedColors.onMediaOverlay.copy(alpha = 0.25f),
                    modifier = Modifier.size(AppTheme.spacing.minTouchTarget)
                )
            }
        }
    }
}

fun projectCoverGradient(category: String) = when (category.lowercase()) {
    "business", "work", "finance", "marketing" -> listOf(CoverBusinessStart, CoverBusinessEnd)
    "tech", "software", "code", "engineering" -> listOf(CoverTechStart, CoverTechEnd)
    "personal", "life", "health" -> listOf(CoverPersonalStart, CoverPersonalEnd)
    "learning", "education", "study" -> listOf(CoverLearningStart, CoverLearningEnd)
    else -> listOf(CoverDefaultStart, CoverDefaultEnd)
}
