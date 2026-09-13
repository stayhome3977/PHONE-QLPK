package com.example.quanlyphongkham.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.quanlyphongkham.R
import com.example.quanlyphongkham.ui.theme.Brand
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

val BrandGradient = Brush.linearGradient(listOf(Color(0xFF2E8BFF), Brand.Primary, Brand.PrimaryDark))

/** The clinic mark (cross + leaf) on its round blue badge. */
@Composable
fun AppLogo(size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(BrandGradient),
        contentAlignment = Alignment.Center,
    ) {
        // The adaptive-icon foreground is drawn on a 108 grid with the mark inside the central 66.
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "Logo ${ClinicInfo.NAME}",
            modifier = Modifier.requiredSize(size * 1.45f),
        )
    }
}

/** Logo + clinic name lockup used on auth screens and headers. */
@Composable
fun BrandLockup(onDark: Boolean, modifier: Modifier = Modifier, logoSize: Dp = 44.dp) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AppLogo(logoSize, if (onDark) Modifier.border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape) else Modifier)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                "PHÒNG KHÁM",
                style = MaterialTheme.typography.labelSmall,
                color = if (onDark) Color.White.copy(alpha = 0.8f) else Brand.TextMuted,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "DA LIỄU",
                style = MaterialTheme.typography.titleLarge,
                color = if (onDark) Color.White else Brand.PrimaryDark,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, onBack: (() -> Unit)? = null, actions: @Composable () -> Unit = {}) {
    CenterAlignedTopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Brand.Primary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White,
        ),
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Brand.Primary),
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text)
        }
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = Brand.Primary, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
    ) { Text(text) }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        isError = isError,
        shape = RoundedCornerShape(14.dp),
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (visible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        supportingText = supportingText?.let { { Text(it) } },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Brand.Border,
            focusedBorderColor = Brand.Primary,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            disabledTextColor = Brand.TextMain,
            disabledBorderColor = Brand.Border,
            disabledLabelColor = Brand.TextMuted,
            disabledLeadingIconColor = Brand.TextMuted,
        ),
    )
}

/** Read-only field that opens a Material date picker; the value is ISO "yyyy-MM-dd". */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(value: String?, onValueChange: (String?) -> Unit, label: String, modifier: Modifier = Modifier) {
    var open by remember { mutableStateOf(false) }
    Box(modifier) {
        AppTextField(
            value = value?.let { formatDate(it) } ?: "",
            onValueChange = {},
            label = label,
            leadingIcon = Icons.Filled.CalendarMonth,
            enabled = false,
        )
        Box(Modifier.matchParentSize().clip(RoundedCornerShape(14.dp)).clickable { open = true })
    }
    if (open) {
        val initial = value?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val state = rememberDatePickerState(
            initialSelectedDateMillis = initial?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    onValueChange(state.selectedDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toString() })
                    open = false
                }) { Text("Chọn") }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text("Huỷ") } },
        ) { DatePicker(state) }
    }
}

@Composable
fun ErrorBanner(message: String?, modifier: Modifier = Modifier) {
    if (message == null) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Brand.DangerSoft)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Brand.Danger, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(message, color = Brand.Danger, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun InfoBanner(message: String, modifier: Modifier = Modifier, color: Color = Brand.Success, background: Color = Brand.SuccessSoft) {
    Text(
        message,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .padding(12.dp),
        color = color,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun AppCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val colors = CardDefaults.cardColors(containerColor = Color.White)
    val border = BorderStroke(1.dp, Brand.BorderSubtle)
    val elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = colors, border = border, elevation = elevation) {
            Column(Modifier.padding(16.dp), content = content)
        }
    } else {
        Card(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = colors, border = border, elevation = elevation) {
            Column(Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val style = appointmentStatus(status)
    Text(
        style.label,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(style.background)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        color = style.color,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun IconBadge(icon: ImageVector, tint: Color, background: Color, size: Dp = 48.dp) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
fun InfoRow(label: String, value: String?, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Brand.TextMuted, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.42f))
        Text(
            value?.takeIf { it.isNotBlank() } ?: "—",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.58f),
        )
    }
}

@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Brand.Primary)
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier.fillMaxWidth().padding(PaddingValues(horizontal = 32.dp, vertical = 40.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconBadge(icon, Brand.Primary, Brand.PrimaryLight, size = 72.dp)
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted, textAlign = TextAlign.Center)
        if (actionText != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            PrimaryButton(actionText, onAction, modifier = Modifier.width(220.dp))
        }
    }
}
