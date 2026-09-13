package com.example.quanlyphongkham.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.repository.AuthRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.captcha.CaptchaState
import com.example.quanlyphongkham.ui.captcha.NOT_SOLVED_MESSAGE
import com.example.quanlyphongkham.ui.captcha.TurnstileCaptcha
import com.example.quanlyphongkham.ui.components.AppTextField
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.InfoBanner
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val auth: AuthRepository, private val notice: MutableStateFlow<String?>) : ViewModel() {
    var login by mutableStateOf("")
    var password by mutableStateOf("")
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    /** Shown only after the server asks for it (several failed attempts). */
    var captcha by mutableStateOf<CaptchaState?>(null)
        private set

    /** Message handed over by the flow that led here (password reset, forced sign-out). */
    val info = notice.asStateFlow()

    fun submit() {
        notice.value = null
        if (login.isBlank() || password.isBlank()) {
            error = "Vui lòng nhập số điện thoại/email và mật khẩu."
            return
        }
        val captcha = captcha
        if (captcha != null && !captcha.solved) {
            error = NOT_SOLVED_MESSAGE
            return
        }
        loading = true
        error = null
        viewModelScope.launch {
            val result = auth.login(login, password, captcha?.consume())
            loading = false
            if (result is ApiResult.Failure) {
                error = result.message
                if (result.errorCode == "captcha_required" && this@LoginViewModel.captcha == null) {
                    this@LoginViewModel.captcha = CaptchaState()
                }
            }
            // On success the session flow switches the app to the patient home.
        }
    }
}

@Composable
fun LoginScreen(onBack: () -> Unit, onRegister: () -> Unit, onForgotPassword: () -> Unit) {
    val vm = appViewModel { LoginViewModel(it.authRepository, it.authNotice) }
    AuthScaffold(title = "Đăng nhập", subtitle = "Dành cho bệnh nhân của phòng khám", onBack = onBack) {
        val info by vm.info.collectAsStateWithLifecycle()
        info?.let {
            InfoBanner(it)
            Spacer(Modifier.height(12.dp))
        }
        AppTextField(
            value = vm.login,
            onValueChange = { vm.login = it },
            label = "Số điện thoại hoặc email",
            leadingIcon = Icons.Filled.Person,
        )
        Spacer(Modifier.height(12.dp))
        AppTextField(
            value = vm.password,
            onValueChange = { vm.password = it },
            label = "Mật khẩu",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onForgotPassword) { Text("Quên mật khẩu?") }
        }
        vm.captcha?.let { TurnstileCaptcha(it) }
        ErrorBanner(vm.error)
        Spacer(Modifier.height(12.dp))
        PrimaryButton("Đăng nhập", vm::submit, loading = vm.loading)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("Chưa có tài khoản?", color = Brand.TextMuted, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onRegister) { Text("Đăng ký ngay", fontWeight = FontWeight.Bold) }
        }
    }
}
