package com.example.quanlyphongkham.ui.doctors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.dto.DoctorListItem
import com.example.quanlyphongkham.data.repository.ClinicRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.components.AppCard
import com.example.quanlyphongkham.ui.components.AppTextField
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.EmptyState
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.LoadingBox
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.components.formatVnd
import com.example.quanlyphongkham.ui.components.specialtyLabel
import com.example.quanlyphongkham.ui.navigation.Routes
import com.example.quanlyphongkham.ui.theme.Brand
import com.example.quanlyphongkham.ui.appContainer
import com.example.quanlyphongkham.ui.home.DoctorsContent
import com.example.quanlyphongkham.ui.home.StaticDoctorCard
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Specialties seeded by the QLPK migrations. */
val SPECIALTIES = listOf(1 to "Da liễu tổng quát", 2 to "Da liễu thẩm mỹ")

class DoctorsViewModel(private val clinic: ClinicRepository) : ViewModel() {
    var search by mutableStateOf("")
        private set
    var specialtyId by mutableStateOf<Int?>(null)
        private set
    var doctors by mutableStateOf<List<DoctorListItem>>(emptyList())
        private set
    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    private var job: Job? = null

    fun onSearch(value: String) {
        search = value
        load(debounce = true)
    }

    fun onSpecialty(id: Int?) {
        specialtyId = id
        load(debounce = false)
    }

    fun load(debounce: Boolean = false) {
        job?.cancel()
        job = viewModelScope.launch {
            if (debounce) delay(350)
            loading = true
            when (val result = clinic.doctors(search, specialtyId)) {
                is ApiResult.Success -> {
                    doctors = result.data
                    error = null
                }
                is ApiResult.Failure -> error = result.message
            }
            loading = false
        }
    }
}

@Composable
fun DoctorsScreen(nav: NavHostController) {
    val session by appContainer().sessionStore.session.collectAsStateWithLifecycle()
    if (session == null) {
        GuestDoctorsScreen(nav)
        return
    }
    val vm = appViewModel { DoctorsViewModel(it.clinicRepository) }
    // The directory API needs a patient token; load once signed in.
    LaunchedEffect(session?.account?.accountId) { vm.load() }
    Scaffold(topBar = { AppTopBar("Đội ngũ bác sĩ") }, containerColor = Brand.Surface) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Column(Modifier.background(Color.White).padding(16.dp)) {
                AppTextField(vm.search, vm::onSearch, "Tìm theo tên bác sĩ", leadingIcon = Icons.Filled.Search)
                Spacer(Modifier.height(8.dp))
                SpecialtyChips(vm.specialtyId, vm::onSpecialty)
            }
            when {
                vm.loading && vm.doctors.isEmpty() -> LoadingBox()
                vm.error != null && vm.doctors.isEmpty() -> Column(Modifier.padding(16.dp)) { ErrorBanner(vm.error) }
                vm.doctors.isEmpty() -> EmptyState(
                    Icons.Filled.MedicalServices,
                    "Chưa có bác sĩ",
                    "Không tìm thấy bác sĩ phù hợp. Hãy thử bộ lọc khác.",
                )
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(vm.doctors, key = { it.doctorId }) { doctor ->
                        DoctorCard(doctor, actionText = "Đặt lịch với bác sĩ") { nav.navigate(Routes.booking(doctor.doctorId)) }
                    }
                }
            }
        }
    }
}

/** Guests get the web home page's doctor cards: the backend has no public doctor directory. */
@Composable
private fun GuestDoctorsScreen(nav: NavHostController) {
    Scaffold(topBar = { AppTopBar("Đội ngũ bác sĩ") }, containerColor = Brand.Surface) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    DoctorsContent.SUBTITLE,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brand.TextMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            items(DoctorsContent.items.chunked(2)) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { doctor -> StaticDoctorCard(doctor, Modifier.weight(1f)) { nav.navigate(Routes.booking()) } }
                }
            }
            item {
                AppCard {
                    Text("Xem lịch trống và đặt lịch", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Đăng nhập để xem danh sách bác sĩ đang nhận lịch, phí khám và khung giờ còn trống.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Brand.TextMuted,
                    )
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton("Đăng nhập", { nav.navigate(Routes.LOGIN) })
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SpecialtyChips(selected: Int?, onSelect: (Int?) -> Unit) {
    val colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = Brand.Primary,
        selectedLabelColor = Color.White,
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item { FilterChip(selected = selected == null, onClick = { onSelect(null) }, label = { Text("Tất cả") }, colors = colors) }
        items(SPECIALTIES) { (id, name) ->
            FilterChip(selected = selected == id, onClick = { onSelect(id) }, label = { Text(name) }, colors = colors)
        }
    }
}

@Composable
fun DoctorAvatar(url: String?, size: Dp = 64.dp) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(Brand.PrimaryLight),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.Person, contentDescription = null, tint = Brand.Primary, modifier = Modifier.size(size * 0.55f))
        if (!url.isNullOrBlank()) {
            AsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(size))
        }
    }
}

@Composable
fun DoctorCard(
    doctor: DoctorListItem,
    selected: Boolean = false,
    actionText: String? = null,
    onClick: () -> Unit,
) {
    AppCard(onClick = if (actionText == null) onClick else null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DoctorAvatar(doctor.avatarUrl)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    listOfNotNull(doctor.degree, doctor.fullName).joinToString(" "),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                val specialties = doctor.specialties.map { specialtyLabel(it.specialtyCode, it.specialtyName) }
                    .ifEmpty { listOf(specialtyLabel(null, doctor.specialtyName)) }
                Text(specialties.distinct().joinToString(" · "), style = MaterialTheme.typography.bodySmall, color = Brand.Primary)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    doctor.yearsOfExperience?.let {
                        Text("$it năm kinh nghiệm", style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                    }
                    if (doctor.reviewCount > 0 && doctor.averageRating != null) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        Text(" %.1f (%d)".format(doctor.averageRating, doctor.reviewCount), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            if (selected) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Đã chọn", tint = Brand.Primary)
            }
        }
        doctor.consultationFee?.let {
            Spacer(Modifier.height(10.dp))
            Row {
                Text("Phí khám: ", style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted)
                Text(formatVnd(it), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Brand.PrimaryDark)
            }
        }
        if (!doctor.isAcceptingAppointments) {
            Spacer(Modifier.height(6.dp))
            Text("Bác sĩ tạm ngưng nhận lịch", style = MaterialTheme.typography.bodySmall, color = Brand.Danger)
        }
        if (actionText != null) {
            Spacer(Modifier.height(12.dp))
            PrimaryButton(actionText, onClick, enabled = doctor.isAcceptingAppointments, modifier = Modifier.height(44.dp))
        }
    }
}
