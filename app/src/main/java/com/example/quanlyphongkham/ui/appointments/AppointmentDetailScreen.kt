package com.example.quanlyphongkham.ui.appointments

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.dto.Appointment
import com.example.quanlyphongkham.data.repository.ClinicRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.components.AppCard
import com.example.quanlyphongkham.ui.components.AppTextField
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.CANCELLABLE_STATUSES
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.InfoBanner
import com.example.quanlyphongkham.ui.components.InfoRow
import com.example.quanlyphongkham.ui.components.LoadingBox
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.components.SecondaryButton
import com.example.quanlyphongkham.ui.components.StatusChip
import com.example.quanlyphongkham.ui.components.consultationModeLabel
import com.example.quanlyphongkham.ui.components.formatDateTime
import com.example.quanlyphongkham.ui.components.formatDayOfWeek
import com.example.quanlyphongkham.ui.components.formatTime
import com.example.quanlyphongkham.ui.components.formatVnd
import com.example.quanlyphongkham.ui.navigation.Routes
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.launch

class AppointmentDetailViewModel(private val clinic: ClinicRepository, private val id: Int) : ViewModel() {
    var appointment by mutableStateOf<Appointment?>(null)
        private set
    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var cancelling by mutableStateOf(false)
        private set
    var cancelError by mutableStateOf<String?>(null)
        private set
    var paying by mutableStateOf(false)
        private set
    var payError by mutableStateOf<String?>(null)
        private set
    var paidInvoice by mutableStateOf<String?>(null)
        private set

    init {
        load()
    }

    fun load() {
        loading = true
        viewModelScope.launch {
            when (val result = clinic.appointment(id)) {
                is ApiResult.Success -> {
                    appointment = result.data
                    error = null
                }
                is ApiResult.Failure -> error = result.message
            }
            loading = false
        }
    }

    /** Prepays the booking; the backend issues the invoice and moves the appointment to confirmed. */
    fun pay(method: String, reference: String, onDone: () -> Unit) {
        paying = true
        payError = null
        viewModelScope.launch {
            when (val result = clinic.confirmPayment(id, method, reference)) {
                is ApiResult.Success -> {
                    paidInvoice = result.data.invoiceNumber
                    onDone()
                    load()
                }
                is ApiResult.Failure -> payError = result.message
            }
            paying = false
        }
    }

    fun cancel(reason: String, onDone: () -> Unit) {
        if (reason.trim().length < 3) {
            cancelError = "Lý do huỷ cần ít nhất 3 ký tự."
            return
        }
        cancelling = true
        cancelError = null
        viewModelScope.launch {
            when (val result = clinic.cancel(id, reason)) {
                is ApiResult.Success -> {
                    appointment = result.data
                    onDone()
                }
                is ApiResult.Failure -> cancelError = result.message
            }
            cancelling = false
        }
    }
}

private val PAYMENT_METHODS = listOf(
    "bank_transfer" to "Chuyển khoản ngân hàng",
    "qr" to "Quét mã QR",
    "card" to "Thẻ ngân hàng",
)

