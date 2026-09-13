package com.example.quanlyphongkham.data.remote.dto

import kotlinx.serialization.Serializable

// Field names are camelCase here and mapped to the API's snake_case by AppJson's naming strategy.

@Serializable
data class LoginRequest(
    val login: String,
    val password: String,
    val deviceInfo: String? = null,
)

@Serializable
data class AuthTokenResponse(
    val accessToken: String,
    val expiresInSeconds: Int = 0,
    val refreshToken: String,
    val refreshTokenExpiresAt: String? = null,
    val account: AuthenticatedAccount,
)

@Serializable
data class AuthenticatedAccount(
    val accountId: Long,
    val fullName: String,
    val phoneNumber: String? = null,
    val roleCode: String,
    val roleName: String? = null,
    val permissions: List<String> = emptyList(),
    val mustChangePassword: Boolean = false,
)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class LogoutRequest(val refreshToken: String, val allDevices: Boolean = false)

@Serializable
data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)

@Serializable
data class RegisterRequest(
    val phoneNumber: String,
    val email: String,
    val password: String,
    val fullName: String,
    val dateOfBirth: String? = null,
    val gender: String? = null,
)

@Serializable
data class OtpChallengeResponse(
    val maskedDestination: String? = null,
    val expiresAt: String? = null,
    val maxAttempts: Int = 0,
    val resendCooldownSeconds: Int = 60,
)

@Serializable
data class VerifyRegistrationRequest(
    val destination: String,
    val code: String,
    val deviceInfo: String? = null,
)

@Serializable
data class DestinationRequest(val destination: String)

@Serializable
data class ResetPasswordRequest(
    val destination: String,
    val code: String,
    val newPassword: String,
)

@Serializable
data class CaptchaVerifyRequest(val providerToken: String, val purpose: String)

@Serializable
data class CaptchaVerifyResponse(val verificationToken: String, val expiresAt: String? = null)
