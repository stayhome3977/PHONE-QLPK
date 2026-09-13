# QLPK Da Liễu: app bệnh nhân Android

App Android (Jetpack Compose) dành cho bệnh nhân của Phòng Khám Da Liễu. App gọi thẳng API QLPK đã deploy trên Azure:

```
https://app-qlpk-hoangqlpk97.azurewebsites.net/api/
```

App không cần chạy backend trên máy. Chỉ cần máy ảo hoặc điện thoại có Internet.

## Chạy app

1. Mở thư mục `F:\PHONE_QLPK` bằng Android Studio và chờ Gradle Sync xong.
2. Chọn thiết bị (ví dụ **Medium Phone**), rồi bấm **Run ▶** (`Shift+F10`).
3. Lần đầu mở app, Android hiện hộp thoại **"Thêm vào màn hình chính"**. Bấm **Thêm**, icon **QLPK Da Liễu** sẽ nằm ngoài màn hình chính.
   - Lỡ bấm bỏ qua: vào tab **Tài khoản → Thêm vào màn hình chính**.
   - Hoặc vuốt lên mở danh sách ứng dụng, nhấn giữ icon rồi kéo ra.

Cài bằng dòng lệnh:

```powershell
cd F:\PHONE_QLPK
.\gradlew.bat :app:installDebug
```

## Đổi máy chủ API

Thêm dòng sau vào `local.properties`, ví dụ để trỏ về backend chạy trên máy (từ emulator):

```properties
qlpk.apiRoot=http://10.0.2.2:5131/api/
```

Muốn gọi địa chỉ `http://` (không mã hoá) thì phải cho phép cleartext trong manifest. Server Azure dùng HTTPS nên mặc định không cần.

## Captcha (Cloudflare Turnstile)

Server dùng Turnstile thật. Các thao tác sau cần giải ô "Tôi không phải robot":

- đăng ký, gửi lại mã OTP
- quên mật khẩu
- đặt lịch khám
- đăng nhập sau nhiều lần sai

Cách app hiển thị ô này:

- App hiện ô Turnstile trong WebView, dưới tên miền của web portal: `wonderful-plant-05c95c300.6.azurestaticapps.net`.
- Site key giống web: `0x4AAAAAAEgL308hsDpDZ-XH`.

Nếu ô báo lỗi **110200** (tên miền chưa được cấp phép), quản trị viên phải thêm tên miền trên vào danh sách hostname của widget trong Cloudflare Dashboard.

## Luồng sử dụng

1. **Đăng ký:** nhập họ tên, SĐT, email, mật khẩu (≥ 8 ký tự, có chữ hoa, chữ thường và số). Mã OTP được gửi về email (xem cả thư mục Spam).
2. **Đặt lịch:** bác sĩ → ngày/giờ → dịch vụ (tuỳ chọn) → người khám → xác nhận (giải captcha).
3. **Thanh toán đặt lịch** trong chi tiết lịch hẹn: lịch chuyển sang *Đã xác nhận*.
4. **Check-in lấy số:** mở từ 30 phút trước đến 15 phút sau giờ hẹn.
5. Hồ sơ bệnh nhân, đổi mật khẩu, đăng xuất.

Kết quả khám, đơn thuốc, hoá đơn và thông báo hiện **"Sắp ra mắt"**: backend chưa có API cho bệnh nhân.
