package com.example.quanlyphongkham.ui.components

import androidx.compose.ui.graphics.Color
import com.example.quanlyphongkham.ui.theme.Brand
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val vietnamese = Locale.forLanguageTag("vi-VN")
private val CLINIC_ZONE = ZoneId.of("Asia/Ho_Chi_Minh")
private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val dayOfWeekFormat = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", vietnamese)

fun formatVnd(amount: Double?): String =
    if (amount == null) "—" else NumberFormat.getNumberInstance(vietnamese).format(amount.toLong()) + " đ"

/** "2026-09-13" -> "13/09/2026" */
fun formatDate(iso: String?): String =
    iso?.let { runCatching { LocalDate.parse(it.take(10)).format(dateFormat) }.getOrNull() } ?: "—"

fun formatDayOfWeek(iso: String): String =
    runCatching { LocalDate.parse(iso.take(10)).format(dayOfWeekFormat).replaceFirstChar { it.uppercase() } }
        .getOrDefault(iso)

/** "08:30:00" -> "08:30" */
fun formatTime(time: String?): String = time?.take(5) ?: "—"

fun formatDateTime(iso: String?): String =
    iso?.let {
        // The API returns UTC instants; show them on the clinic's clock (backend ClinicTime:TimeZoneId).
        runCatching {
            OffsetDateTime.parse(it).atZoneSameInstant(CLINIC_ZONE).format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))
        }.getOrNull()
    } ?: "—"

/** The clinic's calendar date, independent of the phone's time zone. */
fun clinicToday(): LocalDate = LocalDate.now(CLINIC_ZONE)

fun clinicNow(): LocalTime = LocalTime.now(CLINIC_ZONE)

fun parseTime(time: String): LocalTime? = runCatching { LocalTime.parse(time) }.getOrNull()

data class StatusStyle(val label: String, val color: Color, val background: Color)

/** Labels shared with QLPK_FE's MyAppointmentsPage. */
fun appointmentStatus(status: String): StatusStyle = when (status) {
    "pending" -> StatusStyle("Chờ xác nhận", Brand.Warning, Brand.WarningSoft)
    "pending_approval" -> StatusStyle("Chờ duyệt giảm giá", Brand.Warning, Brand.WarningSoft)
    "confirmed" -> StatusStyle("Đã xác nhận", Brand.Primary, Brand.PrimaryLight)
    "checked_in" -> StatusStyle("Đã nhận phòng", Brand.Violet, Brand.VioletSoft)
    "in_progress" -> StatusStyle("Đang khám", Brand.Violet, Brand.VioletSoft)
    "completed" -> StatusStyle("Đã hoàn thành", Brand.Success, Brand.SuccessSoft)
    "cancelled" -> StatusStyle("Đã huỷ", Brand.Danger, Brand.DangerSoft)
    "no_show" -> StatusStyle("Không đến", Brand.TextMuted, Brand.BorderSubtle)
    else -> StatusStyle(status, Brand.TextMuted, Brand.BorderSubtle)
}

val ACTIVE_STATUSES = setOf("pending", "pending_approval", "confirmed", "checked_in", "in_progress")
val CANCELLABLE_STATUSES = setOf("pending", "pending_approval", "confirmed")

/** The seeded specialty rows carry English names; show the Vietnamese ones the web portal uses. */
fun specialtyLabel(code: String?, name: String?): String = when {
    code == "general_dermatology" || name.equals("General Dermatology", ignoreCase = true) -> "Da liễu tổng quát"
    code == "aesthetic_dermatology" || name.equals("Aesthetic Dermatology", ignoreCase = true) -> "Da liễu thẩm mỹ"
    else -> name ?: "Da liễu"
}

fun genderLabel(gender: String?): String = when (gender) {
    "male" -> "Nam"
    "female" -> "Nữ"
    "other" -> "Khác"
    else -> "—"
}

fun relationshipLabel(value: String?): String = when (value) {
    "self" -> "Bản thân"
    "child" -> "Con"
    "parent" -> "Bố/Mẹ"
    "spouse" -> "Vợ/Chồng"
    "other" -> "Người thân"
    else -> "—"
}

fun consultationModeLabel(mode: String?): String = when (mode) {
    "in_clinic" -> "Khám tại phòng khám"
    "video_call" -> "Tư vấn video"
    "chat" -> "Tư vấn trực tuyến"
    else -> "Khám tại phòng khám"
}

object ClinicInfo {
    const val NAME = "Phòng Khám Da Liễu"
    const val TAGLINE = "Chăm sóc sức khỏe làn da tận tâm"
    const val HOTLINE = "0393975116"
    const val HOTLINE_2 = "1900 9004"
    const val ADDRESS = "152 Chu Văn An, Bình Hiên, Hải Châu, Đà Nẵng"
    const val HOURS = "08:00 – 17:00"
    const val EMAIL = "phongkhamdalieu@gmail.com"
}
