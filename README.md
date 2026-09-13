# Cucumber BDD Framework (Java · Maven · Cucumber JVM · JUnit 5 · Selenium)

A production-ready BDD automation framework that demonstrates senior-level SDET
engineering practices: clean architecture, test isolation, data-driven testing,
negative-path business-rule testing, parallel execution, rich reporting, externalised
configuration, logging, failure screenshots and CI/CD readiness.

The primary feature under test is a **vegetable inventory** (`cucumber-mathematics`).
A secondary `@ui` web-smoke feature exercises the Selenium + Page Object foundation
against a stable public page.

---

## Overview

This project is a complete answer to a BDD automation assessment:

* The original exercise asked for an automated BDD test using **Node.js + Cucumber.js**.
* The assessment explicitly allows **any other language + BDD-capable framework**.
* This implementation deliberately uses **Java 21 + Maven + Cucumber JVM + JUnit 5** (LTS).

All core requirements are preserved:

1. Automated BDD test project (Maven, one-command execution).
2. Cucumber with real Gherkin feature files.
3. The supplied Gherkin behaviour implemented.
4. Thin step definitions delegating to a domain layer.
5. Scenario Outlines + Examples tables (data-driven).
6. The supplied valid examples.
7. A realistic **invalid-quantity business scenario** (rejected, not `-2`).
8. Clear setup instructions (this file).
9. Single Maven command to run the whole suite: `mvn clean test` (or
   `.\mvnw.cmd clean test` on Windows / `./mvnw clean test` on macOS/Linux via the
   included **Maven Wrapper** — no global Maven install required).

---

## Why Java Instead of Node.js?

The assessment says the test may be written in *"any other programming language and
testing framework as long as it supports BDD principles"*. We chose:

**Java 21 + Cucumber JVM + JUnit 5**

Reasons:

* **Cucumber JVM is a first-class BDD implementation** — identical Gherkin, Scenario
  Outlines, DataTables and tags as Cucumber.js.
* **JUnit 5** provides modern assertions, a rich extension model and first-class Maven
  integration via the JUnit Platform.
* **Selenium WebDriver** is the de-facto standard for browser automation and is
  required by the wider assessment (WebDriver factory, POM, headless, screenshots).
* **Maven** gives reproducible builds, dependency management and one-command CI runs.
* **Typed step parameters** (`{int}`, `{string}`) and strong compile-time checks make
  the framework more maintainable and less error-prone at scale.
* The team skillset and the assessment's Selenium requirements align naturally with
  the JVM ecosystem.

The behaviour contract (feature files) is identical regardless of the runtime chosen.

---

## Technology Stack

| Concern             | Technology                                        | Version             |
|---------------------|---------------------------------------------------|---------------------|
| Language            | Java                                              | 21 LTS             |
| Build tool          | Maven                                             | 3.8+ (tested on 3.9)|
| BDD engine          | Cucumber JVM (`cucumber-java`)                    | 7.18.1              |
| Runner integration  | Cucumber JUnit Platform Engine                    | 7.18.1              |
| Test framework      | JUnit 5 (`junit-jupiter`, `junit-platform-suite`) | 5.10.3 / 1.10.3     |
| Browser automation  | Selenium WebDriver (`selenium-java`)              | 4.44.0              |
| Logging             | SLF4J API + Logback                               | 2.0.13 / 1.5.6      |

---

## Architecture

```
Gherkin Feature
      │  parsed by Cucumber JVM
      ▼
Step Definitions ............ thin, parse inputs, delegate, assert
      │
      ▼
Scenario Context ........... per-scenario, per-thread isolated state
      │
      ▼
Domain / Business Layer .... VegetableInventory + InvalidQuantityException
      │
      ├── (domain-only scenarios stop here — NO browser started)
      │
      ▼
Page Objects ................ BasePage → ExampleHomePage (UI foundation)
      │
      ▼
WebDriver Factory ........... ThreadLocal, lazy, Chrome/Firefox, headless
      │
      ▼
Selenium WebDriver .......... Selenium Manager resolves driver binaries
```

**Domain-only tests never initialise a browser.** The `@Before` hook installs a
`ScenarioContext` but does *not* create a WebDriver. The driver is created lazily on
first use by `DriverFactory.getDriver()`, which only happens in the `@ui` web-smoke
steps. The `@After` hook therefore has nothing to tear down for mathematics scenarios,
keeping the default suite fast, deterministic and CI-friendly.

---

## Business Rule

> **A user cannot consume more vegetables than are currently available.**

Implemented in `VegetableInventory` and exercised by the `@validation` scenarios:

```
10 carrots available
        ↓
User attempts to eat 12
        ↓
Business validation (eatCarrots)
        ↓
InvalidQuantityException thrown BEFORE state is mutated
        ↓
Operation rejected
        ↓
Inventory remains 10 carrots
        ↓
Meaningful validation message: "Cannot eat more carrots than are available"
```

The inventory is **never allowed to become negative** — `10 − 12` must **not** produce
`-2`. The invalid scenario verifies all three outcomes explicitly:

