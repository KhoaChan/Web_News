# BÁO CÁO ĐỒ ÁN

## WEBSITE TIN TỨC

Ngành: Công nghệ thông tin  
Học phần: Phát triển ứng dụng với J2EE

---

## LỜI CAM ĐOAN

Bài báo cáo đồ án với đề tài "Website tin tức" là kết quả học tập, nghiên cứu và triển khai thực tế của nhóm trong quá trình học học phần Phát triển ứng dụng với J2EE. Toàn bộ nội dung trình bày trong báo cáo được xây dựng trên cơ sở phân tích bài toán, thiết kế hệ thống, cài đặt chương trình và kiểm thử ứng dụng. Các tài liệu tham khảo, công nghệ sử dụng và các nội dung trích dẫn đều được ghi rõ nguồn gốc. Nhóm xin chịu trách nhiệm về tính trung thực của báo cáo này.

---

## LỜI MỞ ĐẦU

Trong bối cảnh công nghệ thông tin phát triển mạnh mẽ, việc tiếp cận tin tức trên nền tảng số đã trở thành nhu cầu thường xuyên của người dùng. Một hệ thống website tin tức hiện đại không chỉ đơn thuần hiển thị bài viết mà còn cần đáp ứng nhiều yêu cầu như quản lý nội dung, kiểm duyệt, phân quyền người dùng, theo dõi tương tác độc giả, tìm kiếm và tối ưu trải nghiệm sử dụng trên môi trường web.

Xuất phát từ yêu cầu thực tế đó, nhóm lựa chọn đề tài "Website tin tức" nhằm xây dựng một hệ thống web có cấu trúc rõ ràng, có quy trình biên tập nội dung, có khả năng phân quyền và mô phỏng tương đối đầy đủ hoạt động của một hệ thống quản trị nội dung tin tức. Đề tài giúp vận dụng tổng hợp kiến thức về Java web, Spring Boot, MVC, bảo mật, ORM, template engine và cơ sở dữ liệu quan hệ.

---

## MỤC LỤC GỢI Ý

1. Chương 1: Tổng quan  
2. Chương 2: Cơ sở lý thuyết  
3. Chương 3: Phân tích, thiết kế và cài đặt hệ thống  
4. Chương 4: Kết luận và hướng phát triển  
5. Tài liệu tham khảo  
6. Phụ lục

---

## DANH MỤC CÁC KÝ HIỆU, CHỮ VIẾT TẮT

| Từ viết tắt | Ý nghĩa |
|---|---|
| J2EE | Java 2 Enterprise Edition |
| MVC | Model - View - Controller |
| ORM | Object Relational Mapping |
| JPA | Java Persistence API |
| UI | User Interface |
| UX | User Experience |
| CRUD | Create, Read, Update, Delete |
| API | Application Programming Interface |
| DBMS | Database Management System |
| URL | Uniform Resource Locator |
| AI | Artificial Intelligence |

---

# CHƯƠNG 1. TỔNG QUAN

## 1.1 Lý do chọn đề tài

### 1.1.1 Bối cảnh thực tế

Trong thời đại số, nhu cầu cập nhật tin tức của người dùng diễn ra liên tục trên nhiều thiết bị khác nhau. Người đọc không chỉ mong muốn tiếp cận thông tin nhanh mà còn yêu cầu hệ thống phải dễ tra cứu, giao diện rõ ràng, nội dung được tổ chức hợp lý và có độ tin cậy cao. Bên cạnh đó, các hệ thống tin tức hiện đại còn cần hỗ trợ quản lý nội dung, phân công vai trò, duyệt bài và kiểm soát bình luận nhằm đảm bảo chất lượng thông tin trước khi công khai.

Thực tế cho thấy, một website tin tức hoàn chỉnh thường có nhiều thành phần hơn so với một website hiển thị bài viết thông thường. Ngoài phần giao diện công khai dành cho độc giả, hệ thống còn cần có khu vực quản trị nội dung cho tác giả, biên tập viên và quản trị viên. Việc phân chia vai trò và quy trình làm việc rõ ràng giúp nội dung được kiểm soát tốt hơn, hạn chế sai sót và phản ánh đúng mô hình vận hành của một hệ thống tin tức thực tế.

### 1.1.2 Nhu cầu xây dựng hệ thống

