package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MainPageSeleniumTests {

    private static ChromeDriver driver;

    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(3, TimeUnit.SECONDS);
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

    @Test
    public void testMainPageAccess() throws InterruptedException {


        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();

        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    public void testMainPageSearchByQuery() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .setUpSearchToken("Tri")
                .pause()
                .submitSearch()
                .pause();

        assertTrue(driver.getPageSource().contains("Trippledex"));
    }

    @Test
    public void siteNavigationFunctionalTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .goToPageContentElement("Book")
                .pause()
                .goToHeaderElement("Genres")
                .pause()
                .goToPageContentElement("Young adult fiction")
                .pause()
                .goToHeaderElement("Recent")
                .pause()
                .goToPageContentElement("Book")
                .pause()
                .goToHeaderElement("Popular")
                .pause()
                .goToPageContentElement("Book")
                .pause()
                .goToHeaderElement("Authors")
                .pause()
                .goToPageContentElement("Nertie Congrave");

    }
}