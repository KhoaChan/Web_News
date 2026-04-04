# 📰 VnExpress Clone - Hệ thống Quản trị Báo Điện tử (CMS)

Một hệ thống Quản trị Nội dung (CMS) dành cho báo điện tử, được thiết kế theo mô hình B2C (Business-to-Consumer) với các quy trình nghiệp vụ tòa soạn thực tế. Dự án tập trung vào kiến trúc bảo mật, phân quyền người dùng (RBAC) và tối ưu hóa trải nghiệm giao diện.

## 🚀 Công nghệ sử dụng

**Backend:**
* Java 17
* Spring Boot 3
* Spring Security (Xác thực & Phân quyền)
* Spring Data JPA / Hibernate

**Frontend:**
* HTML5 / CSS3 / JavaScript
* Thymeleaf (Template Engine)
* Bootstrap 5 (Responsive UI)

**Database & Dịch vụ ngoài:**
* MySQL
* CKEditor 5 (Trình soạn thảo văn bản Rich-text)
* Cloudinary API (Lưu trữ hình ảnh Cloud)

---

## 🎯 Tính năng Nổi bật

### 1. Hệ thống Phân quyền (Role-Based Access Control - RBAC)
Thiết lập kiến trúc 3 cấp độ bảo mật nghiêm ngặt:
* **`ROLE_USER` (Độc giả):** Đăng ký/Đăng nhập, đọc báo, quản lý thông tin cá nhân và tương tác (bình luận).
* **`ROLE_AUTHOR` (Phóng viên):** Kế thừa quyền của User, được cấp quyền truy cập công cụ soạn thảo (`/author/write`) để nộp bài viết mới.
* **`ROLE_ADMIN` (Tổng biên tập):** Toàn quyền quản trị hệ thống tại khu vực nội bộ (`/admin`). Theo dõi bảng điều khiển, kiểm duyệt bài viết, quản lý chuyên mục và bình luận.
* **Bảo mật:** Mã hóa mật khẩu bằng `BCryptPasswordEncoder`. Chặn truy cập trái phép bằng bộ lọc phân quyền của Spring Security.

### 2. Nghiệp vụ Tòa soạn (Publishing Workflow)
Mô phỏng chính xác quy trình xuất bản báo chí:
* **Soạn thảo & Nộp bài:** Phóng viên soạn bài bằng CKEditor 5, hệ thống tự động khởi tạo đường dẫn tĩnh (Auto-slug) chuẩn SEO. Bài viết nộp lên mặc định mang trạng thái `PENDING`.
* **Cơ chế Kiểm duyệt:** Admin có khu vực làm việc riêng để xem trước, chỉnh sửa và quyết định duyệt (đổi thành `PUBLISHED`) hoặc từ chối xuất bản bài báo.
* **Cách ly Dữ liệu (Data Isolation):** Truy vấn CSDL được tối ưu hóa để phóng viên chỉ có thể xem và quản lý những bài viết do chính mình khởi tạo.

### 3. Bảo mật Dữ liệu & Toàn vẹn Hệ thống (Data Security)
* **Ngăn chặn Rò rỉ Dữ liệu (Data Leakage Prevention):** Mọi khu vực hiển thị công khai (Trang chủ, Chuyên mục, Bài viết liên quan, Tìm kiếm) đều được gắn điều kiện truy vấn lọc trạng thái (chỉ lấy bài `PUBLISHED` và bình luận `APPROVED`). Đảm bảo dữ liệu nháp tuyệt đối không bị lộ ra ngoài.
* **Xử lý Ngoại lệ:** Ràng buộc chặt chẽ các trường dữ liệu (Data Constraints) trong Database và Form validation để đảm bảo tính toàn vẹn của dữ liệu.

### 4. Tối ưu Giao diện (UI/UX)
* **Responsive Design:** Giao diện co giãn mượt mà trên mọi thiết bị. Xử lý triệt để hiện tượng text-wrapping trên thanh điều hướng.
* **Dropdown User Menu:** Trải nghiệm người dùng liền mạch với menu tài khoản tích hợp trên Navbar, tự động tạo Avatar từ ký tự đầu của tên người dùng.
* **Dynamic Sidebar:** Menu Bảng điều khiển (Admin Panel) tự động thích ứng, ẩn đi các chức năng nhạy cảm đối với các tài khoản không đủ thẩm quyền.

---

## 🛠 Hướng dẫn Cài đặt (Local Development)

### Yêu cầu môi trường
* JDK 17+
* Maven 3.8+
* MySQL 8.0+

### Các bước cài đặt
1. **Clone dự án:**
   ```bash
   git clone [https://github.com/your-username/vnexpress-clone.git](https://github.com/your-username/vnexpress-clone.git)
   cd vnexpress-clone
