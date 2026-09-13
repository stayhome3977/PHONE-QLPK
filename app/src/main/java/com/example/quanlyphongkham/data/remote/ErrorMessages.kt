package com.example.quanlyphongkham.data.remote

/** Vietnamese texts for the backend's errorCode values, kept in step with QLPK_FE src/api/helpers.ts. */
object ErrorMessages {
    const val NETWORK = "Không kết nối được máy chủ phòng khám. Vui lòng kiểm tra Internet và thử lại."
    const val BAD_RESPONSE = "Máy chủ trả về dữ liệu không đọc được."
    const val UNKNOWN = "Đã có lỗi xảy ra. Vui lòng thử lại."
    const val PATIENT_ONLY = "Ứng dụng chỉ dành cho bệnh nhân. Nhân viên vui lòng dùng trang quản lý phòng khám."

    private val byCode = mapOf(
        "captcha_required" to "Vui lòng hoàn tất ô kiểm tra bảo mật rồi thử lại.",
        "slot_taken" to "Khung giờ này vừa có người đặt mất.",
        "slot_not_bookable" to "Khung giờ này không nằm trong lịch làm việc của bác sĩ.",
        "insufficient_stock" to "Thuốc trong kho không đủ cho yêu cầu này.",
        "reservation_expired" to "Phần thuốc giữ chỗ đã hết hạn. Vui lòng đặt lại.",
        "discount_approval_required" to "Mức giảm giá này cần phòng khám duyệt.",
        "already_checked_in" to "Lịch hẹn này đã được nhận phòng rồi.",
        "checkin_window_closed" to "Ngoài khung giờ nhận phòng. Vui lòng liên hệ quầy lễ tân.",
        "account_locked" to "Tài khoản đang tạm khoá do đăng nhập sai nhiều lần.",
        "invalid_credentials" to "Số điện thoại/email hoặc mật khẩu không đúng.",
        "password_policy" to "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường và chữ số.",
        "registration_code_invalid" to "Mã xác thực không đúng hoặc đã hết hạn. Vui lòng lấy mã mới.",
        "password_reset_code_invalid" to "Mã đặt lại không đúng hoặc đã hết hạn. Vui lòng lấy mã mới.",
        "otp_resend_too_soon" to "Mã vừa được gửi. Vui lòng đợi một lát rồi thử lại.",
        "verification_code_not_sent" to "Không gửi được mã xác thực. Vui lòng thử lại sau ít phút.",
    )

    fun forCode(code: String?): String? = code?.let { byCode[it] }

    fun forStatus(status: Int, retryAfterSeconds: Long?, detail: String?): String = when {
        status == 400 -> detail?.let { "Dữ liệu chưa hợp lệ: $it" } ?: "Dữ liệu chưa hợp lệ."
        status == 401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
        status == 403 -> "Tài khoản không có quyền thực hiện thao tác này."
        status == 404 -> "Không tìm thấy dữ liệu yêu cầu."
        status == 409 -> detail ?: "Thao tác bị từ chối do xung đột dữ liệu."
        status == 429 -> if (retryAfterSeconds != null) {
            "Bạn thao tác quá nhanh. Vui lòng thử lại sau ${formatWait(retryAfterSeconds)}."
        } else {
            "Bạn thao tác quá nhanh. Vui lòng thử lại sau."
        }
        status >= 500 -> "Máy chủ đang gặp sự cố. Vui lòng thử lại sau."
        else -> detail ?: UNKNOWN
    }

    private fun formatWait(seconds: Long): String =
        if (seconds >= 60) "${(seconds + 59) / 60} phút" else "$seconds giây"
}
