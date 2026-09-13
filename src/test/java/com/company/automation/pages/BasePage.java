package com.company.automation.pages;

import com.company.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Base class for all Page Objects.
 *
 * <p>Provides reusable, explicit-wait-based Selenium operations so page objects stay
 * declarative and step definitions never touch Selenium directly. New application pages
 * should extend this class and expose only business-level methods.</p>
 */
public abstract class BasePage {

    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    /** Clicks an element after waiting for it to be clickable. */
    protected void click(By locator) {
        WaitUtils.waitForClickable(driver, locator).click();
    }

    /** Clears and types text into a field after waiting for it to be visible. */
    protected void type(By locator, String text) {
        WebElement element = WaitUtils.waitForVisible(driver, locator);
        element.clear();
        element.sendKeys(text);
    }

    /** Returns the visible text of an element after waiting for it to appear. */
    protected String getText(By locator) {
        return WaitUtils.waitForVisible(driver, locator).getText();
    }

    /** Waits for an element to be visible (configured default timeout). */
    protected WebElement waitForVisible(By locator) {
        return WaitUtils.waitForVisible(driver, locator);
    }

    /** Waits for an element to be clickable (configured default timeout). */
    protected WebElement waitForClickable(By locator) {
        return WaitUtils.waitForClickable(driver, locator);
    }

    /** Returns whether an element is present and visible (no waiting, no throw). */
    protected boolean isDisplayed(By locator) {
        return WaitUtils.isDisplayed(driver, locator);
    }

    /** Returns all matching elements (empty list when none are present). */
    protected List<WebElement> findElements(By locator) {
        return WaitUtils.findElements(driver, locator);
    }

    /** Returns the current page title. */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /** Returns the current page URL. */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
