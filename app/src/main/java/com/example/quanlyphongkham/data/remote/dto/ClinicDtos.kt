package com.example.quanlyphongkham.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    val items: List<T> = emptyList(),
    val pageNumber: Int = 1,
    val pageSize: Int = 20,
    val totalItems: Int = 0,
    val totalPages: Int = 0,
    val hasPreviousPage: Boolean = false,
    val hasNextPage: Boolean = false,
)

@Serializable
data class Specialty(
    val specialtyId: Int,
    val specialtyCode: String? = null,
    val specialtyName: String? = null,
)

@Serializable
data class DoctorListItem(
    val doctorId: Int,
    val fullName: String,
    val avatarUrl: String? = null,
    val specialtyId: Int? = null,
    val specialtyName: String? = null,
    val specialties: List<Specialty> = emptyList(),
    val degree: String? = null,
    val yearsOfExperience: Int? = null,
    val consultationFee: Double? = null,
    val averageRating: Double? = null,
    val reviewCount: Int = 0,
    val isAcceptingAppointments: Boolean = true,
)

@Serializable
data class AvailableSlot(
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int = 0,
    val consultationMode: String? = null,
)

@Serializable
data class DoctorAvailability(
    val doctorId: Int,
    val doctorFullName: String? = null,
    val specialtyName: String? = null,
    val date: String,
    val isClinicHoliday: Boolean = false,
    val slots: List<AvailableSlot> = emptyList(),
)

@Serializable
data class ClinicService(
    val serviceId: Int,
    val serviceName: String,
    val serviceGroup: String? = null,
    val description: String? = null,
    val price: Double = 0.0,
    val durationMinutes: Int? = null,
    val imageUrl: String? = null,
)

@Serializable
data class PatientProfile(
    val patientId: Int,
    val patientCode: String,
    val relationshipToAccount: String = "self",
    val fullName: String,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val address: String? = null,
    val occupation: String? = null,
    val bloodType: String? = null,
    val healthInsuranceNumber: String? = null,
    val healthInsuranceExpiry: String? = null,
    val emergencyContactName: String? = null,
    val emergencyContactPhone: String? = null,
    val allergyNotes: String? = null,
    val accountPhoneNumber: String? = null,
    val accountEmail: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class UpdatePatientProfileRequest(
    val fullName: String,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val address: String? = null,
    val occupation: String? = null,
    val bloodType: String? = null,
    val healthInsuranceNumber: String? = null,
    val healthInsuranceExpiry: String? = null,
    val emergencyContactName: String? = null,
    val emergencyContactPhone: String? = null,
    val allergyNotes: String? = null,
)
