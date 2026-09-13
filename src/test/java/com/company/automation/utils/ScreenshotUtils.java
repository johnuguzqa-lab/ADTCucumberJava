package com.company.automation.utils;

import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots on scenario failure and attaches them to the Cucumber report.
 *
 * <p>Callers (the {@code After} hook) only invoke this for <em>failed</em> scenarios
 * that actually used a browser, so successful runs produce no screenshot noise. The
 * capture itself never throws: if a screenshot cannot be taken the error is logged and
 * the original scenario result is preserved.</p>
 */
public final class ScreenshotUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotUtils.class);

    private ScreenshotUtils() {
        // Utility class - no instantiation.
    }

    /**
     * Captures the current browser state as a PNG and attaches it to the scenario.
     *
     * @param driver   active WebDriver (must support {@link TakesScreenshot})
     * @param scenario the failing Cucumber scenario to attach the image to
     */
    public static void captureScreenshot(WebDriver driver, Scenario scenario) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String name = buildName(scenario.getName());
            scenario.attach(screenshot, "image/png", name);
            LOG.error("Attached failure screenshot '{}' to scenario '{}'", name, scenario.getName());
        } catch (WebDriverException | ClassCastException e) {
            // A screenshot must never mask the original failure; log and continue.
            LOG.error("Failed to capture screenshot for scenario '{}': {}",
                    scenario.getName(), e.getMessage());
        }
    }

    /**
     * Builds a unique, file-safe screenshot name that stays unique under parallel
     * execution by including the worker thread id and a timestamp.
     */
    private static String buildName(String scenarioName) {
        String safeName = scenarioName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        return "failure_t" + Thread.currentThread().getId()
                + "_" + safeName + "_" + timestamp;
    }
}
