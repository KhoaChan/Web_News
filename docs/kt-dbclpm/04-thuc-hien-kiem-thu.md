# Chương 4 - Thực hiện kiểm thử

## 4.1 Test Execution

Bảng thực thi test case được trình bày theo đúng mẫu bắt buộc `Test Case | KQ mong đợi | KQ thực tế | Trạng thái` tại file [`test-execution.csv`](/e:/phat_trien_ung_dung_voi_j2ee/NewsWebsite/docs/kt-dbclpm/test-execution.csv).

Tại thời điểm hoàn thiện bộ tài liệu:

- Một số test case của `REQ01`, `REQ05` và `REQ08` đã có bằng chứng tự động từ bộ test hiện có.
- Các test case còn lại được giữ trạng thái `PENDING` để nhóm cập nhật khi thực hiện demo và chụp minh chứng trên máy local.
- Việc ghi nhận `KQ thực tế` và `Trạng thái` sẽ bám đúng biểu mẫu bắt buộc của môn học.

## 4.2 Bug Report

Bảng bug report được lập theo đúng mẫu bắt buộc `Bug ID | Mô tả lỗi | Bước tái hiện | KQ mong đợi | KQ thực tế | Mức độ | Trạng thái` và lưu tại file [`bug-report.csv`](/e:/phat_trien_ung_dung_voi_j2ee/NewsWebsite/docs/kt-dbclpm/bug-report.csv).

Trong giai đoạn chuẩn bị báo cáo, nhóm ghi nhận hai lỗi tiêu biểu:

- `BUG-01`: gửi bình luận thiếu `articleId` có thể dẫn tới thông báo lỗi không thân thiện.
- `BUG-02`: màn hình review của editor có thể truy cập trực tiếp tới bài không ở trạng thái `IN_REVIEW`.

## 4.3 Automation Testing

Phần automation testing được mô tả theo cấu trúc `Tool - Script - Result` và chi tiết nằm ở file [`phu-luc-automation.md`](/e:/phat_trien_ung_dung_voi_j2ee/NewsWebsite/docs/kt-dbclpm/phu-luc-automation.md).

| Tool | Script | Result |
| --- | --- | --- |
| Selenium IDE | `register-smoke` | Mô tả luồng tự động cho `REQ01`; sẵn sàng dùng để demo đăng ký hợp lệ và đăng ký lỗi |
| Playwright | `login-role-redirect` | Mô tả luồng tự động cho `REQ02`; dùng để kiểm tra đăng nhập thành công và thất bại |
| Cypress | `search-keyword` | Mô tả luồng tự động cho `REQ04`; dùng để kiểm tra tìm kiếm theo từ khóa |
| Selenium WebDriver | `author-submit-workflow` | Mô tả luồng tự động cho `REQ05`; dùng để kiểm tra tạo nháp và gửi bài đi duyệt |
| Selenium WebDriver | `editor-publish-workflow` | Mô tả luồng tự động cho `REQ07`; dùng để kiểm tra duyệt bài từ chờ duyệt sang đã xuất bản |
| Cypress | `comment-submit` | Mô tả luồng tự động cho `REQ08`; dùng để kiểm tra gửi bình luận vào bài đã xuất bản |
