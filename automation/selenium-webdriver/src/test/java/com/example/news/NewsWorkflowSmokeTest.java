package com.example.news;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

class NewsWorkflowSmokeTest {

    private final String baseUrl = env("NEWS_BASE_URL", "http://localhost:8080");
    private final String authorUsername = env("NEWS_AUTHOR_USERNAME", "author");
    private final String authorPassword = env("NEWS_AUTHOR_PASSWORD", "123456");
    private final String editorUsername = env("NEWS_EDITOR_USERNAME", "editor");
    private final String editorPassword = env("NEWS_EDITOR_PASSWORD", "123456");

    private WebDriver driver;

    @Test
    void authorCanSubmitDraftAndEditorCanPublishIt() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String title = "QA Selenium Workflow " + System.currentTimeMillis();
        String slug = "qa-selenium-workflow-" + System.currentTimeMillis();

        login(authorUsername, authorPassword);
        driver.get(baseUrl + "/author/article/create");
        driver.findElement(By.id("title")).sendKeys(title);
        driver.findElement(By.id("slug")).clear();
        driver.findElement(By.id("slug")).sendKeys(slug);
        driver.findElement(By.id("summary")).sendKeys("Bài nháp được tạo bởi Selenium WebDriver.");
        new Select(driver.findElement(By.id("categoryId"))).selectByIndex(1);
        WebElement content = driver.findElement(By.cssSelector("textarea[name='content']"));
        content.clear();
        content.sendKeys("<p>Nội dung kiểm thử workflow.</p>");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement authorRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[.//div[normalize-space()='" + title + "']]")));
        authorRow.findElement(By.cssSelector("form[action*='/author/article/submit/'] button")).click();
        wait.until(ExpectedConditions.urlContains("/author"));

        driver.get(baseUrl + "/logout");

        login(editorUsername, editorPassword);
        driver.get(baseUrl + "/editor");
        WebElement reviewRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[.//div[normalize-space()='" + title + "']]")));
        reviewRow.findElement(By.cssSelector("form[action*='/editor/article/publish/'] button")).click();
        wait.until(ExpectedConditions.urlContains("/editor"));

        assertTrue(driver.getPageSource().contains(title));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String username, String password) {
        driver.get(baseUrl + "/login");
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    private String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
