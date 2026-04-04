# News

Ung dung web tin tuc xay dung bang Spring Boot theo mo hinh MVC, su dung Thymeleaf de render giao dien va MySQL de luu tru du lieu. Du an hien tai da vuot qua muc mini CMS co CRUD co ban va dang hoat dong nhu mot he thong demo newsroom co public site, auth, quan ly user, editorial workflow, comment moderation va backoffice theo role.

## Tong quan nhanh

Tu codebase hien tai, du an dang mo phong mot he thong tin tuc gom:

- Public site de doc bai viet, tim kiem, loc theo chuyen muc va gui binh luan.
- He thong dang nhap, dang ky, profile va doi mat khau.
- Backoffice dung chung layout cho `ADMIN`, `EDITOR`, `AUTHOR`.
- Workflow bien tap cho bai viet tu ban nhap den xuat ban.
- Moderation comment truoc khi hien cong khai.
- Upload thumbnail len Cloudinary.

Noi ngan gon: day la mot modular monolith theo feature, huong toi bai tap lon/do an demo ve he thong quan tri noi dung tin tuc.

## Cong nghe su dung

- Java 21
- Spring Boot 4.0.1
- Spring MVC
- Spring Data JPA
- Spring Security
- Spring Validation
- Thymeleaf
- Thymeleaf Extras Spring Security 6
- MySQL
- H2 cho test
- Lombok
- Bootstrap 5
- CKEditor 5
- Cloudinary

## Kien truc hien tai

Du an da duoc refactor theo huong chia theo feature:

```text
src/main/java/com/example/news
|- article     # Bai viet, public flow, admin flow, author flow, editor workflow
|- bootstrap   # Seed du lieu mau cho local/dev
|- category    # Quan ly chuyen muc
|- comment     # Comment va moderation
|- common      # Exception, web, storage
|- config      # Security, Cloudinary va cau hinh chung
|- user        # Auth, profile, user management, principal, form
```

Thu muc giao dien:

```text
src/main/resources/templates
|- admin       # Giao dien quan tri
|- author      # Giao dien tac gia
|- editor      # Giao dien bien tap
|- auth        # Login / register
|- user        # Profile
|- fragments   # Shared backoffice shell
|- error       # Trang loi
```

## Vai tro va quyen

He thong hien co 4 role:

- `ADMIN`: toan quyen, co the vao khu admin va dong thoi su dung cac workspace cua editor va author.
- `EDITOR`: duyet bai viet, yeu cau sua, huy bai, duyet comment.
- `AUTHOR`: tao bai viet, sua bai cua minh, gui bai di duyet, huy bai.
- `USER`: su dung public site, dang ky/dang nhap, cap nhat profile.

## Trang thai nghiep vu

### Trang thai bai viet

- `DRAFT`
- `IN_REVIEW`
- `CHANGES_REQUESTED`
- `PUBLISHED`
- `CANCELLED`

### Trang thai comment

- `PENDING`
- `APPROVED`
- `REJECTED`

Quy tac hien tai:

- Public site chi hien bai viet `PUBLISHED`.
- Comment moi duoc gui se vao `PENDING`.
- Chi comment `APPROVED` moi hien tren public site.

## Chuc nang hien co

### Public site

- Trang chu co phan trang.
- Tim kiem bai viet theo tu khoa.
- Loc bai viet theo chuyen muc.
- Xem chi tiet bai viet theo slug.
- Tang luot xem khi mo bai viet.
- Gui comment tren bai viet da xuat ban.
- Chi hien comment da duoc duyet.

### Auth va tai khoan

- Custom login page.
- Dang ky tai khoan moi.
- Redirect sau dang nhap theo role:
  - `ADMIN -> /admin`
  - `EDITOR -> /editor`
  - `AUTHOR -> /author`
  - `USER -> /`
- Trang profile.
- Cap nhat thong tin ca nhan.
- Doi mat khau.

### Admin

- Quan ly bai viet.
- Quan ly chuyen muc.
- Quan ly user.
- Tao/sua user va gan role.
- Bat/tat trang thai user.
- Admin co the di chuyen sang editor/author workspace tu shared sidebar.

### Author

- Xem danh sach bai viet cua minh.
- Tao bai viet moi.
- Sua bai viet cua minh.
- Luu bai o trang thai ban nhap.
- Gui bai sang hang doi duyet.
- Huy bai trong workflow.

### Editor

