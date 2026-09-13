package com.example.quanlyphongkham.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.example.quanlyphongkham.ui.theme.Brand

/** Card border and icon-circle colours the web uses outside its token set. */
object LandingColors {
    val CardBorder = Color(0xFFE8EFF7)
    val IconCircle = Color(0xFFE0EDFF)
    val CardTitle = Color(0xFF1E293B)
    val BodyText = Color(0xFF334155)
    val HeroTint = Color(0xFFF0F7FF)
    val CtaDark = Color(0xFF0D5BBD)
}

/** Centered blue section title with a muted subtitle, like the web's .section-main-title. */
@Composable
fun SectionHeader(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Brand.Primary,
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(16.dp))
    }
}

/** Remote photo with a tinted placeholder while it loads or when the network fails. */
@Composable
fun NetworkImage(url: String, modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(16.dp), contentDescription: String? = null) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(shape).background(Brand.PrimaryLight),
        loading = { ImagePlaceholder() },
        error = { ImagePlaceholder() },
    )
}

@Composable
private fun ImagePlaceholder() {
    Box(Modifier.fillMaxWidth().height(40.dp), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.Image, contentDescription = null, tint = Brand.PrimarySubtle, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun Pill(text: String, background: Color = Brand.Primary, color: Color = Color.White) {
    Text(
        text,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(background).padding(horizontal = 12.dp, vertical = 5.dp),
    )
}

/** Pager dots; the active one stretches like the web's testimonial pagination. */
@Composable
fun PagerDots(count: Int, current: Int, modifier: Modifier = Modifier, inactive: Color = Brand.Border) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { index ->
            Box(
                Modifier
                    .height(8.dp)
                    .width(if (index == current) 24.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (index == current) Brand.Primary else inactive),
            )
        }
    }
}
