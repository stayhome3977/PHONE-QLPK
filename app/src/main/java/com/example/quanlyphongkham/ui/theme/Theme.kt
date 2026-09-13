package com.example.quanlyphongkham.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Palette shared with the QLPK_FE web portal (src/index.css), plus a teal accent taken from the logo's leaf.
object Brand {
    val Primary = Color(0xFF1877F2)
    val PrimaryDark = Color(0xFF0F4C9C)
    val PrimaryLight = Color(0xFFEFF6FF)
    val PrimarySubtle = Color(0xFFDBEAFE)
    val Teal = Color(0xFF14B8A6)
    val TealLight = Color(0xFFE6FAF7)
    val TextMain = Color(0xFF0F172A)
    val TextMuted = Color(0xFF64748B)
    val TextLight = Color(0xFF94A3B8)
    val Surface = Color(0xFFF8FAFC)
    val Border = Color(0xFFE2E8F0)
    val BorderSubtle = Color(0xFFF1F5F9)
    val Danger = Color(0xFFDC2626)
    val DangerSoft = Color(0xFFFEF2F2)
    val Success = Color(0xFF047857)
    val SuccessSoft = Color(0xFFECFDF5)
    val Warning = Color(0xFFD97706)
    val WarningSoft = Color(0xFFFFFBEB)
    val Violet = Color(0xFF7C3AED)
    val VioletSoft = Color(0xFFF5F3FF)
    val Rose = Color(0xFFE11D48)
    val RoseSoft = Color(0xFFFFF1F2)
}

private val colors = lightColorScheme(
    primary = Brand.Primary,
    onPrimary = Color.White,
    primaryContainer = Brand.PrimarySubtle,
    onPrimaryContainer = Brand.PrimaryDark,
    secondary = Brand.Teal,
    onSecondary = Color.White,
    secondaryContainer = Brand.TealLight,
    onSecondaryContainer = Color(0xFF0F766E),
    background = Brand.Surface,
    onBackground = Brand.TextMain,
    surface = Color.White,
    onSurface = Brand.TextMain,
    surfaceVariant = Brand.BorderSubtle,
    onSurfaceVariant = Brand.TextMuted,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Brand.PrimaryLight,
    outline = Brand.Border,
    outlineVariant = Brand.BorderSubtle,
    error = Brand.Danger,
    errorContainer = Brand.DangerSoft,
)

private val base = Typography()

private val typography = Typography(
    headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
    headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleLarge = base.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    titleSmall = base.titleSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    bodyLarge = base.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = base.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = base.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
)

private val shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
)

@Composable
fun QuanLyPhongKhamTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, typography = typography, shapes = shapes, content = content)
}
