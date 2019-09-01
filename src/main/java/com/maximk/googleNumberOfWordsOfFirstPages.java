package com.maximk;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class googleNumberOfWordsOfFirstPages {
    private static final String googleURL = "https://google.com";
    private static final String incognitoMode = "--incognito";
    private static final String maximizeWindow = "--start-maximized";
    private WebDriver driver;

    @BeforeMethod
    public void startBrowser(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments(incognitoMode, maximizeWindow);

        driver = new ChromeDriver(options);
    }

    @AfterMethod
    public void closeBrowser(){
        driver.quit();
    }

    @Test
    public void getNumberOfSearchedWords() throws InterruptedException, IOException {
        String searchWord = "Cheetah";
        String csvFileName = searchWord + ".csv";
        Writer writer = new BufferedWriter(new FileWriter(csvFileName));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Page No", "Link No", "Title", "URL", "Occurrences"));

        String listOfLinksXpath = "//div[h2[not(contains(text(), 'People also ask'))]]//div[@class='r']//a[contains(., '" + searchWord + "') or contains(., '" + searchWord.toLowerCase() + "')]";

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); // waits max 5 seconds for element to appear
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS); // waits max 30 seconds to load the page
        driver.get(googleURL);

        WebElement googleSearchField = driver.findElement(By.xpath("//input[@name='q']"));
        googleSearchField.clear();
        googleSearchField.sendKeys(searchWord);
        googleSearchField.submit();

        for (int i = 0; i < 4; i++) {
            List<WebElement> links = driver.findElements(By.xpath(listOfLinksXpath));
            for (int j = 0; j < links.size(); j++) {
                List<WebElement> linksInnerList = driver.findElements(By.xpath(listOfLinksXpath));
                linksInnerList.get(j).click();
                String pageTitle = driver.getTitle();
                String pageURL = driver.getCurrentUrl();
                String numberOfOccurrences = StringUtils.countMatches(driver.getPageSource().toLowerCase(), searchWord.toLowerCase()) + "";
                csvPrinter.printRecord(i+1 + "", j+1 + "", pageTitle, pageURL, numberOfOccurrences);
                while(!driver.getCurrentUrl().contains("google.com")) { // Created to avoid web sites where 1 backspace is not enough
                    driver.navigate().back();
                    // Added in terms to avoid empty list
                    Thread.sleep(4000);
                }
            }
            csvPrinter.flush();
            String pageNumberXpath = "//div[@id='navcnt']//a[@aria-label='Page " + (i + 2) + "']";
            driver.findElement(By.xpath(pageNumberXpath)).click();
        }
    }
}
