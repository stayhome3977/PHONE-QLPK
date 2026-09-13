package com.example.quanlyphongkham.ui.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.repository.AuthRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.auth.passwordPolicyError
import com.example.quanlyphongkham.ui.components.AppTextField
import com.example.quanlyphongkham.ui.components.AppTopBar
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.IconBadge
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val auth: AuthRepository,
    private val notice: MutableStateFlow<String?>,
) : ViewModel() {
    var current by mutableStateOf("")
    var new by mutableStateOf("")
    var confirm by mutableStateOf("")
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun submit() {
        error = when {
            current.isBlank() -> "Vui lòng nhập mật khẩu hiện tại."
            passwordPolicyError(new) != null -> passwordPolicyError(new)
            new != confirm -> "Mật khẩu nhập lại không khớp."
            new == current -> "Mật khẩu mới phải khác mật khẩu hiện tại."
            else -> null
        }
        if (error != null) return
        loading = true
        viewModelScope.launch {
            // Set before the call: success clears the session, which swaps to the login screen straight away.
            notice.value = "Đổi mật khẩu thành công. Vui lòng đăng nhập lại bằng mật khẩu mới."
            val result = auth.changePassword(current, new)
            if (result is ApiResult.Failure) {
                notice.value = null
                error = result.message
            }
            loading = false
        }
    }
}

@Composable
fun ChangePasswordScreen(forced: Boolean, onBack: () -> Unit) {
    val vm = appViewModel { ChangePasswordViewModel(it.authRepository, it.authNotice) }
    Scaffold(
        topBar = { AppTopBar("Đổi mật khẩu", onBack = onBack) },
        containerColor = Brand.Surface,
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            IconBadge(Icons.Filled.LockReset, Brand.Primary, Brand.PrimaryLight, size = 80.dp)
            Spacer(Modifier.height(12.dp))
            Text(
                if (forced) "Tài khoản của bạn cần đổi mật khẩu trước khi tiếp tục sử dụng."
                else "Sau khi đổi mật khẩu, bạn sẽ được đăng xuất khỏi mọi thiết bị.",
                style = MaterialTheme.typography.bodyMedium,
                color = Brand.TextMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            AppTextField(vm.current, { vm.current = it }, "Mật khẩu hiện tại", leadingIcon = Icons.Filled.Lock, isPassword = true)
            Spacer(Modifier.height(12.dp))
            AppTextField(
                vm.new, { vm.new = it }, "Mật khẩu mới", leadingIcon = Icons.Filled.Lock, isPassword = true,
                supportingText = "Ít nhất 8 ký tự, có chữ hoa, chữ thường và chữ số",
            )
            Spacer(Modifier.height(8.dp))
            AppTextField(vm.confirm, { vm.confirm = it }, "Nhập lại mật khẩu mới", leadingIcon = Icons.Filled.Lock, isPassword = true)
            Spacer(Modifier.height(16.dp))
            ErrorBanner(vm.error)
            Spacer(Modifier.height(12.dp))
            PrimaryButton("Đổi mật khẩu", vm::submit, loading = vm.loading)
        }
    }
}
