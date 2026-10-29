package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ============================================================================
// Standardized 8.dp Grid & Component Dimension Tokens
// ============================================================================
@Immutable
data class JlabSpacingTokens(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val smPlus: Dp = 10.dp,
    val md: Dp = 12.dp,
    val mdPlus: Dp = 14.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,

    // Screen & Section Rhythm
    val screenPadding: Dp = 16.dp,
    val screenHorizontal: Dp = 16.dp,
    val screenVertical: Dp = 12.dp,
    val sectionGap: Dp = 16.dp,
    val itemGap: Dp = 10.dp,
    val compactGap: Dp = 6.dp,
    val listBottomPadding: Dp = 88.dp,

    // Card & Surface Padding
    val cardPadding: Dp = 16.dp,
    val compactCardPadding: Dp = 12.dp,
    val sheetHorizontal: Dp = 20.dp,
    val sheetBottom: Dp = 32.dp,
    val badgeHorizontal: Dp = 8.dp,
    val badgeVertical: Dp = 3.dp,

    // Borders & Progress Bars
    val borderThin: Dp = 1.dp,
    val borderSelected: Dp = 2.dp,
    val progressHeight: Dp = 6.dp,
    val progressHeightThin: Dp = 4.dp,

    // Accessibility & Sizing
    val minTouchTarget: Dp = 48.dp,
    val buttonHeight: Dp = 48.dp,
    val buttonHeightLg: Dp = 52.dp,
    val iconXs: Dp = 14.dp,
    val iconSm: Dp = 16.dp,
    val iconMd: Dp = 20.dp,
    val iconLg: Dp = 24.dp,
    val iconXl: Dp = 32.dp,
    val avatarSm: Dp = 36.dp,
    val avatarMd: Dp = 40.dp,
    val avatarLg: Dp = 56.dp,
    val avatarXl: Dp = 72.dp
)

val DefaultJlabSpacingTokens = JlabSpacingTokens()

val LocalSpacing = staticCompositionLocalOf { DefaultJlabSpacingTokens }

/**
 * Convenience singleton aligned with [JlabSpacingTokens] so static and composable call-sites
 * share one authoritative spacing scale.
 */
object JlabSpacing {
    val none = DefaultJlabSpacingTokens.none
    val xxs = DefaultJlabSpacingTokens.xxs
    val xs = DefaultJlabSpacingTokens.xs
    val sm = DefaultJlabSpacingTokens.sm
    val smPlus = DefaultJlabSpacingTokens.smPlus
    val md = DefaultJlabSpacingTokens.md
    val mdPlus = DefaultJlabSpacingTokens.mdPlus
    val lg = DefaultJlabSpacingTokens.lg
    val xl = DefaultJlabSpacingTokens.xl
    val xxl = DefaultJlabSpacingTokens.xxl
    val xxxl = DefaultJlabSpacingTokens.xxxl
    val screenPadding = DefaultJlabSpacingTokens.screenPadding
    val screenHorizontal = DefaultJlabSpacingTokens.screenHorizontal
    val screenVertical = DefaultJlabSpacingTokens.screenVertical
    val sectionGap = DefaultJlabSpacingTokens.sectionGap
    val itemGap = DefaultJlabSpacingTokens.itemGap
    val compactGap = DefaultJlabSpacingTokens.compactGap
    val cardPadding = DefaultJlabSpacingTokens.cardPadding
    val compactCardPadding = DefaultJlabSpacingTokens.compactCardPadding
    val minTouchTarget = DefaultJlabSpacingTokens.minTouchTarget
}
