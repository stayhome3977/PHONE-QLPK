package com.example.quanlyphongkham.data.remote

import com.example.quanlyphongkham.data.remote.dto.Appointment
import com.example.quanlyphongkham.data.remote.dto.AppointmentListItem
import com.example.quanlyphongkham.data.remote.dto.AuthTokenResponse
import com.example.quanlyphongkham.data.remote.dto.AuthenticatedAccount
import com.example.quanlyphongkham.data.remote.dto.CancelAppointmentRequest
import com.example.quanlyphongkham.data.remote.dto.CaptchaVerifyRequest
import com.example.quanlyphongkham.data.remote.dto.CaptchaVerifyResponse
import com.example.quanlyphongkham.data.remote.dto.ChangePasswordRequest
import com.example.quanlyphongkham.data.remote.dto.CheckInRequest
import com.example.quanlyphongkham.data.remote.dto.CheckInResponse
import com.example.quanlyphongkham.data.remote.dto.ClinicService
import com.example.quanlyphongkham.data.remote.dto.ConfirmPaymentRequest
import com.example.quanlyphongkham.data.remote.dto.ConfirmPaymentResponse
import com.example.quanlyphongkham.data.remote.dto.CreateAppointmentRequest
import com.example.quanlyphongkham.data.remote.dto.DestinationRequest
import com.example.quanlyphongkham.data.remote.dto.DoctorAvailability
import com.example.quanlyphongkham.data.remote.dto.DoctorListItem
import com.example.quanlyphongkham.data.remote.dto.LoginRequest
import com.example.quanlyphongkham.data.remote.dto.LogoutRequest
import com.example.quanlyphongkham.data.remote.dto.OtpChallengeResponse
import com.example.quanlyphongkham.data.remote.dto.PagedResponse
import com.example.quanlyphongkham.data.remote.dto.PatientProfile
import com.example.quanlyphongkham.data.remote.dto.RefreshTokenRequest
import com.example.quanlyphongkham.data.remote.dto.RegisterRequest
import com.example.quanlyphongkham.data.remote.dto.ResetPasswordRequest
import com.example.quanlyphongkham.data.remote.dto.UpdatePatientProfileRequest
import com.example.quanlyphongkham.data.remote.dto.VerifyRegistrationRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

const val CAPTCHA_HEADER = "X-Captcha-Token"

/**
 * QLPK patient-facing endpoints. Paths are relative to BuildConfig.API_ROOT (".../api/").
 * Query parameters bind by the C# property name, so they are camelCase unlike the JSON bodies.
 */
interface ApiService {
    // --- Auth ---
    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest,
        @Header(CAPTCHA_HEADER) captchaToken: String? = null,
    ): Response<AuthTokenResponse>

    @GET("auth/me")
    suspend fun me(): Response<AuthenticatedAccount>

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequest): Response<Unit>

    @POST("auth/change-password")
    suspend fun changePassword(@Body body: ChangePasswordRequest): Response<Unit>

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequest,
        @Header(CAPTCHA_HEADER) captchaToken: String,
    ): Response<OtpChallengeResponse>

    @POST("auth/verify-registration")
    suspend fun verifyRegistration(@Body body: VerifyRegistrationRequest): Response<AuthTokenResponse>

    @POST("auth/resend-registration-otp")
    suspend fun resendRegistrationOtp(
        @Body body: DestinationRequest,
        @Header(CAPTCHA_HEADER) captchaToken: String,
    ): Response<OtpChallengeResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body body: DestinationRequest,
        @Header(CAPTCHA_HEADER) captchaToken: String,
    ): Response<OtpChallengeResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): Response<Unit>

    @POST("captcha/verify")
    suspend fun verifyCaptcha(@Body body: CaptchaVerifyRequest): Response<CaptchaVerifyResponse>

    // --- Directory ---
    @GET("doctors")
    suspend fun doctors(
        @Query("specialtyId") specialtyId: Int? = null,
        @Query("search") search: String? = null,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 50,
    ): Response<PagedResponse<DoctorListItem>>

    @GET("doctors/{doctorId}/available-slots")
    suspend fun availableSlots(
        @Path("doctorId") doctorId: Int,
        @Query("date") date: String,
    ): Response<DoctorAvailability>

    @GET("services")
    suspend fun services(
        @Query("search") search: String? = null,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 100,
    ): Response<PagedResponse<ClinicService>>

    // --- Patient ---
    @GET("patient/profile")
    suspend fun profiles(): Response<List<PatientProfile>>

    @PUT("patient/profile/{patientId}")
    suspend fun updateProfile(
        @Path("patientId") patientId: Int,
        @Body body: UpdatePatientProfileRequest,
    ): Response<PatientProfile>

    @GET("patient/appointments")
    suspend fun appointments(
        @Query("fromDate") fromDate: String? = null,
        @Query("toDate") toDate: String? = null,
        @Query("status") status: String? = null,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 100,
    ): Response<PagedResponse<AppointmentListItem>>

    @GET("patient/appointments/{appointmentId}")
    suspend fun appointment(@Path("appointmentId") appointmentId: Int): Response<Appointment>

    @POST("patient/appointments")
    suspend fun bookAppointment(
        @Body body: CreateAppointmentRequest,
        @Header(CAPTCHA_HEADER) captchaToken: String,
    ): Response<Appointment>

    @POST("patient/appointments/check-in")
    suspend fun checkIn(@Body body: CheckInRequest): Response<CheckInResponse>

    @POST("patient/appointments/{appointmentId}/confirm-payment")
    suspend fun confirmPayment(
        @Path("appointmentId") appointmentId: Int,
        @Body body: ConfirmPaymentRequest,
    ): Response<ConfirmPaymentResponse>

    @POST("patient/appointments/{appointmentId}/cancel")
    suspend fun cancelAppointment(
        @Path("appointmentId") appointmentId: Int,
        @Body body: CancelAppointmentRequest,
    ): Response<Appointment>
}

/** Used only by the token authenticator, on a client without auth handling, so it can run synchronously. */
interface RefreshApi {
    @POST("auth/refresh")
    fun refresh(@Body body: RefreshTokenRequest): Call<AuthTokenResponse>
}
