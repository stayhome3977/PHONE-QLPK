package com.example.quanlyphongkham.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

// Static landing content, copied verbatim from the QLPK_FE web home page (src/components/*Section.tsx).
// The web renders these without calling the API, and the backend has no public doctors/services endpoint.

private fun unsplash(id: String, width: Int) = "https://images.unsplash.com/photo-$id?auto=format&fit=crop&w=$width&q=80"

object HeroContent {
    const val BADGE = "Phòng khám uy tín hàng đầu"
    const val TITLE_LINE_1 = "Chăm Sóc Sức Khỏe"
    const val TITLE_LINE_2 = "Làn Da Của Bạn"
    const val DESCRIPTION =
        "Phòng khám Da Liễu với đội ngũ y bác sĩ chuyên môn cao, trang thiết bị hiện đại, cam kết mang đến dịch vụ y tế chăm sóc da chất lượng tốt nhất cho bạn"
    val checklist = listOf("Đội ngũ bác sĩ chuyên môn cao", "Trang thiết bị hiện đại", "Điều trị cá nhân hóa", "Chi phí hợp lí")
    val image = unsplash("1622253692010-333f2da6031d", 1000)
}

data class Stat(val value: String, val label: String)
data class Feature(val icon: ImageVector, val title: String, val description: String)

object AboutContent {
    val image = unsplash("1519494026892-80bbd2d6fd0d", 1000)
    val stats = listOf(
        Stat("15+", "Năm kinh nghiệm"),
        Stat("50+", "Bác sĩ chuyên môn"),
        Stat("10k+", "Bệnh nhân hài lòng"),
        Stat("24/7", "Hỗ trợ tư vấn"),
    )
    const val BADGE = "Về chúng tôi"
    const val TITLE = "Chăm sóc làn da khỏe đẹp\ncùng đội ngũ chuyên gia"
    const val DESCRIPTION =
        "Với đội ngũ bác sĩ giàu kinh nghiệm trong lĩnh vực da liễu, chúng tôi mang đến các giải pháp chăm sóc và điều trị chuyên sâu, giúp bạn tự tin với làn da khỏe mạnh và rạng rỡ."
    val features = listOf(
        Feature(Icons.Filled.HowToReg, "Đội ngũ chuyên môn cao", "Bác sĩ da liễu giàu kinh nghiệm, tận tâm"),
        Feature(Icons.Filled.Apartment, "Công nghệ hiện đại", "Thiết bị tiên tiến, không gian chuyên nghiệp"),
        Feature(Icons.Filled.Shield, "Điều trị cá nhân hóa", "Phác đồ phù hợp với từng tình trạng da"),
        Feature(Icons.Filled.Schedule, "Tư vấn tận tâm 24/7", "Luôn sẵn sàng hỗ trợ và giải đáp"),
    )
}

data class Slide(val image: String, val title: String, val description: String)

object FacilitiesContent {
    const val TITLE = "Khám phá phòng khám da liễu"
    const val SUBTITLE = "Cơ sở vật chất hiện đại, dịch vụ chuyên nghiệp"
    const val INTERVAL_MS = 2800L
    val slides = listOf(
        Slide(unsplash("1629909613654-28e377c37b09", 1400), "Phòng khám hiện đại", "Không gian rộng rãi, trang thiết bị tân tiến"),
        Slide(unsplash("1519494026892-80bbd2d6fd0d", 1400), "Sảnh đón tiếp sang trọng", "Không gian rộng rãi, tiện nghi và ấm cúng"),
        Slide(unsplash("1516549655169-df83a0774514", 1400), "Phòng laser & chăm sóc da", "Công nghệ tái tạo và trẻ hóa da tiên tiến nhất"),
        Slide(unsplash("1586773860418-d37222d8fce3", 1400), "Phòng tư vấn chuyên sâu", "Bác sĩ da liễu trực tiếp thăm khám 1:1"),
    )
}

data class Testimonial(val avatar: String, val quote: String, val name: String)

object TestimonialsContent {
    const val TITLE = "Đánh giá từ bệnh nhân"
    val items = listOf(
        Testimonial(unsplash("1544005313-94ddf0286df2", 200), "Dịch vụ rất chuyên nghiệp, bác sĩ tận tâm. Tôi rất hài lòng với quá trình điều trị tại đây", "Chị Nguyễn Thị A"),
        Testimonial(unsplash("1534528741775-53994a69daeb", 200), "Liệu trình điều trị mụn rất hiệu quả, da mình cải thiện rõ rệt chỉ sau 3 tuần. Bác sĩ tư vấn rất kỹ.", "Chị Lê Thị B"),
        Testimonial(unsplash("1507003211169-0a1dd7228f2d", 200), "Không gian phòng khám sạch sẽ, thiết bị soi da hiện đại bậc nhất. Rất đáng tin cậy!", "Anh Trần Minh C"),
    )
}