Từ các yêu cầu trên, việc xây dựng một hệ thống website tin tức là một đề tài có tính ứng dụng cao, phù hợp với định hướng của học phần Phát triển ứng dụng với J2EE. Đề tài cho phép nhóm vận dụng đồng thời các kiến thức về:

- Phân tích yêu cầu hệ thống
- Thiết kế kiến trúc ứng dụng web
- Xây dựng giao diện động với template engine
- Quản lý xác thực và phân quyền
- Kết nối và thao tác với cơ sở dữ liệu
- Tổ chức quy trình nghiệp vụ nhiều vai trò
- Kiểm thử và đánh giá hệ thống

Đề tài còn có giá trị thực tiễn vì mô phỏng tương đối đầy đủ một mô hình quản trị nội dung tin tức thu nhỏ, có thể dùng làm nền tảng để tiếp tục mở rộng trong tương lai.

## 1.2 Mục tiêu đề tài

Mục tiêu chính của đề tài là xây dựng một hệ thống website tin tức hoạt động trên nền tảng web, hỗ trợ quản lý và xuất bản bài viết theo quy trình rõ ràng, đồng thời cung cấp giao diện thuận tiện cho người dùng tra cứu và theo dõi nội dung.

Các mục tiêu cụ thể bao gồm:

- Xây dựng giao diện công khai cho phép xem danh sách bài viết, xem chi tiết bài viết, tìm kiếm và lọc tin tức theo chuyên mục
- Xây dựng chức năng đăng ký, đăng nhập, quản lý hồ sơ cá nhân và đổi mật khẩu
- Xây dựng khu vực backoffice cho quản trị viên, biên tập viên và tác giả
- Xây dựng quy trình xử lý bài viết theo các trạng thái: bản nháp, chờ duyệt, yêu cầu chỉnh sửa, đã xuất bản và đã hủy
- Xây dựng chức năng bình luận và kiểm duyệt bình luận trước khi hiển thị công khai
- Hỗ trợ người dùng lưu bài viết, theo dõi lịch sử bài đã xem và quản lý bình luận cá nhân
- Ứng dụng các công nghệ phù hợp như Spring Boot, Spring MVC, Spring Security, Spring Data JPA, Thymeleaf, MySQL và Cloudinary
- Tích hợp trợ lý AI cho việc tóm tắt nội dung, hỗ trợ tìm bài viết và gợi ý thông tin trong giao diện web

## 1.3 Đối tượng và phạm vi nghiên cứu

### 1.3.1 Đối tượng nghiên cứu

Đối tượng nghiên cứu của đề tài là quá trình xây dựng một hệ thống website tin tức theo mô hình nhiều vai trò người dùng. Hệ thống tập trung vào các thành phần chính:

- Quản lý bài viết
- Quản lý chuyên mục
- Quản lý tài khoản người dùng
- Quản lý bình luận
- Phân quyền truy cập
- Theo dõi hoạt động người đọc
- Hỗ trợ tìm kiếm và tương tác AI trên nền web

### 1.3.2 Phạm vi nghiên cứu

Phạm vi thực hiện của đề tài bao gồm:

- Xây dựng hệ thống website tin tức chạy trên nền tảng web
- Sử dụng Java 21, Spring Boot, Spring MVC, Spring Security, Spring Data JPA, Thymeleaf và MySQL
- Hỗ trợ người đọc xem tin, tìm kiếm, lọc theo chuyên mục, bình luận, lưu bài viết và xem lịch sử đọc
- Xây dựng khu vực backoffice cho quản trị viên, biên tập viên và tác giả
- Hỗ trợ upload ảnh bài viết thông qua Cloudinary
- Tích hợp trợ lý AI ở giao diện công khai để hỗ trợ tóm tắt và tìm kiếm bài viết

Đề tài chưa tập trung chuyên sâu vào:

- Triển khai production với hạ tầng phân tán thực tế
- Tối ưu SEO chuyên sâu
- Hệ thống quảng cáo, thanh toán hoặc gợi ý thông minh ở quy mô lớn
- Ứng dụng di động native riêng

## 1.4 Phương pháp thực hiện

Để thực hiện đề tài, nhóm tiến hành theo các bước sau:

### 1.4.1 Khảo sát và xác định yêu cầu

Nhóm phân tích bài toán quản lý tin tức, xác định các tác nhân tham gia như người dùng thường, tác giả, biên tập viên và quản trị viên. Từ đó xây dựng tập chức năng tương ứng cho từng vai trò.

