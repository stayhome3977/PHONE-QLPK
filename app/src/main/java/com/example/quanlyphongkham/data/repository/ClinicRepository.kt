package com.example.quanlyphongkham.data.repository

import com.example.quanlyphongkham.data.remote.ApiResult
import com.example.quanlyphongkham.data.remote.ApiService
import com.example.quanlyphongkham.data.remote.apiCall
import com.example.quanlyphongkham.data.remote.dto.Appointment
import com.example.quanlyphongkham.data.remote.dto.AppointmentListItem
import com.example.quanlyphongkham.data.remote.dto.CancelAppointmentRequest
import com.example.quanlyphongkham.data.remote.dto.CheckInRequest
import com.example.quanlyphongkham.data.remote.dto.CheckInResponse
import com.example.quanlyphongkham.data.remote.dto.ClinicService
import com.example.quanlyphongkham.data.remote.dto.ConfirmPaymentRequest
import com.example.quanlyphongkham.data.remote.dto.ConfirmPaymentResponse
import com.example.quanlyphongkham.data.remote.dto.CreateAppointmentRequest
import com.example.quanlyphongkham.data.remote.dto.DoctorAvailability
import com.example.quanlyphongkham.data.remote.dto.DoctorListItem
import com.example.quanlyphongkham.data.remote.dto.PatientProfile
import com.example.quanlyphongkham.data.remote.dto.UpdatePatientProfileRequest
import com.example.quanlyphongkham.data.remote.map

/** Doctors, services, patient profiles and appointments for the signed-in patient. */
class ClinicRepository(
    private val api: ApiService,
    private val authRepository: AuthRepository,
) {
    suspend fun doctors(search: String? = null, specialtyId: Int? = null): ApiResult<List<DoctorListItem>> =
        apiCall { api.doctors(specialtyId = specialtyId, search = search?.takeIf { it.isNotBlank() }) }.map { it.items }

    suspend fun availableSlots(doctorId: Int, date: String): ApiResult<DoctorAvailability> =
        apiCall { api.availableSlots(doctorId, date) }

    suspend fun services(search: String? = null): ApiResult<List<ClinicService>> =
        apiCall { api.services(search = search?.takeIf { it.isNotBlank() }) }.map { it.items }

    suspend fun profiles(): ApiResult<List<PatientProfile>> = apiCall { api.profiles() }

    suspend fun updateProfile(patientId: Int, request: UpdatePatientProfileRequest): ApiResult<PatientProfile> =
        apiCall { api.updateProfile(patientId, request) }

    suspend fun appointments(fromDate: String? = null): ApiResult<List<AppointmentListItem>> =
        apiCall { api.appointments(fromDate = fromDate) }.map { it.items }

    suspend fun appointment(id: Int): ApiResult<Appointment> = apiCall { api.appointment(id) }

    suspend fun book(request: CreateAppointmentRequest, providerToken: String): ApiResult<Appointment> =
        when (val captcha = authRepository.verifyCaptcha(providerToken, CaptchaPurpose.APPOINTMENT_BOOKING)) {
            is ApiResult.Failure -> captcha
            is ApiResult.Success -> apiCall { api.bookAppointment(request, captcha.data) }
        }

    suspend fun checkIn(code: String): ApiResult<CheckInResponse> =
        apiCall { api.checkIn(CheckInRequest(code.trim())) }

    suspend fun confirmPayment(appointmentId: Int, method: String, reference: String?): ApiResult<ConfirmPaymentResponse> =
        apiCall { api.confirmPayment(appointmentId, ConfirmPaymentRequest(method, reference?.trim()?.ifBlank { null })) }

    suspend fun cancel(appointmentId: Int, reason: String): ApiResult<Appointment> =
        apiCall { api.cancelAppointment(appointmentId, CancelAppointmentRequest(reason.trim())) }
}
