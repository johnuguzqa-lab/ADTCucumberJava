@ui
Feature: Web Smoke - Example Application
  Validates the Selenium + Page Object foundation against a stable public page
  (https://example.com).

  This feature is EXCLUDED from the default test run (see junit-platform.properties).
  Run it explicitly when you want to exercise the browser stack:

    mvn test -Dcucumber.filter.tags="@ui" -Dheadless=true

  Browser and headless behaviour are driven by configuration, e.g. -Dbrowser=chrome.

  @smoke @ui
  Scenario: Verify the example application home page loads
    Given I open the example application home page
    Then the page heading should be "Example Domain"
    And the page should display introductory text
