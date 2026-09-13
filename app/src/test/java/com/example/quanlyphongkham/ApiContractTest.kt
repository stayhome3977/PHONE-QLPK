package com.example.quanlyphongkham

import com.example.quanlyphongkham.data.remote.AppJson
import com.example.quanlyphongkham.data.remote.dto.AppointmentListItem
import com.example.quanlyphongkham.data.remote.dto.AuthTokenResponse
import com.example.quanlyphongkham.data.remote.dto.CreateAppointmentRequest
import com.example.quanlyphongkham.data.remote.dto.PagedResponse
import com.example.quanlyphongkham.data.remote.dto.ServiceQuantity
import com.example.quanlyphongkham.data.remote.parseProblem
import com.example.quanlyphongkham.ui.captcha.CaptchaState
import com.example.quanlyphongkham.ui.captcha.turnstileHtml
import com.example.quanlyphongkham.ui.components.formatDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiContractTest {
    @Test
    fun decodesSnakeCaseLoginResponse() {
        val json = """
            {"access_token":"a.b.c","expires_in_seconds":1800,"refresh_token":"r","refresh_token_expires_at":"2026-09-20T10:00:00+07:00",
             "account":{"account_id":7,"full_name":"Nguyễn Văn A","phone_number":"+84393975116","role_code":"patient",
             "role_name":"Patient","permissions":["appointments.book_own"],"must_change_password":false}}
        """.trimIndent()

        val tokens = AppJson.decodeFromString<AuthTokenResponse>(json)

        assertEquals("a.b.c", tokens.accessToken)
        assertEquals("patient", tokens.account.roleCode)
        assertEquals(listOf("appointments.book_own"), tokens.account.permissions)
        assertFalse(tokens.account.mustChangePassword)
    }

    @Test
    fun decodesPagedAppointmentsWithNullsAndUnknownFields() {
        val json = """
            {"items":[{"appointment_id":1,"patient_id":2,"patient_full_name":"B","doctor_id":3,"doctor_full_name":"C",
              "appointment_date":"2026-09-14","appointment_time":"08:30:00","duration_minutes":30,"status":"confirmed",
              "consultation_mode":"in_clinic","queue_number":null,"discount_percent":0,"created_at":"2026-09-13T09:00:00+07:00","extra":1}],
             "page_number":1,"page_size":100,"total_items":1,"total_pages":1,"has_previous_page":false,"has_next_page":false}
        """.trimIndent()

        val page = AppJson.decodeFromString<PagedResponse<AppointmentListItem>>(json)

        assertEquals(1, page.items.size)
        assertEquals("08:30:00", page.items[0].appointmentTime)
        assertNull(page.items[0].queueNumber)
    }

    @Test
    fun encodesBookingRequestInSnakeCaseWithoutNulls() {
        val body = AppJson.encodeToString(
            CreateAppointmentRequest(
                patientId = 2,
                doctorId = 3,
                appointmentDate = "2026-09-14",
                appointmentTime = "08:30:00",
                primaryServiceId = 5,
                services = listOf(ServiceQuantity(5)),
            ),
        )

        assertTrue(body.contains("\"patient_id\":2"))
        assertTrue(body.contains("\"primary_service_id\":5"))
        assertTrue(body.contains("\"services\":[{\"service_id\":5"))
        assertFalse(body.contains("promotion_code"))
    }

    @Test
    fun mapsProblemDetailsErrorCodeToVietnamese() {
        val failure = parseProblem(409, """{"title":"Conflict","status":409,"detail":"Slot taken","errorCode":"slot_taken"}""")

        assertEquals("slot_taken", failure.errorCode)
        assertEquals("Khung giờ này vừa có người đặt mất.", failure.message)
    }

    @Test
    fun fallsBackToStatusMessageWithRetryAfter() {
        val failure = parseProblem(429, null, retryAfterSeconds = 120)

        assertEquals("Bạn thao tác quá nhanh. Vui lòng thử lại sau 2 phút.", failure.message)
    }

    @Test
    fun showsUtcInstantsOnClinicClock() {
        assertEquals("13:09 13/09/2026", formatDateTime("2026-09-13T06:09:12.345+00:00"))
    }

    @Test
    fun turnstilePageRendersTheGivenSiteKeyAndBridgesCallbacks() {
        val html = turnstileHtml("0x4AAAAAAEgL308hsDpDZ-XH")

        assertTrue(html.contains("sitekey: '0x4AAAAAAEgL308hsDpDZ-XH'"))
        assertTrue(html.contains("challenges.cloudflare.com/turnstile/v0/api.js"))
        assertTrue(html.contains("QlpkCaptcha.onVerify(token)"))
    }

    @Test
    fun captchaTokenIsSingleUse() {
        val state = CaptchaState()
        state.onVerify("provider-token")

        assertEquals("provider-token", state.consume())
        assertFalse(state.solved)
        assertNull(state.consume())
    }

    @Test
    fun captchaRetriesOnceThenReportsTheCloudflareCode() {
        val state = CaptchaState()
        state.onError("300030")
        assertNull(state.error)

        state.onError("110200")
        assertTrue(state.error!!.contains("110200"))
    }
}
