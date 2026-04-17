const { defineConfig } = require("cypress");

module.exports = defineConfig({
  e2e: {
    baseUrl: process.env.NEWS_BASE_URL || "http://localhost:8080"
  }
});
