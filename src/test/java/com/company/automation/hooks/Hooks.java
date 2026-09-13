package com.company.automation.hooks;

import com.company.automation.context.ScenarioContext;
import com.company.automation.driver.DriverFactory;
import com.company.automation.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber lifecycle hooks.
 *
 * <p><strong>Before each scenario:</strong> installs a fresh, thread-isolated
 * {@link ScenarioContext} so no state leaks between scenarios or parallel threads.</p>
 *
 * <p><strong>After each scenario:</strong> detects failures, captures a screenshot when a
 * browser was actually used, then tears down the ThreadLocal WebDriver and clears the
 * context. The browser is <em>never</em> started proactively here - domain-only scenarios
 * such as the mathematics feature run without any Selenium overhead.</p>
 */
public class Hooks {

    private static final Logger LOG = LoggerFactory.getLogger(Hooks.class);

    @Before(order = 0)
    public void beforeScenario(Scenario scenario) {
        ScenarioContext.set(ScenarioContext.create());
        LOG.info("=== START scenario: {} [{}] on thread '{}' ===",
                scenario.getName(), scenario.getId(), Thread.currentThread().getName());
    }

    @After(order = 0)
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                LOG.error("=== FAIL scenario: {} ===", scenario.getName());
                // Screenshot only makes sense if this scenario actually opened a browser.
                if (DriverFactory.isDriverInitialized()) {
                    ScreenshotUtils.captureScreenshot(DriverFactory.getDriver(), scenario);
                } else {
                    LOG.warn("Scenario failed but no WebDriver was initialized - no screenshot taken.");
                }
            } else {
                LOG.info("=== PASS scenario: {} ===", scenario.getName());
            }
        } finally {
            DriverFactory.quitDriver();
            ScenarioContext.clear();
        }
    }
}