### 1.4.2 Phân tích và thiết kế hệ thống

Ở giai đoạn này, nhóm thiết kế:

- Mô hình dữ liệu
- Các thực thể chính
- Mối quan hệ giữa thực thể
- Luồng nghiệp vụ
- Kiến trúc tổng thể theo mô hình MVC

### 1.4.3 Cài đặt hệ thống

Hệ thống được triển khai bằng Spring Boot theo hướng phân chia theo tính năng, gồm các phân hệ:

- Public site
- Authentication
- User management
- Article management
- Editorial workflow
- Comment moderation
- AI assistant

### 1.4.4 Kiểm thử và đánh giá

Sau khi hoàn thiện, hệ thống được kiểm tra các chức năng chính như:

- Đăng ký, đăng nhập
- Quản lý bài viết
- Duyệt bài
- Kiểm duyệt bình luận
- Tìm kiếm bài viết
- Lưu bài viết
- Quản lý hồ sơ
- Trợ lý AI

## 1.5 Cấu trúc báo cáo

Báo cáo được chia thành bốn chương:

- Chương 1: Tổng quan về đề tài
- Chương 2: Cơ sở lý thuyết
- Chương 3: Phân tích, thiết kế và cài đặt hệ thống
- Chương 4: Kết luận và hướng phát triển

---

# CHƯƠNG 2. CƠ SỞ LÝ THUYẾT

## 2.1 Tổng quan về nền tảng phát triển ứng dụng web

Ứng dụng web hiện đại không chỉ dừng ở mức hiển thị giao diện mà còn cần xử lý nghiệp vụ, bảo mật, truy xuất dữ liệu, phân quyền và hỗ trợ mở rộng. Trong đề tài này, nhóm lựa chọn Spring Boot làm nền tảng chính vì đây là framework mạnh, phổ biến và phù hợp với hướng phát triển ứng dụng web Java hiện đại.

Spring Boot giúp giảm bớt khối lượng cấu hình ban đầu, hỗ trợ tích hợp nhanh nhiều thành phần như Spring MVC, Spring Security, JPA, Thymeleaf và các thư viện khác. Nhờ đó, nhóm có thể tập trung nhiều hơn vào việc xây dựng nghiệp vụ của hệ thống website tin tức.

## 2.2 Kiến trúc MVC trong Spring Boot

Mô hình MVC gồm:

- Model: biểu diễn dữ liệu và nghiệp vụ
- View: giao diện hiển thị
- Controller: tiếp nhận request và điều phối xử lý

Trong hệ thống website tin tức:

- Controller nhận yêu cầu từ người dùng
- Service xử lý nghiệp vụ
- Repository thao tác với cơ sở dữ liệu
- Thymeleaf render giao diện HTML

Nhờ áp dụng MVC, hệ thống có cấu trúc rõ ràng, dễ kiểm soát luồng xử lý và thuận lợi cho bảo trì.

## 2.3 Spring Data JPA và Hibernate

Spring Data JPA hỗ trợ thao tác cơ sở dữ liệu theo hướng đối tượng. Hibernate đóng vai trò ORM giúp ánh xạ giữa class Java và bảng dữ liệu.

Trong dự án, các entity chính gồm:

- User
- Article
- Category
- Comment
- SavedArticle
- ViewedArticle

Ưu điểm của cách tiếp cận này:

- Giảm số lượng câu lệnh SQL thủ công
- Dễ tổ chức truy vấn theo repository
- Hỗ trợ phân trang và sắp xếp
- Dễ mở rộng thêm thực thể trong tương lai

## 2.4 Spring Security và phân quyền người dùng

Spring Security được sử dụng để:

- Xác thực người dùng
- Phân quyền truy cập theo role
- Bảo vệ các đường dẫn hệ thống

Các vai trò trong hệ thống:

- `USER`: người dùng đọc tin
- `AUTHOR`: tác giả
- `EDITOR`: biên tập viên
- `ADMIN`: quản trị viên

Các quyền được phân tách rõ ràng theo từng nhóm chức năng, đảm bảo hệ thống vận hành đúng nghiệp vụ.

## 2.5 Thymeleaf, Bootstrap và CKEditor

### 2.5.1 Thymeleaf

