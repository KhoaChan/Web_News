# Chương 3 - Thiết kế Test Case

## 3.1 Test Scenario

| Scenario ID | Module | Description |
| --- | --- | --- |
| SCN01 | REQ01 | Người dùng đăng ký tài khoản độc giả mới |
| SCN02 | REQ02 | Người dùng đăng nhập bằng Username và Password hợp lệ |
| SCN03 | REQ04 | Khách truy cập tìm kiếm bài viết bằng từ khóa |
| SCN04 | REQ05 | Tác giả tạo, sửa, lưu nháp và gửi bài đi duyệt |
| SCN05 | REQ07 | Biên tập viên duyệt bài viết từ "Chờ duyệt" sang "Đã xuất bản" |
| SCN06 | REQ08 | Người dùng đã đăng nhập gửi bình luận vào bài viết đã xuất bản |

## 3.2 Black-box Test Case

Bộ test case Black-box được lập theo đúng mẫu bắt buộc `Test Case ID | Chức năng | Mô tả | Dữ liệu vào | KQ mong đợi | KQ thực tế | Trạng thái` và được lưu tại file [`black-box-test-cases.csv`](/e:/phat_trien_ung_dung_voi_j2ee/NewsWebsite/docs/kt-dbclpm/black-box-test-cases.csv).

| Kỹ thuật | Chức năng áp dụng | Mục tiêu |
| --- | --- | --- |
| Phân lớp tương đương | `REQ01`, `REQ02`, `REQ04`, `REQ08` | Tách lớp dữ liệu hợp lệ và không hợp lệ theo yêu cầu nghiệp vụ |
| Phân tích giá trị biên | `REQ01`, `REQ08` | Kiểm tra ngưỡng min/max của dữ liệu đầu vào có ràng buộc độ dài |
| Bảng điều kiện | `REQ07` Duyệt bài | Kiểm tra quyết định xử lý theo vai trò và trạng thái bài viết |
| Chuyển trạng thái | `REQ05` Workflow của tác giả | Kiểm tra chuyển trạng thái từ `DRAFT` hoặc `CHANGES_REQUESTED` sang `IN_REVIEW` |

Ngoài `4` kỹ thuật đại diện trên, nhóm vẫn bổ sung Black-box test case cho `REQ02` và `REQ04` để bảo đảm đủ `6` chức năng được chọn đều có test case trong báo cáo.

### 3.2.1 Áp dụng cho REQ01 - Đăng ký tài khoản

#### Mô tả chức năng

Người dùng chưa đăng nhập truy cập màn hình đăng ký và nhập các trường `username`, `email`, `fullName`, `password`, `confirmPassword`. Khi dữ liệu hợp lệ, hệ thống tạo tài khoản độc giả mới và chuyển người dùng về màn hình đăng nhập với trạng thái đăng ký thành công.

Theo ràng buộc hiện tại của hệ thống:

- `username` là bắt buộc, dài từ `3` đến `50` ký tự, chỉ chứa chữ, số, dấu chấm, gạch dưới và gạch nối.
- `email` là bắt buộc, đúng định dạng email và không được trùng.
- `fullName` không bắt buộc, tối đa `150` ký tự.
- `password` là bắt buộc, dài từ `6` đến `100` ký tự.
- `confirmPassword` là bắt buộc và phải trùng với `password`.

#### Xác định phân lớp tương đương

| Thành phần kiểm thử | Lớp hợp lệ | Lớp không hợp lệ | Test case liên quan |
| --- | --- | --- | --- |
| Username | Đúng pattern, không trùng, đủ độ dài | Chứa ký tự không hợp lệ; trùng username | `BB-TC01`, `BB-TC02`, `BB-TC03` |
| Email | Đúng định dạng, không trùng | Trùng email | `BB-TC01`, `BB-TC04` |
| Confirm password | Trùng với password | Không trùng với password | `BB-TC01`, `BB-TC05` |
| Toàn bộ form đăng ký | Tất cả trường hợp lệ | Có ít nhất một trường vi phạm rule | `BB-TC01..BB-TC05` |

Như vậy, lớp tương đương hợp lệ của `REQ01` là trường hợp người dùng nhập đúng toàn bộ rule của form đăng ký. Các lớp tương đương không hợp lệ tập trung vào lỗi định dạng `username`, lỗi trùng `username/email` và lỗi không khớp `confirmPassword`.

#### Xác định giá trị biên

| Thuộc tính | Biên kiểm thử | Test case liên quan | Kết quả mong đợi |
| --- | --- | --- | --- |
| Username | `2` ký tự | `BB-TC06` | Không hợp lệ vì nhỏ hơn ngưỡng tối thiểu |
| Username | `3` ký tự | `BB-TC07` | Hợp lệ nếu thỏa mãn các rule còn lại |
| Password | `5` ký tự | `BB-TC08` | Không hợp lệ vì nhỏ hơn ngưỡng tối thiểu |
| Password | `6` ký tự | `BB-TC09` | Hợp lệ nếu thỏa mãn các rule còn lại |

