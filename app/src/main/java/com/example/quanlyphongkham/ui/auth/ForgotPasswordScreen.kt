package com.example.quanlyphongkham.ui.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.dto.OtpChallengeResponse
import com.example.quanlyphongkham.data.repository.AuthRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.captcha.CaptchaState
import com.example.quanlyphongkham.ui.captcha.NOT_SOLVED_MESSAGE
import com.example.quanlyphongkham.ui.captcha.TurnstileCaptcha
import com.example.quanlyphongkham.ui.components.AppTextField
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.PrimaryButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val auth: AuthRepository,
    private val notice: MutableStateFlow<String?>,
) : OtpViewModel() {
    var email by mutableStateOf("")
    var code by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirm by mutableStateOf("")
    val captcha = CaptchaState()
    var challenge by mutableStateOf<OtpChallengeResponse?>(null)
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun requestCode() = send(captcha)

    fun resendCode() = send(resendCaptcha)

    private fun send(from: CaptchaState) {
        if (!email.contains('@')) {
            error = "Vui lòng nhập email đã đăng ký."
            return
        }
        val token = from.consume()
        if (token == null) {
            error = NOT_SOLVED_MESSAGE
            return
        }
        loading = true
        error = null
        viewModelScope.launch {
            val result = auth.forgotPassword(email.trim(), token)
            loading = false
            when (result) {
                is ApiResult.Success -> {
                    challenge = result.data
                    startCooldown(result.data)
                }
                is ApiResult.Failure -> error = result.message
            }
        }
    }

    fun reset(onDone: () -> Unit) {
        error = when {
            code.isBlank() -> "Vui lòng nhập mã xác thực."
            passwordPolicyError(newPassword) != null -> passwordPolicyError(newPassword)
            newPassword != confirm -> "Mật khẩu nhập lại không khớp."
            else -> null
        }
        if (error != null) return
        loading = true
        viewModelScope.launch {
            val result = auth.resetPassword(email.trim(), code, newPassword)
            loading = false
            when (result) {
                is ApiResult.Success -> {
                    notice.value = "Đặt lại mật khẩu thành công. Vui lòng đăng nhập bằng mật khẩu mới."
                    onDone()
                }
                is ApiResult.Failure -> error = result.message
            }
        }
    }

    fun back() {
        challenge = null
        error = null
    }
}

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit, onDone: () -> Unit) {
    val vm = appViewModel { ForgotPasswordViewModel(it.authRepository, it.authNotice) }
    val challenge = vm.challenge
    if (challenge == null) {
        AuthScaffold(title = "Quên mật khẩu", subtitle = "Nhập email đã đăng ký để nhận mã đặt lại", onBack = onBack) {
            AppTextField(vm.email, { vm.email = it }, "Email", leadingIcon = Icons.Filled.Email, keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(12.dp))
            TurnstileCaptcha(vm.captcha)
            ErrorBanner(vm.error)
            Spacer(Modifier.height(12.dp))
            PrimaryButton("Gửi mã", vm::requestCode, loading = vm.loading, enabled = vm.captcha.solved)
        }
    } else {
        AuthScaffold(
            title = "Đặt lại mật khẩu",
            subtitle = "Mã đã được gửi tới ${challenge.maskedDestination ?: vm.email}",
            onBack = vm::back,
        ) {
            OtpForm(
                code = vm.code,
                onCodeChange = { vm.code = it },
                cooldown = vm.cooldown,
                resendCaptcha = vm.resendCaptcha,
                loading = vm.loading,
                error = vm.error,
                onSubmit = { vm.reset(onDone) },
                onResend = vm::resendCode,
                submitText = "Đặt lại mật khẩu",
                extraFields = {
                    Spacer(Modifier.height(12.dp))
                    AppTextField(vm.newPassword, { vm.newPassword = it }, "Mật khẩu mới", leadingIcon = Icons.Filled.Lock, isPassword = true)
                    Spacer(Modifier.height(12.dp))
                    AppTextField(vm.confirm, { vm.confirm = it }, "Nhập lại mật khẩu mới", leadingIcon = Icons.Filled.Lock, isPassword = true)
                },
            )
        }
    }
}
