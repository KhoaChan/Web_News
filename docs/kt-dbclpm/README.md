# Hồ sơ KT&ĐBCLPM cho Website Tin tức

Thư mục này gom toàn bộ deliverable phục vụ báo cáo học phần Kiểm thử và Đảm bảo chất lượng phần mềm cho đề tài `Website tin tức`.

## Cấu trúc

- `01-tong-quan-he-thong.md`: Chương 1, giới thiệu hệ thống và phạm vi kiểm thử.
- `02-phan-tich-yeu-cau-va-test-plan.md`: Chương 2, FR/NFR, RTM và Test Plan.
- `03-thiet-ke-test-case.md`: Chương 3, thiết kế Black-box và White-box.
- `04-thuc-hien-kiem-thu.md`: Chương 4, thực thi kiểm thử, bug tracking và kết quả hiện tại.
- `phu-luc-cfg.md`: CFG và phân tích coverage cho 4 đoạn mã White-box.
- `phu-luc-automation.md`: ánh xạ 4 công cụ automation với kịch bản cụ thể.
- `requirement-traceability-matrix.csv`: ma trận truy vết yêu cầu.
- `black-box-test-cases.csv`: bộ test case Black-box.
- `white-box-test-cases.csv`: bộ test case White-box.
- `test-execution.csv`: log thực thi test.
- `bug-report.csv`: log lỗi/issue.
- `evidence/test-summary.md`: tóm tắt lần chạy test gần nhất.
- `evidence/coverage-summary.md`: tóm tắt coverage từ JaCoCo.

## Cách dùng

1. Hoàn thiện báo cáo Word/PDF bằng cách dùng trực tiếp nội dung các file chương 1-4.
2. Dùng các file `.csv` để dán vào biểu mẫu môn học hoặc import sang Excel.
3. Chạy `mvnw.cmd verify` để cập nhật test và JaCoCo cho phần White-box.
4. Dùng thư mục [`automation`](/e:/phat_trien_ung_dung_voi_j2ee/NewsWebsite/automation/README.md) để chạy các kịch bản Selenium IDE, Selenium WebDriver, Playwright và Cypress.

## Ghi chú

- Tài liệu này bám đúng đề tài `Website tin tức` trong PDF yêu cầu học phần.
- Phần thực thi thủ công vẫn cần bổ sung ảnh chụp màn hình khi nhóm chạy demo trên máy local.
- Các script automation được scaffold để dùng lại nhanh với profile `local` và tài khoản seed `admin/editor/author`.
