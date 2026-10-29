package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================================
// Core Brand & Semantic Palette Primitives
// ============================================================================

// Indigo Primary Brand
val JlabIndigoPrimary = Color(0xFF4F46E5)
val JlabIndigoLight = Color(0xFF818CF8)
val JlabIndigoDark = Color(0xFF3730A3)
val JlabIndigoContainerLight = Color(0xFFEEF2FF)
val JlabIndigoContainerDark = Color(0xFF2A285E)

// Emerald — Success / Completed
val JlabEmerald = Color(0xFF10B981)
val JlabEmeraldDarkText = Color(0xFF047857)
val JlabEmeraldLight = Color(0xFF34D399)
val JlabEmeraldContainerLight = Color(0xFFECFDF5)
val JlabEmeraldContainerDark = Color(0xFF064E3B)
val JlabOnEmeraldContainerDark = Color(0xFFA7F3D0)

// Amber — Warning / Pending / High Priority
val JlabAmber = Color(0xFFF59E0B)
val JlabAmberDarkText = Color(0xFFB45309)
val JlabAmberLight = Color(0xFFFBBF24)
val JlabAmberContainerLight = Color(0xFFFFFBEB)
val JlabAmberContainerDark = Color(0xFF451A03)
val JlabOnAmberContainerDark = Color(0xFFFDE68A)

// Ruby — Destructive / Critical / Overdue
val JlabRuby = Color(0xFFEF4444)
val JlabRubyDarkText = Color(0xFFB91C1C)
val JlabRubyLight = Color(0xFFF87171)
val JlabRubyContainerLight = Color(0xFFFEF2F2)
val JlabRubyContainerDark = Color(0xFF450A0A)
val JlabOnRubyContainerDark = Color(0xFFFECACA)

// Cyan / Sky — Active / Info
val JlabCyan = Color(0xFF06B6D4)
val JlabCyanDarkText = Color(0xFF0369A1)
val JlabCyanLight = Color(0xFF38BDF8)
val JlabCyanContainerLight = Color(0xFFECFEFF)
val JlabCyanContainerDark = Color(0xFF083344)
val JlabOnCyanContainerDark = Color(0xFFA5F3FC)

// Violet — Prompts / Creative / Knowledge
val JlabViolet = Color(0xFF7C3AED)
val JlabVioletLight = Color(0xFFA78BFA)
val JlabVioletContainerLight = Color(0xFFF5F3FF)
val JlabVioletContainerDark = Color(0xFF2E1065)
val JlabOnVioletContainerDark = Color(0xFFDDD6FE)

// Pink — Business / Ventures / Marketing
val JlabPink = Color(0xFFDB2777)
val JlabPinkLight = Color(0xFFF472B6)
val JlabPinkContainerLight = Color(0xFFFDF2F8)
val JlabPinkContainerDark = Color(0xFF500724)
val JlabOnPinkContainerDark = Color(0xFFFBCFE8)

// Neutral Slate
val JlabSlate = Color(0xFF64748B)
val JlabSlateLight = Color(0xFF94A3B8)
val JlabSlateContainerLight = Color(0xFFF1F5F9)
val JlabSlateContainerDark = Color(0xFF1E293B)

// Social & Platform Accents
val PlatformYouTube = Color(0xFFEF4444)
val PlatformInstagram = Color(0xFFEC4899)
val PlatformLinkedIn = Color(0xFF0A66C2)
val PlatformLinkedInDark = Color(0xFF38BDF8)
val PlatformTikTok = Color(0xFF14B8A6)
val PlatformXLight = Color(0xFF0F172A)
val PlatformXDark = Color(0xFFE2E8F0)

// Rich-Text Note Highlight Palette (High contrast with dark ink)
val HighlightYellow = Color(0xFFFFF176)
val HighlightGreen = Color(0xFFA5D6A7)
val HighlightBlue = Color(0xFF90CAF9)
val HighlightPink = Color(0xFFF48FB1)
val HighlightPurple = Color(0xFFCE93D8)
val HighlightInk = Color(0xFF0F172A)

