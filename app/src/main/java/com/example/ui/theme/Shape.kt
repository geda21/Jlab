package com.example.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// ============================================================================
// Material 3 Shape Scale
// ============================================================================
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// ============================================================================
// Semantic Component Shape Tokens
// ============================================================================
@Immutable
data class ExtendedShapes(
    val micro: CornerBasedShape = RoundedCornerShape(4.dp),
    val badge: CornerBasedShape = RoundedCornerShape(6.dp),
    val chip: CornerBasedShape = RoundedCornerShape(8.dp),
    val button: CornerBasedShape = RoundedCornerShape(10.dp),
    val input: CornerBasedShape = RoundedCornerShape(12.dp),
    val compactCard: CornerBasedShape = RoundedCornerShape(12.dp),
    val card: CornerBasedShape = RoundedCornerShape(16.dp),
    val heroCard: CornerBasedShape = RoundedCornerShape(20.dp),
    val coverTop: CornerBasedShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    val sheetTop: CornerBasedShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    val pill: Shape = CircleShape
)

val DefaultExtendedShapes = ExtendedShapes()

val LocalExtendedShapes = staticCompositionLocalOf { DefaultExtendedShapes }
