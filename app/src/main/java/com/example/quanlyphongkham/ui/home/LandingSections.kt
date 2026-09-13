package com.example.quanlyphongkham.ui.home

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.quanlyphongkham.ui.components.AppLogo
import com.example.quanlyphongkham.ui.components.IconBadge
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.components.SecondaryButton
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.delay

private val SectionPadding = PaddingValues(top = 32.dp, bottom = 8.dp)

// ---------------------------------------------------------------- Hero

@Composable
fun HeroSection(onBook: () -> Unit, onSeeServices: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Brush.radialGradient(listOf(LandingColors.HeroTint, Color.White), radius = 1400f))
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Pill(HeroContent.BADGE)
        Spacer(Modifier.height(14.dp))
        Text(
            "${HeroContent.TITLE_LINE_1}\n${HeroContent.TITLE_LINE_2}",
            fontSize = 32.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Brand.Primary,
        )
        Spacer(Modifier.height(12.dp))
        Text(HeroContent.DESCRIPTION, style = MaterialTheme.typography.bodyLarge, color = LandingColors.BodyText)
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PrimaryButton("Đặt lịch khám", onBook, modifier = Modifier.weight(1f), icon = Icons.Filled.CalendarMonth)
            SecondaryButton("Xem dịch vụ", onSeeServices, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        HeroContent.checklist.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                row.forEach { item ->
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Brand.Primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(item, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = LandingColors.BodyText)
                    }
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        NetworkImage(
            HeroContent.image,
            contentDescription = "Đội ngũ bác sĩ da liễu",
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = Brand.Primary.copy(alpha = 0.3f))
                .border(4.dp, Color.White, RoundedCornerShape(20.dp)),
        )
    }
}

// ---------------------------------------------------------------- About

