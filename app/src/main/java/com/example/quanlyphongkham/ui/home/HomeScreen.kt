package com.example.quanlyphongkham.ui.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.dto.AppointmentListItem
import com.example.quanlyphongkham.data.remote.dto.PatientProfile
import com.example.quanlyphongkham.data.repository.ClinicRepository
import com.example.quanlyphongkham.ui.appContainer
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.components.ACTIVE_STATUSES
import com.example.quanlyphongkham.ui.components.AppCard
import com.example.quanlyphongkham.ui.components.AppLogo
import com.example.quanlyphongkham.ui.components.ClinicInfo
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.IconBadge
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.components.StatusChip
import com.example.quanlyphongkham.ui.components.clinicToday
import com.example.quanlyphongkham.ui.components.formatDayOfWeek
import com.example.quanlyphongkham.ui.components.formatTime
import com.example.quanlyphongkham.ui.navigation.Routes
import com.example.quanlyphongkham.ui.navigation.navigateToTab
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/** Loads the signed-in patient's own block; the rest of the home page is static. */
class HomeViewModel(private val clinic: ClinicRepository) : ViewModel() {
    var profile by mutableStateOf<PatientProfile?>(null)
        private set
    var upcoming by mutableStateOf<AppointmentListItem?>(null)
        private set
    var upcomingCount by mutableIntStateOf(0)
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun refresh() {
        loading = true
        viewModelScope.launch {
            val profiles = async { clinic.profiles() }
            val appointments = async { clinic.appointments(fromDate = clinicToday().toString()) }
            var failure: String? = null

            when (val p = profiles.await()) {
                is ApiResult.Success -> profile = p.data.firstOrNull { it.relationshipToAccount == "self" } ?: p.data.firstOrNull()
                is ApiResult.Failure -> failure = p.message
            }
            when (val a = appointments.await()) {
                is ApiResult.Success -> {
                    val active = a.data.filter { it.status in ACTIVE_STATUSES }
                        .sortedWith(compareBy({ it.appointmentDate }, { it.appointmentTime }))
                    upcoming = active.firstOrNull()
                    upcomingCount = active.size
                }
                is ApiResult.Failure -> failure = a.message
            }
            error = failure
            loading = false
        }
    }

    fun clear() {
        profile = null
        upcoming = null
        upcomingCount = 0
        error = null
    }
}

/** LazyColumn item keys; the web's anchor links (#services, #contact…) scroll to these. */
private object Section {
    const val HERO = "hero"
    const val PERSONAL = "personal"
    const val ABOUT = "about"
    const val FACILITIES = "facilities"
    const val TESTIMONIALS = "testimonials"
    const val SERVICES = "services"
    const val DOCTORS = "doctors"
    const val CONTACT = "contact"
    const val NEWS = "news"
    const val FOOTER = "footer"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavHostController) {
    val vm = appViewModel { HomeViewModel(it.clinicRepository) }
    val session by appContainer().sessionStore.session.collectAsStateWithLifecycle()
    val signedIn = session != null
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(session?.account?.accountId) {
        if (signedIn) vm.refresh() else vm.clear()
    }

    val order = buildList {
        add(Section.HERO)
        if (signedIn) add(Section.PERSONAL)
        addAll(listOf(Section.ABOUT, Section.FACILITIES, Section.TESTIMONIALS, Section.SERVICES, Section.DOCTORS, Section.CONTACT, Section.NEWS, Section.FOOTER))
    }
    fun scrollTo(section: String) {
        val index = order.indexOf(section)
        if (index >= 0) scope.launch { listState.animateScrollToItem(index) }
    }
    val book = { nav.navigate(Routes.booking()) }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        HomeHeader(
            accountName = session?.account?.fullName,
            onLogin = { nav.navigate(Routes.LOGIN) },
            onAccount = { nav.navigateToTab(Routes.ACCOUNT) },
            onLogoClick = { scrollTo(Section.HERO) },
        )
        PullToRefreshBox(
            isRefreshing = vm.loading,
            onRefresh = { if (signedIn) vm.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                item(Section.HERO) { HeroSection(onBook = book, onSeeServices = { scrollTo(Section.SERVICES) }) }
                if (signedIn) {
                    item(Section.PERSONAL) { PersonalBlock(vm, session?.account?.fullName, nav) }
                }
                item(Section.ABOUT) { AboutSection() }
                item(Section.FACILITIES) { FacilitiesSection(onOpenDetail = { nav.navigate(Routes.CLINIC_DETAIL) }) }
                item(Section.TESTIMONIALS) { TestimonialsSection() }
                item(Section.SERVICES) { ServicesSection(onBook = book, onPricing = { nav.navigate(Routes.SERVICES) }) }
                item(Section.DOCTORS) { DoctorsSection(onBook = book) }
                item(Section.CONTACT) { ContactSection() }
                item(Section.NEWS) { NewsSection() }
                item(Section.FOOTER) {
                    FooterSection(
                        quickLinks = listOf(
                            QuickLink("Trang chủ") { scrollTo(Section.HERO) },
                            QuickLink("Về chúng tôi") { scrollTo(Section.ABOUT) },
                            QuickLink("Cơ sở vật chất") { scrollTo(Section.FACILITIES) },
                            QuickLink("Đánh giá khách hàng") { scrollTo(Section.TESTIMONIALS) },
                            QuickLink("Dịch vụ điều trị") { scrollTo(Section.SERVICES) },
                            QuickLink("Đội ngũ bác sĩ") { scrollTo(Section.DOCTORS) },
                            QuickLink("Tin tức y tế") { scrollTo(Section.NEWS) },
                            QuickLink("Liên hệ") { scrollTo(Section.CONTACT) },
                        ),
                        onTreatment = { scrollTo(Section.SERVICES) },
                    )
                }
            }
        }
    }
}

