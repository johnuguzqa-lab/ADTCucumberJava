package com.company.automation.driver;

import com.company.automation.config.ConfigReader;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central WebDriver lifecycle manager.
 *
 * <p>One WebDriver instance is held per worker thread via {@link ThreadLocal}, which
 * makes the factory safe for Cucumber parallel execution: concurrent scenarios running
 * on different threads never share a browser.</p>
 *
 * <p>The driver is created <strong>lazily</strong> on first use. Domain-only scenarios
 * (e.g. the cucumber-mathematics feature) never call {@link #getDriver()}, so no browser
 * is started for them. The {@code After} hook only tears down a driver if one was
 * actually created for that scenario.</p>
 *
 * <p>Driver binaries are resolved automatically by Selenium Manager (bundled with
 * Selenium 4.6+), so no manual chromedriver/geckodriver download is required.</p>
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);
    private static final ConfigReader CONFIG = ConfigReader.getInstance();

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
        // Utility class - no instantiation.
    }

    /**
     * Returns the WebDriver for the current thread, creating it on first use.
     * Browser and headless settings come from {@link ConfigReader}.
     */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            driver = initializeDriver();
        }
        return driver;
    }

    /**
     * Creates (or returns the already-created) WebDriver for the current thread.
     */
    public static WebDriver initializeDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            return driver;
        }
        String browserName = CONFIG.getBrowser();
        boolean headless = CONFIG.isHeadless();
        BrowserType browser = BrowserType.from(browserName);

        driver = switch (browser) {
            case CHROME -> createChromeDriver(headless);
            case FIREFOX -> createFirefoxDriver(headless);
        };

        DRIVER.set(driver);
        LOG.info("Initialized {} WebDriver (headless={}) on thread '{}'",
                browserName, headless, Thread.currentThread().getName());
        return driver;
    }

    /** Returns whether a driver has been created for the current thread. */
    public static boolean isDriverInitialized() {
        return DRIVER.get() != null;
    }

    /** Quits and clears the driver for the current thread, if one exists. */
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            return;
        }
        try {
            driver.quit();
        } catch (RuntimeException e) {
            // The browser may already be gone (crashed, manually closed); never mask
            // the original scenario result because of a teardown hiccup.
            LOG.warn("Error while quitting WebDriver: {}", e.getMessage());
        } finally {
            DRIVER.remove();
            LOG.info("Quit WebDriver and cleared ThreadLocal on thread '{}'",
                    Thread.currentThread().getName());
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        // CI-friendly flags (harmless locally, required on some container runners).
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.setAcceptInsecureCerts(true);
        try {
            return new ChromeDriver(options);
        } catch (RuntimeException e) {
            throw new DriverInitializationException(
                    "Failed to create ChromeDriver (browser=" + CONFIG.getBrowser()
                            + ", headless=" + headless + ")", e);
        }
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.setAcceptInsecureCerts(true);
        try {
            return new FirefoxDriver(options);
        } catch (RuntimeException e) {
            throw new DriverInitializationException(
                    "Failed to create FirefoxDriver (browser=" + CONFIG.getBrowser()
                            + ", headless=" + headless + ")", e);
        }
    }
}
