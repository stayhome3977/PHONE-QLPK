package com.example.quanlyphongkham.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.dto.OtpChallengeResponse
import com.example.quanlyphongkham.data.remote.dto.RegisterRequest
import com.example.quanlyphongkham.data.repository.AuthRepository
import com.example.quanlyphongkham.ui.appViewModel
import com.example.quanlyphongkham.ui.captcha.CaptchaState
import com.example.quanlyphongkham.ui.captcha.NOT_SOLVED_MESSAGE
import com.example.quanlyphongkham.ui.captcha.TurnstileCaptcha
import com.example.quanlyphongkham.ui.components.AppTextField
import com.example.quanlyphongkham.ui.components.DateField
import com.example.quanlyphongkham.ui.components.ErrorBanner
import com.example.quanlyphongkham.ui.components.InfoBanner
import com.example.quanlyphongkham.ui.components.PrimaryButton
import com.example.quanlyphongkham.ui.theme.Brand
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
private val PHONE = Regex("^\\+?[0-9]{8,15}$")

fun passwordPolicyError(password: String): String? =
    if (password.length < 8 || password.none { it.isUpperCase() } || password.none { it.isLowerCase() } || password.none { it.isDigit() }) {
        "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường và chữ số."
    } else null

/** Keeps the OTP resend button disabled for the cooldown the backend asks for. */
abstract class OtpViewModel : ViewModel() {
    var cooldown by mutableIntStateOf(0)
        private set
    private var timer: Job? = null

    /** Resending a code is captcha-protected too. */
    val resendCaptcha = CaptchaState()

    protected fun startCooldown(challenge: OtpChallengeResponse) {
        timer?.cancel()
        cooldown = challenge.resendCooldownSeconds
        timer = viewModelScope.launch {
            while (cooldown > 0) {
                delay(1000)
                cooldown--
            }
        }
    }
}

class RegisterViewModel(private val auth: AuthRepository) : OtpViewModel() {
    var fullName by mutableStateOf("")
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirm by mutableStateOf("")
    var dateOfBirth by mutableStateOf<String?>(null)
    var gender by mutableStateOf<String?>(null)
    var code by mutableStateOf("")
    val captcha = CaptchaState()

