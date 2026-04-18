import { expect, test } from "@playwright/test";

const articlePath = "/article/doi-tuyen-viet-nam-chuan-bi-cho-giai-dau-lon";

test("anonymous user can submit a comment for moderation", async ({ page }) => {
  await page.goto(articlePath);

  const commentForm = page.locator('form[action="/article/comment"]');
  await expect(commentForm).toBeVisible();

  await commentForm.locator('input[name="name"]').fill("Playwright Reader");
  await commentForm
    .locator('textarea[name="content"]')
    .fill("Playwright comment submission from the automation flow.");
  await commentForm.locator('button[type="submit"]').click();

  await expect(page).toHaveURL(new RegExp(`${articlePath}$`));
  await expect(page.locator(".alert.alert-success")).toBeVisible();
});