// Cover Gradient Palette
val CoverBusinessStart = Color(0xFF1E1B4B)
val CoverBusinessEnd = Color(0xFF312E81)
val CoverTechStart = Color(0xFF0F172A)
val CoverTechEnd = Color(0xFF1E293B)
val CoverPersonalStart = Color(0xFF064E3B)
val CoverPersonalEnd = Color(0xFF047857)
val CoverLearningStart = Color(0xFF4C1D95)
val CoverLearningEnd = Color(0xFF6D28D9)
val CoverDefaultStart = Color(0xFF1E293B)
val CoverDefaultEnd = Color(0xFF334155)

// ============================================================================
// Material 3 Dark Theme Surfaces (Layered Obsidian & Charcoal)
// ============================================================================
val DarkBackground = Color(0xFF0A0C10)
val DarkSurface = Color(0xFF12151D)
val DarkSurfaceDim = Color(0xFF0D1017)
val DarkSurfaceBright = Color(0xFF222838)
val DarkSurfaceContainerLowest = Color(0xFF080A0E)
val DarkSurfaceContainerLow = Color(0xFF12151D)
val DarkSurfaceContainer = Color(0xFF181C27)
val DarkSurfaceContainerHigh = Color(0xFF1F2534)
val DarkSurfaceContainerHighest = Color(0xFF272F42)
val DarkSurfaceVariant = Color(0xFF1A1F2C)
val DarkOnBackground = Color(0xFFF8FAFC)
val DarkOnSurface = Color(0xFFF1F5F9)
val DarkOnSurfaceVariant = Color(0xFF94A3B8)
val DarkPrimary = Color(0xFF818CF8)
val DarkOnPrimary = Color(0xFF1E1B4B)
val DarkPrimaryContainer = Color(0xFF312E81)
val DarkOnPrimaryContainer = Color(0xFFE0E7FF)
val DarkSecondary = Color(0xFF38BDF8)
val DarkOnSecondary = Color(0xFF082F49)
val DarkSecondaryContainer = Color(0xFF0C4A6E)
val DarkOnSecondaryContainer = Color(0xFFBAE6FD)
val DarkTertiary = Color(0xFFFBBF24)
val DarkOnTertiary = Color(0xFF451A03)
val DarkTertiaryContainer = Color(0xFF78350F)
val DarkOnTertiaryContainer = Color(0xFFFEF3C7)
val DarkError = Color(0xFFF87171)
val DarkOnError = Color(0xFF450A0A)
val DarkErrorContainer = Color(0xFF7F1D1D)
val DarkOnErrorContainer = Color(0xFFFEE2E2)
val DarkOutline = Color(0xFF2D3548)
val DarkOutlineVariant = Color(0xFF1E2433)

// ============================================================================
// Material 3 Light Theme Surfaces (Soft Neutral Slate & Clean Elevated White)
// ============================================================================
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceDim = Color(0xFFE2E8F0)
val LightSurfaceBright = Color(0xFFFFFFFF)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF8FAFC)
val LightSurfaceContainer = Color(0xFFF1F5F9)
val LightSurfaceContainerHigh = Color(0xFFE2E8F0)
val LightSurfaceContainerHighest = Color(0xFFCBD5E1)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightOnBackground = Color(0xFF0F172A)
val LightOnSurface = Color(0xFF1E293B)
val LightOnSurfaceVariant = Color(0xFF64748B)
val LightPrimary = Color(0xFF4F46E5)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFEEF2FF)
val LightOnPrimaryContainer = Color(0xFF312E81)
val LightSecondary = Color(0xFF0284C7)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFE0F2FE)
val LightOnSecondaryContainer = Color(0xFF0369A1)
val LightTertiary = Color(0xFFD97706)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFFEF3C7)
val LightOnTertiaryContainer = Color(0xFF92400E)
val LightError = Color(0xFFDC2626)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFEF2F2)
val LightOnErrorContainer = Color(0xFF991B1B)
val LightOutline = Color(0xFFCBD5E1)
val LightOutlineVariant = Color(0xFFE2E8F0)

