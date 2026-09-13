package com.example.quanlyphongkham.ui.services

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.dto.ClinicService
import com.example.quanlyphongkham.data.repository.ClinicRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.components.AppCard
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.EmptyState
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.IconBadge
import com.example.quanlyphongkham.ui.components.LoadingBox
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.components.formatVnd
import com.example.quanlyphongkham.ui.navigation.Routes
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.launch

class ServicesViewModel(private val clinic: ClinicRepository) : ViewModel() {
    var services by mutableStateOf<List<ClinicService>>(emptyList())
        private set
    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            when (val result = clinic.services()) {
                is ApiResult.Success -> services = result.data
                is ApiResult.Failure -> error = result.message
            }
            loading = false
        }
    }
}

@Composable
fun ServicesScreen(nav: NavHostController) {
    val vm = appViewModel { ServicesViewModel(it.clinicRepository) }
    Scaffold(topBar = { AppTopBar("Dịch vụ & bảng giá", onBack = { nav.popBackStack() }) }, containerColor = Brand.Surface) { padding ->
        when {
            vm.loading -> LoadingBox(Modifier.padding(padding))
            vm.error != null -> ErrorBanner(vm.error, Modifier.padding(padding).padding(16.dp))
            vm.services.isEmpty() -> EmptyState(
                Icons.Filled.Spa, "Chưa có dịch vụ", "Phòng khám chưa công bố dịch vụ nào.",
                modifier = Modifier.padding(padding),
            )
            else -> {
                val groups = vm.services.groupBy { it.serviceGroup?.takeIf(String::isNotBlank) ?: "Dịch vụ khác" }
                LazyColumn(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    groups.forEach { (group, items) ->
                        item(key = "g-$group") {
                            Text(group, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        }
                        items(items, key = { it.serviceId }) { ServiceCard(it) }
                    }
                    item {
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton("Đặt lịch khám", { nav.navigate(Routes.booking()) })
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: ClinicService, trailing: @Composable () -> Unit = {}) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(Icons.Filled.Spa, Brand.Teal, Brand.TealLight, size = 44.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(service.serviceName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                service.description?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted, maxLines = 2)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(formatVnd(service.price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Brand.PrimaryDark)
                    service.durationMinutes?.let {
                        Spacer(Modifier.width(10.dp))
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = Brand.TextLight, modifier = Modifier.height(14.dp))
                        Text(" $it phút", style = MaterialTheme.typography.bodySmall, color = Brand.TextMuted)
                    }
                }
            }
            trailing()
        }
    }
}