Thymeleaf là template engine được sử dụng để render giao diện động phía server. Công nghệ này cho phép kết hợp dữ liệu từ backend vào HTML một cách thuận tiện.

### 2.5.2 Bootstrap

Bootstrap hỗ trợ xây dựng giao diện responsive, giúp hệ thống hiển thị tốt trên desktop và mobile, đồng thời rút ngắn thời gian thiết kế giao diện.

### 2.5.3 CKEditor

CKEditor được dùng để hỗ trợ nhập nội dung bài viết dưới dạng rich text. Điều này giúp tác giả và quản trị viên biên soạn nội dung thuận tiện hơn.

## 2.6 MySQL

MySQL là hệ quản trị cơ sở dữ liệu quan hệ được sử dụng để lưu trữ dữ liệu chính của hệ thống. Việc kết hợp MySQL với JPA giúp hệ thống:

- Lưu trữ dữ liệu ổn định
- Truy vấn hiệu quả
- Hỗ trợ lọc, phân trang và thống kê

## 2.7 Cloudinary

Cloudinary được dùng để lưu ảnh bài viết. Khi người dùng upload ảnh, hệ thống gửi file lên Cloudinary và lưu URL ảnh về cơ sở dữ liệu. Cách tiếp cận này giúp hệ thống không phụ thuộc vào bộ nhớ cục bộ.

## 2.8 AI Assistant trong website

Hệ thống được mở rộng thêm trợ lý AI nhằm nâng cao trải nghiệm người dùng. Trợ lý AI hoạt động trên giao diện công khai và hỗ trợ:

- Tóm tắt bài viết đang xem
- Gợi ý bài viết liên quan
- Tìm kiếm bài viết bằng ngôn ngữ tự nhiên
- Hỗ trợ đọc lại nội dung tóm tắt

Kiến trúc AI được tổ chức theo hướng đa provider, cho phép cấu hình sử dụng OpenAI hoặc Gemini thông qua cấu hình môi trường, tạo điều kiện thuận lợi cho việc thay đổi nhà cung cấp mô hình.

---

# CHƯƠNG 3. PHÂN TÍCH, THIẾT KẾ VÀ CÀI ĐẶT HỆ THỐNG

## 3.1 Phân tích yêu cầu hệ thống

### 3.1.1 Yêu cầu chức năng

Hệ thống cần đáp ứng các nhóm chức năng sau:

#### Đối với người dùng công khai

- Xem danh sách bài viết
- Xem bài viết theo chuyên mục
- Xem chi tiết bài viết
- Tìm kiếm bài viết
- Xem danh sách bài xem nhiều
- Sử dụng trợ lý AI

#### Đối với người dùng đã đăng nhập

- Đăng nhập, đăng xuất
- Cập nhật hồ sơ
- Đổi mật khẩu
- Bình luận bài viết
- Lưu bài viết
- Xem lịch sử bài đã xem
- Xem danh sách bài đã lưu
- Xem danh sách bình luận cá nhân

#### Đối với tác giả

- Tạo bài viết mới
- Chỉnh sửa bài viết của mình
- Lưu bài dưới dạng bản nháp
- Gửi bài đi duyệt
- Hủy bài trong quy trình

#### Đối với biên tập viên

- Xem hàng đợi duyệt bài
- Xuất bản bài viết
- Yêu cầu chỉnh sửa
- Hủy bài
- Duyệt và từ chối bình luận

#### Đối với quản trị viên

- Quản lý người dùng
- Quản lý chuyên mục
- Quản lý bài viết
- Truy cập toàn bộ khu vực backoffice

### 3.1.2 Yêu cầu phi chức năng

- Hệ thống có giao diện rõ ràng, dễ sử dụng
- Thời gian phản hồi hợp lý với các chức năng cơ bản
- Phân quyền đúng theo vai trò
- Dữ liệu được tổ chức nhất quán
- Dễ bảo trì và mở rộng
- Có kiểm thử cho các chức năng quan trọng

## 3.2 Phân tích tác nhân và use case

### 3.2.1 Tác nhân

- Khách truy cập
- Người dùng đã đăng nhập
- Tác giả
- Biên tập viên
- Quản trị viên

### 3.2.2 Các use case chính

- Đăng ký tài khoản
- Đăng nhập
- Xem bài viết
- Tìm kiếm bài viết
- Bình luận bài viết
- Lưu bài viết
- Quản lý hồ sơ
- Tạo bài viết
- Duyệt bài viết
- Kiểm duyệt bình luận
- Quản lý người dùng
- Quản lý chuyên mục
- Tương tác với AI assistant