1. the operation was rejected,
2. the validation message is correct,
3. the inventory was not modified.

The exception carries the exact business message (`getMessage()`) plus diagnostic
context (`getAvailable()` / `getRequested()`) so logs stay informative without
polluting the message asserted against Gherkin data.

---

## Project Structure

```
├── pom.xml
├── README.md
├── .gitignore
├── .gitlab-ci.yml
├── Jenkinsfile
└── src/test
    ├── java/com/company/automation
    │   ├── runners/TestRunner.java        @Suite runner (JUnit Platform + Cucumber)
    │   ├── stepdefinitions/CucumberMathSteps.java
    │   ├── stepdefinitions/WebSmokeSteps.java
    │   ├── hooks/Hooks.java               Before/After lifecycle
    │   ├── driver/DriverFactory.java      ThreadLocal WebDriver lifecycle
    │   ├── driver/BrowserType.java        chrome | firefox
    │   ├── driver/DriverInitializationException.java
    │   ├── pages/BasePage.java            reusable explicit-wait UI operations
    │   ├── pages/ExampleHomePage.java     @ui smoke page object
    │   ├── domain/VegetableInventory.java business rules
    │   ├── domain/InvalidQuantityException.java
    │   ├── context/ScenarioContext.java   per-scenario isolated state
    │   ├── config/ConfigReader.java       externalised configuration
    │   ├── config/ConfigurationException.java
    │   └── utils/{WaitUtils, ScreenshotUtils, TestDataUtils}.java
    └── resources
        ├── features/cucumber-mathematics.feature
        ├── features/web-smoke.feature
        ├── config/config.properties
        ├── junit-platform.properties
        └── logback.xml
```

---

## Prerequisites

* **Java 21 LTS** (the project compiles to Java 21 bytecode).
* **Maven 3.8+** — *optional.* The project ships a **Maven Wrapper**
  (`.\mvnw.cmd` on Windows, `./mvnw` on macOS/Linux). If Maven is not on your
  `PATH`, just use the wrapper — it downloads and runs its own Maven 3.9.x on the
  first invocation, with no global install or manual configuration.
* **Chrome and/or Firefox** installed for browser (`@ui`) scenarios.

**No manual browser-driver downloads are required.** Selenium 4.6+ embeds **Selenium
Manager**, which downloads the matching `chromedriver`/`geckodriver` automatically at
runtime.

---

## Installation

```bash
mvn clean install            # global Maven on PATH
.\mvnw.cmd clean install     # Maven Wrapper (Windows)
./mvnw clean install         # Maven Wrapper (macOS / Linux)
```

(One of the above compiles the framework and verifies the dependency graph; the test
suite runs in the `test` phase.)

---

## Run Tests

Run the complete suite with a single command:

```bash
mvn clean test            # global Maven on PATH
.\mvnw.cmd clean test     # Maven Wrapper (Windows — no global Maven required)
./mvnw clean test         # Maven Wrapper (macOS / Linux)
```

> **Tip:** If `mvn: The term 'mvn' is not recognized` (or `mvn: command not found`),
> Maven is simply not on your `PATH`. Use the **Maven Wrapper** instead —
> `.\mvnw.cmd clean test` (Windows) or `./mvnw clean test` (macOS/Linux). The wrapper
> downloads its own Maven automatically, so the project runs on any machine with only
> a JDK.

The default run executes all domain scenarios (the `@ui` feature is excluded by
default) — fast, deterministic and browser-free.

---

## Browser

```bash
mvn test -Dbrowser=chrome
mvn test -Dbrowser=firefox
```

Firefox must be installed on the machine for `firefox` to work.

## Headless

```bash
mvn test -Dheadless=true
```

Headless is recommended (and default in the CI samples) for agents without a display.

## Tags

Run a targeted subset with a **Maven profile** (recommended on Windows — avoids shell
quoting problems with `-Dcucumber.filter.tags="@smoke"`, which PowerShell/cmd mangle
into `Unknown lifecycle phase ...`):

```bash
mvn test -Psmoke        # @smoke scenarios, browser-free
mvn test -Pvalidation   # @validation scenarios
mvn test -Pui           # @ui browser smoke test (headless Chrome)
mvn test                # default: everything except @ui
```

The raw `-D` form still works on shells that quote it correctly:

```bash
mvn test -Dcucumber.filter.tags="@math"
mvn test -Dcucumber.filter.tags="@regression"
```

Available tags: `@smoke`, `@regression`, `@math`, `@validation`, `@ui`. Profiles are
defined in `pom.xml` (surefire `systemPropertyVariables` override `junit-platform.properties`).

---

## Configuration

Values in `src/test/resources/config/config.properties` are defaults only. **JVM system
properties always override them**, so no source edits are needed per environment:

```bash
mvn test -Dbrowser=firefox -Dheadless=true -DexplicitWait=15 -DbaseUrl=https://example.com
```

| Key                  | Default | Purpose                                  |
|----------------------|---------|------------------------------------------|
| `browser`            | `chrome`| chrome \| firefox                        |
| `headless`           | `false` | run browsers without a window            |
| `baseUrl`            | *(empty)* | application under test URL            |
| `explicitWait`       | `10`    | explicit-wait timeout (seconds)          |
| `screenshotOnFailure`| `true`  | capture/attach screenshot on failure     |

