# Chương 2 - Phân tích yêu cầu & Test Plan

## 2.1 Functional Requirements

| Requirement ID | Description |
| --- | --- |
| REQ01 | Người dùng có thể đăng ký tài khoản độc giả mới. |
| REQ02 | Người dùng có thể đăng nhập vào hệ thống bằng Username và Password hợp lệ. |
| REQ03 | Hệ thống điều hướng đúng giao diện theo vai trò sau khi đăng nhập. |
| REQ04 | Khách truy cập có thể tìm kiếm bài viết bằng từ khóa. |
| REQ05 | Tác giả có thể tạo, sửa, lưu bản nháp và gửi bài đi duyệt. |
| REQ06 | Biên tập viên duyệt hoặc từ chối bình luận. |
| REQ07 | Biên tập viên có thể duyệt bài viết từ trạng thái "Chờ duyệt" sang "Đã xuất bản". |
| REQ08 | Người dùng đã đăng nhập có thể gửi bình luận vào bài viết đã xuất bản. |

Ghi chú: trong `8` yêu cầu chức năng trên, nhóm chọn `6` chức năng để kiểm thử chuyên sâu là `REQ01`, `REQ02`, `REQ04`, `REQ05`, `REQ07`, `REQ08`.

## 2.2 Non-functional Requirements

| Requirement ID | Description |
| --- | --- |
| NFR01 | Hệ thống phân quyền truy cập đúng theo vai trò `USER`, `AUTHOR`, `EDITOR`, `ADMIN`. |
| NFR02 | Dữ liệu đầu vào phải được kiểm tra hợp lệ trước khi lưu hoặc xử lý. |
| NFR03 | Trạng thái workflow bài viết phải được chuyển đổi đúng quy tắc nghiệp vụ. |
| NFR04 | Các luồng chính phải có thể kiểm thử trên Chrome và Edge. |
| NFR05 | Hệ thống phải trả thông báo lỗi hoặc trạng thái thao tác rõ ràng. |

## 2.3 Requirement Traceability Matrix

| Requirement | Test Case |
| --- | --- |
| REQ01 | `BB-TC01..BB-TC09`, `WB-TC01..WB-TC02`, `AUTO-TC01` |
| REQ02 | `BB-TC10..BB-TC14`, `WB-TC03..WB-TC06`, `AUTO-TC02` |
| REQ03 | Theo dõi cùng luồng điều hướng sau đăng nhập của `REQ02` |
| REQ04 | `BB-TC15..BB-TC17`, `AUTO-TC03` |
| REQ05 | `BB-TC18..BB-TC21`, `WB-TC07..WB-TC09`, `AUTO-TC04` |
| REQ06 | Ghi nhận ở mức yêu cầu hệ thống, không kiểm thử chuyên sâu trong báo cáo này |
| REQ07 | `BB-TC22..BB-TC24`, `AUTO-TC05` |
| REQ08 | `BB-TC25..BB-TC28`, `WB-TC10..WB-TC12`, `AUTO-TC06` |

RTM chi tiết được trình bày trong file [`requirement-traceability-matrix.csv`](/e:/phat_trien_ung_dung_voi_j2ee/NewsWebsite/docs/kt-dbclpm/requirement-traceability-matrix.csv).

## 2.4 Test Plan

| Mục | Nội dung |
| --- | --- |
| Tên dự án | Website tin tức |
| Mục tiêu kiểm thử | Xác nhận `6` chức năng được chọn hoạt động đúng yêu cầu; áp dụng đủ kỹ thuật Black-box theo rubric; lựa chọn `4` đoạn chương trình để phân tích White-box; chuẩn bị tối thiểu `4` công cụ automation cho demo học phần. |
| Phạm vi kiểm thử | Kiểm thử chuyên sâu `REQ01`, `REQ02`, `REQ04`, `REQ05`, `REQ07`, `REQ08`. `REQ03` và `REQ06` chỉ được ghi nhận ở mức yêu cầu hệ thống, không thiết kế sâu Black-box, White-box và automation trong phạm vi nhóm. |
| Chiến lược kiểm thử | Black-box: áp dụng `Phân lớp tương đương`, `Phân tích giá trị biên`, `Bảng điều kiện`, `Chuyển trạng thái`. White-box: vẽ `CFG` và xác định các mức bao phủ cho `AuthService.validateRegistrationForm`, `SecurityConfig.handleSuccessRedirect`, `ArticleWorkflowService.submitForReview`, `CommentService.createComment`. Automation: dùng `Selenium IDE`, `Playwright`, `Cypress`, `Selenium WebDriver` để mô tả các kịch bản tự động tương ứng với các chức năng được chọn. |
| Môi trường | Windows 11; Java 21; Spring Boot; MySQL cho môi trường local; H2 cho test tự động; trình duyệt Chrome và Edge. |
| Lịch trình | Tuần 1: chốt yêu cầu và Test Plan. Tuần 2: thiết kế Black-box, White-box và CFG. Tuần 3: thực hiện test case, ghi nhận bug. Tuần 4: hoàn thiện automation testing và tổng hợp báo cáo. |
| Phân công | Thành viên 1: `REQ01`, `REQ02`. Thành viên 2: `REQ04`, `REQ05`. Thành viên 3: `REQ07`, `REQ08`. |
