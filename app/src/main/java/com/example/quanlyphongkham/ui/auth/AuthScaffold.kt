package com.example.quanlyphongkham.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quanlyphongkham.ui.components.BrandGradient
import com.example.quanlyphongkham.ui.components.BrandLockup
import com.example.quanlyphongkham.ui.components.ClinicInfo
import com.example.quanlyphongkham.ui.theme.Brand

/** Blue hospital-style header with the clinic lockup, and a white sheet holding the form. */
@Composable
fun AuthScaffold(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Brand.Surface)
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(BrandGradient)
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 56.dp),
        ) {
            Column {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                } else {
                    Spacer(Modifier.height(36.dp))
                }
                BrandLockup(onDark = true, logoSize = 56.dp)
                Spacer(Modifier.height(10.dp))
                Text(ClinicInfo.TAGLINE, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-28).dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 4.dp,
        ) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Brand.TextMain)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted)
                Spacer(Modifier.height(20.dp))
                content()
            }
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}
