# Automation Scaffolds for News Website

Thư mục này chứa 4 scaffold automation tương ứng 4 công cụ đã chọn trong kế hoạch báo cáo:

- `selenium-ide`: smoke flow cho auth.
- `selenium-webdriver`: E2E nhiều vai trò bằng Java + Selenium.
- `playwright`: comment flow.
- `cypress`: browse/search/category/detail flow.

## Chuẩn bị chung

1. Chạy ứng dụng với profile `local`:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

2. Dùng các tài khoản seed:

- `admin / 123456`
- `editor / 123456`
- `author / 123456`

3. Base URL mặc định là `http://localhost:8080`.

## Cách dùng nhanh

### Selenium IDE

- Mở file `selenium-ide/news-smoke.side` trong Selenium IDE.
- Chỉnh `baseUrl` nếu cần.

### Selenium WebDriver

```bash
cd automation/selenium-webdriver
mvn test
```

Biến môi trường hỗ trợ:

- `NEWS_BASE_URL`
- `NEWS_AUTHOR_USERNAME`
- `NEWS_AUTHOR_PASSWORD`
- `NEWS_EDITOR_USERNAME`
- `NEWS_EDITOR_PASSWORD`

### Playwright

```bash
cd automation/playwright
npm install
npx playwright test
```

### Cypress

```bash
cd automation/cypress
npm install
npx cypress run
```

## Ghi chú

- Các script này là scaffold sẵn dùng, chưa được chạy tự động trong phiên làm việc này vì cần browser/Node environment riêng.
- Nếu muốn demo ổn định, nên khởi động app local trước và chạy theo thứ tự Selenium IDE -> WebDriver -> Playwright -> Cypress.
