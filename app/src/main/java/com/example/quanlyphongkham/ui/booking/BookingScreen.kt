package com.example.quanlyphongkham.ui.booking

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.dto.Appointment
import com.example.quanlyphongkham.data.remote.dto.AvailableSlot
import com.example.quanlyphongkham.data.remote.dto.ClinicService
import com.example.quanlyphongkham.data.remote.dto.CreateAppointmentRequest
import com.example.quanlyphongkham.data.remote.dto.DoctorListItem
import com.example.quanlyphongkham.data.remote.dto.PatientProfile
import com.example.quanlyphongkham.data.remote.dto.ServiceQuantity
import com.example.quanlyphongkham.data.repository.ClinicRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.captcha.CaptchaState
import com.example.quanlyphongkham.ui.captcha.NOT_SOLVED_MESSAGE
import com.example.quanlyphongkham.ui.captcha.TurnstileCaptcha
import com.example.quanlyphongkham.ui.components.AppCard
import com.example.quanlyphongkham.ui.components.AppTextField
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.EmptyState
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.IconBadge
import com.example.quanlyphongkham.ui.components.InfoRow
import com.example.quanlyphongkham.ui.components.LoadingBox
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.components.SecondaryButton
import com.example.quanlyphongkham.ui.components.StatusChip
import com.example.quanlyphongkham.ui.components.clinicNow
import com.example.quanlyphongkham.ui.components.clinicToday
import com.example.quanlyphongkham.ui.components.formatDate
import com.example.quanlyphongkham.ui.components.formatDayOfWeek
import com.example.quanlyphongkham.ui.components.formatTime
import com.example.quanlyphongkham.ui.components.formatVnd
import com.example.quanlyphongkham.ui.components.parseTime
import com.example.quanlyphongkham.ui.components.relationshipLabel
import com.example.quanlyphongkham.ui.doctors.DoctorCard
import com.example.quanlyphongkham.ui.doctors.SpecialtyChips
import com.example.quanlyphongkham.ui.navigation.Routes
import com.example.quanlyphongkham.ui.services.ServiceCard
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val STEP_TITLES = listOf("Bác sĩ", "Thời gian", "Dịch vụ", "Người khám", "Xác nhận")

class BookingViewModel(private val clinic: ClinicRepository, preselectedDoctorId: Int?) : ViewModel() {
    var step by mutableIntStateOf(0)
        private set
    var error by mutableStateOf<String?>(null)
    var notice by mutableStateOf<String?>(null)
        private set

    // Step 1 — doctor
    var doctors by mutableStateOf<List<DoctorListItem>>(emptyList())
        private set
    var doctorsLoading by mutableStateOf(true)
        private set
    var specialtyId by mutableStateOf<Int?>(null)
        private set
    var search by mutableStateOf("")
    var doctor by mutableStateOf<DoctorListItem?>(null)
        private set

    // Step 2 — date & slot
    private val today = clinicToday()
    val dates: List<LocalDate> = (0L until 14L).map { today.plusDays(it) }
    var date by mutableStateOf(today)
        private set
    var slots by mutableStateOf<List<AvailableSlot>>(emptyList())
        private set
    var holiday by mutableStateOf(false)
        private set
    var slotsLoading by mutableStateOf(false)
        private set
    var slot by mutableStateOf<AvailableSlot?>(null)
        private set
    private var slotsJob: Job? = null

    // Step 3 — services (optional)
    var services by mutableStateOf<List<ClinicService>>(emptyList())
        private set
    val selectedServices = mutableStateListOf<Int>()

    // Step 4 — who is visiting
    var profiles by mutableStateOf<List<PatientProfile>>(emptyList())
        private set
    var patientId by mutableStateOf<Int?>(null)
    var reason by mutableStateOf("")
    var promotionCode by mutableStateOf("")

    // Step 5
    val captcha = CaptchaState()
    var submitting by mutableStateOf(false)
        private set
    var booked by mutableStateOf<Appointment?>(null)
        private set

