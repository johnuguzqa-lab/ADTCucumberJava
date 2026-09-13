package com.company.automation.pages;

import com.company.automation.config.ConfigReader;
import com.company.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the public example application (https://example.com) used by the
 * {@code @ui} web-smoke feature. This demonstrates the Selenium + POM foundation on a
 * stable public page without faking a Selenium dependency into the mathematics feature.
 */
public class ExampleHomePage extends BasePage {

    private static final String DEFAULT_URL = "https://example.com/";
    private static final By HEADING = By.cssSelector("h1");
    private static final By INTRO_PARAGRAPH = By.cssSelector("p");

    public ExampleHomePage(WebDriver driver) {
        super(driver);
    }

    /** Opens the application, preferring the configured baseUrl when present. */
    public void open() {
        String baseUrl = ConfigReader.getInstance().getBaseUrl();
        String url = (baseUrl == null || baseUrl.isBlank()) ? DEFAULT_URL : baseUrl;
        driver.get(url);
        WaitUtils.waitForPageToLoad(driver);
    }

    /** Returns the main page heading text. */
    public String getHeading() {
        return getText(HEADING);
    }

    /** Returns the first paragraph (introductory) text. */
    public String getIntroText() {
        return getText(INTRO_PARAGRAPH);
    }

    /** Whether the page has loaded far enough to show its heading. */
    public boolean isPageLoaded() {
        return isDisplayed(HEADING);
    }
}
