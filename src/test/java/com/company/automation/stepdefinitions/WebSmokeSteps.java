package com.company.automation.stepdefinitions;

import com.company.automation.driver.DriverFactory;
import com.company.automation.pages.ExampleHomePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step definitions for the {@code @ui} web-smoke feature.
 *
 * <p>This is the only place the framework's Selenium stack is exercised. It demonstrates
 * the WebDriver factory (lazy, ThreadLocal, headless-capable), the Page Object Model and
 * explicit waits against a stable public page. It is excluded from the default test run
 * (see {@code junit-platform.properties}) so {@code mvn clean test} stays fast and
 * deterministic.</p>
 */
public class WebSmokeSteps {

    private static final Logger LOG = LoggerFactory.getLogger(WebSmokeSteps.class);

    @Given("I open the example application home page")
    public void iOpenTheExampleHomePage() {
        ExampleHomePage page = new ExampleHomePage(DriverFactory.getDriver());
        page.open();
        LOG.info("Opened example application, title='{}', url='{}'",
                page.getPageTitle(), page.getCurrentUrl());
    }

    @Then("the page heading should be {string}")
    public void thePageHeadingShouldBe(String expectedHeading) {
        String actual = new ExampleHomePage(DriverFactory.getDriver()).getHeading();
        assertEquals(expectedHeading, actual,
                () -> "Unexpected page heading. Expected '" + expectedHeading
                        + "' but found '" + actual + "'");
    }

    @Then("the page should display introductory text")
    public void thePageShouldDisplayIntroductoryText() {
        ExampleHomePage page = new ExampleHomePage(DriverFactory.getDriver());
        assertTrue(page.isPageLoaded(), "Example page should be loaded (heading visible)");
        assertTrue(!page.getIntroText().isBlank(),
                "Example page should display an introductory paragraph");
    }
}