---

## Reports

The Maven test lifecycle generates:

```
target/
├── cucumber-report.html          single self-contained HTML report
├── cucumber.json                 machine-readable JSON
├── cucumber-timeline-report/     parallel execution timeline (HTML/JS)
└── surefire-reports/             JUnit XML (used by CI, e.g. GitLab/Jenkins)
```

Reports include feature, scenarios, steps, pass/fail status, failure details and
attached screenshots (see below).

---

## Screenshots

Screenshots are captured **only** for **failed scenarios that actually used a browser**
(`@ui` scenarios). They are attached to the Cucumber report with a unique, thread-safe
name (`failure_t<thread>_<scenario>_<timestamp>.png`). Successful runs and domain-only
scenarios produce no screenshot noise. Screenshots never expose credentials — only the
page pixels at failure time.

---

## Parallel Execution

Parallelism is provided by the Cucumber engine's own scheduler and is **on by default**
(4 workers):

```bash
mvn test                              # parallel on, 4 threads
mvn test -Dcucumber.execution.parallel.enabled=false
mvn test -Dcucumber.execution.parallel.config.fixed.parallelism=8
```

Thread safety is guaranteed by:

* **ThreadLocal WebDriver** — one browser per worker thread (`DriverFactory`).
* **Scenario-isolated state** — `ScenarioContext` is stored in a `ThreadLocal` and
  replaced/cleared per scenario by the hooks.
* **No shared mutable static test data** — the only statics are effectively-immutable
  (config singleton) or `ThreadLocal` holders.
* **Lazy driver creation** — parallel domain scenarios never contend for a browser.

Parallel support is validated by the `timeline` report and the scenario logs, which show
scenarios executing on distinct worker threads.

---

## Data-Driven Testing

Two mechanisms are demonstrated:

* **Scenario Outline + Examples** — the cucumber outline (`@math`) and the invalid
  carrot outline (`@validation`) both drive one scenario through multiple data rows.
* **Cucumber DataTables** — the salad scenario initialises the inventory from a table:

```gherkin
Given I have the following vegetables
  | vegetable | quantity |
  | cucumber  | 8        |
  | carrot    | 5        |
```

The DataTable is parsed into `List<Map<String, String>>` in the step definition and
fed into the domain layer.

---

## Design Decisions

* **Domain logic is separate from Cucumber.** `VegetableInventory` contains no Gherkin
  or Selenium concerns; step definitions only parse, delegate and assert. Business rules
  are therefore testable and reusable outside the BDD suite.
* **Selenium is not artificially injected into the mathematics feature.** The math
  feature is a pure domain exercise. The Selenium stack is demonstrated separately by
  the opt-in `@ui` feature, so the default suite has zero browser flakiness.
* **Invalid consumption is rejected, not clamped to zero.** `10 − 12` throws
  `InvalidQuantityException` before mutation — the inventory never goes negative and is
  provably unchanged after rejection.
* **ThreadLocal WebDriver** is used so each parallel worker owns its browser, avoiding
  cross-thread driver contention and making teardown safe.
* **Scenario state is isolated** via a per-thread `ScenarioContext` created/destroyed by
  hooks; no scenario leaks state into another.
* **Lazy driver lifecycle** keeps domain tests browser-free while still supporting full
  UI scenarios.
* **Explicit waits only** — there is no `Thread.sleep` anywhere; `WaitUtils`/`BasePage`
  use `WebDriverWait` with configurable timeouts.
* **Configuration is externalised** and overridable via system properties, so no
  hard-coded environment values exist in the framework.
* **Scaling to real UI tests** — add feature files, page objects extending `BasePage`,
  and thin step definitions; the driver factory, hooks, screenshots, reporting and
  parallel infrastructure are already in place.

---

## CI / CD

CI-friendly by construction: headless execution, no IDE dependency, no manual driver
downloads, JUnit XML + HTML/JSON report artifacts.

Samples are provided:

* `.gitlab-ci.yml` — runs `mvn clean test -Dheadless=true` in a
  `maven:3.9-eclipse-temurin-21` image and publishes JUnit/HTML/JSON reports as
  artifacts.
* `Jenkinsfile` — declarative pipeline that runs the suite headless, publishes JUnit
  results and archives reports.

---

## Commands Cheat-Sheet

Substitute `.\mvnw.cmd` (Windows) / `./mvnw` (macOS/Linux) for `mvn` whenever Maven is
not on your `PATH`.

```bash
mvn clean test                                # full default suite
mvn clean test -Dheadless=true                # headless (CI)
mvn test -Dbrowser=firefox                    # Firefox
mvn test -Dcucumber.filter.tags="@smoke"      # tag-filtered
mvn test -Dcucumber.filter.tags="@validation" # business-rule scenarios only
mvn test -Dcucumber.filter.tags="@ui" -Dheadless=true   # browser smoke (opt-in)
```



