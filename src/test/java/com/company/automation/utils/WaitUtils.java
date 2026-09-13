package com.company.automation.utils;

import com.company.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Explicit-wait helpers built on Selenium's {@link WebDriverWait}.
 *
 * <p>There is deliberately no {@code Thread.sleep} anywhere in the framework:
 * waiting is always conditional on an expected state, which keeps tests fast and
 * robust.</p>
 */
public final class WaitUtils {

    private WaitUtils() {
        // Utility class - no instantiation.
    }

    /** Waits for an element to be visible, using the configured default timeout. */
    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return waitForVisible(driver, locator, ConfigReader.getInstance().getExplicitWait());
    }

    /** Waits for an element to be visible within the supplied timeout (seconds). */
    public static WebElement waitForVisible(WebDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits for an element to be clickable, using the configured default timeout. */
    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return waitForClickable(driver, locator, ConfigReader.getInstance().getExplicitWait());
    }

    /** Waits for an element to be clickable within the supplied timeout (seconds). */
    public static WebElement waitForClickable(WebDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Returns true if the element is present and visible; never throws. */
    public static boolean isDisplayed(WebDriver driver, By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /** Finds all matching elements (empty list when none exist). */
    public static List<WebElement> findElements(WebDriver driver, By locator) {
        return driver.findElements(locator);
    }

    /** Waits until the page's {@code document.readyState} is {@code complete}. */
    public static void waitForPageToLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInstance().getExplicitWait()))
                .until(webDriver -> "complete".equals(
                        ((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
    }
}