// ============================================================================
// Semantic Extended Color Tokens (Light & Dark Adaptive)
// ============================================================================
@Immutable
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
    val danger: Color,
    val onDanger: Color,
    val dangerContainer: Color,
    val onDangerContainer: Color,
    val violet: Color,
    val violetContainer: Color,
    val onVioletContainer: Color,
    val pink: Color,
    val pinkContainer: Color,
    val onPinkContainer: Color,
    val neutral: Color,
    val neutralContainer: Color,
    val onNeutralContainer: Color,
    val quoteText: Color,
    val codeSurface: Color,
    val mediaScrim: Color,
    val onMediaOverlay: Color,
    val cardBorder: Color,
    val platformYoutube: Color,
    val platformInstagram: Color,
    val platformLinkedin: Color,
    val platformTiktok: Color,
    val platformX: Color
)

val LightExtendedColors = ExtendedColors(
    success = JlabEmeraldDarkText,
    onSuccess = Color.White,
    successContainer = JlabEmeraldContainerLight,
    onSuccessContainer = JlabEmeraldDarkText,
    warning = JlabAmberDarkText,
    onWarning = Color.White,
    warningContainer = JlabAmberContainerLight,
    onWarningContainer = JlabAmberDarkText,
    info = JlabCyanDarkText,
    onInfo = Color.White,
    infoContainer = JlabCyanContainerLight,
    onInfoContainer = JlabCyanDarkText,
    danger = JlabRubyDarkText,
    onDanger = Color.White,
    dangerContainer = JlabRubyContainerLight,
    onDangerContainer = JlabRubyDarkText,
    violet = JlabViolet,
    violetContainer = JlabVioletContainerLight,
    onVioletContainer = JlabViolet,
    pink = JlabPink,
    pinkContainer = JlabPinkContainerLight,
    onPinkContainer = JlabPink,
    neutral = JlabSlate,
    neutralContainer = JlabSlateContainerLight,
    onNeutralContainer = LightOnSurfaceVariant,
    quoteText = JlabIndigoPrimary,
    codeSurface = LightSurfaceContainerHigh,
    mediaScrim = Color(0xA6000000),
    onMediaOverlay = Color.White,
    cardBorder = LightOutlineVariant,
    platformYoutube = PlatformYouTube,
    platformInstagram = PlatformInstagram,
    platformLinkedin = PlatformLinkedIn,
    platformTiktok = PlatformTikTok,
    platformX = PlatformXLight
)

val DarkExtendedColors = ExtendedColors(
    success = JlabEmeraldLight,
    onSuccess = DarkBackground,
    successContainer = JlabEmeraldContainerDark,
    onSuccessContainer = JlabOnEmeraldContainerDark,
    warning = JlabAmberLight,
    onWarning = DarkBackground,
    warningContainer = JlabAmberContainerDark,
    onWarningContainer = JlabOnAmberContainerDark,
    info = JlabCyanLight,
    onInfo = DarkBackground,
    infoContainer = JlabCyanContainerDark,
    onInfoContainer = JlabOnCyanContainerDark,
    danger = JlabRubyLight,
    onDanger = DarkBackground,
    dangerContainer = JlabRubyContainerDark,
    onDangerContainer = JlabOnRubyContainerDark,
    violet = JlabVioletLight,
    violetContainer = JlabVioletContainerDark,
    onVioletContainer = JlabOnVioletContainerDark,
    pink = JlabPinkLight,
    pinkContainer = JlabPinkContainerDark,
    onPinkContainer = JlabOnPinkContainerDark,
    neutral = JlabSlateLight,
    neutralContainer = JlabSlateContainerDark,
    onNeutralContainer = DarkOnSurfaceVariant,
    quoteText = Color(0xFFA5B4FC),
    codeSurface = DarkSurfaceContainerHigh,
    mediaScrim = Color(0xB3000000),
    onMediaOverlay = Color.White,
    cardBorder = DarkOutline,
    platformYoutube = JlabRubyLight,
    platformInstagram = JlabPinkLight,
    platformLinkedin = PlatformLinkedInDark,
    platformTiktok = JlabEmeraldLight,
    platformX = PlatformXDark
)

val LocalExtendedColors = staticCompositionLocalOf { DarkExtendedColors }
