package com.example.quanlyphongkham.ui.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.dto.CheckInResponse
import com.example.quanlyphongkham.data.repository.ClinicRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.components.AppCard
import com.example.quanlyphongkham.ui.components.AppTextField
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.BrandGradient
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.IconBadge
import com.example.quanlyphongkham.ui.components.InfoRow
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.components.SecondaryButton
import com.example.quanlyphongkham.ui.components.formatDateTime
import com.example.quanlyphongkham.ui.components.formatTime
import com.example.quanlyphongkham.ui.components.specialtyLabel
import com.example.quanlyphongkham.ui.navigation.Routes
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.launch

class CheckInViewModel(private val clinic: ClinicRepository, initialCode: String?) : ViewModel() {
    var code by mutableStateOf(initialCode.orEmpty())
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var result by mutableStateOf<CheckInResponse?>(null)
        private set

    fun submit() {
        val trimmed = code.trim()
        if (trimmed.length !in 8..20) {
            error = "Mã check-in gồm 8–20 ký tự, xem trong chi tiết lịch hẹn."
            return
        }
        loading = true
        error = null
        viewModelScope.launch {
            when (val response = clinic.checkIn(trimmed)) {
                is ApiResult.Success -> result = response.data
                is ApiResult.Failure -> error = when {
                    response.errorCode != null -> response.message
                    // Without an error code, a 400 means the appointment is not confirmed (prepaid) yet.
                    response.status == 400 -> "Lịch hẹn chưa được xác nhận. Vui lòng thanh toán đặt lịch trong chi tiết lịch hẹn trước khi check-in."
                    response.status == 404 -> "Mã check-in không đúng. Vui lòng kiểm tra lại trong chi tiết lịch hẹn."
                    else -> response.message
                }
            }
            loading = false
        }
    }
}

@Composable
fun CheckInScreen(nav: NavHostController, initialCode: String?) {
    val vm = appViewModel(key = "check-in-$initialCode") { CheckInViewModel(it.clinicRepository, initialCode) }
    Scaffold(topBar = { AppTopBar("Check-in lấy số", onBack = { nav.popBackStack() }) }, containerColor = Brand.Surface) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val result = vm.result
            if (result == null) {
                Spacer(Modifier.height(12.dp))
                IconBadge(Icons.Filled.QrCode2, Brand.Primary, Brand.PrimaryLight, size = 88.dp)
                Spacer(Modifier.height(16.dp))
                Text("Tự check-in tại phòng khám", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Khi đã đến phòng khám, nhập mã check-in của lịch hẹn để nhận số thứ tự. " +
                        "Có thể check-in từ 30 phút trước đến 15 phút sau giờ hẹn.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brand.TextMuted,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))
                AppTextField(vm.code, { vm.code = it.trim().take(20) }, "Mã check-in", leadingIcon = Icons.Filled.HowToReg)
                Spacer(Modifier.height(12.dp))
                ErrorBanner(vm.error)
                Spacer(Modifier.height(12.dp))
                PrimaryButton("Check-in", vm::submit, loading = vm.loading)
                Spacer(Modifier.height(10.dp))
                SecondaryButton("Tìm mã trong lịch hẹn", { nav.navigate(Routes.APPOINTMENTS) { popUpTo(Routes.HOME) } })
            } else {
                Spacer(Modifier.height(8.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(BrandGradient)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("SỐ THỨ TỰ CỦA BẠN", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.labelLarge)
                    Text("${result.queueNumber}", color = Color.White, fontSize = 96.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Vui lòng chờ gọi tên tại khu vực khám", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(16.dp))
                AppCard {
                    InfoRow("Bác sĩ", result.doctorFullName)
                    InfoRow("Chuyên khoa", specialtyLabel(null, result.specialtyName))
                    InfoRow("Giờ hẹn", formatTime(result.appointmentTime))
                    InfoRow("Check-in lúc", formatDateTime(result.checkedInAt))
                }
                Spacer(Modifier.height(20.dp))
                PrimaryButton("Xem lịch hẹn", { nav.navigate(Routes.appointment(result.appointmentId)) { popUpTo(Routes.HOME) } })
                Spacer(Modifier.height(10.dp))
                SecondaryButton("Về trang chủ", { nav.popBackStack(Routes.HOME, inclusive = false) })
            }
        }
    }
}