## 3.3 Thiết kế kiến trúc hệ thống

## 3.3.1 Kiến trúc tổng thể

Hệ thống được xây dựng theo mô hình modular monolith, tổ chức theo tính năng. Các package chính bao gồm:

- `article`
- `category`
- `comment`
- `user`
- `common`
- `config`
- `ai`

Ưu điểm của cách tổ chức này:

- Dễ theo dõi luồng nghiệp vụ
- Dễ mở rộng chức năng
- Hạn chế phụ thuộc chéo giữa các thành phần

### 3.3.2 Sơ đồ phân tầng

Hệ thống được tổ chức theo các tầng:

- Presentation layer: Controller + Thymeleaf
- Service layer: xử lý nghiệp vụ
- Repository layer: truy xuất dữ liệu
- Database layer: MySQL

## 3.4 Thiết kế cơ sở dữ liệu

### 3.4.1 Các bảng chính

#### Bảng `users`

Lưu thông tin tài khoản người dùng:

- id
- username
- email
- password
- full_name
- avatar_url
- role
- enabled
- created_at
- updated_at

#### Bảng `categories`

Lưu thông tin chuyên mục:

- id
- name
- slug
- description

#### Bảng `articles`

Lưu thông tin bài viết:

- id
- title
- slug
- summary
- content
- thumbnail_url
- status
- review_note
- published_at
- views
- author_id
- category_id
- created_at
- updated_at

#### Bảng `comments`

Lưu bình luận:

- id
- article_id
- user_id
- commenter_name
- content
- status
- created_at

#### Bảng `saved_articles`

Lưu danh sách bài viết đã lưu:

- id
- user_id
- article_id
- saved_at

#### Bảng `viewed_articles`

Lưu lịch sử đọc bài:

- id
- user_id
- article_id
- viewed_at

### 3.4.2 Quan hệ giữa các bảng

- Một `Category` có nhiều `Article`
- Một `User` có thể là tác giả của nhiều `Article`
- Một `Article` có nhiều `Comment`
- Một `User` có nhiều `SavedArticle`
- Một `User` có nhiều `ViewedArticle`

## 3.5 Thiết kế nghiệp vụ

### 3.5.1 Workflow bài viết

Quy trình xử lý bài viết gồm các trạng thái:

- `DRAFT`
- `IN_REVIEW`
- `CHANGES_REQUESTED`
- `PUBLISHED`
- `CANCELLED`

Luồng xử lý:

1. Tác giả tạo bài viết và lưu bản nháp
2. Tác giả gửi bài đi duyệt
3. Biên tập viên xem xét bài
4. Biên tập viên có thể:
   - xuất bản
   - yêu cầu chỉnh sửa
   - hủy bài

### 3.5.2 Workflow bình luận

Trạng thái bình luận:

- `PENDING`
- `APPROVED`
- `REJECTED`

Người dùng gửi bình luận, hệ thống lưu dưới dạng chờ duyệt. Chỉ bình luận được duyệt mới hiển thị công khai.

## 3.6 Cài đặt các chức năng chính

### 3.6.1 Public site

Phân hệ công khai cho phép:

- Hiển thị bài viết mới nhất
- Xem bài viết theo chuyên mục
- Xem bài chi tiết
- Tìm kiếm và lọc bài viết
- Hiển thị bài xem nhiều

Các route tiêu biểu:

- `/`
- `/search`
- `/category/{slug}`
- `/article/{slug}`

### 3.6.2 Authentication và quản lý tài khoản

Hệ thống hỗ trợ:

- Đăng ký
- Đăng nhập
- Đăng xuất
- Cập nhật hồ sơ
- Đổi mật khẩu

Việc xác thực và điều hướng sau đăng nhập được xử lý bằng Spring Security.

### 3.6.3 Quản lý bài viết

Tác giả và quản trị viên có thể:

- Tạo bài viết
- Cập nhật bài viết
- Upload thumbnail
- Lưu bài nháp
- Gửi bài duyệt

### 3.6.4 Biên tập nội dung

Biên tập viên có thể:

- Xem danh sách bài chờ duyệt
- Duyệt và xuất bản
- Yêu cầu tác giả chỉnh sửa
- Hủy bài

