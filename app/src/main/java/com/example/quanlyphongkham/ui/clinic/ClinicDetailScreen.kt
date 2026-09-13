package com.example.quanlyphongkham.ui.clinic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.IconBadge
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.components.SecondaryButton
import com.example.quanlyphongkham.ui.home.ClinicDetailContent
import com.example.quanlyphongkham.ui.home.LandingColors
import com.example.quanlyphongkham.ui.home.NetworkImage
import com.example.quanlyphongkham.ui.home.Pill
import com.example.quanlyphongkham.ui.home.StaticDoctorCard
import com.example.quanlyphongkham.ui.navigation.Routes
import com.example.quanlyphongkham.ui.theme.Brand

/** Port of QLPK_FE's ClinicDetailPage, opened from the facilities slideshow. */
@Composable
fun ClinicDetailScreen(nav: NavHostController) {
    val book = { nav.navigate(Routes.booking()) }
    Scaffold(topBar = { AppTopBar("Phòng khám Da Liễu", onBack = { nav.popBackStack() }) }, containerColor = Color.White) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
            // Hero banner: photo under a white wash, like the web.
            Box(Modifier.fillMaxWidth().height(260.dp)) {
                NetworkImage(ClinicDetailContent.heroImage, shape = RoundedCornerShape(0.dp), modifier = Modifier.fillMaxSize())
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.94f), Color.White.copy(alpha = 0.88f), Color.White.copy(alpha = 0.4f))),
                    ),
                )
                Column(Modifier.align(Alignment.CenterStart).padding(20.dp)) {
                    Pill(ClinicDetailContent.TAG)
                    Spacer(Modifier.height(10.dp))
                    Text(ClinicDetailContent.TITLE, fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.ExtraBold, color = LandingColors.CardTitle)
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrimaryButton("Đặt lịch khám ngay", book, modifier = Modifier.weight(1.25f))
                        SecondaryButton("Về trang chủ", { nav.popBackStack(Routes.HOME, inclusive = false) }, modifier = Modifier.weight(1f))
                    }
                }
            }

            Column(Modifier.padding(20.dp)) {
                DetailTitle(ClinicDetailContent.INTRO_TITLE)
                Text(ClinicDetailContent.INTRO, style = MaterialTheme.typography.bodyLarge, color = LandingColors.BodyText)
                Spacer(Modifier.height(14.dp))
                ClinicDetailContent.introFeatures.forEach { feature ->
                    Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(feature.icon, Brand.Primary, LandingColors.IconCircle, size = 44.dp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(feature.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = LandingColors.CardTitle)
                            Text(feature.description, style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                NetworkImage(
                    ClinicDetailContent.introImage,
                    contentDescription = "Đội ngũ bác sĩ da liễu hội chẩn",
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                )

                Spacer(Modifier.height(28.dp))
                DetailTitle("Cơ sở vật chất")
                ClinicDetailContent.facilities.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { facility ->
                            Column(Modifier.weight(1f)) {
                                NetworkImage(facility.image, contentDescription = facility.title, modifier = Modifier.fillMaxWidth().height(120.dp))
                                Text(facility.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = LandingColors.CardTitle, modifier = Modifier.padding(top = 6.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))
                DetailTitle("Quy trình khám")
                ClinicDetailContent.steps.forEachIndexed { index, (title, description) ->
                    Row(Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "%02d".format(index + 1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Brand.Primary).padding(top = 9.dp),
                            )
                            if (index < ClinicDetailContent.steps.lastIndex) {
                                Box(Modifier.width(2.dp).height(34.dp).background(Brand.PrimarySubtle))
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.padding(top = 8.dp)) {
                            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = LandingColors.CardTitle)
                            Text(description, style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))
                DetailTitle("Đội ngũ bác sĩ")
                ClinicDetailContent.doctors.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { doctor -> StaticDoctorCard(doctor, Modifier.weight(1f), book) }
                    }
                }

                Spacer(Modifier.height(28.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(Brand.Primary, LandingColors.CtaDark)))
                        .padding(22.dp),
                ) {
                    Text(ClinicDetailContent.CTA_TITLE, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(6.dp))
                    Text(ClinicDetailContent.CTA_TEXT, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = book,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Brand.Primary),
                    ) { Text("Đặt lịch khám ngay", fontWeight = FontWeight.Bold) }
                }
                Spacer(Modifier.navigationBarsPadding().height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailTitle(text: String) {
    Text(text, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Brand.Primary, modifier = Modifier.padding(bottom = 12.dp))
}