object ServicesContent {
    const val TITLE = "Dịch vụ của chúng tôi"
    const val SUBTITLE = "Cung cấp các dịch vụ chăm sóc da chất lượng cao"
    val items = listOf(
        Feature(Icons.Filled.HowToReg, "Khám da liễu cơ bản", "Kiểm tra và đánh giá tình trạng da tổng quát"),
        Feature(Icons.Filled.MedicalServices, "Soi da chuyên sâu", "Phân tích chi tiết các vấn đề tiềm ẩn của làn da"),
        Feature(Icons.Filled.WaterDrop, "Điều trị mụn", "Tư vấn và điều trị mụn theo tình trạng da"),
        Feature(Icons.Filled.Brightness7, "Laser thẩm mỹ", "Điều trị nám, tàn nhang và sẹo rỗ công nghệ cao"),
        Feature(Icons.Filled.Shield, "Trẻ hóa làn da", "Cải thiện độ đàn hồi và phục hồi cấu trúc da"),
        Feature(Icons.Filled.FavoriteBorder, "Dưỡng sáng chuyên sâu", "Liệu trình nuôi dưỡng làn da trắng hồng tự nhiên"),
    )
}

data class StaticDoctor(val name: String, val specialty: String, val image: String, val experience: String? = null)

object DoctorsContent {
    const val TITLE = "Đội ngũ bác sĩ"
    const val SUBTITLE = "Những chuyên gia da liễu hàng đầu"
    val items = listOf(
        StaticDoctor("BS. Nguyễn Thị A", "Chuyên khoa Da liễu & Thẩm mỹ", unsplash("1559839734-2b71ea197ec2", 600)),
        StaticDoctor("BS. Nguyễn Thị D", "Chuyên khoa Điều trị Laser", unsplash("1594824813515-78335025d2c4", 600)),
        StaticDoctor("BS. Nguyễn Tiến B", "Chuyên khoa Da liễu tổng quát", unsplash("1622253692010-333f2da6031d", 600)),
        StaticDoctor("BS. Nguyễn Văn C", "Chuyên khoa Phục hồi da chuyên sâu", unsplash("1612349317150-e413f6a5b16d", 600)),
    )
}

data class Branch(val name: String, val address: String)
data class Phone(val display: String, val dial: String)

object ContactContent {
    const val TITLE = "Liên hệ với chúng tôi"
    const val SUBTITLE = "Chúng tôi luôn sẵn sàng hỗ trợ bạn!"
    val branches = listOf(
        Branch("Cơ sở 1", "152 Chu Văn An, Bình Hiên, Hải Châu, Đà Nẵng"),
        Branch("Cơ sở 2", "3130 Hoàng Văn Thụ, Phường 9, Phú Nhuận, TP. Hồ Chí Minh"),
    )
    val hours = listOf("Thứ 2 – Thứ 6: 8am – 6pm", "Thứ 7: 8am – 12am", "Chủ nhật: Nghỉ")
    const val EMAIL = "info.chamuseum.danang.vn@gmail.com"
    val phones = listOf(Phone("1900 9004", "19009004"), Phone("097 4567 513", "0974567513"))
    const val MAP_URI = "geo:16.0597816,108.2173167?q=16.0597816,108.2173167(Phòng Khám Da Liễu)"
}

data class NewsItem(val title: String, val summary: String, val image: String, val date: String, val category: String)

object NewsContent {
    const val TITLE = "Một vài tin tức y tế hot"
    const val SUBTITLE = "Cập nhập những tin tức mới nhất về y tế"
    val items = listOf(
        NewsItem("Tin tức A", "lorem ipsum dolor sit amet consectetuer adipiscing elit sed diam nonummy nibh euismod tincidunt ut...", unsplash("1584515979956-d9f6e5d09982", 600), "05/09/2026", "Chăm sóc da"),
        NewsItem("Tin tức A", "lorem ipsum dolor sit amet consectetuer adipiscing elit sed...", unsplash("1576091160399-112ba8d25d1d", 600), "04/09/2026", "Y tế"),
        NewsItem("Tin tức A", "lorem ipsum dolor sit amet consectetuer adipiscing elit sed...", unsplash("1505751172876-fa1923c5c528", 600), "03/09/2026", "Điều trị"),
        // The web leaves this card's title out.
        NewsItem("", "lorem ipsum dolor sit amet consectetuer adipiscing elit...", unsplash("1588776814546-1ffcf47267a5", 600), "02/09/2026", "Lời khuyên"),
        NewsItem("Tin tức A", "lorem ipsum dolor sit amet consectetuer adipiscing elit sed...", unsplash("1584467735815-f778f274e296", 600), "01/09/2026", "Phòng ngừa"),
        NewsItem("Tin tức A", "lorem ipsum dolor sit amet consectetuer adipiscing elit sed...", unsplash("1584308666744-24d5c474f2ae", 600), "31/08/2026", "Dược phẩm"),
    )
}