### 3.6.5 Quản lý chuyên mục

Quản trị viên có thể:

- Thêm chuyên mục
- Cập nhật chuyên mục
- Xem danh sách chuyên mục

### 3.6.6 Quản lý người dùng

Quản trị viên có thể:

- Xem danh sách người dùng
- Thêm người dùng
- Cập nhật thông tin người dùng
- Bật hoặc tắt trạng thái hoạt động

### 3.6.7 Tương tác độc giả

Người dùng đã đăng nhập có thể:

- Lưu bài viết
- Xem danh sách bài đã lưu
- Theo dõi lịch sử bài đã xem
- Xem bình luận cá nhân

### 3.6.8 AI assistant

AI assistant được tích hợp dưới dạng widget nổi trên giao diện công khai. Các chức năng chính:

- Tóm tắt bài viết hiện tại
- Gợi ý bài viết liên quan
- Tìm kiếm bài viết bằng câu hỏi tự nhiên
- Hỗ trợ phát nội dung tóm tắt

Hệ thống hiện hỗ trợ kiến trúc đa provider:

- OpenAI
- Gemini

Người phát triển có thể chuyển provider chỉ bằng cấu hình môi trường.

## 3.7 Giao diện hệ thống

Trong chương này khi đưa vào báo cáo Word, cần chèn các hình ảnh minh họa cho:

- Trang chủ
- Trang chi tiết bài viết
- Trang tìm kiếm
- Trang đăng nhập và đăng ký
- Trang hồ sơ người dùng
- Dashboard quản trị viên
- Dashboard biên tập viên
- Dashboard tác giả
- Widget AI assistant

Gợi ý chú thích hình:

- Hình 3.1: Giao diện trang chủ hệ thống
- Hình 3.2: Giao diện chi tiết bài viết
- Hình 3.3: Giao diện tìm kiếm bài viết
- Hình 3.4: Giao diện quản trị người dùng
- Hình 3.5: Giao diện quy trình duyệt bài
- Hình 3.6: Widget trợ lý AI

## 3.8 Kiểm thử hệ thống

Hệ thống đã xây dựng bộ kiểm thử cho nhiều thành phần:

- Controller test
- Service test
- Security integration test
- Auth flow integration test

Các nội dung đã kiểm thử bao gồm:

- Điều hướng đăng nhập theo role
- Phân quyền truy cập
- Tạo và cập nhật bài viết
- Workflow duyệt bài
- Tạo bình luận và kiểm duyệt bình luận
- Hoạt động lưu bài và xem bài
- API AI assistant

Kết quả kiểm thử cho thấy các chức năng cốt lõi của hệ thống hoạt động ổn định trong phạm vi bài toán đồ án.

## 3.9 Đánh giá kết quả đạt được

Sau khi hoàn thiện, hệ thống đã đáp ứng được phần lớn mục tiêu đề ra:

- Xây dựng được website tin tức hoạt động theo mô hình nhiều vai trò
- Có public site và backoffice tách biệt
- Có phân quyền rõ ràng
- Có workflow bài viết
- Có kiểm duyệt bình luận
- Có theo dõi hoạt động độc giả
- Có hỗ trợ AI assistant

Những điểm nổi bật:

- Kiến trúc rõ ràng theo tính năng
- Dễ mở rộng
- Có kiểm thử
- Giao diện đủ dùng cho demo và báo cáo đồ án

---

# CHƯƠNG 4. KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN

## 4.1 Kết luận

Đề tài "Website tin tức" đã xây dựng được một hệ thống web tương đối hoàn chỉnh, đáp ứng các chức năng quan trọng của một mô hình quản trị nội dung tin tức thu nhỏ. Hệ thống không chỉ phục vụ nhu cầu đọc tin của người dùng mà còn hỗ trợ quản trị viên, tác giả và biên tập viên xử lý nội dung theo quy trình rõ ràng.

Thông qua quá trình thực hiện đề tài, nhóm đã vận dụng được nhiều kiến thức quan trọng trong phát triển ứng dụng web Java như:

- Thiết kế hệ thống theo mô hình MVC
- Xây dựng ứng dụng với Spring Boot
- Quản lý dữ liệu bằng Spring Data JPA và MySQL
- Bảo mật và phân quyền bằng Spring Security
- Xây dựng giao diện bằng Thymeleaf và Bootstrap
- Tích hợp lưu trữ ảnh với Cloudinary
- Tích hợp AI assistant theo hướng đa provider

