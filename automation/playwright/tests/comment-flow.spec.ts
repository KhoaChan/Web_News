import { expect, test } from "@playwright/test";

test("anonymous user can submit a comment for moderation", async ({ page }) => {
  await page.goto("/article/doi-tuyen-viet-nam-chuan-bi-cho-giai-dau-lon");

  await page.getByPlaceholder("Tên của bạn").fill("Playwright Reader");
  await page.getByPlaceholder("Viết bình luận...").fill("Bình luận được gửi từ Playwright.");
  await page.getByRole("button", { name: "Gửi bình luận" }).click();

  await expect(page).toHaveURL(/\/article\/doi-tuyen-viet-nam-chuan-bi-cho-giai-dau-lon/);
  await expect(page.getByText("Đã gửi bình luận", { exact: false })).toBeVisible();
});
