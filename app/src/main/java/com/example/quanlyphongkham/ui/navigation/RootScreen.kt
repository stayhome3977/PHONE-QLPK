package com.example.quanlyphongkham.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.ErrorMessages
import com.example.quanlyphongkham.ui.account.AccountScreen
import com.example.quanlyphongkham.ui.account.ChangePasswordScreen
import com.example.quanlyphongkham.ui.account.ComingSoonScreen
import com.example.quanlyphongkham.ui.account.ContactScreen
import com.example.quanlyphongkham.ui.account.ProfileEditScreen
import com.example.quanlyphongkham.ui.account.ProfilesScreen
import com.example.quanlyphongkham.ui.appContainer
import com.example.quanlyphongkham.ui.appointments.AppointmentDetailScreen
import com.example.quanlyphongkham.ui.appointments.AppointmentsScreen
import com.example.quanlyphongkham.ui.auth.ForgotPasswordScreen
import com.example.quanlyphongkham.ui.auth.LoginScreen
import com.example.quanlyphongkham.ui.auth.RegisterScreen
import com.example.quanlyphongkham.ui.booking.BookingScreen
import com.example.quanlyphongkham.ui.checkin.CheckInScreen
import com.example.quanlyphongkham.ui.clinic.ClinicDetailScreen
import com.example.quanlyphongkham.ui.components.BrandGradient
import com.example.quanlyphongkham.ui.doctors.DoctorsScreen
import com.example.quanlyphongkham.ui.home.HomeScreen
import com.example.quanlyphongkham.ui.services.ServicesScreen
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.launch

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot-password"

    const val HOME = "home"
    const val APPOINTMENTS = "appointments"
    const val DOCTORS = "doctors"
    const val ACCOUNT = "account"
    const val SERVICES = "services"
    const val CLINIC_DETAIL = "clinic-detail"
    const val BOOKING = "booking?doctorId={doctorId}"
    const val APPOINTMENT = "appointment/{id}"
    const val CHECK_IN = "check-in?code={code}"
    const val PROFILES = "profiles"
    const val PROFILE_EDIT = "profile/{id}"
    const val CHANGE_PASSWORD = "change-password"
    const val CONTACT = "contact"
    const val COMING_SOON = "coming-soon/{feature}"

    val AUTH = setOf(LOGIN, REGISTER, FORGOT)

    fun booking(doctorId: Int? = null) = if (doctorId == null) "booking" else "booking?doctorId=$doctorId"
    fun appointment(id: Int) = "appointment/$id"
    fun checkIn(code: String? = null) = if (code == null) "check-in" else "check-in?code=$code"
    fun profile(id: Int) = "profile/$id"
    fun comingSoon(feature: String) = "coming-soon/$feature"
}

/**
 * Like the web portal, the app opens on the public home page. Signing in is only asked for when a
 * screen needs an account (see [RequireAuth]).
 */
@Composable
fun RootScreen() {
    val container = appContainer()
    val session by container.sessionStore.session.collectAsStateWithLifecycle()
    val current = session

    if (current?.account?.mustChangePassword == true) {
        val scope = rememberCoroutineScope()
        ChangePasswordScreen(forced = true, onBack = { scope.launch { container.authRepository.logout() } })
        return
    }
    if (current != null) {
        LaunchedEffect(current.account.accountId) {
            val result = container.authRepository.refreshAccount()
            if (result is ApiResult.Failure && result.message == ErrorMessages.PATIENT_ONLY) {
                container.authNotice.value = ErrorMessages.PATIENT_ONLY
            }
        }
    }
    MainNavHost()
}

private data class Tab(val route: String, val label: String, val icon: ImageVector, val selectedIcon: ImageVector)

private val tabs = listOf(
    Tab(Routes.HOME, "Trang chủ", Icons.Outlined.Home, Icons.Filled.Home),
    Tab(Routes.APPOINTMENTS, "Lịch hẹn", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth),
    Tab(Routes.DOCTORS, "Bác sĩ", Icons.Outlined.MedicalServices, Icons.Filled.MedicalServices),
    Tab(Routes.ACCOUNT, "Tài khoản", Icons.Outlined.Person, Icons.Filled.Person),
)

/** Leaves the sign-in screens and returns to whatever the patient was doing before. */
private fun NavHostController.finishAuth() {
    while (currentDestination?.route in Routes.AUTH && previousBackStackEntry != null) {
        popBackStack()
    }
}

