package com.example.quanlyphongkham.ui.account

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.quanlyphongkham.BuildConfig
import com.example.quanlyphongkham.ui.HomeScreenShortcut
import com.example.quanlyphongkham.ui.appContainer
import com.example.quanlyphongkham.ui.components.AppCard
import com.example.quanlyphongkham.ui.components.AppLogo
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.BrandGradient
import com.example.quanlyphongkham.ui.components.ClinicInfo
import com.example.quanlyphongkham.ui.components.IconBadge
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.home.ContactLine
import com.example.quanlyphongkham.ui.navigation.Routes
import com.example.quanlyphongkham.ui.navigation.navigateToTab
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(nav: NavHostController) {
    val container = appContainer()
    val context = LocalContext.current
    val session by container.sessionStore.session.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var confirmLogout by remember { mutableStateOf(false) }
    val account = session?.account

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(BrandGradient)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 56.dp),
        ) {
            if (account == null) {
                Text("Chào bạn!", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Đăng nhập để đặt lịch khám, xem lịch hẹn và hồ sơ bệnh nhân.",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { nav.navigate(Routes.LOGIN) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Brand.Primary),
                    ) { Text("Đăng nhập", fontWeight = FontWeight.Bold) }
                    OutlinedButton(
                        onClick = { nav.navigate(Routes.REGISTER) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    ) { Text("Đăng ký") }
                }
            } else Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    Modifier.size(64.dp).clip(RoundedCornerShape(50)).background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        account.fullName?.trim()?.split(" ")?.lastOrNull()?.take(1)?.uppercase() ?: "B",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Brand.Primary,
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(account.fullName ?: "", color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Text(account.phoneNumber ?: "", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                    // role_name is seeded in English; this app only ever signs in patients.
                    Text("Bệnh nhân", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Column(Modifier.offset(y = (-32).dp).padding(horizontal = 16.dp)) {
            AppCard {
                MenuItem(Icons.Filled.AssignmentInd, "Hồ sơ bệnh nhân") { nav.navigate(Routes.PROFILES) }
                MenuItem(Icons.Filled.CalendarMonth, "Lịch hẹn của tôi") { nav.navigateToTab(Routes.APPOINTMENTS) }
                MenuItem(Icons.Filled.Lock, "Đổi mật khẩu") { nav.navigate(Routes.CHANGE_PASSWORD) }
            }
            Spacer(Modifier.height(16.dp))
            AppCard {
                MenuItem(Icons.Filled.Science, "Kết quả khám", soon = true) { nav.navigate(Routes.comingSoon("results")) }
                MenuItem(Icons.Filled.LocalPharmacy, "Đơn thuốc", soon = true) { nav.navigate(Routes.comingSoon("prescriptions")) }
                MenuItem(Icons.AutoMirrored.Filled.ReceiptLong, "Hóa đơn & thanh toán", soon = true) { nav.navigate(Routes.comingSoon("invoices")) }
                MenuItem(Icons.Filled.Notifications, "Thông báo", soon = true) { nav.navigate(Routes.comingSoon("notifications")) }
            }
            Spacer(Modifier.height(16.dp))
            AppCard {
                MenuItem(Icons.Filled.Apartment, "Giới thiệu phòng khám") { nav.navigate(Routes.CLINIC_DETAIL) }
                if (HomeScreenShortcut.isSupported(context)) {
                    MenuItem(Icons.AutoMirrored.Filled.AddToHomeScreen, "Thêm vào màn hình chính") { HomeScreenShortcut.request(context) }
                }
                MenuItem(Icons.Filled.SupportAgent, "Liên hệ phòng khám") { nav.navigate(Routes.CONTACT) }
                if (account != null) {
                    MenuItem(Icons.AutoMirrored.Filled.Logout, "Đăng xuất", tint = Brand.Danger) { confirmLogout = true }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "${ClinicInfo.NAME} · phiên bản ${BuildConfig.VERSION_NAME}\nMáy chủ: ${BuildConfig.API_ROOT}",
                style = MaterialTheme.typography.bodySmall,
                color = Brand.TextLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            // Clearance for the raised booking button over the bottom bar.
            Spacer(Modifier.height(56.dp))
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text("Đăng xuất?") },
            text = { Text("Bạn sẽ cần đăng nhập lại để đặt lịch và xem lịch hẹn.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    scope.launch { container.authRepository.logout() }
                }) { Text("Đăng xuất", color = Brand.Danger) }
            },
            dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Huỷ") } },
        )
    }
}

@Composable
private fun MenuItem(icon: ImageVector, label: String, tint: Color = Brand.Primary, soon: Boolean = false, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconBadge(icon, tint, if (tint == Brand.Danger) Brand.DangerSoft else Brand.PrimaryLight, size = 38.dp)
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), color = if (tint == Brand.Danger) Brand.Danger else Brand.TextMain)
        if (soon) {
            Text(
                "Sắp có",
                style = MaterialTheme.typography.labelSmall,
                color = Brand.TextMuted,
                modifier = Modifier.clip(RoundedCornerShape(50)).background(Brand.BorderSubtle).padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Brand.TextLight)
    }
}