/** Mobile take on the web TopBar + sticky Navbar. */
@Composable
private fun HomeHeader(accountName: String?, onLogin: () -> Unit, onAccount: () -> Unit, onLogoClick: () -> Unit) {
    val context = LocalContext.current
    Surface(color = Color.White, shadowElevation = 3.dp) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Brand.Primary)
                    .statusBarsPadding()
                    .clickable { runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, "tel:${ClinicInfo.HOTLINE}".toUri())) } }
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Hotline ${ClinicInfo.HOTLINE}", color = Color.White, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text("Giờ làm việc: 8:00 - 17:00", color = Color.White, style = MaterialTheme.typography.bodySmall)
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f).clickable(onClick = onLogoClick), verticalAlignment = Alignment.CenterVertically) {
                    AppLogo(40.dp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Phòng Khám Da Liễu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = LandingColors.CardTitle)
                        Text("Chăm sóc sức khỏe tận tâm", style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                    }
                }
                if (accountName == null) {
                    OutlinedButton(onClick = onLogin, shape = RoundedCornerShape(10.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)) {
                        Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Đăng nhập")
                    }
                } else {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Brand.PrimaryLight)
                            .clickable(onClick = onAccount)
                            .padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            accountName.trim().split(" ").lastOrNull()?.take(1)?.uppercase() ?: "B",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.size(30.dp).clip(CircleShape).background(Brand.Primary).padding(top = 4.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            accountName.trim().split(" ").lastOrNull() ?: accountName,
                            style = MaterialTheme.typography.labelLarge,
                            color = LandingColors.CardTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(80.dp),
                        )
                    }
                }
            }
        }
    }
}

private data class Shortcut(val label: String, val icon: ImageVector, val tint: Color, val background: Color, val onClick: () -> Unit)

/** Signed-in only: greeting, upcoming appointment and shortcuts, placed right under the hero. */
@Composable
private fun PersonalBlock(vm: HomeViewModel, fullName: String?, nav: NavHostController) {
    val shortcuts = listOf(
        Shortcut("Lịch hẹn", Icons.Filled.CalendarMonth, Brand.Violet, Brand.VioletSoft) { nav.navigateToTab(Routes.APPOINTMENTS) },
        Shortcut("Check-in", Icons.Filled.HowToReg, Brand.Teal, Brand.TealLight) { nav.navigate(Routes.checkIn()) },
        Shortcut("Hồ sơ", Icons.Filled.AssignmentInd, Brand.Warning, Brand.WarningSoft) { nav.navigate(Routes.PROFILES) },
        Shortcut("Bảng giá", Icons.Filled.Spa, Brand.Rose, Brand.RoseSoft) { nav.navigate(Routes.SERVICES) },
    )
    Column(Modifier.fillMaxWidth().background(Brand.Surface).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Xin chào,", style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted)
                Text(fullName ?: "Bệnh nhân", style = MaterialTheme.typography.titleLarge, color = LandingColors.CardTitle)
            }
            vm.profile?.let { Pill("Mã BN: ${it.patientCode}", background = Brand.PrimaryLight, color = Brand.Primary) }
        }
        Spacer(Modifier.height(12.dp))
        UpcomingCard(vm.upcoming, vm.upcomingCount, nav)
        vm.error?.let {
            Spacer(Modifier.height(10.dp))
            ErrorBanner(it)
        }
        Spacer(Modifier.height(12.dp))
        AppCard {
            Row(Modifier.fillMaxWidth()) {
                shortcuts.forEach { shortcut ->
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).clickable(onClick = shortcut.onClick).padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        IconBadge(shortcut.icon, shortcut.tint, shortcut.background, size = 48.dp)
                        Spacer(Modifier.height(6.dp))
                        Text(shortcut.label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
    HorizontalDivider(color = Brand.BorderSubtle)
}

@Composable
private fun UpcomingCard(item: AppointmentListItem?, count: Int, nav: NavHostController) {
    AppCard(onClick = item?.let { { nav.navigate(Routes.appointment(it.appointmentId)) } }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Lịch hẹn sắp tới", style = MaterialTheme.typography.titleSmall, color = Brand.TextMuted, modifier = Modifier.weight(1f))
            if (count > 1) Text("+${count - 1} lịch khác", style = MaterialTheme.typography.labelMedium, color = Brand.Primary)
        }
        Spacer(Modifier.height(10.dp))
        if (item == null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(Icons.Filled.CalendarMonth, Brand.Primary, Brand.PrimaryLight)
                Spacer(Modifier.width(12.dp))
                Text("Bạn chưa có lịch hẹn nào sắp tới.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            PrimaryButton("Đặt lịch khám ngay", { nav.navigate(Routes.booking()) })
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    Modifier.clip(RoundedCornerShape(14.dp)).background(Brand.PrimaryLight).padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(formatTime(item.appointmentTime), style = MaterialTheme.typography.titleLarge, color = Brand.Primary)
                    Text(item.appointmentDate.takeLast(5).split("-").reversed().joinToString("/"), style = MaterialTheme.typography.labelMedium, color = Brand.PrimaryDark)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("BS. ${item.doctorFullName ?: ""}", style = MaterialTheme.typography.titleMedium)
                    Text(formatDayOfWeek(item.appointmentDate), style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                    Spacer(Modifier.height(6.dp))
                    StatusChip(item.status)
                }
            }
            item.queueNumber?.let {
                Spacer(Modifier.height(10.dp))
                Text("Số thứ tự của bạn: $it", color = Brand.Teal, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Contact row reused by the contact screen. */
@Composable
fun ContactLine(icon: ImageVector, label: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(icon, Brand.Primary, Brand.PrimaryLight, size = 40.dp)
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
