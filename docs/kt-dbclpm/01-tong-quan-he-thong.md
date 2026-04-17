# Chương 1 - Tổng quan hệ thống

## 1.1 Giới thiệu hệ thống

Đề tài `Website tin tức` là hệ thống web hỗ trợ cả phía người đọc lẫn backoffice biên tập. Ứng dụng cho phép người dùng đăng ký, đăng nhập, tìm kiếm bài viết, gửi bình luận; đồng thời hỗ trợ tác giả tạo bài, gửi bài đi duyệt và biên tập viên xuất bản nội dung.

Trong báo cáo học phần `Kiểm thử và Đảm bảo chất lượng phần mềm`, hệ thống được mô tả ở mức yêu cầu nghiệp vụ trước, sau đó nhóm lựa chọn một số chức năng trọng tâm để thiết kế test case Black-box, White-box, thực hiện bug tracking và mô tả automation testing.

## 1.2 Mục tiêu hệ thống

- Cung cấp website tin tức có nội dung công khai cho người đọc.
- Hỗ trợ xác thực người dùng và điều hướng giao diện theo vai trò.
- Hỗ trợ quy trình biên tập nội dung từ tác giả đến biên tập viên.
- Kiểm soát việc gửi và duyệt bình luận trước khi hiển thị công khai.
- Làm đối tượng để nhóm áp dụng các kỹ thuật kiểm thử Black-box, White-box, quản lý lỗi và kiểm thử tự động.

## 1.3 Đối tượng sử dụng

| Đối tượng | Vai trò | Nhu cầu chính |
| --- | --- | --- |
| Guest | Khách truy cập | Xem tin, tìm kiếm bài viết, xem chi tiết bài viết |
| User | Người dùng đã đăng nhập | Đăng nhập, gửi bình luận vào bài đã xuất bản |
| Author | Tác giả | Tạo, sửa, lưu nháp và gửi bài đi duyệt |
| Editor | Biên tập viên | Duyệt bài, xuất bản bài, duyệt hoặc từ chối bình luận |
| Admin | Quản trị viên | Quản trị tổng thể hệ thống |

## 1.4 Chức năng chính

| ID | Description (Mô tả yêu cầu) |
| --- | --- |
| REQ01 | Người dùng có thể đăng ký tài khoản độc giả mới. |
| REQ02 | Người dùng có thể đăng nhập vào hệ thống bằng Username và Password hợp lệ. |
| REQ03 | Hệ thống điều hướng đúng giao diện theo vai trò sau khi đăng nhập. |
| REQ04 | Khách truy cập có thể tìm kiếm bài viết bằng từ khóa. |
| REQ05 | Tác giả có thể tạo, sửa, lưu bản nháp và gửi bài đi duyệt. |
| REQ06 | Biên tập viên duyệt hoặc từ chối bình luận. |
| REQ07 | Biên tập viên có thể duyệt bài viết từ trạng thái "Chờ duyệt" sang "Đã xuất bản". |
| REQ08 | Người dùng đã đăng nhập có thể gửi bình luận vào bài viết đã xuất bản. |

Ghi chú: hệ thống thực tế có các vai trò `USER`, `AUTHOR`, `EDITOR`, `ADMIN`. Vì vậy khi mô tả và kiểm thử `REQ03`, báo cáo sẽ bám theo đủ 4 vai trò này.

## 1.5 Phạm vi kiểm thử

### Trong phạm vi

- Nhóm lựa chọn kiểm thử chuyên sâu `6` chức năng: `REQ01`, `REQ02`, `REQ04`, `REQ05`, `REQ07`, `REQ08`.
- Mỗi thành viên phụ trách `2` chức năng:
  - Thành viên 1: `REQ01`, `REQ02`
  - Thành viên 2: `REQ04`, `REQ05`
  - Thành viên 3: `REQ07`, `REQ08`
- Sáu chức năng trên là cơ sở để thiết kế Black-box test case, chọn đoạn chương trình White-box và mô tả automation testing.
- Phần White-box trong báo cáo chính tập trung vào `4` đoạn chương trình đại diện theo đúng rubric môn học.

### Ngoài phạm vi

- `REQ03` và `REQ06` vẫn được ghi nhận ở mức yêu cầu hệ thống, nhưng không phải hai chức năng được nhóm chọn để kiểm thử chuyên sâu trong phạm vi báo cáo này.
- Kiểm thử hiệu năng, bảo mật chuyên sâu, SEO, triển khai production và CI/CD không nằm trong phạm vi báo cáo.