    init {
        loadDoctors(preselectedDoctorId)
        viewModelScope.launch {
            (clinic.services() as? ApiResult.Success)?.let { services = it.data }
        }
        viewModelScope.launch {
            (clinic.profiles() as? ApiResult.Success)?.let { result ->
                profiles = result.data
                patientId = result.data.firstOrNull { it.relationshipToAccount == "self" }?.patientId ?: result.data.firstOrNull()?.patientId
            }
        }
    }

    fun loadDoctors(preselectId: Int? = null) {
        doctorsLoading = true
        viewModelScope.launch {
            when (val result = clinic.doctors(search, specialtyId)) {
                is ApiResult.Success -> {
                    doctors = result.data
                    error = null
                    preselectId?.let { id -> result.data.firstOrNull { it.doctorId == id }?.let { selectDoctor(it) } }
                }
                is ApiResult.Failure -> error = result.message
            }
            doctorsLoading = false
        }
    }

    fun onSpecialty(id: Int?) {
        specialtyId = id
        loadDoctors()
    }

    fun selectDoctor(value: DoctorListItem) {
        doctor = value
        slot = null
        step = 1
        loadSlots()
    }

    fun selectDate(value: LocalDate) {
        date = value
        slot = null
        loadSlots()
    }

    fun loadSlots() {
        val doctorId = doctor?.doctorId ?: return
        slotsJob?.cancel()
        slotsLoading = true
        slotsJob = viewModelScope.launch {
            when (val result = clinic.availableSlots(doctorId, date.toString())) {
                is ApiResult.Success -> {
                    holiday = result.data.isClinicHoliday
                    val now = clinicNow()
                    slots = result.data.slots.filter { s ->
                        date != clinicToday() || (parseTime(s.startTime)?.isAfter(now) ?: true)
                    }
                    error = null
                }
                is ApiResult.Failure -> {
                    slots = emptyList()
                    error = result.message
                }
            }
            slotsLoading = false
        }
    }

    fun selectSlot(value: AvailableSlot) {
        slot = value
    }

    fun toggleService(id: Int) {
        if (id in selectedServices) selectedServices.remove(id) else selectedServices.add(id)
    }

    fun next() {
        error = null
        when (step) {
            1 -> if (slot == null) error = "Vui lòng chọn khung giờ khám." else step = 2
            2 -> step = 3
            3 -> if (patientId == null) error = "Vui lòng chọn hồ sơ người đi khám." else step = 4
        }
    }

    fun back(): Boolean {
        if (booked != null || step == 0) return false
        error = null
        step -= 1
        return true
    }

    fun goToStep(target: Int) {
        if (target < step && booked == null) {
            error = null
            step = target
        }
    }

    val servicesTotal: Double
        get() = services.filter { it.serviceId in selectedServices }.sumOf { it.price }

    fun submit() {
        val doctor = doctor ?: return
        val slot = slot ?: return
        val patientId = patientId ?: return
        val token = captcha.consume()
        if (token == null) {
            error = NOT_SOLVED_MESSAGE
            return
        }
        submitting = true
        error = null
        viewModelScope.launch {
            val request = CreateAppointmentRequest(
                patientId = patientId,
                doctorId = doctor.doctorId,
                appointmentDate = date.toString(),
                appointmentTime = slot.startTime,
                consultationMode = slot.consultationMode,
                visitType = "new_visit",
                reasonForVisit = reason.trim().ifBlank { null },
                primaryServiceId = selectedServices.firstOrNull(),
                services = selectedServices.map { ServiceQuantity(it) },
                promotionCode = promotionCode.trim().ifBlank { null },
            )
            when (val result = clinic.book(request, token)) {
                is ApiResult.Success -> {
                    booked = result.data
                    notice = if (result.status == 202 || result.data.status == "pending_approval") {
                        "Lịch hẹn đang chờ phòng khám duyệt mức giảm giá."
                    } else null
                }
                is ApiResult.Failure -> {
                    error = result.message
                    if (result.errorCode == "slot_taken" || result.errorCode == "slot_not_bookable") {
                        this@BookingViewModel.slot = null
                        step = 1
                        loadSlots()
                    }
                }
            }
            submitting = false
        }
    }
}