@Composable
fun ContactScreen(nav: NavHostController) {
    val context = LocalContext.current
    Scaffold(topBar = { AppTopBar("Liên hệ phòng khám", onBack = { nav.popBackStack() }) }, containerColor = Brand.Surface) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(12.dp))
            AppLogo(96.dp)
            Spacer(Modifier.height(12.dp))
            Text(ClinicInfo.NAME, style = MaterialTheme.typography.headlineSmall, color = Brand.PrimaryDark)
            Text(ClinicInfo.TAGLINE, style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted)
            Spacer(Modifier.height(20.dp))
            AppCard {
                ContactLine(Icons.Filled.Phone, "Hotline", ClinicInfo.HOTLINE) {
                    context.startActivity(Intent(Intent.ACTION_DIAL, "tel:${ClinicInfo.HOTLINE}".toUri()))
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Brand.BorderSubtle)
                ContactLine(Icons.Filled.SupportAgent, "Tổng đài", ClinicInfo.HOTLINE_2) {
                    context.startActivity(Intent(Intent.ACTION_DIAL, "tel:${ClinicInfo.HOTLINE_2.replace(" ", "")}".toUri()))
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Brand.BorderSubtle)
                ContactLine(Icons.Filled.Email, "Email", ClinicInfo.EMAIL) {
                    context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:${ClinicInfo.EMAIL}".toUri()))
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Brand.BorderSubtle)
                ContactLine(Icons.Filled.LocationOn, "Địa chỉ", ClinicInfo.ADDRESS)
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Brand.BorderSubtle)
                ContactLine(Icons.Filled.AccessTime, "Giờ làm việc", ClinicInfo.HOURS)
            }
        }
    }
}

private val COMING_SOON_TITLES = mapOf(
    "results" to ("Kết quả khám" to Icons.Filled.Science),
    "prescriptions" to ("Đơn thuốc" to Icons.Filled.LocalPharmacy),
    "invoices" to ("Hóa đơn & thanh toán" to Icons.AutoMirrored.Filled.ReceiptLong),
    "notifications" to ("Thông báo" to Icons.Filled.Notifications),
)

/** Placeholder for patient features the QLPK API does not expose yet. */
@Composable
fun ComingSoonScreen(nav: NavHostController, feature: String) {
    val (title, icon) = COMING_SOON_TITLES[feature] ?: ("Tính năng" to Icons.Filled.Info)
    Scaffold(topBar = { AppTopBar(title, onBack = { nav.popBackStack() }) }, containerColor = Brand.Surface) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                IconBadge(icon, Brand.Primary, Brand.PrimaryLight, size = 110.dp)
                IconBadge(Icons.Filled.Construction, Color.White, Brand.Warning, size = 36.dp)
            }
            Spacer(Modifier.height(20.dp))
            Text("$title sắp ra mắt", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                "Phòng khám đang hoàn thiện tính năng này. Trong thời gian chờ, vui lòng liên hệ quầy lễ tân để được hỗ trợ.",
                style = MaterialTheme.typography.bodyMedium,
                color = Brand.TextMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Liên hệ phòng khám", { nav.navigate(Routes.CONTACT) })
        }
    }
}
