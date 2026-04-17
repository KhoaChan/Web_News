# Phụ lục - Automation Testing

## 1. Mục tiêu

Phần phụ lục này mô tả các kịch bản automation testing dùng trong báo cáo. Nội dung được trình bày theo dạng `Tool - Script - Result` để bám đúng rubric môn học và phục vụ phần demo.

## 2. Danh sách công cụ và kịch bản

| ID | Tool | Script | Chức năng áp dụng | Result |
| --- | --- | --- | --- | --- |
| AUTO-TC01 | Selenium IDE | `register-smoke` | `REQ01` Đăng ký tài khoản | Mô tả kịch bản đăng ký hợp lệ và đăng ký lỗi; sẵn sàng chạy demo |
| AUTO-TC02 | Playwright | `login-role-redirect` | `REQ02` Đăng nhập | Mô tả kịch bản đăng nhập thành công và thất bại; dùng để kiểm tra điều hướng sau đăng nhập |
| AUTO-TC03 | Cypress | `search-keyword` | `REQ04` Tìm kiếm bài viết | Mô tả kịch bản tìm kiếm theo từ khóa có kết quả và không có kết quả |
| AUTO-TC04 | Selenium WebDriver | `author-submit-workflow` | `REQ05` Workflow của tác giả | Mô tả kịch bản tạo nháp và gửi bài từ `DRAFT` sang `IN_REVIEW` |
| AUTO-TC05 | Selenium WebDriver | `editor-publish-workflow` | `REQ07` Duyệt bài | Mô tả kịch bản xuất bản bài từ trạng thái `IN_REVIEW` |
| AUTO-TC06 | Cypress | `comment-submit` | `REQ08` Gửi bình luận | Mô tả kịch bản người dùng đăng nhập gửi bình luận vào bài đã xuất bản |

## 3. Ghi chú sử dụng

- Báo cáo chỉ mô tả kịch bản và công cụ tương ứng với từng chức năng được chọn.
- Kết quả `PASS` hoặc `PENDING` của từng script được cập nhật tại phần `Automation Testing` của Chương 4.
- Nhóm có thể dùng lại các kịch bản này để quay video demo hoặc chạy trực tiếp khi bảo vệ.
