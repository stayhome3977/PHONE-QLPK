package com.example.quanlyphongkham.data.repository

import android.os.Build
import com.example.quanlyphongkham.data.local.SessionStore
import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.ApiService
import com.example.quanlyphongkham.data.remote.ErrorMessages
import com.example.quanlyphongkham.data.remote.apiCall
import com.example.quanlyphongkham.data.remote.dto.AuthTokenResponse
import com.example.quanlyphongkham.data.remote.dto.AuthenticatedAccount
import com.example.quanlyphongkham.data.remote.dto.CaptchaVerifyRequest
import com.example.quanlyphongkham.data.remote.dto.ChangePasswordRequest
import com.example.quanlyphongkham.data.remote.dto.DestinationRequest
import com.example.quanlyphongkham.data.remote.dto.LoginRequest
import com.example.quanlyphongkham.data.remote.dto.LogoutRequest
import com.example.quanlyphongkham.data.remote.dto.OtpChallengeResponse
import com.example.quanlyphongkham.data.remote.dto.RegisterRequest
import com.example.quanlyphongkham.data.remote.dto.ResetPasswordRequest
import com.example.quanlyphongkham.data.remote.dto.VerifyRegistrationRequest

/** Captcha purposes accepted by POST /captcha/verify. */
object CaptchaPurpose {
    const val APPOINTMENT_BOOKING = "appointment_booking"
    const val PATIENT_REGISTRATION = "patient_registration"
    const val OTP_RESEND = "otp_resend"
    const val LOGIN_AFTER_FAILURES = "login_after_failures"
    const val PASSWORD_RESET = "password_reset"
}

class AuthRepository(
    private val api: ApiService,
    private val sessionStore: SessionStore,
) {
    private val deviceInfo = "QLPK Da Lieu Android ${Build.VERSION.RELEASE} (${Build.MANUFACTURER} ${Build.MODEL})".take(255)

    /**
     * Trades the Turnstile widget's one-time provider token for the backend's verification token,
     * which is what the protected request carries in X-Captcha-Token.
     */
    suspend fun verifyCaptcha(providerToken: String, purpose: String): ApiResult<String> =
        when (val result = apiCall { api.verifyCaptcha(CaptchaVerifyRequest(providerToken, purpose)) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.verificationToken, result.status)
            is ApiResult.Failure -> result
        }

    /** [providerToken] is only needed once the server has answered `captcha_required`. */
    suspend fun login(login: String, password: String, providerToken: String? = null): ApiResult<AuthenticatedAccount> {
        val captcha = providerToken?.let {
            when (val verified = verifyCaptcha(it, CaptchaPurpose.LOGIN_AFTER_FAILURES)) {
                is ApiResult.Failure -> return verified
                is ApiResult.Success -> verified.data
            }
        }
        return adopt(apiCall { api.login(LoginRequest(login.trim(), password, deviceInfo), captcha) })
    }

    suspend fun register(request: RegisterRequest, providerToken: String): ApiResult<OtpChallengeResponse> =
        when (val captcha = verifyCaptcha(providerToken, CaptchaPurpose.PATIENT_REGISTRATION)) {
            is ApiResult.Failure -> captcha
            is ApiResult.Success -> apiCall { api.register(request, captcha.data) }
        }

    suspend fun resendRegistrationOtp(destination: String, providerToken: String): ApiResult<OtpChallengeResponse> =
        when (val captcha = verifyCaptcha(providerToken, CaptchaPurpose.OTP_RESEND)) {
            is ApiResult.Failure -> captcha
            is ApiResult.Success -> apiCall { api.resendRegistrationOtp(DestinationRequest(destination), captcha.data) }
        }

    suspend fun verifyRegistration(destination: String, code: String): ApiResult<AuthenticatedAccount> =
        adopt(apiCall { api.verifyRegistration(VerifyRegistrationRequest(destination, code.trim(), deviceInfo)) })

    suspend fun forgotPassword(destination: String, providerToken: String): ApiResult<OtpChallengeResponse> =
        when (val captcha = verifyCaptcha(providerToken, CaptchaPurpose.PASSWORD_RESET)) {
            is ApiResult.Failure -> captcha
            is ApiResult.Success -> apiCall { api.forgotPassword(DestinationRequest(destination), captcha.data) }
        }

    suspend fun resetPassword(destination: String, code: String, newPassword: String): ApiResult<Unit> =
        apiCall { api.resetPassword(ResetPasswordRequest(destination, code.trim(), newPassword)) }

    /** Re-reads the account (role, must_change_password) for a stored session. */
    suspend fun refreshAccount(): ApiResult<AuthenticatedAccount> {
        val result = apiCall { api.me() }
        if (result is ApiResult.Success) {
            if (result.data.roleCode != PATIENT_ROLE) {
                logout()
                return ApiResult.Failure(ErrorMessages.PATIENT_ONLY)
            }
            sessionStore.updateAccount(result.data)
        }
        return result
    }

    /** Password changes revoke every session server-side, so the user signs in again afterwards. */
    suspend fun changePassword(current: String, new: String): ApiResult<Unit> {
        val result = apiCall { api.changePassword(ChangePasswordRequest(current, new)) }
        if (result is ApiResult.Success) sessionStore.clear()
        return result
    }

    suspend fun logout() {
        sessionStore.current?.let { session ->
            apiCall { api.logout(LogoutRequest(session.refreshToken)) }
        }
        sessionStore.clear()
    }

    private suspend fun adopt(result: ApiResult<AuthTokenResponse>): ApiResult<AuthenticatedAccount> = when (result) {
        is ApiResult.Failure -> result
        is ApiResult.Success -> {
            val tokens = result.data
            if (tokens.account.roleCode != PATIENT_ROLE) {
                // Staff accounts belong on the web console; revoke the session we were just issued.
                apiCall { api.logout(LogoutRequest(tokens.refreshToken)) }
                ApiResult.Failure(ErrorMessages.PATIENT_ONLY)
            } else {
                sessionStore.save(tokens)
                ApiResult.Success(tokens.account, result.status)
            }
        }
    }

    private companion object {
        const val PATIENT_ROLE = "patient"
    }
}