@Composable
private fun MainNavHost() {
    val nav = rememberNavController()
    val sessionStore = appContainer().sessionStore
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val showBar = tabs.any { it.route == route }

    Scaffold(
        containerColor = Brand.Surface,
        contentWindowInsets = WindowInsets(0),
        bottomBar = { if (showBar) BottomBar(nav, route) },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(PaddingValues(bottom = padding.calculateBottomPadding())),
        ) {
            // Public
            composable(Routes.HOME) { HomeScreen(nav) }
            composable(Routes.DOCTORS) { DoctorsScreen(nav) }
            composable(Routes.ACCOUNT) { AccountScreen(nav) }
            composable(Routes.CLINIC_DETAIL) { ClinicDetailScreen(nav) }
            composable(Routes.CONTACT) { ContactScreen(nav) }
            composable(Routes.COMING_SOON) { entry -> ComingSoonScreen(nav, entry.arguments?.getString("feature") ?: "") }

            // Sign-in flow: leaves by itself as soon as a session exists.
            composable(Routes.LOGIN) {
                val session by sessionStore.session.collectAsStateWithLifecycle()
                LaunchedEffect(session) { if (session != null) nav.finishAuth() }
                LoginScreen(
                    onBack = { nav.popBackStack() },
                    onRegister = { nav.navigate(Routes.REGISTER) },
                    onForgotPassword = { nav.navigate(Routes.FORGOT) },
                )
            }
            composable(Routes.REGISTER) {
                val session by sessionStore.session.collectAsStateWithLifecycle()
                LaunchedEffect(session) { if (session != null) nav.finishAuth() }
                RegisterScreen(onBack = { nav.popBackStack() })
            }
            composable(Routes.FORGOT) {
                ForgotPasswordScreen(
                    onBack = { nav.popBackStack() },
                    onDone = { if (!nav.popBackStack(Routes.LOGIN, inclusive = false)) nav.navigate(Routes.LOGIN) { popUpTo(Routes.FORGOT) { inclusive = true } } },
                )
            }

            // Need an account
            composable(Routes.APPOINTMENTS) {
                RequireAuth(nav, "Lịch hẹn của tôi", "Đăng nhập để xem và quản lý các lịch hẹn khám của bạn.", showBack = false) {
                    AppointmentsScreen(nav)
                }
            }
            composable(Routes.SERVICES) {
                RequireAuth(nav, "Dịch vụ & bảng giá", "Đăng nhập để xem bảng giá dịch vụ đang áp dụng tại phòng khám.") {
                    ServicesScreen(nav)
                }
            }
            composable(
                Routes.BOOKING,
                arguments = listOf(navArgument("doctorId") { type = NavType.IntType; defaultValue = -1 }),
            ) { entry ->
                RequireAuth(nav, "Đặt lịch khám", "Đăng nhập hoặc tạo tài khoản bệnh nhân để đặt lịch khám với bác sĩ da liễu.") {
                    BookingScreen(nav, preselectedDoctorId = entry.arguments?.getInt("doctorId")?.takeIf { it > 0 })
                }
            }
            composable(Routes.APPOINTMENT, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                RequireAuth(nav, "Chi tiết lịch hẹn", "Đăng nhập để xem lịch hẹn của bạn.") {
                    AppointmentDetailScreen(nav, entry.arguments!!.getInt("id"))
                }
            }
            composable(
                Routes.CHECK_IN,
                arguments = listOf(navArgument("code") { type = NavType.StringType; nullable = true; defaultValue = null }),
            ) { entry ->
                RequireAuth(nav, "Check-in lấy số", "Đăng nhập để check-in lịch hẹn và nhận số thứ tự.") {
                    CheckInScreen(nav, initialCode = entry.arguments?.getString("code"))
                }
            }
            composable(Routes.PROFILES) {
                RequireAuth(nav, "Hồ sơ bệnh nhân", "Đăng nhập để xem và cập nhật hồ sơ bệnh nhân.") { ProfilesScreen(nav) }
            }
            composable(Routes.PROFILE_EDIT, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                RequireAuth(nav, "Cập nhật hồ sơ", "Đăng nhập để cập nhật hồ sơ bệnh nhân.") {
                    ProfileEditScreen(nav, entry.arguments!!.getInt("id"))
                }
            }
            composable(Routes.CHANGE_PASSWORD) {
                RequireAuth(nav, "Đổi mật khẩu", "Đăng nhập để đổi mật khẩu tài khoản.") {
                    ChangePasswordScreen(forced = false, onBack = { nav.popBackStack() })
                }
            }
        }
    }
}

fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun BottomBar(nav: NavHostController, route: String?) {
    Box {
        NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
            tabs.take(2).forEach { TabItem(it, route, nav) }
            // Room for the raised booking button.
            NavigationBarItem(
                selected = false,
                onClick = { nav.navigate(Routes.booking()) },
                icon = { Spacer(Modifier.size(24.dp)) },
                label = { Text("Đặt lịch", fontWeight = FontWeight.SemiBold, color = Brand.Primary) },
            )
            tabs.drop(2).forEach { TabItem(it, route, nav) }
        }
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(BrandGradient)
                .clickable { nav.navigate(Routes.booking()) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Đặt lịch khám", tint = Color.White, modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun RowScope.TabItem(tab: Tab, route: String?, nav: NavHostController) {
    val selected = route == tab.route
    NavigationBarItem(
        selected = selected,
        onClick = { nav.navigateToTab(tab.route) },
        icon = { Icon(if (selected) tab.selectedIcon else tab.icon, contentDescription = tab.label) },
        label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Brand.Primary,
            selectedTextColor = Brand.Primary,
            indicatorColor = Brand.PrimaryLight,
            unselectedIconColor = Brand.TextLight,
            unselectedTextColor = Brand.TextMuted,
        ),
    )
}