Các test case trên cho thấy nhóm áp dụng cả `phân lớp tương đương` và `giá trị biên` trên cùng một chức năng, tương tự cách trình bày của bài mẫu nhưng bám đúng rule thực tế của hệ thống.

### 3.2.2 Áp dụng cho REQ02 - Đăng nhập

#### Mô tả chức năng

Người dùng nhập `username` và `password` tại màn hình đăng nhập. Khi thông tin xác thực đúng, hệ thống cho phép đăng nhập thành công. Nếu thông tin sai, hệ thống quay lại màn hình đăng nhập với trạng thái lỗi. Trong trường hợp người dùng đã đăng nhập mà vẫn truy cập `/login`, hệ thống điều hướng người dùng về khu vực phù hợp với vai trò hiện tại.

Lưu ý: `REQ02` chỉ mô tả thao tác đăng nhập. Hành vi điều hướng theo vai trò được liên hệ sang `REQ03`, nhưng trong quá trình kiểm thử nhóm vẫn theo dõi hậu điều kiện này ở các luồng đăng nhập thành công.

#### Xác định phân lớp tương đương

| Điều kiện kiểm thử | Lớp hợp lệ | Lớp không hợp lệ | Test case liên quan |
| --- | --- | --- | --- |
| Username và password | Cặp thông tin đúng của tài khoản tồn tại | Sai username; sai password; bỏ trống dữ liệu | `BB-TC10`, `BB-TC11`, `BB-TC12`, `BB-TC13` |
| Trạng thái phiên làm việc | Người dùng chưa đăng nhập truy cập `/login` | Người dùng đã đăng nhập nhưng vẫn truy cập `/login` | `BB-TC10..BB-TC13`, `BB-TC14` |

Từ bảng trên, lớp tương đương hợp lệ của `REQ02` là người dùng chưa đăng nhập và nhập đúng cặp `username/password`. Các lớp không hợp lệ gồm sai `username`, sai `password`, hoặc không nhập dữ liệu. Ngoài ra, trường hợp đã có phiên đăng nhập cũng được xem là một nhánh nghiệp vụ riêng cần kiểm thử.

#### Ghi chú về giá trị biên

Đối với chức năng đăng nhập, hệ thống hiện không đặc tả riêng ngưỡng min/max cho dữ liệu đầu vào tại màn hình `/login`. Vì vậy nhóm không xây dựng một bộ `giá trị biên` độc lập cho `REQ02` như với `REQ01`, mà ưu tiên `phân lớp tương đương` để kiểm tra tính đúng/sai của dữ liệu xác thực và trạng thái phiên làm việc.

#### Liên hệ với bộ test case

| Nhóm test case | Ý nghĩa |
| --- | --- |
| `BB-TC10` | Đăng nhập thành công với tài khoản hợp lệ |
| `BB-TC11` | Đăng nhập thất bại do sai mật khẩu |
| `BB-TC12` | Đăng nhập thất bại do sai username |
| `BB-TC13` | Đăng nhập thất bại do bỏ trống dữ liệu |
| `BB-TC14` | Người dùng đã đăng nhập truy cập lại `/login` và bị điều hướng về dashboard phù hợp |

## 3.3 White-box Testing

### Control Flow Graph

Phần White-box tập trung vào `4` đoạn chương trình đại diện theo đúng rubric môn học. CFG chi tiết được đặt tại file [`phu-luc-cfg.md`](/e:/phat_trien_ung_dung_voi_j2ee/NewsWebsite/docs/kt-dbclpm/phu-luc-cfg.md).

| Đoạn chương trình | Chức năng liên quan | Tài liệu CFG |
| --- | --- | --- |
| `AuthService.validateRegistrationForm` | `REQ01` | Phụ lục CFG - Mục 1 |
| `SecurityConfig.handleSuccessRedirect` | `REQ02` | Phụ lục CFG - Mục 2 |
| `ArticleWorkflowService.submitForReview` | `REQ05` | Phụ lục CFG - Mục 3 |
| `CommentService.createComment` | `REQ08` | Phụ lục CFG - Mục 4 |

### Coverage

Các test case White-box được trình bày theo đúng mẫu bắt buộc tại file [`white-box-test-cases.csv`](/e:/phat_trien_ung_dung_voi_j2ee/NewsWebsite/docs/kt-dbclpm/white-box-test-cases.csv).

| Đoạn chương trình | Mức độ bao phủ mục tiêu | Test case White-box |
| --- | --- | --- |
| `AuthService.validateRegistrationForm` | Phủ lệnh, phủ nhánh, phủ điều kiện | `WB-TC01`, `WB-TC02` |
| `SecurityConfig.handleSuccessRedirect` | Phủ lệnh, phủ nhánh, phủ đường đi chính | `WB-TC03`, `WB-TC04`, `WB-TC05`, `WB-TC06` |
| `ArticleWorkflowService.submitForReview` | Phủ lệnh, phủ nhánh, phủ đường đi | `WB-TC07`, `WB-TC08`, `WB-TC09` |
| `CommentService.createComment` | Phủ lệnh, phủ nhánh, phủ đường đi chính | `WB-TC10`, `WB-TC11`, `WB-TC12` |