- Xem review queue.
- Publish bai viet.
- Yeu cau tac gia chinh sua.
- Huy bai trong workflow.
- Duyet va tu choi comment.

### Giao dien va UI

- Shared backoffice layout cho `ADMIN`, `EDITOR`, `AUTHOR`.
- Header/footer backoffice thong nhat.
- Sidebar desktop co the collapse theo kieu icon rail.
- Trang `profile` render theo role:
  - `ADMIN/EDITOR/AUTHOR`: dung backoffice shell
  - `USER`: dung account/public layout rieng

## Route quan trong

### Public

- `/`
- `/search?keyword=...`
- `/category/{slug}`
- `/article/{slug}`
- `/article/comment`

### Auth / account

- `/login`
- `/register`
- `/profile`
- `/profile/password`

### Admin

- `/admin`
- `/admin/article/create`
- `/admin/article/edit/{id}`
- `/admin/categories`
- `/admin/category/create`
- `/admin/category/edit/{id}`
- `/admin/users`
- `/admin/user/create`
- `/admin/user/edit/{id}`

### Author

- `/author`
- `/author/article/create`
- `/author/article/edit/{id}`
- `/author/article/save`
- `/author/article/submit/{id}`
- `/author/article/cancel/{id}`

### Editor

- `/editor`
- `/editor/article/review/{id}`
- `/editor/article/publish/{id}`
- `/editor/article/request-changes/{id}`
- `/editor/article/cancel/{id}`
- `/editor/comments`
- `/editor/comment/approve/{id}`
- `/editor/comment/reject/{id}`

## Du lieu mau va profile chay local

`DataSeeder` hien chi chay voi profile `dev` hoac `local`.

Neu chay ung dung voi profile nay, he thong se seed:

- Categories mau
- User mau
- Mot vai bai viet `PUBLISHED`

Tai khoan mau:

- `admin / 123456`
- `editor / 123456`
- `author / 123456`

Neu ban chay khong co profile `dev` hoac `local`, du lieu mau se khong duoc tao.

## Cau hinh database va Cloudinary

Mac dinh du an dung MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/news_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

Ban can tao DB truoc:

```sql
CREATE DATABASE news_db;
```

### Cloudinary

Tinh nang upload thumbnail dang su dung Cloudinary thong qua `StorageService`.

Cau hinh co the dat bang env vars:

```powershell
setx CLOUDINARY_CLOUD_NAME "your_cloud_name"
setx CLOUDINARY_API_KEY "your_api_key"
setx CLOUDINARY_API_SECRET "your_api_secret"
```

Luu y:

- Khong nen commit secret that len repo.
- File `src/main/resources/application.properties` dang duoc xu ly nhu local config tren may phat trien.

## Cach chay du an

### Chay bang Maven Wrapper

Windows:

```bash
mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

### Chay voi du lieu mau

Windows:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

macOS/Linux:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Sau khi chay thanh cong:

- Public site: `http://localhost:8080/`
- Login: `http://localhost:8080/login`
- Admin: `http://localhost:8080/admin`
- Editor: `http://localhost:8080/editor`
- Author: `http://localhost:8080/author`

## Test

Codebase hien da co test cho:

- Controller public va admin
- Service article/category/comment/user
- Security integration
- Auth flow
- Editorial workflow
- Comment moderation

Chay test:

```bash
mvnw.cmd test
```

Hoac:

```bash
mvn test
```

## Hien trang codebase

Nhung diem dang tot o thoi diem hien tai:

- Kien truc da tach theo feature, de doc va de mo rong hon kien truc layer cu.
- Security va role-based UI da kha ro.
- Workflow bien tap va moderation da du cho demo newsroom.
- Backoffice dung chung shell, UX nhat quan hon truoc.
- Da co bo test kha day du cho muc tieu do an/demo.

Nhung diem co the phat trien tiep:

- Tach CSS/JS rieng thay vi de nhieu inline style trong template.
- Bo sung tim kiem/filter nang hon cho user, bai viet va comment.
- Them lich su workflow hoac audit log.
- Bo sung email/notification neu can day them demo.
- Hoan thien quy trinh deploy va secret management ro rang hon.

## Ghi chu

- README nay duoc cap nhat theo codebase hien tai sau cac dot refactor, auth, user management, editorial workflow va backoffice UI refresh.
- Neu README va local config cua ban khac nhau, uu tien xem code va profile dang chay thuc te tren may.
