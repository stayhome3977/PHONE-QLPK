package com.example.quanlyphongkham.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.dto.PatientProfile
import com.example.quanlyphongkham.data.remote.dto.UpdatePatientProfileRequest
import com.example.quanlyphongkham.data.repository.ClinicRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.components.AppCard
import com.example.quanlyphongkham.ui.components.AppTextField
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.DateField
import com.example.quanlyphongkham.ui.components.EmptyState
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.IconBadge
import com.example.quanlyphongkham.ui.components.InfoBanner
import com.example.quanlyphongkham.ui.components.InfoRow
import com.example.quanlyphongkham.ui.components.LoadingBox
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.components.formatDate
import com.example.quanlyphongkham.ui.components.genderLabel
import com.example.quanlyphongkham.ui.components.relationshipLabel
import com.example.quanlyphongkham.ui.navigation.Routes
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.launch

class ProfilesViewModel(private val clinic: ClinicRepository) : ViewModel() {
    var profiles by mutableStateOf<List<PatientProfile>>(emptyList())
        private set
    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun load() {
        loading = true
        viewModelScope.launch {
            when (val result = clinic.profiles()) {
                is ApiResult.Success -> {
                    profiles = result.data
                    error = null
                }
                is ApiResult.Failure -> error = result.message
            }
            loading = false
        }
    }
}

@Composable
fun ProfilesScreen(nav: NavHostController) {
    val vm = appViewModel { ProfilesViewModel(it.clinicRepository) }
    LaunchedEffect(Unit) { vm.load() }
    Scaffold(topBar = { AppTopBar("Hồ sơ bệnh nhân", onBack = { nav.popBackStack() }) }, containerColor = Brand.Surface) { padding ->
        when {
            vm.loading && vm.profiles.isEmpty() -> LoadingBox(Modifier.padding(padding))
            vm.error != null && vm.profiles.isEmpty() -> ErrorBanner(vm.error, Modifier.padding(padding).padding(16.dp))
            vm.profiles.isEmpty() -> EmptyState(Icons.Filled.AssignmentInd, "Chưa có hồ sơ", "Tài khoản chưa có hồ sơ bệnh nhân.", Modifier.padding(padding))
            else -> LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(vm.profiles, key = { it.patientId }) { profile ->
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconBadge(Icons.Filled.AssignmentInd, Brand.Primary, Brand.PrimaryLight)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(profile.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${profile.patientCode} · ${relationshipLabel(profile.relationshipToAccount)}", style = MaterialTheme.typography.bodySmall, color = Brand.Primary)
                            }
                            IconButton(onClick = { nav.navigate(Routes.profile(profile.patientId)) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Sửa hồ sơ", tint = Brand.Primary)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoRow("Ngày sinh", formatDate(profile.dateOfBirth))
                        InfoRow("Giới tính", genderLabel(profile.gender))
                        InfoRow("Điện thoại", profile.accountPhoneNumber)
                        InfoRow("Email", profile.accountEmail)
                        InfoRow("Địa chỉ", profile.address)
                        InfoRow("Nhóm máu", profile.bloodType)
                        InfoRow("Số BHYT", profile.healthInsuranceNumber)
                        InfoRow("Dị ứng", profile.allergyNotes)
                        InfoRow("Liên hệ khẩn cấp", listOfNotNull(profile.emergencyContactName, profile.emergencyContactPhone).joinToString(" · "))
                    }
                }
            }
        }
    }
}

class ProfileEditViewModel(private val clinic: ClinicRepository, private val patientId: Int) : ViewModel() {
    var loading by mutableStateOf(true)
        private set
    var saving by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var saved by mutableStateOf(false)
        private set

    var fullName by mutableStateOf("")
    var dateOfBirth by mutableStateOf<String?>(null)
    var gender by mutableStateOf<String?>(null)
    var address by mutableStateOf("")
    var occupation by mutableStateOf("")
    var bloodType by mutableStateOf<String?>(null)
    var insuranceNumber by mutableStateOf("")
    var insuranceExpiry by mutableStateOf<String?>(null)
    var emergencyName by mutableStateOf("")
    var emergencyPhone by mutableStateOf("")
    var allergies by mutableStateOf("")

    init {
        viewModelScope.launch {
            when (val result = clinic.profiles()) {
                is ApiResult.Success -> result.data.firstOrNull { it.patientId == patientId }?.let(::fill)
                    ?: run { error = "Không tìm thấy hồ sơ." }
                is ApiResult.Failure -> error = result.message
            }
            loading = false
        }
    }

    private fun fill(p: PatientProfile) {
        fullName = p.fullName
        dateOfBirth = p.dateOfBirth
        gender = p.gender
        address = p.address.orEmpty()
        occupation = p.occupation.orEmpty()
        bloodType = p.bloodType
        insuranceNumber = p.healthInsuranceNumber.orEmpty()
        insuranceExpiry = p.healthInsuranceExpiry
        emergencyName = p.emergencyContactName.orEmpty()
        emergencyPhone = p.emergencyContactPhone.orEmpty()
        allergies = p.allergyNotes.orEmpty()
    }