@Composable
fun AboutSection() {
    Column(Modifier.fillMaxWidth().padding(SectionPadding).padding(horizontal = 20.dp)) {
        Box {
            NetworkImage(
                AboutContent.image,
                contentDescription = "Phòng khám và chăm sóc da",
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().height(240.dp),
            )
        }
        // Floating stats card overlapping the photo, as on the web.
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-36).dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            AboutContent.stats.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                    row.forEach { stat ->
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stat.value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Brand.Primary)
                            Text(stat.label, style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                        }
                    }
                }
            }
        }
        Column(Modifier.offset(y = (-20).dp)) {
            Pill(AboutContent.BADGE, background = Brand.PrimaryLight, color = Brand.Primary)
            Spacer(Modifier.height(10.dp))
            Text(AboutContent.TITLE, fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.ExtraBold, color = Brand.Primary)
            Spacer(Modifier.height(10.dp))
            Text(AboutContent.DESCRIPTION, style = MaterialTheme.typography.bodyLarge, color = LandingColors.BodyText)
            Spacer(Modifier.height(14.dp))
            AboutContent.features.forEach { feature ->
                Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconBadge(feature.icon, Color.White, Brand.Primary, size = 44.dp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(feature.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = LandingColors.CardTitle)
                        Text(feature.description, style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Facilities

@Composable
fun FacilitiesSection(onOpenDetail: () -> Unit) {
    val slides = FacilitiesContent.slides
    val pager = rememberPagerState { slides.size }
    LaunchedEffect(pager) {
        while (true) {
            delay(FacilitiesContent.INTERVAL_MS)
            pager.animateScrollToPage((pager.currentPage + 1) % slides.size)
        }
    }
    Column(Modifier.fillMaxWidth().padding(SectionPadding)) {
        SectionHeader(FacilitiesContent.TITLE, FacilitiesContent.SUBTITLE)
        HorizontalPager(
            state = pager,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp,
        ) { page ->
            val slide = slides[page]
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onOpenDetail),
            ) {
                NetworkImage(slide.image, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxSize(), contentDescription = slide.title)
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xAD1E293B))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(slide.title, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(slide.description, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            PagerDots(slides.size, pager.currentPage)
        }
        Text(
            "Nhấn để xem chi tiết phòng khám",
            style = MaterialTheme.typography.bodySmall,
            color = Brand.TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        )
    }
}

// ---------------------------------------------------------------- Testimonials

@Composable
fun TestimonialsSection() {
    val items = TestimonialsContent.items
    val pager = rememberPagerState { items.size }
    Column(Modifier.fillMaxWidth().padding(SectionPadding)) {
        SectionHeader(TestimonialsContent.TITLE)
        HorizontalPager(state = pager, contentPadding = PaddingValues(horizontal = 20.dp), pageSpacing = 12.dp) { page ->
            val item = items[page]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, LandingColors.CardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    NetworkImage(item.avatar, shape = CircleShape, modifier = Modifier.size(76.dp), contentDescription = item.name)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "“${item.quote}”",
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyLarge,
                        color = LandingColors.BodyText,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(item.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Brand.Primary)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            PagerDots(items.size, pager.currentPage)
        }
    }
}

// ---------------------------------------------------------------- Services

@Composable
fun ServicesSection(onBook: () -> Unit, onPricing: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(SectionPadding).padding(horizontal = 16.dp)) {
        SectionHeader(ServicesContent.TITLE, ServicesContent.SUBTITLE)
        ServicesContent.items.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { service ->
                    Card(
                        onClick = onBook,
                        modifier = Modifier.weight(1f).height(170.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, LandingColors.CardBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            IconBadge(service.icon, Brand.Primary, LandingColors.IconCircle, size = 52.dp)
                            Spacer(Modifier.height(10.dp))
                            Text(service.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = LandingColors.CardTitle)
                            Spacer(Modifier.height(4.dp))
                            Text(service.description, style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        SecondaryButton("Xem bảng giá dịch vụ", onPricing)
    }
}

// ---------------------------------------------------------------- Doctors

@Composable
fun DoctorsSection(onBook: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(SectionPadding)) {
        SectionHeader(DoctorsContent.TITLE, DoctorsContent.SUBTITLE)
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(DoctorsContent.items) { doctor -> StaticDoctorCard(doctor, Modifier.width(210.dp), onBook) }
        }
    }
}

@Composable
fun StaticDoctorCard(doctor: StaticDoctor, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, LandingColors.CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        NetworkImage(
            doctor.image,
            contentDescription = doctor.name,
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
            modifier = Modifier.fillMaxWidth().aspectRatio(0.9f),
        )
        Column(Modifier.padding(14.dp)) {
            Text(doctor.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = LandingColors.CardTitle)
            Text(doctor.specialty, style = MaterialTheme.typography.bodySmall, color = Brand.Primary, fontWeight = FontWeight.SemiBold)
            doctor.experience?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted) }
        }
    }
}

// ---------------------------------------------------------------- Contact

@Composable
fun ContactSection() {
    val context = LocalContext.current
    fun open(uri: String, action: String = Intent.ACTION_VIEW) =
        runCatching { context.startActivity(Intent(action, uri.toUri())) }

    Column(Modifier.fillMaxWidth().padding(SectionPadding).padding(horizontal = 16.dp)) {
        SectionHeader(ContactContent.TITLE, ContactContent.SUBTITLE)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, LandingColors.CardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ContactBlock(Icons.Filled.LocationOn, "Địa chỉ") {
                    ContactContent.branches.forEach { branch ->
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("${branch.name}: ") }
                                append(branch.address)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = LandingColors.BodyText,
                        )
                    }
                }
                ContactBlock(Icons.Filled.AccessTime, "Thời gian") {
                    ContactContent.hours.forEach { Text(it, style = MaterialTheme.typography.bodyMedium, color = LandingColors.BodyText) }
                }
                ContactBlock(Icons.Filled.Email, "Email") {
                    Text(
                        ContactContent.EMAIL,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Brand.Primary,
                        modifier = Modifier.clickable { open("mailto:${ContactContent.EMAIL}", Intent.ACTION_SENDTO) },
                    )
                }
                ContactBlock(Icons.Filled.Phone, "Điện thoại") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ContactContent.phones.forEachIndexed { index, phone ->
                            if (index > 0) Text("–", color = Brand.TextMuted)
                            Text(
                                phone.display,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Brand.Primary,
                                modifier = Modifier.clickable { open("tel:${phone.dial}", Intent.ACTION_DIAL) },
                            )
                        }
                    }
                }
                PrimaryButton("Xem bản đồ", { open(ContactContent.MAP_URI) }, icon = Icons.Filled.Map)
            }
        }
    }
}