    var challenge by mutableStateOf<OtpChallengeResponse?>(null)
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun submit() {
        error = when {
            fullName.trim().length < 2 -> "Vui lòng nhập họ và tên."
            !PHONE.matches(phone.trim()) -> "Số điện thoại chỉ gồm 8–15 chữ số."
            !EMAIL.matches(email.trim()) -> "Email không hợp lệ. Mã xác thực sẽ được gửi tới email này."
            passwordPolicyError(password) != null -> passwordPolicyError(password)
            password != confirm -> "Mật khẩu nhập lại không khớp."
            !captcha.solved -> NOT_SOLVED_MESSAGE
            else -> null
        }
        if (error != null) return
        val token = captcha.consume() ?: return
        loading = true
        viewModelScope.launch {
            val result = auth.register(
                RegisterRequest(phone.trim(), email.trim(), password, fullName.trim(), dateOfBirth, gender),
                token,
            )
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

    fun verify() {
        if (code.isBlank()) {
            error = "Vui lòng nhập mã xác thực."
            return
        }
        loading = true
        error = null
        viewModelScope.launch {
            val result = auth.verifyRegistration(email.trim(), code)
            loading = false
            if (result is ApiResult.Failure) error = result.message
        }
    }

    fun resend() {
        val token = resendCaptcha.consume()
        if (token == null) {
            error = NOT_SOLVED_MESSAGE
            return
        }
        loading = true
        error = null
        viewModelScope.launch {
            val result = auth.resendRegistrationOtp(email.trim(), token)
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

    fun backToForm() {
        challenge = null
        code = ""
        error = null
    }
}

@Composable
fun RegisterScreen(onBack: () -> Unit) {
    val vm = appViewModel { RegisterViewModel(it.authRepository) }
    val challenge = vm.challenge

    if (challenge == null) {
        AuthScaffold(title = "Tạo tài khoản", subtitle = "Đăng ký để đặt lịch khám và theo dõi hồ sơ", onBack = onBack) {
            AppTextField(vm.fullName, { vm.fullName = it }, "Họ và tên", leadingIcon = Icons.Filled.Badge)
            Spacer(Modifier.height(12.dp))
            AppTextField(vm.phone, { vm.phone = it }, "Số điện thoại", leadingIcon = Icons.Filled.Phone, keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(12.dp))
            AppTextField(vm.email, { vm.email = it }, "Email nhận mã xác thực", leadingIcon = Icons.Filled.Email, keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(12.dp))
            DateField(vm.dateOfBirth, { vm.dateOfBirth = it }, "Ngày sinh (không bắt buộc)")
            Spacer(Modifier.height(12.dp))
            Text("Giới tính", style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("male" to "Nam", "female" to "Nữ", "other" to "Khác").forEach { (value, label) ->
                    FilterChip(
                        selected = vm.gender == value,
                        onClick = { vm.gender = if (vm.gender == value) null else value },
                        label = { Text(label) },
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            AppTextField(
                vm.password, { vm.password = it }, "Mật khẩu", leadingIcon = Icons.Filled.Lock, isPassword = true,
                supportingText = "Ít nhất 8 ký tự, có chữ hoa, chữ thường và chữ số",
            )
            Spacer(Modifier.height(8.dp))
            AppTextField(vm.confirm, { vm.confirm = it }, "Nhập lại mật khẩu", leadingIcon = Icons.Filled.Lock, isPassword = true)
            Spacer(Modifier.height(16.dp))
            TurnstileCaptcha(vm.captcha)
            ErrorBanner(vm.error)
            Spacer(Modifier.height(12.dp))
            PrimaryButton("Tiếp tục", vm::submit, loading = vm.loading, enabled = vm.captcha.solved)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Đã có tài khoản?", color = Brand.TextMuted, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onBack) { Text("Đăng nhập") }
            }
        }
    } else {
        AuthScaffold(
            title = "Xác thực email",
            subtitle = "Nhập mã đã gửi tới ${challenge.maskedDestination ?: vm.email}",
            onBack = vm::backToForm,
        ) {
            OtpForm(
                code = vm.code,
                onCodeChange = { vm.code = it },
                cooldown = vm.cooldown,
                resendCaptcha = vm.resendCaptcha,
                loading = vm.loading,
                error = vm.error,
                onSubmit = vm::verify,
                onResend = vm::resend,
                submitText = "Xác thực & đăng nhập",
            )
        }
    }
}

@Composable
fun OtpForm(
    code: String,
    onCodeChange: (String) -> Unit,
    cooldown: Int,
    resendCaptcha: CaptchaState,
    loading: Boolean,
    error: String?,
    onSubmit: () -> Unit,
    onResend: () -> Unit,
    submitText: String,
    extraFields: @Composable () -> Unit = {},
) {
    InfoBanner(
        "Mã xác thực đã được gửi tới email của bạn. Nếu không thấy, hãy kiểm tra cả mục Thư rác/Spam.",
        color = Brand.PrimaryDark,
        background = Brand.PrimaryLight,
    )
    Spacer(Modifier.height(12.dp))
    AppTextField(
        value = code,
        onValueChange = { value -> onCodeChange(value.filter { it.isLetterOrDigit() }.take(10)) },
        label = "Mã xác thực",
        leadingIcon = Icons.Filled.Pin,
        keyboardType = KeyboardType.Number,
    )
    extraFields()
    Spacer(Modifier.height(12.dp))
    ErrorBanner(error)
    Spacer(Modifier.height(12.dp))
    PrimaryButton(submitText, onSubmit, loading = loading)
    Spacer(Modifier.height(8.dp))
    if (cooldown > 0) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Gửi lại mã sau ${cooldown}s", color = Brand.TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        Text("Chưa nhận được mã?", color = Brand.TextMuted, style = MaterialTheme.typography.bodyMedium)
        TurnstileCaptcha(resendCaptcha)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = onResend, enabled = resendCaptcha.solved && !loading) { Text("Gửi lại mã") }
        }
    }
}
