# Coverage Summary

## Lần chạy gần nhất

- Thời điểm: `2026-04-09 17:56 (+07:00)`
- Lệnh: `mvnw.cmd verify`
- Report HTML: `target/site/jacoco/index.html`
- Report XML: `target/site/jacoco/jacoco.xml`

## Overall Coverage

| Counter | Covered | Missed | Coverage |
| --- | --- | --- | --- |
| Instruction | 2822 | 1982 | 58.7% |
| Line | 650 | 436 | 59.9% |
| Branch | 158 | 208 | 43.2% |

## White-box Focus Methods

| Method | Line | Branch | Nhận xét |
| --- | --- | --- | --- |
| `AuthService.validateRegistrationForm` | 10/10 = 100% | 10/14 = 71.4% | Bao phủ toàn bộ statement, còn thiếu một số tổ hợp branch hiếm |
| `UserProfileService.validateChangePasswordForm` | 9/9 = 100% | 7/10 = 70.0% | Bao phủ đầy đủ statement, còn branch do tổ hợp trường rỗng |
| `ArticleWorkflowService.submitForReview` | 6/6 = 100% | 4/4 = 100% | Đạt trọn vẹn cho luồng thành công và lỗi |
| `CommentModerationService.getPendingComment` | 5/5 = 100% | 2/2 = 100% | Đạt trọn vẹn cho pending và invalid state |

## Ý nghĩa đối với báo cáo

- 4 phương thức White-box trọng tâm đã có dữ liệu coverage thật từ JaCoCo.
- Hai phương thức workflow/moderation đã đạt `100% line` và `100% branch`.
- Hai phương thức validation đã đạt `100% line`, phù hợp để giải thích thêm bằng CFG và condition analysis trong báo cáo.
