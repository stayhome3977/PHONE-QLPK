package com.example.quanlyphongkham.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentListItem(
    val appointmentId: Int,
    val patientId: Int,
    val patientFullName: String? = null,
    val doctorId: Int,
    val doctorFullName: String? = null,
    val appointmentDate: String,
    val appointmentTime: String,
    val durationMinutes: Int = 0,
    val status: String,
    val consultationMode: String? = null,
    val queueNumber: Int? = null,
    val discountPercent: Double = 0.0,
    val createdAt: String? = null,
)

@Serializable
data class AppointmentServiceLine(
    val serviceId: Int,
    val serviceName: String,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val lineAmount: Double = 0.0,
)

@Serializable
data class Appointment(
    val appointmentId: Int,
    val patientId: Int,
    val patientFullName: String? = null,
    val doctorId: Int,
    val doctorFullName: String? = null,
    val appointmentDate: String,
    val appointmentTime: String,
    val durationMinutes: Int = 0,
    val status: String,
    val bookingSource: String? = null,
    val consultationMode: String? = null,
    val checkInCode: String? = null,
    val queueNumber: Int? = null,
    val checkedInAt: String? = null,
    val cancellationReason: String? = null,
    val discountPercent: Double = 0.0,
    val discountApprovalRequired: Boolean = false,
    val subtotalAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val services: List<AppointmentServiceLine> = emptyList(),
    val createdAt: String? = null,
)

@Serializable
data class ServiceQuantity(val serviceId: Int, val quantity: Int = 1)

@Serializable
data class CreateAppointmentRequest(
    val patientId: Int,
    val doctorId: Int,
    val appointmentDate: String,
    val appointmentTime: String,
    val consultationMode: String? = null,
    val visitType: String? = null,
    val reasonForVisit: String? = null,
    val primaryServiceId: Int? = null,
    val services: List<ServiceQuantity>,
    val promotionCode: String? = null,
)

@Serializable
data class CheckInRequest(val checkInCode: String)

@Serializable
data class CheckInResponse(
    val appointmentId: Int,
    val queueNumber: Int,
    val doctorFullName: String? = null,
    val specialtyName: String? = null,
    val appointmentTime: String? = null,
    val checkedInAt: String? = null,
    val selfCheckIn: Boolean = true,
)

@Serializable
data class CancelAppointmentRequest(val reason: String)

@Serializable
data class ConfirmPaymentRequest(val paymentMethod: String, val externalReference: String? = null)

@Serializable
data class ConfirmPaymentResponse(
    val appointmentId: Int,
    val invoiceId: Int? = null,
    val invoiceNumber: String? = null,
    val appointmentStatus: String,
    val paymentStatus: String? = null,
    val totalAmount: Double = 0.0,
    val amountCollected: Double = 0.0,
    val paidAt: String? = null,
)
