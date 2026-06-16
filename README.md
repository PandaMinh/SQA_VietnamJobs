# VietnamJobs

Job Seeker Assistance and Job Listing Platform.

## Giới thiệu

VietnamJobs là một nền tảng website và ứng dụng di động hỗ trợ:
- Người tìm việc tìm kiếm cơ hội việc làm.
- Nhà tuyển dụng đăng và quản lý tin tuyển dụng.
- Quản trị viên quản lý người dùng, bài đăng và dữ liệu hệ thống.

Công nghệ chính: Java Spring Boot, Thymeleaf, MySQL, Flutter.

## Cấu trúc project

- `Website/vietnamjobs_42`: Backend + Web (Spring Boot)
- `App/aptech`: Mobile app (Flutter)
- `Database/vietnamjobs.sql`: Dữ liệu database mẫu

## Yêu cầu môi trường

1. Java JDK 21
2. MySQL 8.x (hoặc tương thích)
3. PowerShell (Windows)
4. (Tuỳ chọn) Flutter SDK nếu chạy mobile app

## Chạy project local (step-by-step)

### 1. Chuẩn bị database

1. Tạo database:
```sql
CREATE DATABASE vietnamjobs;
```
2. Import file SQL:
- File: `Database/vietnamjobs.sql`
- Có thể import bằng MySQL Workbench hoặc lệnh:
```powershell
mysql -u root -p vietnamjobs < Database\vietnamjobs.sql
```

3. Nếu cần seed thêm dữ liệu:
- Dùng `Database/seed_smoke.sql` cho E2E, integration, regression hằng ngày
- Dùng `Database/seed_performance.sql` cho benchmark/performance riêng

Ví dụ:
```powershell
mysql -u root -p vietnamjobs < Database\seed_smoke.sql
```

Lưu ý:
- `seed_smoke.sql` sẽ dọn dữ liệu `perf_*` cũ rồi seed bộ nhỏ
- `seed_performance.sql` sẽ dọn dữ liệu `perf_*` cũ rồi seed bộ lớn
- Không nên dùng `seed_performance.sql` để chạy UI regression thường xuyên vì các route public hiện tại còn xử lý full-scan
- `seed_smoke.sql` hiện được chỉnh cho autotest hằng ngày với quy mô nhỏ:
  - `55` seeker
  - `50` employer
  - `60` posting
  - `65` application
  - `60` follow

### 2. Cấu hình env cho backend

1. Vào thư mục backend:
```powershell
cd Website\vietnamjobs_42
```
2. Tạo file env local:
```powershell
Copy-Item .env.local.ps1.example .env.local.ps1
```
3. Mở `.env.local.ps1` và chỉnh các biến cần thiết (đặc biệt DB user/password):
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `MAIL_USERNAME`, `MAIL_PASSWORD` (nếu cần chức năng gửi mail)

Ghi chú:
- Script chạy local đã có sẵn trong `run-local.ps1`.
- Script sẽ load `.env.local.ps1`, set `SPRING_PROFILES_ACTIVE=local` và chạy Maven Wrapper.

### 3. Chạy backend + website

Trong `Website\vietnamjobs_42` chạy:
```powershell
.\run-local.ps1
```

Khi chạy thành công, website mở tại:
- `http://localhost:8087`

### 4. (Tuỳ chọn) Chạy Flutter app

1. Mở terminal mới, vào thư mục app:
```powershell
cd App\aptech
```
2. Cài dependency:
```powershell
flutter pub get
```
3. Chạy app:
```powershell
flutter run
```

Lưu ý:
- Đảm bảo app trỏ đúng backend URL (thường là `http://localhost:8087` hoặc IP LAN khi chạy trên thiết bị thật).

## Troubleshooting nhanh

1. Lỗi kết nối DB:
- Kiểm tra MySQL đang chạy.
- Kiểm tra đúng `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` trong `.env.local.ps1`.

2. Port `8087` bị chiếm:
- Đổi `SERVER_PORT` trong `.env.local.ps1`.

3. Lỗi Java:
- Kiểm tra cài JDK 21.
- Kiểm tra `JAVA_HOME` (script mặc định dùng `C:\Program Files\Java\jdk-21` nếu chưa set).

## Tính năng chính

1. Người tìm việc:
- Tìm kiếm/lọc việc làm.
- Quản lý hồ sơ và CV.
- Ứng tuyển, theo dõi trạng thái.

2. Nhà tuyển dụng:
- Đăng/chỉnh sửa/xoá tin tuyển dụng.
- Xem hồ sơ ứng viên.
- Quản lý quy trình tuyển dụng.

3. Quản trị viên:
- Quản lý tài khoản, bài đăng.
- Thống kê/báo cáo.
- Hỗ trợ người dùng.

## Tài liệu tham khảo

- Demo website: https://drive.google.com/file/d/1JIO2ZKBJ-Ft_5Qd71PWQuP4ZOuoFaUvA/view?usp=drive_link
- Demo app: https://drive.google.com/file/d/1ktE5Q1gBZEoVxmnPB2ePlyoingwgaC0B/view?usp=sharing
- Báo cáo: https://docs.google.com/document/d/1pFXK16MTiGwkGbooDZsSsAjUhwBkbXmmUYY3STUuJvs/edit?tab=t.0#heading=h.6g0rregb0izk