@Composable
private fun ContactBlock(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: @Composable () -> Unit) {
    Row {
        IconBadge(icon, Brand.Primary, LandingColors.IconCircle, size = 44.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = LandingColors.CardTitle)
            Spacer(Modifier.height(2.dp))
            content()
        }
    }
}

// ---------------------------------------------------------------- News

@Composable
fun NewsSection() {
    Column(Modifier.fillMaxWidth().padding(SectionPadding)) {
        SectionHeader(NewsContent.TITLE, NewsContent.SUBTITLE)
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(NewsContent.items) { news ->
                Card(
                    modifier = Modifier.width(290.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, LandingColors.CardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    NetworkImage(
                        news.image,
                        contentDescription = news.title,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        modifier = Modifier.fillMaxWidth().height(170.dp),
                    )
                    Column(Modifier.padding(14.dp).height(128.dp)) {
                        if (news.title.isNotEmpty()) {
                            Text(news.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = LandingColors.CardTitle)
                            Spacer(Modifier.height(4.dp))
                        }
                        Text(
                            news.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Brand.TextMuted,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        // "Đọc thêm" does nothing on the web either.
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Đọc thêm", style = MaterialTheme.typography.labelLarge, color = Brand.Primary)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Brand.Primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Footer

data class QuickLink(val label: String, val onClick: () -> Unit)

@Composable
fun FooterSection(quickLinks: List<QuickLink>, onTreatment: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var subscribed by remember { mutableStateOf(false) }
    LaunchedEffect(subscribed) {
        if (subscribed) {
            delay(3000)
            subscribed = false
        }
    }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .background(Brand.Primary)
            .padding(horizontal = 20.dp, vertical = 28.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppLogo(46.dp, Modifier.border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Phòng Khám Da Liễu", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Text("Chăm sóc sức khỏe tận tâm", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(FooterContent.DESCRIPTION, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(10.dp))
        FooterLine(Icons.Filled.Phone, FooterContent.HOTLINE) {
            runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, "tel:0393975116".toUri())) }
        }
        FooterLine(Icons.Filled.Email, FooterContent.EMAIL) {
            runCatching { context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:${FooterContent.EMAIL}".toUri())) }
        }
        FooterLine(Icons.Filled.LocationOn, FooterContent.ADDRESS)

        FooterHeading("Liên Kết Nhanh")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(quickLinks) { link ->
                Text(
                    link.label,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .clickable(onClick = link.onClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }

        FooterHeading("Dịch Vụ Điều Trị")
        FooterContent.treatments.forEach { item ->
            Text(
                "› $item",
                color = Color.White.copy(alpha = 0.92f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth().clickable(onClick = onTreatment).padding(vertical = 3.dp),
            )
        }

        FooterHeading(FooterContent.NEWSLETTER_TITLE)
        Text(FooterContent.NEWSLETTER_TEXT, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Nhập địa chỉ email...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                ),
            )
            Spacer(Modifier.width(8.dp))
            // Front-end only, like the web form: no API call is made.
            FilledIconButton(
                onClick = { if (email.contains('@')) { subscribed = true; email = "" } },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = LandingColors.CtaDark),
                modifier = Modifier.size(52.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Đăng ký", tint = Color.White)
            }
        }
        if (subscribed) {
            Spacer(Modifier.height(6.dp))
            Text(FooterContent.NEWSLETTER_DONE, color = Color(0xFFA7F3D0), style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(20.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.25f)))
        Spacer(Modifier.height(12.dp))
        Text(FooterContent.COPYRIGHT, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(
            "Chính sách bảo mật • Điều khoản sử dụng • Hỗ trợ",
            color = Color.White.copy(alpha = 0.75f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        )
        // Clearance for the raised booking button over the bottom bar.
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun FooterHeading(text: String) {
    Spacer(Modifier.height(20.dp))
    Text(text, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun FooterLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: (() -> Unit)? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}
