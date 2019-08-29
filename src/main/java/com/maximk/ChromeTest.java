package com.maximk;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

public class ChromeTest {
    private WebDriver driver;
    private ChromeOptions options;

    @Test
    public void LaunchChrome_Method2() {
        options = new ChromeOptions();
        options.addArguments("disable-infobars");

        driver = new ChromeDriver(options);
        driver.get("http://www.google.com");
    }


}