@Composable
fun BookingScreen(nav: NavHostController, preselectedDoctorId: Int?) {
    val vm = appViewModel(key = "booking-$preselectedDoctorId") { BookingViewModel(it.clinicRepository, preselectedDoctorId) }
    val booked = vm.booked

    BackHandler(enabled = vm.step > 0 && booked == null) { vm.back() }

    Scaffold(
        topBar = { AppTopBar(if (booked == null) "Đặt lịch khám" else "Đặt lịch thành công", onBack = { if (!vm.back()) nav.popBackStack() }) },
        containerColor = Brand.Surface,
        bottomBar = {
            if (booked == null && vm.step > 0) {
                Surface(shadowElevation = 8.dp, color = Color.White) {
                    Column(Modifier.navigationBarsPadding().padding(16.dp)) {
                        ErrorBanner(vm.error)
                        if (vm.error != null) Spacer(Modifier.height(8.dp))
                        if (vm.step < 4) {
                            PrimaryButton(if (vm.step == 2 && vm.selectedServices.isEmpty()) "Bỏ qua, tiếp tục" else "Tiếp tục", vm::next)
                        } else {
                            PrimaryButton("Xác nhận đặt lịch", vm::submit, loading = vm.submitting, enabled = vm.captcha.solved)
                        }
                    }
                }
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (booked != null) {
                BookedResult(booked, vm.notice, nav)
                return@Column
            }
            StepIndicator(vm.step, vm::goToStep)
            when (vm.step) {
                0 -> DoctorStep(vm)
                1 -> TimeStep(vm)
                2 -> ServiceStep(vm)
                3 -> PatientStep(vm, nav)
                else -> ConfirmStep(vm)
            }
        }
    }
}

@Composable
private fun StepIndicator(step: Int, onStep: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        STEP_TITLES.forEachIndexed { index, title ->
            val done = index < step
            val active = index == step
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).clickable(enabled = done) { onStep(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (done || active) Brand.Primary else Brand.BorderSubtle),
                    contentAlignment = Alignment.Center,
                ) {
                    if (done) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    } else {
                        Text("${index + 1}", color = if (active) Color.White else Brand.TextMuted, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = if (active) Brand.Primary else Brand.TextMuted, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun DoctorStep(vm: BookingViewModel) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            AppTextField(vm.search, { vm.search = it; vm.loadDoctors() }, "Tìm bác sĩ", leadingIcon = Icons.Filled.Search)
            Spacer(Modifier.height(8.dp))
            SpecialtyChips(vm.specialtyId, vm::onSpecialty)
        }
        when {
            vm.doctorsLoading && vm.doctors.isEmpty() -> LoadingBox()
            vm.error != null && vm.doctors.isEmpty() -> ErrorBanner(vm.error, Modifier.padding(16.dp))
            vm.doctors.isEmpty() -> EmptyState(Icons.Filled.MedicalServices, "Chưa có bác sĩ nhận lịch", "Phòng khám chưa có bác sĩ phù hợp để đặt lịch.")
            else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(vm.doctors, key = { it.doctorId }) { doctor ->
                    DoctorCard(doctor, selected = vm.doctor?.doctorId == doctor.doctorId) {
                        if (doctor.isAcceptingAppointments) vm.selectDoctor(doctor)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimeStep(vm: BookingViewModel) {
    val locale = Locale.forLanguageTag("vi-VN")
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 16.dp)) {
        vm.doctor?.let {
            Text("BS. ${it.fullName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
        }
        Text("Chọn ngày khám", style = MaterialTheme.typography.titleSmall, color = Brand.TextMuted, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.dates) { day ->
                val selected = day == vm.date
                Column(
                    Modifier
                        .width(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) Brand.Primary else Color.White)
                        .border(1.dp, if (selected) Brand.Primary else Brand.Border, RoundedCornerShape(14.dp))
                        .clickable { vm.selectDate(day) }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val label = if (day == vm.dates.first()) "Hôm nay" else day.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
                    Text(label, style = MaterialTheme.typography.labelSmall, color = if (selected) Color.White else Brand.TextMuted)
                    Text("${day.dayOfMonth}", style = MaterialTheme.typography.titleLarge, color = if (selected) Color.White else Brand.TextMain)
                    Text("Th${day.monthValue}", style = MaterialTheme.typography.labelSmall, color = if (selected) Color.White else Brand.TextMuted)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Khung giờ còn trống", style = MaterialTheme.typography.titleSmall, color = Brand.TextMuted, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        when {
            vm.slotsLoading -> LoadingBox(Modifier.height(120.dp))
            vm.holiday -> EmptyState(Icons.Filled.EventBusy, "Phòng khám nghỉ", "Ngày này phòng khám nghỉ lễ. Vui lòng chọn ngày khác.")
            vm.slots.isEmpty() -> EmptyState(Icons.Filled.EventBusy, "Hết lịch trống", "Bác sĩ không còn khung giờ trống trong ngày này. Hãy chọn ngày khác.")
            else -> FlowRow(
                Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 4,
            ) {
                vm.slots.forEach { slot ->
                    val selected = vm.slot == slot
                    Text(
                        formatTime(slot.startTime),
                        // A fixed share of the row keeps a short last row from stretching its chips.
                        modifier = Modifier
                            .fillMaxWidth(0.23f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) Brand.Primary else Color.White)
                            .border(1.dp, if (selected) Brand.Primary else Brand.Border, RoundedCornerShape(10.dp))
                            .clickable { vm.selectSlot(slot) }
                            .padding(vertical = 12.dp),
                        textAlign = TextAlign.Center,
                        color = if (selected) Color.White else Brand.TextMain,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceStep(vm: BookingViewModel) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                "Chọn dịch vụ bạn quan tâm (không bắt buộc). Bác sĩ sẽ tư vấn thêm khi khám.",
                style = MaterialTheme.typography.bodyMedium,
                color = Brand.TextMuted,
            )
        }
        if (vm.services.isEmpty()) {
            item { Text("Phòng khám chưa công bố dịch vụ.", color = Brand.TextMuted) }
        }
        items(vm.services, key = { it.serviceId }) { service ->
            Box(Modifier.clickable { vm.toggleService(service.serviceId) }) {
                ServiceCard(service) {
                    Checkbox(
                        checked = service.serviceId in vm.selectedServices,
                        onCheckedChange = { vm.toggleService(service.serviceId) },
                        colors = CheckboxDefaults.colors(checkedColor = Brand.Primary),
                    )
                }
            }
        }
    }
}

@Composable
private fun PatientStep(vm: BookingViewModel, nav: NavHostController) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Hồ sơ người đi khám", style = MaterialTheme.typography.titleSmall, color = Brand.TextMuted)
        Spacer(Modifier.height(8.dp))
        if (vm.profiles.isEmpty()) {
            EmptyState(Icons.Filled.Groups, "Chưa có hồ sơ", "Không tải được hồ sơ bệnh nhân.", actionText = "Mở hồ sơ", onAction = { nav.navigate(Routes.PROFILES) })
        }
        vm.profiles.forEach { profile ->
            val selected = vm.patientId == profile.patientId
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) Brand.PrimaryLight else Color.White)
                    .border(BorderStroke(1.dp, if (selected) Brand.Primary else Brand.Border), RoundedCornerShape(14.dp))
                    .clickable { vm.patientId = profile.patientId }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = selected, onClick = { vm.patientId = profile.patientId })
                Column(Modifier.weight(1f)) {
                    Text(profile.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "${relationshipLabel(profile.relationshipToAccount)} · ${profile.patientCode}" +
                            (profile.dateOfBirth?.let { " · ${formatDate(it)}" } ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = Brand.TextMuted,
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        AppTextField(vm.reason, { vm.reason = it.take(1000) }, "Triệu chứng / lý do khám", singleLine = false, minLines = 3)
        Spacer(Modifier.height(12.dp))
        AppTextField(vm.promotionCode, { vm.promotionCode = it.take(50) }, "Mã khuyến mãi (nếu có)", leadingIcon = Icons.Filled.Sell)
    }
}

@Composable
private fun ConfirmStep(vm: BookingViewModel) {
    val doctor = vm.doctor
    val slot = vm.slot
    val profile = vm.profiles.firstOrNull { it.patientId == vm.patientId }
    val chosen = vm.services.filter { it.serviceId in vm.selectedServices }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        AppCard {
            Text("Thông tin lịch khám", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            InfoRow("Bác sĩ", doctor?.let { listOfNotNull(it.degree, it.fullName).joinToString(" ") })
            InfoRow("Ngày khám", formatDayOfWeek(vm.date.toString()))
            InfoRow("Giờ khám", slot?.let { "${formatTime(it.startTime)} – ${formatTime(it.endTime)}" })
            InfoRow("Người khám", profile?.let { "${it.fullName} (${it.patientCode})" })
            if (vm.reason.isNotBlank()) InfoRow("Lý do khám", vm.reason)
            if (vm.promotionCode.isNotBlank()) InfoRow("Mã khuyến mãi", vm.promotionCode.trim())
        }
        Spacer(Modifier.height(12.dp))
        AppCard {
            Text("Chi phí dự kiến", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            doctor?.consultationFee?.let { InfoRow("Phí khám bác sĩ (tham khảo)", formatVnd(it)) }
            if (chosen.isEmpty()) InfoRow("Dịch vụ", "Bác sĩ tư vấn khi khám")
            chosen.forEach { InfoRow(it.serviceName, formatVnd(it.price)) }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Tạm tính dịch vụ", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text(formatVnd(vm.servicesTotal), style = MaterialTheme.typography.titleLarge, color = Brand.Danger)
            }
            Text(
                "Số tiền cuối cùng do phòng khám xác nhận (đã áp dụng khuyến mãi nếu có).",
                style = MaterialTheme.typography.bodySmall,
                color = Brand.TextMuted,
            )
        }
        Spacer(Modifier.height(12.dp))
        Text("Xác minh bảo mật", style = MaterialTheme.typography.titleSmall, color = Brand.TextMuted)
        Spacer(Modifier.height(6.dp))
        TurnstileCaptcha(vm.captcha)
    }
}

@Composable
private fun BookedResult(appointment: Appointment, notice: String?, nav: NavHostController) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(12.dp))
        IconBadge(Icons.Filled.CheckCircle, Brand.Success, Brand.SuccessSoft, size = 88.dp)
        Spacer(Modifier.height(16.dp))
        Text("Đặt lịch thành công!", style = MaterialTheme.typography.headlineSmall, color = Brand.Success)
        Spacer(Modifier.height(6.dp))
        Text(
            "Hãy thanh toán đặt lịch trong phần chi tiết để lịch được xác nhận, " +
                "sau đó đến trước giờ hẹn và check-in bằng mã bên dưới để lấy số thứ tự.",
            textAlign = TextAlign.Center,
            color = Brand.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
        notice?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Brand.Warning, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(20.dp))
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Lịch hẹn #${appointment.appointmentId}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                StatusChip(appointment.status)
            }
            Spacer(Modifier.height(8.dp))
            InfoRow("Bác sĩ", appointment.doctorFullName)
            InfoRow("Thời gian", "${formatTime(appointment.appointmentTime)} · ${formatDate(appointment.appointmentDate)}")
            InfoRow("Người khám", appointment.patientFullName)
            InfoRow("Tổng tiền", formatVnd(appointment.totalAmount))
            appointment.checkInCode?.let {
                Spacer(Modifier.height(10.dp))
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Brand.PrimaryLight).padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Mã check-in", style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                    Text(it, style = MaterialTheme.typography.headlineSmall, color = Brand.PrimaryDark, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        PrimaryButton("Xem chi tiết lịch hẹn", {
            nav.navigate(Routes.appointment(appointment.appointmentId)) { popUpTo(Routes.HOME) }
        })
        Spacer(Modifier.height(10.dp))
        SecondaryButton("Về trang chủ", { nav.popBackStack(Routes.HOME, inclusive = false) })
    }
}