@Composable
fun AppointmentDetailScreen(nav: NavHostController, id: Int) {
    val vm = appViewModel(key = "appointment-$id") { AppointmentDetailViewModel(it.clinicRepository, id) }
    var showCancel by remember { mutableStateOf(false) }
    var showPay by remember { mutableStateOf(false) }
    // Coming back from check-in: pick up the queue number.
    LaunchedEffect(Unit) { if (vm.appointment != null) vm.load() }

    Scaffold(topBar = { AppTopBar("Chi tiết lịch hẹn", onBack = { nav.popBackStack() }) }, containerColor = Brand.Surface) { padding ->
        val appointment = vm.appointment
        when {
            vm.loading && appointment == null -> LoadingBox(Modifier.padding(padding))
            appointment == null -> ErrorBanner(vm.error, Modifier.padding(padding).padding(16.dp))
            else -> Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                AppointmentSummary(appointment)

                vm.paidInvoice?.let {
                    Spacer(Modifier.height(12.dp))
                    InfoBanner("Đã ghi nhận thanh toán – hoá đơn $it. Lịch hẹn đã được xác nhận.")
                }
                when (appointment.status) {
                    "pending" -> {
                        Spacer(Modifier.height(16.dp))
                        InfoBanner(
                            "Lịch hẹn cần được thanh toán/xác nhận trước khi check-in lấy số.",
                            color = Brand.Warning,
                            background = Brand.WarningSoft,
                        )
                        Spacer(Modifier.height(10.dp))
                        PrimaryButton("Thanh toán đặt lịch", { showPay = true }, icon = Icons.Filled.Payments)
                    }
                    "pending_approval" -> {
                        Spacer(Modifier.height(16.dp))
                        InfoBanner("Mức giảm giá đang chờ phòng khám duyệt, sau đó bạn có thể thanh toán.", color = Brand.Warning, background = Brand.WarningSoft)
                    }
                    "confirmed" -> {
                        Spacer(Modifier.height(16.dp))
                        PrimaryButton(
                            "Check-in lấy số thứ tự",
                            { nav.navigate(Routes.checkIn(appointment.checkInCode)) },
                            icon = Icons.Filled.HowToReg,
                        )
                    }
                }
                if (appointment.status in CANCELLABLE_STATUSES) {
                    Spacer(Modifier.height(10.dp))
                    SecondaryButton("Huỷ lịch hẹn", { showCancel = true }, color = Brand.Danger)
                }
            }
        }
    }

    if (showPay) {
        var method by remember { mutableStateOf("bank_transfer") }
        var reference by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { if (!vm.paying) showPay = false },
            title = { Text("Thanh toán đặt lịch") },
            text = {
                Column {
                    Text("Tổng tiền: ${formatVnd(vm.appointment?.totalAmount)}", style = MaterialTheme.typography.titleMedium, color = Brand.Danger)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Chọn hình thức bạn đã thanh toán. Phòng khám sẽ đối soát theo mã giao dịch.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Brand.TextMuted,
                    )
                    Spacer(Modifier.height(8.dp))
                    PAYMENT_METHODS.forEach { (value, label) ->
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable { method = value },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = method == value, onClick = { method = value })
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    AppTextField(reference, { reference = it.take(100) }, "Mã giao dịch (nếu có)")
                    vm.payError?.let {
                        Spacer(Modifier.height(8.dp))
                        ErrorBanner(it)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { vm.pay(method, reference) { showPay = false } }, enabled = !vm.paying) {
                    Text(if (vm.paying) "Đang xử lý…" else "Xác nhận đã thanh toán")
                }
            },
            dismissButton = { TextButton(onClick = { showPay = false }, enabled = !vm.paying) { Text("Đóng") } },
        )
    }

    if (showCancel) {
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { if (!vm.cancelling) showCancel = false },
            title = { Text("Huỷ lịch hẹn?") },
            text = {
                Column {
                    Text("Vui lòng cho phòng khám biết lý do huỷ.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    AppTextField(reason, { reason = it.take(255) }, "Lý do huỷ", singleLine = false, minLines = 2)
                    vm.cancelError?.let {
                        Spacer(Modifier.height(8.dp))
                        ErrorBanner(it)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { vm.cancel(reason) { showCancel = false } }, enabled = !vm.cancelling) {
                    Text("Xác nhận huỷ", color = Brand.Danger)
                }
            },
            dismissButton = { TextButton(onClick = { showCancel = false }, enabled = !vm.cancelling) { Text("Đóng") } },
        )
    }
}

@Composable
private fun AppointmentSummary(appointment: Appointment) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Lịch hẹn #${appointment.appointmentId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            StatusChip(appointment.status)
        }
        Spacer(Modifier.height(12.dp))
        if (appointment.queueNumber != null) {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Brand.TealLight).padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Số thứ tự khám", style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                Text("${appointment.queueNumber}", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, color = Brand.Teal)
                appointment.checkedInAt?.let { Text("Check-in lúc ${formatDateTime(it)}", style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted) }
            }
            Spacer(Modifier.height(12.dp))
        } else if (appointment.checkInCode != null) {
            val code = appointment.checkInCode
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Brand.PrimaryLight).padding(start = 14.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Mã check-in", style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                    Text(code, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Brand.PrimaryDark)
                }
                IconButton(onClick = { scope.launch { clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Mã check-in", code))) } }) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Sao chép mã", tint = Brand.Primary)
                }
            }
            Text(
                "Xuất trình mã này tại quầy lễ tân hoặc tự check-in trong khoảng 30 phút trước giờ hẹn.",
                style = MaterialTheme.typography.bodySmall,
                color = Brand.TextMuted,
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
            )
        }
        InfoRow("Bác sĩ", appointment.doctorFullName)
        InfoRow("Ngày khám", formatDayOfWeek(appointment.appointmentDate))
        InfoRow("Giờ khám", "${formatTime(appointment.appointmentTime)} (${appointment.durationMinutes} phút)")
        InfoRow("Người khám", appointment.patientFullName)
        InfoRow("Hình thức", consultationModeLabel(appointment.consultationMode))
        appointment.cancellationReason?.let { InfoRow("Lý do huỷ", it) }
    }
    Spacer(Modifier.height(12.dp))
    AppCard {
        Text("Chi phí", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        if (appointment.services.isEmpty()) InfoRow("Dịch vụ", "Bác sĩ tư vấn khi khám")
        appointment.services.forEach { line ->
            InfoRow(if (line.quantity > 1) "${line.serviceName} ×${line.quantity}" else line.serviceName, formatVnd(line.lineAmount))
        }
        HorizontalDivider(Modifier.padding(vertical = 6.dp), color = Brand.BorderSubtle)
        InfoRow("Tạm tính", formatVnd(appointment.subtotalAmount))
        if (appointment.discountPercent > 0) InfoRow("Giảm giá", "${appointment.discountPercent.toInt()}%")
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Tổng cộng", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            Text(formatVnd(appointment.totalAmount), style = MaterialTheme.typography.titleLarge, color = Brand.Danger)
        }
        if (appointment.discountApprovalRequired) {
            Text("Mức giảm giá đang chờ phòng khám duyệt.", style = MaterialTheme.typography.bodySmall, color = Brand.Warning)
        }
    }
}
