# Phụ lục - Control Flow Graph và Coverage

Các CFG dưới đây được dùng cho phần White-box testing trong báo cáo. Mỗi CFG tương ứng với một đoạn chương trình đại diện cho một chức năng được chọn để kiểm thử chuyên sâu.

## 1. `AuthService.validateRegistrationForm`

```mermaid
flowchart TD
    A([Start]) --> B[Chuẩn hóa username và email]
    B --> C{Password và confirmPassword có dữ liệu?}
    C -- Không --> D{Username đã tồn tại?}
    C -- Có --> E{Password có trùng confirmPassword?}
    E -- Không --> F[Thêm lỗi confirmPassword]
    E -- Có --> D
    F --> D
    D -- Có --> G[Thêm lỗi username]
    D -- Không --> H{Email đã tồn tại?}
    G --> H
    H -- Có --> I[Thêm lỗi email]
    H -- Không --> J([End])
    I --> J
```

## 2. `SecurityConfig.handleSuccessRedirect`

```mermaid
flowchart TD
    A([Start]) --> B{Có ROLE_ADMIN?}
    B -- Có --> C[Redirect /admin]
    B -- Không --> D{Có ROLE_EDITOR?}
    D -- Có --> E[Redirect /editor]
    D -- Không --> F{Có ROLE_AUTHOR?}
    F -- Có --> G[Redirect /author]
    F -- Không --> H[Redirect /]
    C --> I([End])
    E --> I
    G --> I
    H --> I
```

## 3. `ArticleWorkflowService.submitForReview`

```mermaid
flowchart TD
    A([Start]) --> B[Tìm bài viết thuộc tác giả]
    B --> C{Status là DRAFT hoặc CHANGES_REQUESTED?}
    C -- Có --> D[Đổi status sang IN_REVIEW]
    D --> E[Xóa reviewNote]
    E --> F[Lưu bài viết]
    F --> G([End])
    C -- Không --> H[Ném InvalidOperationException]
    H --> G
```

## 4. `CommentService.createComment`

```mermaid
flowchart TD
    A([Start]) --> B[Tìm article theo articleId]
    B --> C{Article tồn tại?}
    C -- Không --> D[Ném ResourceNotFoundException]
    C -- Có --> E{Article có status PUBLISHED?}
    E -- Không --> F[Ném ResourceNotFoundException]
    E -- Có --> G[Tạo comment mới với status PENDING]
    G --> H{Có userId?}
    H -- Không --> I[Gán commenterName từ form]
    H -- Có --> J[Tải user và gán commenterName từ tài khoản]
    I --> K[Lưu comment]
    J --> K
    K --> L([End])
```

## 5. Coverage áp dụng

| Đoạn chương trình | Test case White-box | Mức bao phủ mục tiêu |
| --- | --- | --- |
| `AuthService.validateRegistrationForm` | `WB-TC01`, `WB-TC02` | Phủ lệnh, phủ nhánh, phủ điều kiện |
| `SecurityConfig.handleSuccessRedirect` | `WB-TC03`, `WB-TC04`, `WB-TC05`, `WB-TC06` | Phủ lệnh, phủ nhánh, phủ đường đi chính |
| `ArticleWorkflowService.submitForReview` | `WB-TC07`, `WB-TC08`, `WB-TC09` | Phủ lệnh, phủ nhánh, phủ đường đi |
| `CommentService.createComment` | `WB-TC10`, `WB-TC11`, `WB-TC12` | Phủ lệnh, phủ nhánh, phủ đường đi chính |
