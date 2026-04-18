import { expect, test, type Page } from "@playwright/test";

const credentials = {
  admin: { username: "admin", password: "123456", redirectPath: "/admin" },
  editor: { username: "editor", password: "123456", redirectPath: "/editor" },
  author: { username: "author", password: "123456", redirectPath: "/author" }
};

async function login(page: Page, username: string, password: string) {
  await page.goto("/login");
  await page.locator('input[name="username"]').fill(username);
  await page.locator('input[name="password"]').fill(password);
  await page.locator('button[type="submit"]').click();
}

test.describe("login role redirect", () => {
  for (const account of Object.values(credentials)) {
    test(`redirects ${account.username} to ${account.redirectPath}`, async ({ page }) => {
      await login(page, account.username, account.password);

      await expect(page).toHaveURL(new RegExp(`${account.redirectPath}$`));
    });
  }

  test("shows an error on invalid credentials", async ({ page }) => {
    await login(page, "author", "wrong-password");

    await expect(page).toHaveURL(/\/login\?error$/);
    await expect(page.locator(".alert.alert-danger")).toBeVisible();
  });
});