object FooterContent {
    const val DESCRIPTION =
        "Hệ thống phòng khám chuyên khoa da liễu hàng đầu, mang đến giải pháp điều trị & thẩm mỹ da chuẩn y khoa, an toàn và hiệu quả cao."
    const val HOTLINE = "Hotline: 0393975116 - 1900 9004"
    const val EMAIL = "phongkhamdalieu@gmail.com"
    const val ADDRESS = "152 Chu Văn An, Bình Hiên, Hải Châu, Đà Nẵng"
    val treatments = listOf(
        "Khám da liễu cơ bản",
        "Soi da chuyên sâu & Vi quang",
        "Điều trị mụn & thâm sẹo",
        "Trẻ hóa & tái tạo làn da",
        "Laser thẩm mỹ công nghệ cao",
        "Điều trị viêm da cơ địa",
    )
    const val NEWSLETTER_TITLE = "Nhận Tư Vấn Miễn Phí"
    const val NEWSLETTER_TEXT = "Đăng ký email để nhận cẩm nang chăm sóc da và thông báo ưu đãi mới nhất."
    const val NEWSLETTER_DONE = "Đăng ký thành công! Cảm ơn bạn."
    const val COPYRIGHT = "© 2026 Phòng Khám Da Liễu. Bản quyền thuộc về phòng khám."
}

object ClinicDetailContent {
    val heroImage = unsplash("1629909613654-28e377c37b09", 1600)
    const val TAG = "Phòng khám da liễu"
    const val TITLE = "Chăm sóc làn da khỏe đẹp\ncùng đội ngũ chuyên gia"
    const val INTRO_TITLE = "Giới thiệu về phòng khám"
    const val INTRO =
        "Phòng khám Da Liễu tự hào là đơn vị y tế chuyên sâu, cung cấp các dịch vụ khám, chẩn đoán và điều trị toàn diện các bệnh lý về da cũng như thẩm mỹ da chuẩn y khoa. Chúng tôi kết hợp phác đồ chuẩn quốc tế cùng kỹ thuật hiện đại nhằm đem lại hiệu quả bền vững và an toàn tuyệt đối."
    val introFeatures = listOf(
        Feature(Icons.Filled.HowToReg, "Đội ngũ chuyên gia", "Bác sĩ da liễu giàu kinh nghiệm"),
        Feature(Icons.Filled.Apartment, "Trang thiết bị hiện đại", "Công nghệ tiên tiến, hiện đại"),
        Feature(Icons.Filled.MedicalServices, "Dịch vụ tận tâm", "Chăm sóc khách hàng chu đáo"),
    )
    val introImage = unsplash("1622253692010-333f2da6031d", 800)
    val facilities = listOf(
        Slide(unsplash("1629909613654-28e377c37b09", 600), "Phòng khám đa năng & điều trị", ""),
        Slide(unsplash("1519494026892-80bbd2d6fd0d", 600), "Phòng chăm sóc da chuyên sâu", ""),
        Slide(unsplash("1516549655169-df83a0774514", 600), "Hệ thống thiết bị Laser thẩm mỹ", ""),
        Slide(unsplash("1586773860418-d37222d8fce3", 600), "Khu vực phục hồi & trị liệu", ""),
    )
    val steps = listOf(
        "Đặt lịch khám" to "Khách đặt lịch qua website hoặc hotline",
        "Tiếp nhận thông tin" to "Xác nhận lịch hẹn và tư vấn sơ bộ",
        "Thăm khám & Tư vấn" to "Bác sĩ thăm khám và đưa ra phác đồ phù hợp",
        "Điều trị & Theo dõi" to "Tiến hành điều trị và hẹn lịch tái khám",
    )
    val doctors = listOf(
        StaticDoctor("BS. Nguyễn Thu Hà", "Chuyên khoa da liễu", unsplash("1559839734-2b71ea197ec2", 600), "Kinh nghiệm 10 năm"),
        StaticDoctor("BS. Trần Minh Anh", "Chuyên khoa da liễu & Laser", unsplash("1622253692010-333f2da6031d", 600), "Kinh nghiệm 8 năm"),
        StaticDoctor("BS. Lê Hoàng Nam", "Chuyên khoa da liễu tổng quát", unsplash("1594824813515-78335025d2c4", 600), "Kinh nghiệm 12 năm"),
        StaticDoctor("BS. Phạm Quỳnh Trang", "Chuyên khoa thẩm mỹ da", unsplash("1612349317150-e413f6a5b16d", 600), "Kinh nghiệm 9 năm"),
    )
    const val CTA_TITLE = "Sẵn sàng trải nghiệm dịch vụ chuẩn y khoa?"
    const val CTA_TEXT = "Đặt lịch ngay hôm nay để nhận tư vấn trực tiếp cùng đội ngũ bác sĩ chuyên khoa da liễu đầu ngành."
}
