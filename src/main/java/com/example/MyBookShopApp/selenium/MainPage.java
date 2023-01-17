package com.example.MyBookShopApp.selenium;

import liquibase.pro.packaged.P;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class MainPage {

    private String url = "http://localhost:8085/";
    private final ChromeDriver driver;

    public MainPage(ChromeDriver driver) {
        this.driver = driver;
    }

    public MainPage callPage() {
        driver.get(url);
        return this;
    }

    public MainPage pause() throws InterruptedException {
        Thread.sleep(1500);
        return this;
    }

    public MainPage setUpSearchToken(String searchWord) {
        WebElement element = driver.findElement(By.id("query"));
        element.sendKeys(searchWord);
        return this;
    }
    public MainPage submitSearch() {
        WebElement element = driver.findElement(By.id("search"));
        element.submit();
        return this;
    }

    public MainPage goToPageContentElement(String contentElementName){
        WebElement element;
        if(contentElementName.equals("Book")){
            element = driver.findElementByClassName("Card-picture");
        }else {

            element = driver.findElementByLinkText(contentElementName);
        }

        element.click();

        return this;
    }

    public MainPage goToHeaderElement(String headerElement) {

        WebElement element = driver.findElementByLinkText(headerElement);

        element.click();
        return this;
    }
}