    fun save() {
        error = when {
            fullName.trim().length < 2 -> "Vui lòng nhập họ và tên."
            emergencyPhone.isNotBlank() && !Regex("^\\+?[0-9]{1,15}$").matches(emergencyPhone.trim()) -> "Số điện thoại liên hệ khẩn cấp chỉ gồm chữ số."
            else -> null
        }
        if (error != null) return
        saving = true
        saved = false
        viewModelScope.launch {
            val request = UpdatePatientProfileRequest(
                fullName = fullName.trim(),
                dateOfBirth = dateOfBirth,
                gender = gender,
                address = address.trim().ifBlank { null },
                occupation = occupation.trim().ifBlank { null },
                bloodType = bloodType,
                healthInsuranceNumber = insuranceNumber.trim().ifBlank { null },
                healthInsuranceExpiry = insuranceExpiry,
                emergencyContactName = emergencyName.trim().ifBlank { null },
                emergencyContactPhone = emergencyPhone.trim().ifBlank { null },
                allergyNotes = allergies.trim().ifBlank { null },
            )
            when (val result = clinic.updateProfile(patientId, request)) {
                is ApiResult.Success -> {
                    fill(result.data)
                    saved = true
                }
                is ApiResult.Failure -> error = result.message
            }
            saving = false
        }
    }
}

private val BLOOD_TYPES = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileEditScreen(nav: NavHostController, patientId: Int) {
    val vm = appViewModel(key = "profile-$patientId") { ProfileEditViewModel(it.clinicRepository, patientId) }
    Scaffold(
        topBar = { AppTopBar("Cập nhật hồ sơ", onBack = { nav.popBackStack() }) },
        containerColor = Brand.Surface,
        bottomBar = {
            if (!vm.loading) {
                Surface(shadowElevation = 8.dp, color = Color.White) {
                    Column(Modifier.navigationBarsPadding().imePadding().padding(16.dp)) {
                        ErrorBanner(vm.error)
                        if (vm.saved) InfoBanner("Đã lưu hồ sơ.")
                        if (vm.error != null || vm.saved) Spacer(Modifier.height(8.dp))
                        PrimaryButton("Lưu thay đổi", vm::save, loading = vm.saving)
                    }
                }
            }
        },
    ) { padding ->
        if (vm.loading) {
            LoadingBox(Modifier.padding(padding))
            return@Scaffold
        }
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            SectionLabel("Thông tin cá nhân")
            AppTextField(vm.fullName, { vm.fullName = it.take(100) }, "Họ và tên", leadingIcon = Icons.Filled.Badge)
            Spacer(Modifier.height(12.dp))
            DateField(vm.dateOfBirth, { vm.dateOfBirth = it }, "Ngày sinh")
            Spacer(Modifier.height(12.dp))
            Text("Giới tính", style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("male" to "Nam", "female" to "Nữ", "other" to "Khác").forEach { (value, label) ->
                    FilterChip(selected = vm.gender == value, onClick = { vm.gender = value }, label = { Text(label) })
                }
            }
            Spacer(Modifier.height(8.dp))
            AppTextField(vm.address, { vm.address = it.take(255) }, "Địa chỉ", leadingIcon = Icons.Filled.Home)
            Spacer(Modifier.height(12.dp))
            AppTextField(vm.occupation, { vm.occupation = it.take(100) }, "Nghề nghiệp", leadingIcon = Icons.Filled.Work)

            Spacer(Modifier.height(20.dp))
            SectionLabel("Thông tin y tế")
            Text("Nhóm máu", style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BLOOD_TYPES.forEach { type ->
                    FilterChip(
                        selected = vm.bloodType == type,
                        onClick = { vm.bloodType = if (vm.bloodType == type) null else type },
                        label = { Text(type) },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            AppTextField(vm.insuranceNumber, { vm.insuranceNumber = it.take(50) }, "Số thẻ BHYT", leadingIcon = Icons.Filled.HealthAndSafety)
            Spacer(Modifier.height(12.dp))
            DateField(vm.insuranceExpiry, { vm.insuranceExpiry = it }, "Hạn thẻ BHYT")
            Spacer(Modifier.height(12.dp))
            AppTextField(vm.allergies, { vm.allergies = it.take(1000) }, "Tiền sử dị ứng (thuốc, mỹ phẩm…)", singleLine = false, minLines = 3)

            Spacer(Modifier.height(20.dp))
            SectionLabel("Liên hệ khẩn cấp")
            AppTextField(vm.emergencyName, { vm.emergencyName = it.take(100) }, "Họ tên người thân", leadingIcon = Icons.Filled.Badge)
            Spacer(Modifier.height(12.dp))
            AppTextField(vm.emergencyPhone, { vm.emergencyPhone = it.take(15) }, "Số điện thoại", leadingIcon = Icons.Filled.Phone, keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall, color = Brand.Primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
}
