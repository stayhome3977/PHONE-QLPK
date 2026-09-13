package com.example.quanlyphongkham.ui.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.dto.AppointmentListItem
import com.example.quanlyphongkham.data.repository.ClinicRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.components.ACTIVE_STATUSES
import com.example.quanlyphongkham.ui.components.AppCard
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.EmptyState
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.StatusChip
import com.example.quanlyphongkham.ui.components.consultationModeLabel
import com.example.quanlyphongkham.ui.components.formatDayOfWeek
import com.example.quanlyphongkham.ui.components.formatTime
import com.example.quanlyphongkham.ui.navigation.Routes
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.launch

class AppointmentsViewModel(private val clinic: ClinicRepository) : ViewModel() {
    var items by mutableStateOf<List<AppointmentListItem>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var loaded by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun refresh() {
        loading = true
        viewModelScope.launch {
            when (val result = clinic.appointments()) {
                is ApiResult.Success -> {
                    items = result.data
                    error = null
                }
                is ApiResult.Failure -> error = result.message
            }
            loading = false
            loaded = true
        }
    }
}

private val TABS = listOf("Sắp tới", "Đã khám", "Đã huỷ")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(nav: NavHostController) {
    val vm = appViewModel { AppointmentsViewModel(it.clinicRepository) }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    // Reload whenever the tab is shown again, e.g. after booking or cancelling.
    LaunchedEffect(Unit) { vm.refresh() }

    val filtered = when (tab) {
        0 -> vm.items.filter { it.status in ACTIVE_STATUSES }.sortedWith(compareBy({ it.appointmentDate }, { it.appointmentTime }))
        1 -> vm.items.filter { it.status == "completed" || it.status == "no_show" }.sortedByDescending { it.appointmentDate + it.appointmentTime }
        else -> vm.items.filter { it.status == "cancelled" }.sortedByDescending { it.appointmentDate + it.appointmentTime }
    }

    Scaffold(
        topBar = {
            AppTopBar("Lịch hẹn của tôi", actions = {
                IconButton(onClick = { nav.navigate(Routes.checkIn()) }) {
                    Icon(Icons.Filled.HowToReg, contentDescription = "Check-in")
                }
            })
        },
        containerColor = Brand.Surface,
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            PrimaryTabRow(selectedTabIndex = tab, containerColor = Color.White, contentColor = Brand.Primary) {
                TABS.forEachIndexed { index, title ->
                    Tab(selected = tab == index, onClick = { tab = index }, text = { Text(title, fontWeight = FontWeight.SemiBold) })
                }
            }
            PullToRefreshBox(isRefreshing = vm.loading && vm.loaded, onRefresh = vm::refresh, modifier = Modifier.fillMaxSize()) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    vm.error?.let { item { ErrorBanner(it) } }
                    if (vm.loaded && filtered.isEmpty()) {
                        item {
                            EmptyState(
                                Icons.Filled.CalendarMonth,
                                if (tab == 0) "Chưa có lịch hẹn sắp tới" else "Chưa có lịch hẹn",
                                if (tab == 0) "Đặt lịch để được bác sĩ da liễu thăm khám." else "Danh sách này đang trống.",
                                actionText = if (tab == 0) "Đặt lịch khám" else null,
                                onAction = { nav.navigate(Routes.booking()) },
                            )
                        }
                    }
                    items(filtered, key = { it.appointmentId }) { item ->
                        AppointmentCard(item) { nav.navigate(Routes.appointment(item.appointmentId)) }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(item: AppointmentListItem, onClick: () -> Unit) {
    AppCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brand.PrimaryLight)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(item.appointmentDate.substring(8, 10), style = MaterialTheme.typography.titleLarge, color = Brand.Primary)
                Text("Th${item.appointmentDate.substring(5, 7).toInt()}", style = MaterialTheme.typography.labelSmall, color = Brand.PrimaryDark)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("BS. ${item.doctorFullName ?: ""}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "${formatTime(item.appointmentTime)} · ${formatDayOfWeek(item.appointmentDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Brand.TextMuted,
                )
                Text(
                    "${item.patientFullName ?: ""} · ${consultationModeLabel(item.consultationMode)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Brand.TextMuted,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusChip(item.status)
            Spacer(Modifier.weight(1f))
            item.queueNumber?.let {
                Text("STT: $it", color = Brand.Teal, fontWeight = FontWeight.Bold)
            }
        }
    }
}
