package com.company.automation.runners;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 / JUnit Platform runner for Cucumber.
 *
 * <p>Discovers all feature files under {@code src/test/resources/features}, binds the
 * glue classes (hooks + step definitions), and delegates execution to the Cucumber
 * JUnit Platform engine. Maven Surefire picks this class up automatically, so the
 * whole suite runs with a single {@code mvn clean test}.</p>
 *
 * <p>Runtime behaviour (plugins/reports, parallel execution, default tag filter,
 * publish settings) is configured in {@code junit-platform.properties}; CLI overrides
 * such as {@code -Dcucumber.filter.tags="@smoke"} take precedence over that file.</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.company.automation.hooks, com.company.automation.stepdefinitions")
public class TestRunner {
    // No code here: the annotations are the entire configuration.
}