Đề tài có giá trị thực tiễn và là nền tảng tốt để tiếp tục phát triển thành một hệ thống hoàn chỉnh hơn trong tương lai.

## 4.2 Hạn chế

Mặc dù đã đạt được nhiều kết quả, hệ thống vẫn còn một số hạn chế:

- Chưa triển khai production đầy đủ
- Chưa tối ưu SEO chuyên sâu
- Chưa có dashboard thống kê nâng cao
- Chưa có email hoặc notification workflow
- Chưa có ứng dụng mobile riêng
- AI assistant vẫn phụ thuộc vào quota của nhà cung cấp mô hình

## 4.3 Hướng phát triển

Trong tương lai, hệ thống có thể tiếp tục mở rộng theo các hướng:

- Bổ sung dashboard thống kê doanh thu, lượt xem và hành vi người đọc
- Bổ sung email thông báo khi bài viết được duyệt hoặc bị yêu cầu chỉnh sửa
- Bổ sung phân tích từ khóa và SEO cho bài viết
- Bổ sung hệ thống gợi ý bài viết thông minh hơn
- Tích hợp thêm nhiều AI provider hoặc fallback tự động
- Tách CSS và JavaScript thành các module độc lập để dễ bảo trì
- Triển khai hệ thống trên môi trường cloud thực tế

---

## TÀI LIỆU THAM KHẢO

1. Tài liệu chính thức của Spring Boot  
2. Tài liệu chính thức của Spring Security  
3. Tài liệu chính thức của Spring Data JPA  
4. Tài liệu chính thức của Thymeleaf  
5. Tài liệu chính thức của MySQL  
6. Tài liệu chính thức của Cloudinary  
7. Tài liệu chính thức của OpenAI API  
8. Tài liệu chính thức của Google Gemini API

---

## PHỤ LỤC A. DANH SÁCH CHỨC NĂNG HỆ THỐNG

### A.1 Chức năng công khai

- Xem trang chủ
- Xem bài viết theo chuyên mục
- Xem chi tiết bài viết
- Tìm kiếm bài viết
- Xem bài viết nhiều lượt xem
- Sử dụng AI assistant

### A.2 Chức năng người dùng

- Đăng ký
- Đăng nhập
- Đăng xuất
- Cập nhật hồ sơ
- Đổi mật khẩu
- Bình luận
- Lưu bài viết
- Xem bài đã lưu
- Xem bài đã xem
- Xem bình luận cá nhân

### A.3 Chức năng tác giả

- Tạo bài viết
- Sửa bài viết của mình
- Lưu bản nháp
- Gửi duyệt
- Hủy bài

### A.4 Chức năng biên tập viên

- Xem hàng đợi duyệt bài
- Duyệt và xuất bản bài
- Yêu cầu chỉnh sửa
- Hủy bài
- Duyệt bình luận
- Từ chối bình luận

### A.5 Chức năng quản trị viên

- Quản lý người dùng
- Quản lý chuyên mục
- Quản lý bài viết
- Điều hướng vào các workspace của editor và author

---

## PHỤ LỤC B. MỘT SỐ ROUTE QUAN TRỌNG

### Public

- `/`
- `/search`
- `/category/{slug}`
- `/article/{slug}`
- `/api/ai/chat`

### Auth / User

- `/login`
- `/register`
- `/profile`
- `/profile/comments`
- `/profile/saved`
- `/profile/viewed`

### Admin

- `/admin`
- `/admin/users`
- `/admin/categories`

### Author

- `/author`
- `/author/article/create`
- `/author/article/edit/{id}`

### Editor

- `/editor`
- `/editor/article/review/{id}`
- `/editor/comments`

---

## GHI CHÚ SỬ DỤNG

Tài liệu này là bản nháp hoàn chỉnh để tiếp tục dàn trang vào file Word báo cáo chính thức. Khi hoàn thiện báo cáo cuối cùng, cần:

- Chuẩn hóa font, cỡ chữ, giãn dòng và đánh số mục
- Chèn ảnh giao diện thực tế từ hệ thống
- Chèn mục lục tự động trong Word
- Bổ sung số bảng, số hình, chú thích hình
- Cập nhật tên thành viên, MSSV và giảng viên theo mẫu trường
