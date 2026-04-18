import { expect, test, type Page } from "@playwright/test";

const registerPath = "/register";
const loginPathPattern = /\/login\?registered$/;

type RegisterData = {
  username: string;
  email: string;
  fullName: string;
  password: string;
  confirmPassword: string;
};

function uniqueSuffix() {
  return Date.now().toString(36).slice(-6);
}

function uniqueEmail(prefix: string) {
  return `${prefix}-${uniqueSuffix()}@example.com`;
}

async function fillRegisterForm(page: Page, data: RegisterData) {
  await page.goto(registerPath);
  await page.locator('input[name="username"]').fill(data.username);
  await page.locator('input[name="email"]').fill(data.email);
  await page.locator('input[name="fullName"]').fill(data.fullName);
  await page.locator('input[name="password"]').fill(data.password);
  await page.locator('input[name="confirmPassword"]').fill(data.confirmPassword);
}

async function submitRegisterForm(page: Page, data: RegisterData) {
  await fillRegisterForm(page, data);
  await page.locator('button[type="submit"]').click();
}

test.describe("register flow black-box cases", () => {
  test("BB-TC01 registers successfully with valid data", async ({ page }) => {
    await submitRegisterForm(page, {
      username: `reader01-${uniqueSuffix()}`,
      email: uniqueEmail("reader01"),
      fullName: "Reader 01",
      password: "123456",
      confirmPassword: "123456"
    });

    await expect(page).toHaveURL(loginPathPattern);
    await expect(page.locator(".alert.alert-success")).toBeVisible();
  });

  test("BB-TC02 rejects username with invalid characters", async ({ page }) => {
    await submitRegisterForm(page, {
      username: "reader 01",
      email: uniqueEmail("reader02"),
      fullName: "Reader 02",
      password: "123456",
      confirmPassword: "123456"
    });

    await expect(page).toHaveURL(/\/register$/);
    await expect(page.locator('input[name="username"]')).toHaveClass(/is-invalid/);
  });

  test("BB-TC03 rejects duplicate username", async ({ page }) => {
    await submitRegisterForm(page, {
      username: "admin",
      email: uniqueEmail("reader03"),
      fullName: "Reader 03",
      password: "123456",
      confirmPassword: "123456"
    });

    await expect(page).toHaveURL(/\/register$/);
    await expect(page.locator('input[name="username"]')).toHaveClass(/is-invalid/);
  });

  test("BB-TC04 rejects duplicate email", async ({ page }) => {
    await submitRegisterForm(page, {
      username: `reader04-${uniqueSuffix()}`,
      email: "admin@news.com",
      fullName: "Reader 04",
      password: "123456",
      confirmPassword: "123456"
    });

    await expect(page).toHaveURL(/\/register$/);
    await expect(page.locator('input[name="email"]')).toHaveClass(/is-invalid/);
  });

  test("BB-TC05 rejects mismatched confirmation password", async ({ page }) => {
    await submitRegisterForm(page, {
      username: `reader05-${uniqueSuffix()}`,
      email: uniqueEmail("reader05"),
      fullName: "Reader 05",
      password: "123456",
      confirmPassword: "654321"
    });

    await expect(page).toHaveURL(/\/register$/);
    await expect(page.locator('input[name="confirmPassword"]')).toHaveClass(/is-invalid/);
  });

  test("BB-TC06 rejects username shorter than 3 characters", async ({ page }) => {
    await submitRegisterForm(page, {
      username: "ab",
      email: uniqueEmail("reader06"),
      fullName: "Reader 06",
      password: "123456",
      confirmPassword: "123456"
    });

    await expect(page).toHaveURL(/\/register$/);
    await expect(page.locator('input[name="username"]')).toHaveClass(/is-invalid/);
  });

  test("BB-TC07 accepts username with exactly 3 characters", async ({ page }) => {
    await submitRegisterForm(page, {
      username: uniqueSuffix().slice(-3),
      email: uniqueEmail("reader07"),
      fullName: "Reader 07",
      password: "123456",
      confirmPassword: "123456"
    });

    await expect(page).toHaveURL(loginPathPattern);
    await expect(page.locator(".alert.alert-success")).toBeVisible();
  });

  test("BB-TC08 rejects password shorter than 6 characters", async ({ page }) => {
    await submitRegisterForm(page, {
      username: `reader08-${uniqueSuffix()}`,
      email: uniqueEmail("reader08"),
      fullName: "Reader 08",
      password: "12345",
      confirmPassword: "12345"
    });

    await expect(page).toHaveURL(/\/register$/);
    await expect(page.locator('input[name="password"]')).toHaveClass(/is-invalid/);
  });

  test("BB-TC09 accepts password with exactly 6 characters", async ({ page }) => {
    await submitRegisterForm(page, {
      username: `reader09-${uniqueSuffix()}`,
      email: uniqueEmail("reader09"),
      fullName: "Reader 09",
      password: "123456",
      confirmPassword: "123456"
    });

    await expect(page).toHaveURL(loginPathPattern);
    await expect(page.locator(".alert.alert-success")).toBeVisible();
  });
});
