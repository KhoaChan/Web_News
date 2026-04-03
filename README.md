# NewsWebsite

Ung dung web tin tuc xay dung bang Spring Boot theo mo hinh MVC, su dung Thymeleaf de render giao dien va MySQL de luu tru du lieu. Du an hien dang o giai doan hoc tap/demo, huong toi mo phong mot website tin tuc co trang doc bai viet cong khai va khu vuc quan tri noi dung cho admin.

## Ban dang lam gi trong du an nay?

Tu codebase hien tai, co the thay ban dang xay dung:

- Mot website tin tuc co giao dien trang chu, danh sach bai viet, loc theo chuyen muc, tim kiem va trang chi tiet bai viet.
- Mot khu vuc admin de dang nhap, quan ly bai viet, quan ly chuyen muc va upload anh dai dien.
- He thong xac thuc/phan quyen voi Spring Security, trong do duong dan `/admin/**` chi cho tai khoan co vai tro `ADMIN`.
- Co che seed du lieu mau khi khoi dong de co san tai khoan admin, chuyen muc va mot so bai viet mau.

Noi cach khac, day la mot CMS mini cho trang tin tuc, ket hop giua phan hien thi noi dung cho nguoi doc va phan quan tri noi dung o backend.

## Cong nghe su dung

- Java 21
- Spring Boot 4.0.5
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- MySQL
- Lombok
- Bootstrap 5
- CKEditor 5

## Chuc nang hien co

### Public site

- Trang chu hien thi danh sach bai viet co phan trang.
- Xem chi tiet bai viet theo slug.
- Loc bai viet theo chuyen muc.
- Tim kiem bai viet theo tieu de.
- Gui binh luan tren trang chi tiet bai viet.
- Hien thi danh sach chuyen muc trong thanh dieu huong.

### Admin site

- Dang nhap bang Spring Security form login mac dinh.
- Dashboard quan ly danh sach bai viet.
- Tao, sua, xoa bai viet.
- Tao va sua chuyen muc.
- Upload thumbnail cho bai viet.
- Soan noi dung bai viet bang CKEditor.

## Tai khoan va du lieu mac dinh

Khi ung dung chay lan dau, lop `DataSeeder` se tu dong tao du lieu mau neu database dang rong:

- Tai khoan admin:
  - Username: `admin`
  - Password: `123456`
- Chuyen muc mau:
  - `the-thao`
  - `cong-nghe`
  - `giai-tri`
- Mot vai bai viet mau de kiem thu giao dien.

## Cau truc du an

```text
src/main/java/com/example/news
|- component       # Seed du lieu mau khi khoi dong
|- config          # Cau hinh bao mat
|- controller      # Controller public va admin
|- entity          # Article, Category, Comment, User, Role
|- repository      # JPA Repository
|- security        # UserDetailsService tuy bien
|- service         # Tang nghiep vu

src/main/resources
|- static/uploads  # Anh upload
|- templates       # Giao dien Thymeleaf
   |- admin        # Giao dien quan tri
```

## Luong chinh trong he thong

### 1. Public content flow

- `HomeController` phuc vu trang chu va tim kiem.
- `ArticleController` phuc vu trang chi tiet, loc theo chuyen muc va luu binh luan.
- `ArticleService` va `ArticleRepository` xu ly truy van bai viet, phan trang va tim kiem.

### 2. Admin content flow

- `AdminController` xu ly dashboard, form bai viet, form chuyen muc va upload anh.
- Bai viet duoc gan tac gia dua tren tai khoan dang dang nhap.
- Anh thumbnail duoc luu vao `src/main/resources/static/uploads/`.

### 3. Security flow

- `SecurityConfig` mo public cho cac trang doc tin va khoa toan bo `/admin/**` bang role `ADMIN`.
- `CustomUserDetailsService` tai thong tin user tu database.
- Password duoc ma hoa bang BCrypt.

## Cau hinh database

File cau hinh hien tai nam tai `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/news_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Ban can tao truoc database:

```sql
CREATE DATABASE news_db;
```

Sau do chinh lai `spring.datasource.username` va `spring.datasource.password` cho phu hop may cua ban.

## Cach chay du an

### Cach 1: dung Maven Wrapper

Windows:

```bash
mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

### Cach 2: dung Maven da cai san

```bash
mvn spring-boot:run
```

Neu `mvnw.cmd` tren Windows bao loi `Cannot start maven from wrapper`, ban co the:

- Dung Maven da cai san trong may.
- Hoac tao lai Maven Wrapper sau khi moi truong Maven da san sang.

Sau khi chay thanh cong, truy cap:

- Trang chu: `http://localhost:8080/`
- Trang dang nhap: `http://localhost:8080/login`
- Trang admin: `http://localhost:8080/admin`

## Cac URL quan trong

- `/` : trang chu
- `/search?keyword=...` : tim kiem bai viet
- `/category/{slug}` : xem bai viet theo chuyen muc
- `/article/{slug}` : xem chi tiet bai viet
- `/admin` : dashboard quan tri
- `/admin/article/create` : tao bai viet moi
- `/admin/categories` : danh sach chuyen muc

## Hien trang codebase

Day la nhung gi co the ket luan tu code hien tai:

- Kien truc da tach thanh cac tang kha ro rang: controller, service, repository, entity.
- Du an da co du luong cho public site va admin site, phu hop voi bai tap lon/mini CMS.
- Giao dien duoc cham chut kha ky, dac biet o trang chu, trang chi tiet va dashboard admin.
- He thong comment da co luu du lieu, nhung chua co moderation.
- Test hien tai moi o muc co ban voi `contextLoads()`.

## Nhung diem can hoan thien tiep

- Bo sung test cho service, controller va repository.
- Hoan thien quy trinh xuat ban/draft de public site chi hien thi bai viet mong muon.
- Hoan thien bo dem luot xem bai viet.
- Them giao dien tim kiem ro rang hon tren frontend.
- Can nhac tach file CSS/JS rieng thay vi de nhieu style inline trong template.
- Bo sung validation va xu ly loi than thien hon o form admin.

## Ghi chu thuc te khi tiep tuc phat trien

- Thu muc `target/` dang co trong workspace, nhung khong nen dua vao tai lieu thiet ke hay commit phu thuoc build output.
- Anh upload hien dang luu thang trong `src/main/resources/static/uploads`, phu hop cho demo/noi bo, nhung ve lau dai nen tach sang storage rieng.
- Form login hien tai la trang mac dinh cua Spring Security, chua co giao dien dang nhap tu thiet ke rieng.

## Dinh huong phat trien phu hop

Neu ban muon day du an nay len muc hoan chinh hon, huong di hop ly la:

1. On dinh hoa CRUD bai viet/chuyen muc.
2. Bo sung custom login page va quan ly user.
3. Tach frontend resources ra file CSS/JS rieng.
4. Them validation, exception handling va trang loi.
5. Viet test va tai lieu API/noi bo day du hon.

---

README nay duoc viet dua tren hien trang codebase thuc te trong repo o thoi diem hien tai, de phuc vu viec onboarding, bao cao do an va tiep tuc phat trien.
