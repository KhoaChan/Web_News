import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./tests",
  timeout: 30_000,
  use: {
    baseURL: process.env.NEWS_BASE_URL || "http://localhost:8080",
    trace: "on-first-retry"
  }
});
